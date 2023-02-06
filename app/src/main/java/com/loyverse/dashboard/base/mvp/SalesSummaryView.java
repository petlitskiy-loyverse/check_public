package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.api.EarningsReportResponse;

public interface SalesSummaryView extends BasePeriodView {
    void updateData(
        EarningsReportResponse.TotalValues totalValues,
        EarningsReportResponse.HideFields hideFields
    );
}
