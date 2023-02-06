package com.loyverse.dashboard.core.api;

@SuppressWarnings("unused")
public class EarningsReportResponse extends BaseResponsePOJO {
    public String divider;
    public EarningsRow[] earningsRows;
    public TotalValues totalValues;
    public FillSelectData fillSelectData;
    public HideFields hideFields;
    private String earningsReportResult;
    private int pages;

    public static class HideFields {
        public boolean taxes;
        public boolean tips;
        public boolean cost;
        public boolean surcharge;
    }
    public static class EarningsRow {
        public long from;
        public long to;
        public long totalSum;
        public long cashSum;
        public long cardSum;
        public long bonusSum;
        public long discountSum;
        public long returnSum;
        public long earningsSum;
    }

    public static class TotalValues {
        public long totalPeriodSum;
        public long totalPeriodCashSum;
        public long totalPeriodCardSum;
        public long totalPeriodReturnSum;
        public long totalPeriodBonusSum;
        public long totalPeriodDiscountSum;
        public long totalPeriodEarningsSum;
        public long totalPeriodEarningsSumBefore;
        public long totalPeriodSumBefore;
        public long totalPeriodBonusSumBefore;
        public long totalAverageReceipt;
        public long totalAverageReceiptBefore;
        public long totalReceiptsCount;
        public long totalReceiptsCountBefore;
        public long totalPeriodCostOfGoodsSum;
        public long totalPeriodGrossProfit;
        public long totalPeriodMargin;
        public long totalPeriodAllTaxesSum;
        public long totalPeriodTipsSum;
        public long totalTendered;
        public InfoPercent infoPercent;

        public static class InfoPercent {
            public String diff;
            public String percent;
            public long increase;
        }
    }
}
