package com.haavar.insteon;

import com.haavar.insteon.messages.StandardMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

/**
 * @author Haavar Valeur
 */
public class InsteonNetworkIT {
    private InsteonNetwork insteonNetwork;
    DeviceId readingLamp = new DeviceId("2F57D5");
    DeviceId couchRight = new DeviceId("2D0F46");


    @Before
    public void setUp() {
        insteonNetwork = new InsteonNetwork("/dev/tty.usbserial-A6028NA9");
    }


    @Test
    @Ignore
    public void turnOnAndOffLight() throws InterruptedException {
        for (int i = 0; i < 5; i ++){
            try {
                insteonNetwork.turnOn(readingLamp, (byte) 0xff);
                Thread.sleep(2000);
                insteonNetwork.turnOffFast(readingLamp);
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Ignore
    public void getProductInfo() throws TimeoutException {
        InsteonNetwork.ProductInfo productInfo = insteonNetwork.getProductInfo(readingLamp);
        System.out.print("productInfo = " + productInfo);

    }

    @Test
    //@Ignore
    public void getStatus() throws TimeoutException, InterruptedException {
        byte readingLampLevel = insteonNetwork.getLevel(readingLamp);
        System.out.print("readingLampLevel = " + readingLampLevel);
        Thread.sleep(200);
        byte couchRightLevel = insteonNetwork.getLevel(couchRight);
        System.out.print("couchRightLevel = " + couchRightLevel);

    }


}
