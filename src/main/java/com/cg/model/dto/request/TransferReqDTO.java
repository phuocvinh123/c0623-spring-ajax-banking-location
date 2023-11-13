package com.cg.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TransferReqDTO {
    private String senderId;
    private String recipientId;
    private String transferAmount;
}
