package com.haavar.insteon;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Haavar Valeur
 */
@Slf4j
public class ModemCommandParser {
    public static final byte HELLO = 0x02;

    //todo: should take an interface, and not a serial port reference
    public StandardMessage readMessage(SerialPort serialPort, int readTimeout) throws InvalidStateException, IOException {
        try {
            byte[] header;
            // should I listen for events intead?
            try {
                header = serialPort.readBytes(2, readTimeout);
            } catch (SerialPortTimeoutException e) {
                log.trace("Timed out while reading header");
                return null;
            }
            if (header.length != 2 || header[0] != HELLO) {
                log.error("Did not get hello. Possibly out of sync. data=" + ByteUtils.bytesToHex(header));
                throw new InvalidStateException();
            }
            ModemCommand modemCommand = ModemCommand.getInboundCommand(header[1]);
            if (modemCommand == null) {
                throw new InvalidStateException();
            }

            byte[] body;
            switch (modemCommand) {
                case SEND_STANDARD_OR_EXTENDED_MESSAGE_REPLY:
                    // <to:3><flag:1><
                    byte[] stdBody = serialPort.readBytes(7, readTimeout); // can be 21 if extended
                    byte flags = stdBody[3];
                    //http://www.madreporite.com/insteon/plm_basics.html
                    boolean isExtended = (flags & (1 << 4)) != 0; // argh, why is there not a different cmd for each type...
                    if (isExtended) {
                        byte[] extraBytes = serialPort.readBytes(14, readTimeout); // we already read 7, 14 more to go
                        log.info("Received extended echo " + ByteUtils.bytesToHex(stdBody) +  ByteUtils.bytesToHex(extraBytes));
                    } else {
                        log.info("Received standard echo " + ByteUtils.bytesToHex(stdBody));
                    }

                    return null;// todo: should probably return something...
                case STANDARD_MESSAGE_RECEIVED:
                    body = serialPort.readBytes(modemCommand.length, readTimeout);
                    return new StandardMessage(body);
                default:
                    body = serialPort.readBytes(modemCommand.length, readTimeout);
                    log.error("Ignoring modem command " + modemCommand + " body=" + ByteUtils.bytesToHex(body));
                    return null;// todo: should probably return just a binary message...
            }
        } catch (SerialPortException e) {
            log.error("Error reading message from " + serialPort.getPortName(), e);
            throw new IOException(e);
        } catch (SerialPortTimeoutException e) {
            log.error("Timeout reading message from " + serialPort.getPortName());
            throw new IOException(e);
        }
    }

    /**
     * Exception thrown when the stream has gotten into an invalid state.
     * When this is caught, the port should be reset
     */
    public static class InvalidStateException extends Exception {

    }

}
