package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.sales.BaseSalesItem;

@SuppressWarnings("unused")
public class CategoriesReportResponse extends BaseResponsePOJO {
    public Categories[] top5;
    public Categories[] categories;
    public long totalNetSales;
    public FillSelectData fillSelectData;

    public static class Categories extends BaseSalesItem {
        String categoryName;
        double itemsSold;//1000
        double grossSales;
        long itemsRefunded;
        long refunds;
        double returns;
        long discounts;
        long netSales;//100
        long costOfGoods;
        long grossProfit;
        long margin;
        long taxes;
        String categoryColor;
        String categoryColorName;

        @Override
        public String getImageLink() {
            return null;
        }

        @Override
        public String getColor() {
            return categoryColor;
        }

        @Override
        public String getColorName() {
            return categoryColorName;
        }

        @Override
        public String getName() {
            return categoryName;
        }

        @Override
        public double getQuantity() {
            return (itemsSold - itemsRefunded) / Utils.QUANTITY_DIVIDER;
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
