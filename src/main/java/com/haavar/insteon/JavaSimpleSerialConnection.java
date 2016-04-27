package com.haavar.insteon;

import jssc.SerialPort;
import jssc.SerialPortException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Class for communicating with an USB PowerLinc
 * @author Haavar Valeur
 */
@Slf4j
public class JavaSimpleSerialConnection {
    private static final Object LOCK = new Object();
    private MessageListener messageListener;
    private ModemCommandParser commandParser = new ModemCommandParser();
    private SerialPort serialPort;
    private int readTimeout = 2000;


    public JavaSimpleSerialConnection(String port, MessageListener messageListener) {
        this.messageListener = messageListener;
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
                    StandardMessage message = commandParser.readMessage(serialPort, readTimeout);
                    if (message != null) {
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

    public void sendMessage(StandardMessage message) {
        synchronized (LOCK) {
            try {
                serialPort.writeBytes(message.toBytes());
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
