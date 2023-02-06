package com.loyverse.dashboard.core.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GetOwnerProfileResponse extends BaseResponsePOJO {

    @SerializedName("moneyFormat")
    MoneyFormat moneyFormat;

    @SerializedName("cashFractionDigits")
    Long cashFractionDigits;

    public Long getCashFractionDigits() {
        return cashFractionDigits;
    }

    public MoneyFormat getMoneyFormat() {
        return moneyFormat;
    }

    public static class MoneyFormat {
        @SerializedName("decSeparator")
        String decSeparator;
        @SerializedName("grSeparator")
        GrSeparator grSeparator;
        @SerializedName("currency")
        Currency currency;
        @SerializedName("minus")
        Minus minus;

        public String getDecSeparator() {
            return decSeparator;
        }

        public GrSeparator getGrSeparator() {
            return grSeparator;
        }

        public Currency getCurrency() {
            return currency;
        }

        public Minus getMinus() {
            return minus;
        }
    }

    public static class GrSeparator {
        @SerializedName("symbol")
        String symbol;
        @SerializedName("first")
        int first;
        @SerializedName("other")
        int other;

        public String getSymbol() {
            return symbol;
        }

        public int getFirst() {
            return first;
        }

        public int getOther() {
            return other;
        }
    }

    public static class Currency {
        @SerializedName("symbol")
        String symbol;
        @SerializedName("onTheLeft")
        boolean onTheLeft;
        @SerializedName("withSpace")
        boolean withSpace;
        @SerializedName("denominationValues")
        int[] denominationValues;

        public String getSymbol() {
            return symbol;
        }

        public boolean isOnTheLeft() {
            return onTheLeft;
        }

        public boolean isWithSpace() {
            return withSpace;
        }

        public int[] getDenominationValues() {
            return denominationValues;
        }
    }

    public static class Minus {
        @SerializedName("onTheLeft")
        boolean onTheLeft;
        @SerializedName("beforeCurrency")
        boolean beforeCurrency;

        public boolean isOnTheLeft() {
            return onTheLeft;
        }

        public boolean isBeforeCurrency() {
            return beforeCurrency;
        }
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
