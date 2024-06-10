package com.altinsoy.batch.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BillingData {
    private int dataYear;
    private int dataMonth;
    private int accountId;
    private String phoneNumber;
    private float dataUsage;
    private int callDuration;
    private int smsCount;
}
