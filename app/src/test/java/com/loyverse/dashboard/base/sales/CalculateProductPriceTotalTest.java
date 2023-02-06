package com.loyverse.dashboard.base.sales;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by ruslanko on 13.03.18.
 */
public class CalculateProductPriceTotalTest {
    @Test
    public void getWaresEarningSum_ItemWithoutVariations_ShouldReturnRightEarningSum() {
        long itemEarningSum = 23000;
        WaresPeriodReportResponse.Wares item = new WaresPeriodReportResponse.Wares();
        item.setEarningSum(itemEarningSum);
        item.setVariations(new ArrayList());
        double expectedEarningSum = 230D;

        double actualEarningSum = Utils.CalculateProductPriceTotal.getWaresEarningSum(item);
        assertThat(actualEarningSum, equalTo(expectedEarningSum));
    }

    @Test
    public void getWaresEarningSum_ItemWithVariations_ShouldReturnRightEarningSum() {
        //TODO: make builder for it
        long itemEarningSum = 23000;
        WaresPeriodReportResponse.Wares item = new WaresPeriodReportResponse.Wares();
        item.setEarningSum(itemEarningSum);

        WaresPeriodReportResponse.Variant variant1 = new WaresPeriodReportResponse.Variant();
        variant1.setEarningSum(100);
        WaresPeriodReportResponse.Variant variant2 = new WaresPeriodReportResponse.Variant();
        variant2.setEarningSum(300);

        ArrayList<WaresPeriodReportResponse.Variant> variants = new ArrayList<>();
        variants.add(variant1);
        variants.add(variant2);
        item.setVariations(variants);
        double expectedEarningSum = 4D;

        double actualEarningSum = Utils.CalculateProductPriceTotal.getWaresEarningSum(item);
        assertThat(actualEarningSum, equalTo(expectedEarningSum));
    }


}