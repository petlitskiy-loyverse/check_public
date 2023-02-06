package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.base.PeriodUtils;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import rx.Subscriber;

public abstract class BasePeriodPresenter<T extends BasePeriodView> extends OutletPreloadPresenter<T> {

    public static final int DEBOUNCE_TIME = 250;

    protected BasePeriodPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public void prevPeriod() {
        dataModel.prevPeriod();
    }

    public void nextPeriod() {
        if (PeriodUtils.canIncreasePeriod(dataModel.getToDate())) {
            dataModel.nextPeriod();
        }
    }

    public void handlePeriodUnitChange() {
        //TODO: figure out why view is null here
        runIfNotNull(view, (view) -> view.showSelectionWindow(dataModel.getPeriod()));
    }

    public void nonCustomPeriodUnitSelected(int period) {
        if (period == Utils.CUSTOM_PERIOD) {
            view.showCustomCalendarDialog(
                    dataModel.getFromDate(),
                    dataModel.getToDate(),
                    PeriodUtils.formatDatePeriod(
                            dataModel.getFromDate(),
                            dataModel.getToDate(),
                            dataModel.getPeriod()),
                    dataModel.getStartTime(), dataModel.getEndTime()
            );
        } else dataModel.setPeriod(period);
    }

    public void customPeriodUnitSelected(List<Date> dates, Integer startTime, Integer endTime) {
        dataModel.setPeriod(Utils.CUSTOM_PERIOD);
        dataModel.setStartTime(startTime);
        dataModel.setEndTime(endTime);
        dataModel.setCustomPeriodInDays(dates.get(0).getTime(), dates.size());
    }

    protected void updatePeriodText() {
        //TODO: figure out why view is null here
        runIfNotNull(view, (view) -> {
            view.updatePeriodText(
                    PeriodUtils.formatDatePeriod(
                            dataModel.getFromDate(),
                            dataModel.getToDate(),
                            dataModel.getPeriod()),
                    PeriodUtils.formatYear(dataModel.getFromDate(),
                            dataModel.getToDate())
            );
            view.updatePeriodTime(PeriodUtils.formatTimePeriod(
                    dataModel.getStartTime(),
                    dataModel.getEndTime()
            ));
        });
    }
}
