package com.loyverse.dashboard.base.sales;

public abstract class BaseSalesItem {

    public BaseSalesItem() {
    }

    public abstract String getImageLink();

    public abstract String getColor();

    public abstract String getColorName();

    public abstract String getName();

    public abstract double getQuantity();

    public abstract double getAmount();

    public abstract double getReturns();
}
