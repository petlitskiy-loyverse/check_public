package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.sales.BaseSalesItem;

@SuppressWarnings("unused")
public class MerchantsReportResponse extends BaseResponsePOJO {
    public int pages;
    public Report[] top5;
    public Report[] report;
    public long totalNetSales;
    public FillSelectData fillSelectData;

    public static class Report extends BaseSalesItem {
        int merchantId;
        String merchantName;
        String merchantStatus;
        String merchantType;
        double grossSales;
        double refunds;
        double returns;
        long earning;
        long receiptsCount;
        long averageReceiptAmount;
        long newClientsCount;
        long amountDiscountSum;
        long amountRefundSum;
        long amountTips;
        long netSales;

        @Override
        public String getImageLink() {
            return null;
        }

        @Override
        public String getColor() {
            return null;
        }

        @Override
        public String getColorName() {
            return null;
        }

        @Override
        public String getName() {
            return merchantName;
        }

        @Override
        public double getQuantity() {
            return 0;
        }

        @Override
        public double getAmount() {
            return netSales / Utils.FRACTIONAL_DIVIDER;
        }

        @Override
        public double getReturns() {
            return returns;
        }
    }
}
