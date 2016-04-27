package com.haavar.insteon;

import com.haavar.insteon.messages.AllLinkResponse;
import com.haavar.insteon.messages.BinaryMessage;
import com.haavar.insteon.messages.ReplyMessage;
import com.haavar.insteon.messages.StandardMessage;
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
    public void turnOnLight() throws InterruptedException {
        MessageListener listener = (message) -> {
            log.info("Got message " + message);
        };

        JavaSimpleSerialConnection powerLinc = new JavaSimpleSerialConnection("/dev/tty.usbserial-A6028NA9", listener);
        DeviceId readingLamp = new DeviceId("2F57D5");

        StandardMessage message = new StandardMessage(readingLamp, InsteonCommand.LIGHT_ON_FAST, (byte) 0xff);
        powerLinc.sendMessage(message);
        Thread.sleep(1000);
        message = new StandardMessage(readingLamp, InsteonCommand.LIGHT_OFF_FAST, (byte) 0x00);
        powerLinc.sendMessage(message);
        while(true){
            Thread.sleep(1000);
        }

    }

/*
Get First Database Entry
[0x69, Get First ALL-Link Record33]: Returns the very first record in the PLM’s ALLLink
Database in an 0x57 ALL-Link Record Response message.
[TX] - 02 69
[RX] - 02 69 06 02 57 E2 01 11 11 11 01 00 22
Get Next Database Entry
[0x6A, Get Next ALL-Link Record34]: Returns all the other records in the PLM’s ALLLink
Database incrementally in a series of 0x57 ALL-Link Record Response
messages. When there are no more records, you will receive a NAK (0x15).
[TX] - 02 6A
[RX] - 02 6A 06 02 57 A2 01 04 F7 EE 01 00 22
[TX] - 02 6A
[RX] - 02 6A 15
 */
    @Test
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
        ReplyMessage replyMessage = powerLinc.sendMessageBlocking(message, 3);
        while (replyMessage.isOk()) {
           Thread.sleep(100);
            BinaryMessage getNextMsg = new BinaryMessage(ModemCommand.GET_NEXT_ALL_LINK_RECORD, null);
            replyMessage = powerLinc.sendMessageBlocking(getNextMsg, 3);
            log.info("reply message ok=" + replyMessage.isOk());
        }

        while(true){
            Thread.sleep(1000);
        }


    }
}
