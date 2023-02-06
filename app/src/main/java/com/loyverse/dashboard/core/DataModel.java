package com.loyverse.dashboard.core;

import android.content.Context;

import com.loyverse.dashboard.base.PeriodUtils;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.multishop.Permission;
import com.loyverse.dashboard.base.products.ProductItem;
import com.loyverse.dashboard.base.sales.BaseSalesItem;
import com.loyverse.dashboard.core.api.CategoriesReportResponse;
import com.loyverse.dashboard.core.api.EarningsReportResponse;
import com.loyverse.dashboard.core.api.MerchantsReportResponse;
import com.loyverse.dashboard.core.api.Outlet;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;

import timber.log.Timber;

public class DataModel {
    private Integer startTime;
    private Integer endTime;
    public static final String SHARED_PREFERENCES = "data";
    public static final String SEND_PUSHES = "push";
    private static final String OWNER_ID = "owner_id";
    private static final String COOKIE_HASH = "cookie_hash";
    private static final String MERCHANT_ID = "merchant_id";
    private static final String EMAIL = "email";
    private static final String SEND_PUSHES_PERMISSION = "push_permission";
    private static final int DEFAULT_PERIOD = Utils.DAY_PERIOD;
    public static final SortType DEFAULT_SORT_BY_NET_TYPE = SortType.DESC;
    public static final SortType DEFAULT_SORT_BY_NAME_TYPE = SortType.ASC;
    public static final SortBy DEFAULT_SALES_SORT_BY = SortBy.NET;
    private static final String DEFAULT_PRODUCTS_FILTER_TYPE = Utils.ALL_PRODUCT_TYPE;
    private static final boolean DEFAULT_IS_TIPS_SHOW = false;
    private static final boolean DEFAULT_IS_TAXES_SHOW = false;

    private Context context;

    private Integer ownerId;
    private String cookieHash;
    private Integer merchantId;
    private String email;
    private Boolean sendStockNotifications;
    private Boolean sendStockNotificationsPermission;
    private List<String> permissions = new ArrayList<>();

    private CategoriesReportResponse.Categories[] topCategories = new CategoriesReportResponse.Categories[0];
    private MerchantsReportResponse.Report[] topMerchants = new MerchantsReportResponse.Report[0];
    private WaresPeriodReportResponse.Wares[] topWares = new WaresPeriodReportResponse.Wares[0];
    private EarningsReportResponse.EarningsRow[] earningsData = new EarningsReportResponse.EarningsRow[0];


    private List<BaseSalesItem> categoriesList = new ArrayList<>();
    private List<BaseSalesItem> merchantsList = new ArrayList<>();
    private List<BaseSalesItem> waresList = new ArrayList<>();


    private List<Outlet> selectedOutlets = new ArrayList<>();
    // TODO: 12.01.17 Remove it, no needing of caching it, it is needed just for products list and it is wiped each time
    private List<Outlet> outletList = new ArrayList<>();
    private List<Outlet> periodOutletList = new ArrayList<>();

    private List<ProductItem> productList = new ArrayList<>();

    private EarningsReportResponse.TotalValues earningTotalValues;
    private EarningsReportResponse.HideFields hideFields;
    private boolean isTaxesShow = DEFAULT_IS_TAXES_SHOW;
    private boolean isTipsShow = DEFAULT_IS_TIPS_SHOW;

    private DashboardChartData operationsChartData;
    private DashboardChartData totalSalesChartData;
    private DashboardChartData avgTicketChartData;
    private long fromDate;
    private long toDate;
    private int selectedPeriod;
    private SortBy salesSortBy;
    private SortType salesSortType;
    @Utils.ProductsFilterType
    private String productsFilterType;
    private int customPeriodInDays;
    private double waresTotal;
    private double variantsTotal;
    private double merchantsTotal;
    private double categoriesTotal;

