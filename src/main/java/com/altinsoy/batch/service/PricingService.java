package com.altinsoy.batch.service;

import com.altinsoy.batch.exception.PricingException;

import java.util.Random;

public class PricingService {
    private float dataPricing=0.01f;

    private float callPricing=0.5f;

    private float smsPricing=0.1f;

    private final Random random = new Random();

    public float getDataPricing() {
        if (this.random.nextInt(1000) % 7 == 0) {
            throw new PricingException("Error while retrieving data pricing");
        }
        return this.dataPricing;
    }

    public float getCallPricing() {
        return this.callPricing;
    }

    public float getSmsPricing() {
        return this.smsPricing;
    }
}
