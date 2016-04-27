package com.haavar.insteon.messages;

import com.haavar.insteon.ByteUtils;
import com.haavar.insteon.DeviceId;
import com.haavar.insteon.ModemCommand;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;

/**
 * @author Haavar Valeur
 */
@Getter
@ToString
public class AllLinkResponse implements Message {
    private final boolean inUse;
    private final boolean imIsMaster;
    private final boolean hasBeenUsedBefore;
    private final byte allLinkGroup;
    private final DeviceId deviceId;
    private final byte[] data; //varies by device ALL-Linked to

    public AllLinkResponse(byte[] body) {
        byte recordFlag = body[0];
        inUse = ByteUtils.isBitSet(recordFlag, 7);
        imIsMaster = ByteUtils.isBitSet(recordFlag, 6);
        hasBeenUsedBefore = ByteUtils.isBitSet(recordFlag, 1);
        allLinkGroup = body[1];
        deviceId = new DeviceId(body[2], body[3], body[4]);
        this.data = new byte[]{body[5], body[6], body[7]};
    }

    @Override
    public ModemCommand getModemCommand() {
        return ModemCommand.ALL_LINK_RECORD_RESPONSE;
    }
}
