package com.haavar.insteon.messages;

import com.haavar.insteon.DeviceId;
import com.haavar.insteon.InsteonCommand;
import com.haavar.insteon.ModemCommand;
import lombok.Getter;

/**
 * @author Haavar Valeur
 */
@Getter
public class StandardMessageReceived implements Reply {
    public static final int MESSAGE_LENGTH = 9;
    private DeviceId from;
    private DeviceId to;
    private InsteonCommand cmd1;
    private byte cmd2;
    private byte flags = 0x0f;

    public StandardMessageReceived(byte[] msg) {
        assert msg.length == MESSAGE_LENGTH: "Message length is not " + MESSAGE_LENGTH;
        from = new DeviceId(msg[0], msg[1], msg[2]);
        to = new DeviceId(msg[3], msg[4], msg[5]);
        flags = msg[6];
        cmd1 = InsteonCommand.getCommand(msg[7]);
        cmd2 = msg[8];

    }

    @Override
    public ModemCommand getModemCommand() {
        return ModemCommand.SEND_STANDARD_OR_EXTENDED_MESSAGE_REPLY;
    }

    public String toString() {
        return "cmd= " + ModemCommand.SEND_STANDARD_OR_EXTENDED_MESSAGE_REPLY + "from=" + from +  " to=" +
                to + " cmd1=" + cmd1 + String.format(" cmd2=%02x flags=%02x", cmd2, flags);
    }

}
