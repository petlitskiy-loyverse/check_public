package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.api.CategoriesReportResponse;
import com.loyverse.dashboard.core.api.EarningsReportResponse;
import com.loyverse.dashboard.core.api.MerchantsReportResponse;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import java.util.List;

public interface ReportsView extends BasePeriodView, ScrolledView {
    void updateProductsReport(List<WaresPeriodReportResponse.Wares> newItems);

    void updateEmployeesReport(List<MerchantsReportResponse.Report> newItems);

    void updateCategoriesReport(List<CategoriesReportResponse.Categories> newItems);

    void clearLists();

    void updateEarningsView(EarningsReportResponse.EarningsRow[] earningsRows, String divider);

    void updateOperationChart(DataModel.DashboardChartData chartData);

    void updateTotalSalesChart(DataModel.DashboardChartData chartData);

    void updateAverageTicketChart(DataModel.DashboardChartData chartData);
}

