package com.haavar.insteon.messages;

import com.haavar.insteon.DeviceId;
import com.haavar.insteon.ModemCommand;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Haavar Valeur
 */
@Getter
@ToString
public class ExtendedMessageReceived implements Message {
    private DeviceId to;
    private DeviceId from;

    public ExtendedMessageReceived(byte[] body) {
        from = new DeviceId(body[0], body[1], body[2]);
        to = new DeviceId(body[3], body[4], body[5]);
    }

    @Override
    public ModemCommand getModemCommand() {
        return ModemCommand.EXTENDED_MESSAGE_RECEIVED;
    }
}
