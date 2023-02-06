package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.sales.BaseSalesItem;

import java.util.List;

@SuppressWarnings("unused")
public class WaresPeriodReportResponse extends BaseResponsePOJO {
    public Wares[] top5;
    public Wares[] wares;
    public Total total;
    public FillSelectData fillSelectData;

    public static class Wares extends BaseSalesItem {
        int id;
        String name;
        double quantity;
        long price;
        long profit;
        long created;
        String category;
        long dateFrom;
        long dateTo;
        String type;
        double returns;
        long returnsSum;
        long totalDiscount;
        long totalTax;
        String article;
        long earningSum;
        long primeCostAmount;
        long grossProfit;
        long margin;
        String imageUrl;
        String color;
        String colorName;
        List<Variant> variations;

        @Override
        public String getImageLink() {
            return imageUrl;
        }

        @Override
        public String getColor() {
            return color;
        }

        @Override
        public String getColorName() {
            return colorName;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public double getQuantity() {
            return quantity - returns;
        }

        @Override
        public double getReturns() {
            return returns;
        }

        @Override
        public double getAmount() {
            return earningSum / Utils.FRACTIONAL_DIVIDER;
        }

        public List<Variant> getVariations() {
            return variations;
        }

        public void setVariations(List<Variant> variations) {
            this.variations = variations;
        }

        public void setEarningSum(long earningSum) {
            this.earningSum = earningSum;
        }

        public int getId() {
            return id;
        }
    }

    public static class Total {
        public double quantity;
        public double netSales;

        public double getQuantity() {
            return quantity;
        }

        public double getNetSales() {
            return netSales;
        }
    }

    //FIXME: it cannot extend BaseSalesItem
    public static class Variant {

        long id;
        String name;
        double quantity;
        long criticalCount;
        boolean keepCount;
        List<String> options;
        double price;
        double returns;
        long earningSum;

        public double getEarningSum() {
            return earningSum / Utils.FRACTIONAL_DIVIDER;
        }

        public void setEarningSum(long earningSum) {
            this.earningSum = earningSum;
        }

        public double getPrice() {
            return price / Utils.FRACTIONAL_DIVIDER;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getQuantity() {
            return quantity - returns;
        }

        public long getCriticalCount() {
            return criticalCount;
        }

        public boolean isKeepCount() {
            return keepCount;
        }

        public List<String> getOptions() {
            return options;
        }
    }


}

