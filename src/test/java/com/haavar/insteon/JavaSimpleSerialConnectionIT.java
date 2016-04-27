package com.haavar.insteon;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author Haavar Valeur
 */
@Slf4j
public class JavaSimpleSerialConnectionIT {

    @Test
    public void turnOnLight() throws InterruptedException {
        MessageListener listener = (message) -> {
            log.info("Got message " + message);
        };

        JavaSimpleSerialConnection powerLinc = new JavaSimpleSerialConnection("/dev/tty.usbserial-A6028NA9", listener);
        DeviceId readingLamp = new DeviceId("2F57D5");

        StandardMessage message = new StandardMessage(readingLamp, Command.LIGHT_ON_FAST, (byte) 0xff);
        powerLinc.sendMessage(message);
        Thread.sleep(1000);
        message = new StandardMessage(readingLamp, Command.LIGHT_OFF_FAST, (byte) 0x00);
        powerLinc.sendMessage(message);
        while(true){
            Thread.sleep(1000);
        }

    }
}
