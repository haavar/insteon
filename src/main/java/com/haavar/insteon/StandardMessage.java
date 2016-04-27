package com.haavar.insteon;

/**
 * @author Haavar Valeur
 */
//todo this might need to be broken into 2 classes. One for receiving and one for sending
public class StandardMessage {
    public static final int MESSAGE_LENGTH = 9;
    private DeviceId from;
    private DeviceId to;
    private Command cmd1;
    private byte cmd2;
    private byte flags = 0x0f;


    public StandardMessage(byte[] msg) {
        assert msg.length == MESSAGE_LENGTH: "Message length is not " + MESSAGE_LENGTH;
        from = new DeviceId(new byte[]{msg[0], msg[1], msg[2]});
        to = new DeviceId(new byte[]{msg[3], msg[4], msg[5]});
        flags = msg[6];
        cmd1 = Command.getCommand(msg[7]);
        cmd2 = msg[8];

    }

    public StandardMessage(DeviceId to, Command cmd1, byte cmd2) {
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
        return "from=" + from +  " to=" + to + " cmd1=" + cmd1 + String.format(" cmd2=%02x flags=%02x", cmd2, flags);
    }

}
