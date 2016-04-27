package com.haavar.insteon;

/**
 * Source: http://cache.insteon.com/pdf/INSTEON_Command_Tables_20070925a.pdf
 * @author Haavar Valeur
 */
public enum InsteonCommand {
    NO_OP(0x00),
    ASSIGN_TO_GROUP(0x01),
    DELETE_FROM_GROUP(0x02),
    PING(0x10),

    LIGHT_ON(0x11),
    LIGHT_ON_FAST(0x12),
    LIGHT_OFF(0x13),
    LIGHT_OFF_FAST(0x14),

    BRIGHT(0x15),
    DIM(0x16),
    START_MANUAL(0x17),
    STOP_MANUAL(0x18),
    STATUS_REQUEST(0x19),
    DO_READ_EE(0x24),
    SET_ADDRESS_MSB(0x28),
    POKE(0x29),
    POKE_EXTENDED(0x2A),
    PEEK(0x2B),
    PEEK_INTERNAL(0x2C),
    POKE_INTERNAL(0x2D),
    SEND_RF_TEST_SIGNAL(0xFC),
    REQUEST_RF_TEST_REPORT(0xFD),
    RESET_RF_TEST_REPORT(0xFE),
    RF_AIR_ONLY_TEST(0xFF);


    private final byte code;

    InsteonCommand(int code) {
        this.code = (byte)code;
    }

    public static InsteonCommand getCommand(byte cmd1) {
        for (InsteonCommand cmd : InsteonCommand.values()) {
            if (cmd.code == cmd1) {
                return cmd;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}