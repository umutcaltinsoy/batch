package com.altinsoy.batch.config;


import com.altinsoy.batch.dto.BillingData;
import com.altinsoy.batch.dto.ReportingData;
import com.altinsoy.batch.service.PricingService;
import org.springframework.batch.item.ItemProcessor;

public class BillingDataProcessor implements ItemProcessor<BillingData, ReportingData> {

    private final PricingService pricingService;

    public BillingDataProcessor(PricingService pricingService) {
        this.pricingService = pricingService;
    }
    private float spendingThreshold=150.0f;

    @Override
    public ReportingData process(BillingData item) {
        double billingTotal =
                item.getDataUsage() * pricingService.getDataPricing() +
                        item.getCallDuration() * pricingService.getCallPricing() +
                        item.getSmsCount() * pricingService.getSmsPricing();
        if (billingTotal < spendingThreshold) {
            return null;
        }
        return new ReportingData(item, billingTotal);
    }
}
