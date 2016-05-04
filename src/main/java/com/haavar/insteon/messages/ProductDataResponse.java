package com.haavar.insteon.messages;

import com.haavar.insteon.ModemCommand;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Haavar Valeur
 */
@Getter
@ToString(callSuper = true)
public class ProductDataResponse extends ExtendedMessageReceived {
    private byte deviceCat;
    private byte deviceSubCat;
    private byte firmware;


    public ProductDataResponse(byte body[]) {
        super(body);
        deviceCat = body[13];
        deviceSubCat = body[14];
        firmware = body[15];
    }


}
