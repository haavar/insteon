package com.haavar.insteon;

import com.haavar.insteon.messages.AllLinkResponse;
import com.haavar.insteon.messages.BinaryMessage;
import com.haavar.insteon.messages.ExtendedMessageReceived;
import com.haavar.insteon.messages.Reply;
import com.haavar.insteon.messages.SimpleReply;
import com.haavar.insteon.messages.StandardMessage;
import com.haavar.insteon.messages.StandardMessageReceived;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author Haavar Valeur
 */
@Slf4j
public class JavaSimpleSerialConnectionIT {



    @Test
    @Ignore
    public void readLinks() throws InterruptedException, TimeoutException {
        List<AllLinkResponse> allLinkResponses = new ArrayList<>();

        MessageListener listener = (message) -> {
            if (AllLinkResponse.class.isAssignableFrom(message.getClass())) {
                AllLinkResponse response = (AllLinkResponse)message;
                log.info("Got link response=" + response);
                allLinkResponses.add(response);
            } else {
                log.info("Got message cmd=" + message.getModemCommand());
            }

        };
        JavaSimpleSerialConnection powerLinc = new JavaSimpleSerialConnection("/dev/tty.usbserial-A6028NA9", listener);

        BinaryMessage message = new BinaryMessage(ModemCommand.GET_FIRST_ALL_LINK_RECORD, null);
        SimpleReply replyMessage = (SimpleReply)powerLinc.sendMessageBlocking(message, 3);
        while (replyMessage.isOk()) {
           Thread.sleep(100);
            BinaryMessage getNextMsg = new BinaryMessage(ModemCommand.GET_NEXT_ALL_LINK_RECORD, null);
            replyMessage = (SimpleReply)powerLinc.sendMessageBlocking(getNextMsg, 3);
            log.info("reply message ok=" + replyMessage.isOk());
        }

        while(true){
            Thread.sleep(1000);
        }


    }
}
