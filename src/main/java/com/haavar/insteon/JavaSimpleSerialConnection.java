package com.haavar.insteon;

import com.haavar.insteon.messages.Message;
import com.haavar.insteon.messages.OutboundMessage;
import com.haavar.insteon.messages.Reply;
import jssc.SerialPort;
import jssc.SerialPortException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for communicating with an USB PowerLinc
 * @author Haavar Valeur
 */
@Slf4j
public class JavaSimpleSerialConnection {
    private static final Object LOCK = new Object();
    private final AtomicReference<Reply> replyMessageReference = new AtomicReference<>();
    private ModemCommandParser commandParser = new ModemCommandParser();
    private SerialPort serialPort;
    private int readTimeout = 2000;
    private int writeDelay = 500;
    private long lastWriteTime;


    public JavaSimpleSerialConnection(String port, MessageListener messageListener) {
        serialPort = new SerialPort(port);
        openPort(serialPort);

        Thread reader = new Thread(() -> {
            while (true) {
                try {
                    /*
                    // on the raspberry, it kept re-opening the port...
                    if (!serialPort.isCTS()) {
                        log.info("Is not clear to send. Re-opening port.");
                        // we could also listen for cts events
                        serialPort.closePort();
                        openPort(serialPort);
                    }*/
                    Message message = commandParser.readMessage(serialPort, readTimeout);
                    if (message != null) {
                        if (Reply.class.isAssignableFrom(message.getClass())) {
                            synchronized (replyMessageReference) {
                                replyMessageReference.set((Reply)message);
                                replyMessageReference.notify(); // because of the lock, only one is waiting
                            }
                        }
                        messageListener.onMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // todo: reset stream
                }
            }
        });
        reader.setDaemon(true);
        reader.start();

    }

    public Reply sendMessageBlocking(OutboundMessage message, int timeoutSec) throws TimeoutException {
        synchronized (LOCK) {
            writeDelay();
            replyMessageReference.set(null);
            try {
                log.info("About to send cts=" + serialPort.isCTS());
                serialPort.writeBytes(message.toBytes());
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
            synchronized (replyMessageReference) {
                long deadLine = System.currentTimeMillis() + timeoutSec * 1000;
                while (System.currentTimeMillis() < deadLine) {
                    Reply replyMessage = replyMessageReference.getAndSet(null);
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
            writeDelay();
            try {
                serialPort.writeBytes(message.toBytes());
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * For some reason the PLM does not handle back to back writes, and I have to give it a break.
     * I have not found anything in the comm port state that indicates that it's ready for data.
     * It does not matter that the PLM has ack'ed and responded to the last message.
     */
    private void writeDelay() {
        long sleepTime =  lastWriteTime + writeDelay - System.currentTimeMillis();
        if (sleepTime > 0) {
            log.info("Sleeping " + sleepTime + "ms");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
        lastWriteTime = System.currentTimeMillis();
    }

    private void openPort(SerialPort serialPort) {
        log.info("Opening serial port");

        while(true) {
            try {
                serialPort.openPort();
                serialPort.setParams(SerialPort.BAUDRATE_19200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                log.info("Serial port opened");
                return;
            } catch (SerialPortException e) {
                log.info("Unable to open serial port " + e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

}
