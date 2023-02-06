package com.loyverse.dashboard.base.products;

import androidx.annotation.IntDef;

import com.loyverse.dashboard.base.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class ProductItem {
    public static final int UNCOUNTED_STOCK = 3;
    public static final int OUT_OF_STOCK = 2;
    public static final int LOW_STOCK = 1;
    public static final int NORMAL_STOCK = 0;

    long id;
    String name;
    String imgUrl;
    private String color;
    private String colorName;
    Long criticalCount;
    long wareCount;
    boolean keepCount;
    List<Variant> variations;

    public List<Variant> getVariations() {
        return variations;
    }

    public void setVariations(List<Variant> variations) {
        this.variations = variations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorName() {
        return colorName;
    }

    public long getCriticalCount() {
        return criticalCount;
    }

    public void setCriticalCount(long criticalCount) {
        this.criticalCount = criticalCount;
    }

    public long getWareCount() {
        return wareCount;
    }

    public void setWareCount(long wareCount) {
        this.wareCount = wareCount;
    }

    public double getStock() {
        return wareCount / Utils.QUANTITY_DIVIDER;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public
    @StockType
    int getStockType() {
        if (!keepCount)
            return UNCOUNTED_STOCK;
        if (criticalCount == null)
            return NORMAL_STOCK;
        if (wareCount <= 0) {
            return OUT_OF_STOCK;
        } else if (wareCount <= criticalCount) {
            return LOW_STOCK;
        }

        return NORMAL_STOCK;
    }

    @IntDef({OUT_OF_STOCK, LOW_STOCK, NORMAL_STOCK, UNCOUNTED_STOCK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StockType {
    }

    public class Variant {
        long id;
        String name;
        long wareCount;
        Long criticalCount;
        boolean keepCount;
        List<String> options;
        long price;

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getWareCount() {
            return wareCount / Utils.QUANTITY_DIVIDER;
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

        public long getPrice() {
            return price;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setWareCount(int wareCount) {
            this.wareCount = wareCount;
        }

        public void setCriticalCount(Long criticalCount) {
            this.criticalCount = criticalCount;
        }

        public void setKeepCount(boolean keepCount) {
            this.keepCount = keepCount;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public void setPrice(long price) {
            this.price = price;
        }


        @StockTypeVariant
        int getStockType() {
            if (!keepCount)
                return UNCOUNTED_STOCK;
            if (criticalCount == null)
                return NORMAL_STOCK;
            if (wareCount <= 0) {
                return OUT_OF_STOCK;
            } else if (wareCount <= criticalCount) {
                return LOW_STOCK;
            }

            return NORMAL_STOCK;
        }
    }

    @IntDef({OUT_OF_STOCK, LOW_STOCK, NORMAL_STOCK, UNCOUNTED_STOCK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StockTypeVariant {
    }
}
