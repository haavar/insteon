package com.haavar.insteon;

import com.haavar.insteon.messages.Message;
import com.haavar.insteon.messages.OutboundMessage;
import com.haavar.insteon.messages.ReplyMessage;
import jssc.SerialPort;
import jssc.SerialPortException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for communicating with an USB PowerLinc
 * @author Haavar Valeur
 */
@Slf4j
public class JavaSimpleSerialConnection {
    private static final Object LOCK = new Object();
    private final AtomicReference<ReplyMessage> replyMessageReference = new AtomicReference<>();
    private ModemCommandParser commandParser = new ModemCommandParser();
    private SerialPort serialPort;
    private int readTimeout = 2000;


    public JavaSimpleSerialConnection(String port, MessageListener messageListener) {
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_19200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
        Thread reader = new Thread(() -> {
            while (serialPort.isOpened()) {
                try {
                    Message message = commandParser.readMessage(serialPort, readTimeout);
                    if (message != null) {
                        if (ReplyMessage.class.isAssignableFrom(message.getClass())) {
                            synchronized (replyMessageReference) {
                                replyMessageReference.set((ReplyMessage)message);
                                replyMessageReference.notify(); // because of the lock, only one is waiting
                            }
                        }
                        messageListener.onMessage(message);
                    }
                } catch (ModemCommandParser.InvalidStateException | IOException e) {
                    e.printStackTrace(); // todo: reset stream
                }
            }
            log.info("Serial port is closed. Stopped listening.");
        });
        reader.setDaemon(true);
        reader.start();

    }

    public ReplyMessage sendMessageBlocking(OutboundMessage message, int timeoutSec) throws TimeoutException {
        synchronized (LOCK) {
            replyMessageReference.set(null);
            try {
                serialPort.writeBytes(message.toBytes());
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
            synchronized (replyMessageReference) {
                long deadLine = System.currentTimeMillis() + timeoutSec * 1000;
                while (System.currentTimeMillis() < deadLine) {
                    ReplyMessage replyMessage = replyMessageReference.getAndSet(null);
                    if (replyMessage != null) {
                        return replyMessage;
                    }
                    try {
                        replyMessageReference.wait(deadLine - System.currentTimeMillis());
                    } catch (InterruptedException ignore) {
                    }
                }
                throw new TimeoutException();
            }

        }
    }
    
    public void sendMessage(OutboundMessage message) {
        synchronized (LOCK) {
            try {
                serialPort.writeBytes(message.toBytes());
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
