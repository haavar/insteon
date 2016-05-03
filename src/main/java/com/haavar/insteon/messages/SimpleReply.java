package com.haavar.insteon.messages;

import com.haavar.insteon.ModemCommand;
import lombok.Getter;

/**
 * A generic class representing a reply from the PLM.
 * A reply is a confirmation (or not) that the message was received.
 * @author Haavar Valeur
 */
@Getter
public class SimpleReply implements Reply {
    private final boolean ok;
    private final ModemCommand modemCommand;

    public SimpleReply(ModemCommand command, byte[] body) {
        assert body.length == 1: "Body is is not 1 byte";
        ok = body[0] == 0x06;
        this.modemCommand = command;
    }
}
