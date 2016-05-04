package com.haavar.insteon;

import com.haavar.insteon.messages.AllLinkResponse;
import com.haavar.insteon.messages.BinaryMessage;
import com.haavar.insteon.messages.ExtendedMessageReceived;
import com.haavar.insteon.messages.NotOkReply;
import com.haavar.insteon.messages.ProductDataResponse;
import com.haavar.insteon.messages.SimpleReply;
import com.haavar.insteon.messages.Message;
import com.haavar.insteon.messages.StandardMessageReceived;
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
    public Message readMessage(SerialPort serialPort, int readTimeout) throws InvalidStateException, IOException {
        try {
            byte[] okByte;
            // should I listen for events intead?
            try {
                okByte = serialPort.readBytes(1, readTimeout);
            } catch (SerialPortTimeoutException e) {
                log.trace("Timed out while reading first byte");
                return null;
            }
            if(okByte[0] == 0x15) {
                log.info("Got NOK byte");
                return new NotOkReply();
            } else if (okByte[0] != HELLO) {
                log.error("Did not get hello. Possibly out of sync. data=" + ByteUtils.bytesToHex(okByte));
                throw new InvalidStateException();
            }

            byte[] cmdByte = serialPort.readBytes(1, readTimeout);
            ModemCommand modemCommand = ModemCommand.getInboundCommand(cmdByte[0]);
            if (modemCommand == null) {
                throw new InvalidStateException();
            }

            byte[] body = null;
            if (modemCommand.length != null) {
                body = serialPort.readBytes(modemCommand.length, readTimeout);
            }
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
                    return new StandardMessageReceived(body);
                case ALL_LINK_RECORD_RESPONSE:
                    return new AllLinkResponse(body);
                case GET_FIRST_ALL_LINK_RECORD_REPLY:
                case GET_NEXT_ALL_LINK_RECORD_REPLY:
                    return new SimpleReply(modemCommand, body);

                case EXTENDED_MESSAGE_RECEIVED:
                    //0251 2d0f462a00a01103000000000002374600000000000000
                    if(body[7] == InsteonCommand.PRODUCT_DATA_REQUEST.getCode() && body[8] == 0x00) {
                        return new ProductDataResponse(body);
                    } else {
                        return new ExtendedMessageReceived(body);
                    }

                    //return new BinaryMessage(modemCommand, body);
                default:
                    log.info("Handling generic modem command " + modemCommand);
                    return new BinaryMessage(modemCommand, body);
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
