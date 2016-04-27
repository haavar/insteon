package com.haavar.insteon;

import com.haavar.insteon.messages.Message;

/**
 * @author Haavar Valeur
 */
public interface MessageListener {
    void onMessage(Message message);
}
