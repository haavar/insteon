package com.haavar.insteon.messages;

import com.haavar.insteon.InsteonCommand;
import com.haavar.insteon.DeviceId;
import com.haavar.insteon.ModemCommand;
import com.haavar.insteon.ModemCommandParser;

/**
 * @author Haavar Valeur
 */
public class StandardMessage implements OutboundMessage {
    private DeviceId to;
    private InsteonCommand cmd1;
    private byte cmd2;
    private byte flags = 0x0f;

    @Override
    public ModemCommand getModemCommand() {
        return ModemCommand.SEND_STANDARD_OR_EXTENDED_MESSAGE;
    }

    public StandardMessage(DeviceId to, InsteonCommand cmd1, byte cmd2) {
        this.to = to;
        this.cmd1 = cmd1;
        this.cmd2 = cmd2;
    }

    public byte[] toBytes() {
        //todo: this is for sending only, excluding from
        byte[] dev = to.getBytes();
        return new byte[] {
                ModemCommandParser.HELLO, ModemCommand.SEND_STANDARD_OR_EXTENDED_MESSAGE.cmd,
                dev[0], dev[1], dev[2],
                flags,
                cmd1.getCode(), cmd2
        };
    }

    public String toString() {
        return "to=" + to + " cmd1=" + cmd1 + String.format(" cmd2=%02x flags=%02x", cmd2, flags);
    }

}