    @Inject
    public DataModel(Context context) {
        this.context = context;
        ownerId = Utils.readFromSharedPreferences(context, OWNER_ID, Integer.class);
        cookieHash = Utils.readFromSharedPreferences(context, COOKIE_HASH, String.class);
        merchantId = Utils.readFromSharedPreferences(context, MERCHANT_ID, Integer.class);
        //For supporting previous versions
        if (merchantId == 0)
            merchantId = null;
        email = Utils.readFromSharedPreferences(context, EMAIL, String.class);
        sendStockNotifications = Utils.readFromSharedPreferences(context, SEND_PUSHES, Boolean.class);
        sendStockNotificationsPermission = Utils.readFromSharedPreferences(context, SEND_PUSHES_PERMISSION, Boolean.class);

        Timber.d("constructor ownerId = %d, cookieHash = %s", ownerId, cookieHash);
        selectedPeriod = DEFAULT_PERIOD;
        salesSortBy = DEFAULT_SALES_SORT_BY;
        salesSortType = DEFAULT_SORT_BY_NET_TYPE;
        productsFilterType = DEFAULT_PRODUCTS_FILTER_TYPE;
        recalculateDates();
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void updatePermissions(List<String> permissions) {
        this.permissions = permissions;
        if (permissions.contains(Permission.ACCESS_WARES)) {
            if (!sendStockNotificationsPermission) {
                sendStockNotificationsPermission = true;
                sendStockNotifications = true;
            }
        } else {
            sendStockNotificationsPermission = false;
            sendStockNotifications = false;
        }
    }

    public boolean isUserLoggedIn() {
        return ownerId != 0;
    }

    public void deleteLoginData() {
        ownerId = 0;
        cookieHash = "";
        merchantId = 0;
        email = "";
        selectedPeriod = DEFAULT_PERIOD;
        salesSortBy = DEFAULT_SALES_SORT_BY;
        salesSortType = DEFAULT_SORT_BY_NET_TYPE;
        sendStockNotifications = true;
        Utils.removeFromSharedPreferences(context, OWNER_ID);
        Utils.removeFromSharedPreferences(context, COOKIE_HASH);
        Utils.removeFromSharedPreferences(context, MERCHANT_ID);
        Utils.removeFromSharedPreferences(context, EMAIL);
        Utils.removeFromSharedPreferences(context, SEND_PUSHES);
        Utils.removeFromSharedPreferences(context, SEND_PUSHES_PERMISSION);

        permissions = new ArrayList<>();

        recalculateDates();
        clearSavedData();
        clearProductList();
        clearOutletsList();
    }

    public void saveLoginData(int ownerId, String cookieHash, int merchantId) {
        this.ownerId = ownerId;
        this.cookieHash = cookieHash;
        this.merchantId = merchantId;
        Utils.saveToSharedPreferences(context, ownerId, OWNER_ID);
        Utils.saveToSharedPreferences(context, cookieHash, COOKIE_HASH);
        Utils.saveToSharedPreferences(context, merchantId, MERCHANT_ID);
        Utils.saveToSharedPreferences(context, email, EMAIL);
        Utils.saveToSharedPreferences(context, sendStockNotifications, SEND_PUSHES);
        Utils.saveToSharedPreferences(context, sendStockNotificationsPermission, SEND_PUSHES_PERMISSION);
    }

    public String getCookieHash() {
        return cookieHash;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public CategoriesReportResponse.Categories[] getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(CategoriesReportResponse.Categories[] topCategories) {
        this.topCategories = topCategories;
    }

    public MerchantsReportResponse.Report[] getTopMerchants() {
        return topMerchants;
    }

    public void setTopMerchants(MerchantsReportResponse.Report[] topMerchants) {
        this.topMerchants = topMerchants;
    }

    public WaresPeriodReportResponse.Wares[] getTopWares() {
        return topWares;
    }

    public void setTopWares(WaresPeriodReportResponse.Wares[] topWares) {
        this.topWares = topWares;
    }

    public void nextPeriod() {
        if (PeriodUtils.canIncreasePeriod(toDate)) {
            fromDate = toDate + 1;
            toDate = PeriodUtils.getEndDate(fromDate, selectedPeriod, customPeriodInDays);
            clearSavedData();
            Timber.d("next period: %s - %s", new Date(fromDate).toString(), new Date(toDate).toString());
        }
    }

    public void clearPeriodLists() {
        categoriesList = new ArrayList<>();
        merchantsList = new ArrayList<>();
        waresList = new ArrayList<>();

        topCategories = new CategoriesReportResponse.Categories[0];
        topMerchants = new MerchantsReportResponse.Report[0];
        topWares = new WaresPeriodReportResponse.Wares[0];
        earningsData = new EarningsReportResponse.EarningsRow[0];
    }

    public void clearSavedData() {
        avgTicketChartData = null;
        operationsChartData = null;
        totalSalesChartData = null;
        earningTotalValues = null;

        clearPeriodLists();
    }

    public void prevPeriod() {
        toDate = fromDate - 1;
        fromDate = PeriodUtils.getStartDate(toDate, selectedPeriod, customPeriodInDays);
        clearSavedData();
        Timber.d("prev period: %s - %s", new Date(fromDate).toString(), new Date(toDate).toString());
    }

    private void recalculateDates() {
        toDate = PeriodUtils.getEndDateFromToday();
        fromDate = PeriodUtils.getStartDateFromToday(selectedPeriod);
        clearSavedData();
        Timber.d("new period: %s - %s", new Date(fromDate).toString(), new Date(toDate).toString());
    }

    public int getPeriod() {
        return selectedPeriod;
    }

    public void setPeriod(int newPeriod) {
        selectedPeriod = newPeriod;
        recalculateDates();
    }

    public long getFromDate() {
        return fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setCustomPeriodInDays(long fromDate, int customPeriodInDays) {
        this.fromDate = fromDate;
        this.customPeriodInDays = customPeriodInDays;
        recalculateDatesCustom();
    }

    public int getCustomPeriodInDays() {
        return customPeriodInDays;
    }

    private void recalculateDatesCustom() {
        toDate = PeriodUtils.getEndDate(fromDate, selectedPeriod, customPeriodInDays);
        clearPeriodLists();
        Timber.d("new period: %s - %s", new Date(fromDate).toString(), new Date(toDate).toString());
    }

    public SortBy getSalesSortBy() {
        return salesSortBy;
    }

    public void setSalesSortBy(SortBy sortBy) {
        this.salesSortBy = sortBy;
    }

    public SortType getSalesSortType() {
        return salesSortType;
    }

    public void setSalesSortType(SortType sortType) {
        this.salesSortType = sortType;
    }

    public EarningsReportResponse.EarningsRow[] getEarningsData() {
        return earningsData;
    }

    public void setEarningsData(EarningsReportResponse.EarningsRow[] earningsData) {
        this.earningsData = earningsData;
    }

    public double getWaresTotal() {
        return waresTotal;
    }

    public void setWaresTotal(double waresTotal) {
        this.waresTotal = waresTotal;
    }

    public double getVariantTotal(){
        return variantsTotal;
    }

    public void setVariantTotal(double variantsTotal) {
        this.variantsTotal = variantsTotal;
    }

    public double getMerchantsTotal() {
        return merchantsTotal;
    }

    public void setMerchantsTotal(double merchantsTotal) {
        this.merchantsTotal = merchantsTotal;
    }

    public double getCategoriesTotal() {
        return categoriesTotal;
    }

    public void setCategoriesTotal(double categoriesTotal) {
        this.categoriesTotal = categoriesTotal;
    }

    public List<BaseSalesItem> getWaresList() {
        return waresList;
    }

    public void addToWaresList(List<WaresPeriodReportResponse.Wares> waresList) {
        this.waresList.addAll(waresList);
    }

    public List<BaseSalesItem> getMerchantsList() {
        return merchantsList;
    }

    public void addToMerchantsList(List<MerchantsReportResponse.Report> merchantsList) {
        this.merchantsList.addAll(merchantsList);
    }

    public List<BaseSalesItem> getCategoriesList() {
        return categoriesList;
    }

    public void addToCategoriesList(List<CategoriesReportResponse.Categories> categoriesList) {
        this.categoriesList.addAll(categoriesList);
    }

    public DashboardChartData getOperationsChartData() {
        return operationsChartData;
    }

    public void setOperationsChartData(DashboardChartData operationsChartData) {
        this.operationsChartData = operationsChartData;
    }

    public DashboardChartData getTotalSalesChartData() {
        return totalSalesChartData;
    }

    public void setTotalSalesChartData(DashboardChartData totalSalesChartData) {
        this.totalSalesChartData = totalSalesChartData;
    }

    public DashboardChartData getAvgTicketChartData() {
        return avgTicketChartData;
    }

    public void setAvgTicketChartData(DashboardChartData avgTicketChartData) {
        this.avgTicketChartData = avgTicketChartData;
    }

    public EarningsReportResponse.TotalValues getEarningTotalValues() {
        return earningTotalValues;
    }

    public void setEarningTotalValues(EarningsReportResponse.TotalValues earningTotalValues) {
        this.earningTotalValues = earningTotalValues;
    }

    public void clearWaresList() {
        waresList = new ArrayList<>();
    }

    public void clearCategoriesList() {
        categoriesList = new ArrayList<>();
    }

    public void clearMerchantsList() {
        merchantsList = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSendStockNotifications() {
        return sendStockNotifications;
    }

    public void setSendStockNotifications(boolean value) {
        sendStockNotifications = value;
        Utils.saveToSharedPreferences(context, sendStockNotifications, SEND_PUSHES);

    }

    @Utils.ProductsFilterType
    public String getProductsFilterType() {
        return productsFilterType;
    }

    public void setProductsFilterType(@Utils.ProductsFilterType String productsFilterType) {
        this.productsFilterType = productsFilterType;
    }

    public List<ProductItem> getProductList() {
        return productList;
    }

    public void addToProductList(List<ProductItem> productList) {
        this.productList.addAll(productList);
    }

    public void clearProductList() {
        this.productList = new ArrayList<>();
    }

    public void clearOutletsList() {
        this.outletList = new ArrayList<>();
        this.selectedOutlets = new ArrayList<>();
        this.periodOutletList = new ArrayList<>();
    }

    public boolean isTipsShow() {
        return isTipsShow;
    }

    public void setTipsShow(boolean tipsShow) {
        isTipsShow = tipsShow;
    }

    public boolean isTaxesShow() {
        return isTaxesShow;
    }

    public void setTaxesShow(boolean taxesShow) {
        isTaxesShow = taxesShow;
    }

    public EarningsReportResponse.HideFields getHideFields() {
        return hideFields;
    }

    public void setHideFields(EarningsReportResponse.HideFields hideFields) {
        this.hideFields = hideFields;
    }

    public long[] getSelectedOutletIds() {
        long[] ids = new long[selectedOutlets.size()];
        for (int i = 0; i < selectedOutlets.size(); i++) {
            ids[i] = selectedOutlets.get(i).id;
        }
        return ids;
    }

    public String getOutletName() {
        if (selectedOutlets.size() == 0)
            return "";

        return selectedOutlets.get(0).name;
    }

    public List<Outlet> getSelectedOutlets() {
        return selectedOutlets;
    }

    public void setSelectedOutlets(List<Outlet> selectedOutlets) {
        if (selectedOutlets.size() != 0) {
            clearSavedData();
            clearProductList();
        }
        this.selectedOutlets = selectedOutlets;
    }

    public List<Outlet> getOutletList() {
        return outletList;
    }

    public void updateOutletList(List<Outlet> outletList) {
        this.outletList = outletList;
        if (outletList.size() == 0) {
            selectedOutlets.clear();
            return;
        }

        validateSelectedOutlets();
    }

    private void validateSelectedOutlets() {
        for (ListIterator<Outlet> it = selectedOutlets.listIterator(); it.hasNext(); ) {
            Outlet outlet = it.next();
            if (this.outletList.contains(outlet)) {
                it.set(this.outletList.get(this.outletList.indexOf(outlet)));
            } else if (periodOutletList.contains(outlet)) {
                it.set(periodOutletList.get(periodOutletList.indexOf(outlet)));
            } else {
                it.remove();
            }
        }
        if (selectedOutlets.size() == 0 && outletList.size() > 0) {
            selectedOutlets.add(outletList.get(0));
        }
    }

    public List<Outlet> getPeriodOutletList() {
        return periodOutletList;
    }

    public void updatePeriodOutletList(List<Outlet> periodOutletList) {
        this.periodOutletList = periodOutletList;
        if (periodOutletList.size() == 0) {
            //PeriodOutletList contains all elements of outletList
            outletList = new ArrayList<>();
            selectedOutlets.clear();
            return;
        }

        validateSelectedOutlets();
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public static class DashboardChartData {
        private long totalValue;
        private double percentageDifference;

        public long getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(long totalValue) {
            this.totalValue = totalValue;
        }

        public double getPercentageDifference() {
            return percentageDifference;
        }

        public void setPercentageDifference(double percentageDifference) {
            this.percentageDifference = percentageDifference;
        }
    }

    public enum SortType {
        DESC("desc"),
        ASC("asc");

        private String value;

        SortType(String value) {
            this.value = value;
        }

        public SortType invert(){
            if(this == SortType.ASC){
                return SortType.DESC;
            } else return SortType.ASC;
        }

        @NotNull
        @Override
        public String toString() {
            return value;
        }
    }

    public enum SortBy {
        NAME("name"),
        NET("netSales");

        private String value;

        SortBy(String value) {
            this.value = value;
        }

        public SortBy invert(){
            if(this == SortBy.NAME){
                return SortBy.NET;
            } else return SortBy.NAME;
        }

        @NotNull
        @Override
        public String toString() {
            return value;
        }
    }
}
