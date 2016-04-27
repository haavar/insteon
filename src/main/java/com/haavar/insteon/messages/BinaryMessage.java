package com.haavar.insteon.messages;

import com.haavar.insteon.ByteUtils;
import com.haavar.insteon.ModemCommand;
import com.haavar.insteon.ModemCommandParser;

/**
 * Catch all class for messages
 * @author Haavar Valeur
 */
public class BinaryMessage implements OutboundMessage {
    private ModemCommand modemCommand;
    private byte[] data;

    public BinaryMessage(ModemCommand modemCommand, byte[] data) {
        this.modemCommand = modemCommand;
        this.data = data;
    }

    @Override
    public ModemCommand getModemCommand() {
        return modemCommand;
    }

    public String toString() {
        return "modemCommand=" + modemCommand + " data=" + ByteUtils.bytesToHex(data);
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes;
        if (data != null && data.length > 0) {
            bytes = new byte[data.length + 2];
            System.arraycopy(data, 0, bytes, 2, data.length);
        } else {
            bytes = new byte[2];
        }
        bytes[0] = ModemCommandParser.HELLO;
        bytes[1] = modemCommand.cmd;
        return bytes;
    }
}
