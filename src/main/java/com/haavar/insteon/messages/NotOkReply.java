package com.haavar.insteon.messages;

import com.haavar.insteon.ModemCommand;

/**
 * @author Haavar Valeur
 */
public class NotOkReply implements Reply {
    @Override
    public ModemCommand getModemCommand() {
        return ModemCommand.NOT_ACK;
    }
}
