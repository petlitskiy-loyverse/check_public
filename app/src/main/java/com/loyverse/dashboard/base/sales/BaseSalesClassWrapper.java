package com.loyverse.dashboard.base.sales;

import androidx.annotation.Nullable;

import com.loyverse.dashboard.core.DataModel.SortBy;
import com.loyverse.dashboard.core.DataModel.SortType;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import java.util.Objects;

public abstract class BaseSalesClassWrapper {
    public static class Item<T extends BaseSalesItem> extends BaseSalesClassWrapper {
        private final T item;

        public Item(T item) {
            this.item = item;
        }

        public T get() {
            return item;
        }
    }

    public static class Variant extends BaseSalesClassWrapper {
        private WaresPeriodReportResponse.Variant variant;

        public Variant(WaresPeriodReportResponse.Variant variant) {
            this.variant = variant;
        }

        public WaresPeriodReportResponse.Variant get() {
            return variant;
        }
    }

    public static class Total extends BaseSalesClassWrapper {
        private double total;

        public Total(double total) {
            this.total = total;
        }

        public double getTotal() {
            return total;
        }
    }

    public static class Sorting extends BaseSalesClassWrapper {
        private SortType sortType;
        private SortBy sortBy;
        private String sortName;

        public Sorting(SortBy sortBy, SortType sortType, String sortName){
            this.sortType = sortType;
            this.sortBy = sortBy;
            this.sortName = sortName;
        }

        public SortType getSortType() {
            return sortType;
        }

        public SortBy getSortBy() {
            return sortBy;
        }

        public String getNameSort() {
            return sortName;
        }

        public Sorting changeSortType(SortType sortType) {
            return new Sorting(sortBy, sortType, sortName);
        }

        public Sorting changeSortBy(SortBy sortBy) {
            return new Sorting(sortBy, sortType, sortName);
        }

        public Sorting changeSortByAndType(SortBy sortBy, SortType sortType) {
            return new Sorting(sortBy, sortType, sortName);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if(obj == this) return true;
            if (obj == null) return false;
            if(obj instanceof Sorting){
                Sorting castedObj = (Sorting) obj;
                return sortType.equals(castedObj.sortType) && sortBy.equals(castedObj.sortBy) && sortName.equals(castedObj.sortName);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sortType, sortBy, sortName);
        }
    }
}