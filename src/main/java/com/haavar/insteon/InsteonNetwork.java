package com.haavar.insteon;

import com.haavar.insteon.messages.OutboundMessage;
import com.haavar.insteon.messages.ProductDataResponse;
import com.haavar.insteon.messages.Reply;
import com.haavar.insteon.messages.StandardMessage;
import com.haavar.insteon.messages.StandardMessageReceived;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Haavar Valeur
 */
@Slf4j
public class InsteonNetwork {
    private JavaSimpleSerialConnection powerLinc;
    private final AtomicReference<ProductDataResponse> productDataResponseAtomicReference = new AtomicReference<>();
    private int defaultTimeoutInSeconds = 3;


    public InsteonNetwork(String serialPort) {
        powerLinc = new JavaSimpleSerialConnection(serialPort, (message) -> {
            log.info("Got insteon message " + message);
            if (ProductDataResponse.class.isAssignableFrom(message.getClass())) {
                synchronized (productDataResponseAtomicReference) {
                    productDataResponseAtomicReference.set((ProductDataResponse)message);
                    productDataResponseAtomicReference.notifyAll();
                }
            }
        });
    }

    public byte getLevel(DeviceId id) throws TimeoutException {
        StandardMessage message = new StandardMessage(id, InsteonCommand.STATUS_REQUEST, (byte) 0x00);
            log.info("Getting status for device id=" + id);
            Reply replyMessage = powerLinc.sendMessageBlocking(message, defaultTimeoutInSeconds);
            if (StandardMessageReceived.class.isAssignableFrom(replyMessage.getClass())) {
                log.info("status for device id=" + id + " status=" + ((StandardMessageReceived) replyMessage).getCmd2());
                return ((StandardMessageReceived) replyMessage).getCmd2();
            }  else {
                log.error("Unknown response for getting status " + replyMessage);
                throw new RuntimeException("Unknown response for getting status " + replyMessage);
            }
    }

    public void turnOn(DeviceId id, byte level) {
        StandardMessage message = new StandardMessage(id, InsteonCommand.LIGHT_ON, level);
        powerLinc.sendMessage(message);
    }

    public void turnOffFast(DeviceId id) {
        StandardMessage message = new StandardMessage(id, InsteonCommand.LIGHT_OFF_FAST, (byte) 0x00);
        powerLinc.sendMessage(message);
    }

    public ProductInfo getProductInfo(DeviceId id) throws TimeoutException{
        StandardMessage message = new StandardMessage(id, InsteonCommand.PRODUCT_DATA_REQUEST, (byte) 0x00);
        ProductDataResponse response = sendAndwWaitAndGetFromReference(message, productDataResponseAtomicReference, defaultTimeoutInSeconds);
        ProductInfo productInfo = new ProductInfo();
        productInfo.setCategory(response.getDeviceCat());
        productInfo.setSubCategory(response.getDeviceSubCat());
        productInfo.setFirmwareVersion(response.getFirmware());
        return productInfo;
    }

    private <R> R sendAndwWaitAndGetFromReference(OutboundMessage outboundMessage, final AtomicReference<R> reference, int timeoutInSeconds) throws TimeoutException {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (reference) {
            reference.set(null);
            powerLinc.sendMessage(outboundMessage);

            long deadLine = System.currentTimeMillis() + timeoutInSeconds * 1000;
            while (System.currentTimeMillis() < deadLine) {
                R response = reference.getAndSet(null);
                if (response != null) {
                    return response;
                }
                try {
                    reference.wait(deadLine - System.currentTimeMillis());
                } catch (InterruptedException ignore) {
                }
            }
            throw new TimeoutException();
        }
    }

    @Data
    @NoArgsConstructor
    public static class ProductInfo {
        byte category;
        byte subCategory;
        byte firmwareVersion;

        public ProductInfo(String info) {
            this(ByteUtils.hexToBytes(info));
        }

        public ProductInfo(byte[] info) {
            category = info[0];
            subCategory = info[1];
            firmwareVersion = info[2];
        }

        public String toString() {
            return String.format("%02x.%02x.%02x", category, subCategory, firmwareVersion);
        }

    }


}
