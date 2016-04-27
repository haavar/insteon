package com.haavar.insteon;

/**
 * This represents the 2nd byte of messages sent and received to the modem.
 * In some cases, the length of the frame is determined by the type of command, and other times
 * it's dependent on flags in the payload. If the frame length is fixed it will be set in the enum.
 * If toModem is set to true, this message can be sent to the PLM, if it's false the message can be sent by
 * the PLM.
 * @author Haavar Valeur
 */
public enum ModemCommand {
    NOT_ACK(0x15, 0, false), // not really a command
    STANDARD_MESSAGE_RECEIVED(0x50, 9, false),
    EXTENDED_MESSAGE_RECEIVED(0x51, 23, false),
    X10_RECEIVED(0x52, 2, false),
    ALL_LINKING_COMPLETED(0x53, 8, false),
    BUTTON_EVENT_REPORT(0x54, 1, false),
    USER_RESET_DETECTED(0x55, 0, false),
    ALL_LINK_CLEANUP_FAILURE_REPORT(0x56, 4, false),
    ALL_LINK_RECORD_RESPONSE(0x57, 8, false),
    ALL_LINK_CLEANUP_STATUS_REPORT(0x58, 1, false),
    GET_IM_INFO_REPLY(0x60, 7, false),
    GET_IM_INFO(0x60, 0, true),
    SEND_ALL_LINK_COMMAND(0x61, 3, true),
    SEND_ALL_LINK_COMMAND_REPLY(0x61, 4, false),
    SEND_STANDARD_OR_EXTENDED_MESSAGE(0x62, null, true), // 6 or 20??
    SEND_STANDARD_OR_EXTENDED_MESSAGE_REPLY(0x62, null, false), // 7 or 21
    SEND_X10_MESSAGE(0x63, 2, true),
    SEND_X10_MESSAGE_REPLY(0x63, 3, false),
    START_ALL_LINKING(0x64, 2, true),
    START_ALL_LINKING_REPLY(0x64, 3, false),
    CANCEL_ALL_LINKING(0x65, 0, true),
    CANCEL_ALL_LINKING_REPLY(0x65, 1, false),
    SET_HOST_DEVICE_CATEGORY(0x66, 3, true),
    SET_HOST_DEVICE_CATEGORY_REPLY(0x66, 4, false),
    RESET_IM(0x67, 0, true),
    RESET_IM_REPLY(0x67, 1, false),
    SET_ACK_MESSAGE_BYTE(0x68, 1, true),
    SET_ACK_MESSAGE_BYTE_REPLY(0x68, 2, false),
    GET_FIRST_ALL_LINK_RECORD(0x69, 0, true),
    GET_FIRST_ALL_LINK_RECORD_REPLY(0x69, 1, false),
    GET_NEXT_ALL_LINK_RECORD(0x6a, 0, true),
    GET_NEXT_ALL_LINK_RECORD_REPLY(0x6a, 1, false),
    MANAGE_ALL_LINK_RECORD(0x6f, 9, true),
    MANAGE_ALL_LINK_RECORD_REPLY(0x6f, 10, false),
    BEEP(0x77, 0, true),
    BEEP_REPLY(0x77, 1, false),
    UNKNOWN_MESSAGE_7F(0x7f, 2, false);
    // more here: http://cache.insteon.com/pdf/INSTEON_Modem_Developer's_Guide_20071012a.pdf

    byte cmd;
    Integer length;
    boolean toModem;

    ModemCommand(int command, Integer len, boolean toModem) {
        this.cmd = (byte)command;
        this.length = len;
        this.toModem = toModem;
    }

    public static ModemCommand getInboundCommand(byte cmd) {
        for (ModemCommand command : ModemCommand.values()) {
            if ( ! command.toModem && command.cmd == cmd) {
                return command;
            }
        }
        return null;
    }

}
