package com.loyverse.dashboard.base.mvp;

import java.util.Date;
import java.util.List;

public interface BasePeriodView extends BaseView, OutletsPreloadView {
    void showSelectionWindow(int currentPeriod);

    void updatePeriodText(String periodText, String yearText);

    void updatePeriodTime(String periodTime);

    void onPeriodLengthChanged(int position);

    void onCustomPeriodSelected(List<Date> dates,Integer startTime,Integer endTime);

    void showCustomCalendarDialog(long from, long to, String date,Integer startTime,Integer endTime);
}
