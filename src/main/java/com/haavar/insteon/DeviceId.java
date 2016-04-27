package com.haavar.insteon;

/**
 * @author Haavar Valeur
 */
public class DeviceId {
    private byte[] id;

    public DeviceId(byte high, byte middle, byte low) {
        this(new byte[]{high, middle, low});
    }

    public DeviceId(byte[] id) {
        assert id.length == 3: "Device id is not 3 bytes";
        this.id = id;
    }

    public DeviceId(String id) {
        this(ByteUtils.hexToBytes(id));
    }

    public byte[] getBytes() {
        return id;
    }

    public String toString() {
        return String.format("%02x.%02x.%02x", id[0], id[1], id[2]);
    }

}
