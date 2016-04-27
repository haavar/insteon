package com.haavar.insteon.messages;

/**
 * @author Haavar Valeur
 */
public interface OutboundMessage extends Message {
    byte[] toBytes();
}
