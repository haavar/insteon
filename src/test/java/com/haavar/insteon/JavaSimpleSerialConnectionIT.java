package com.haavar.insteon;

import com.haavar.insteon.messages.AllLinkResponse;
import com.haavar.insteon.messages.BinaryMessage;
import com.haavar.insteon.messages.Reply;
import com.haavar.insteon.messages.SimpleReply;
import com.haavar.insteon.messages.StandardMessage;
import com.haavar.insteon.messages.StandardMessageReceived;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import javax.sound.midi.SysexMessage;
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
            log.info("Got message modemCommand=" + message.getModemCommand() + " "  + message);
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

    @Test
    public void getProductInfo() throws TimeoutException, InterruptedException {
        MessageListener listener = (message) -> {
            log.info("Message " + message);

        };
        JavaSimpleSerialConnection powerLinc = new JavaSimpleSerialConnection("/dev/tty.usbserial-A6028NA9", listener);
        DeviceId readingLamp = new DeviceId("2F57D5");
        DeviceId couchRight = new DeviceId("2D0F46");

        StandardMessage message = new StandardMessage(couchRight, InsteonCommand.PRODUCT_DATA_REQUEST, (byte) 0x00);
        Reply replyMessage = powerLinc.sendMessageBlocking(message, 3);
        System.out.print(replyMessage);
        while(true){
            Thread.sleep(1000);
        }
    }

    @Test
    @Ignore
    public void getStatus() throws InterruptedException, TimeoutException {
        MessageListener listener = (message) -> {
           log.info("Message " + message);

        };
        JavaSimpleSerialConnection powerLinc = new JavaSimpleSerialConnection("/dev/tty.usbserial-A6028NA9", listener);
        DeviceId readingLamp = new DeviceId("2F57D5");
        DeviceId couchRight = new DeviceId("2D0F46");
        //2D32BC
        StandardMessage message = new StandardMessage(couchRight, InsteonCommand.STATUS_REQUEST, (byte) 0x00);
        Reply replyMessage = powerLinc.sendMessageBlocking(message, 3);
        if (StandardMessageReceived.class.isAssignableFrom(replyMessage.getClass())) {
            System.out.println("Level is " + (((StandardMessageReceived)replyMessage).getCmd2() & 0xff));

        }
     //   Thread.sleep(200);
        message = new StandardMessage(readingLamp, InsteonCommand.STATUS_REQUEST, (byte) 0x00);
        replyMessage = powerLinc.sendMessageBlocking(message, 3);
        if (StandardMessageReceived.class.isAssignableFrom(replyMessage.getClass())) {
            System.out.println("Level is " + (((StandardMessageReceived)replyMessage).getCmd2() & 0xff));

        }


        while(true){
            Thread.sleep(1000);
        }
       // byte status = (byte)0xff;
       // System.out.println("status = " + (status & 0xff));


    }


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
