package com.loyverse.dashboard.base.sales;


import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseFragment;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.BasePeriodPresenter;
import com.loyverse.dashboard.base.mvp.BasePeriodView;
import com.loyverse.dashboard.base.mvp.MainView;
import com.loyverse.dashboard.core.Navigator;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public abstract class BaseToolbarFragment<T extends BasePeriodPresenter> extends BaseFragment implements BasePeriodView {
    private static final String LOG_CONTEXT = "BaseToolbarFragment";

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.ic_back)
    protected ImageView backIcon;
    @BindView(R.id.ic_outlets)
    protected ImageView outletsIcon;
    @BindView(R.id.period_text)
    protected TextSwitcher periodText;
    @BindView(R.id.period_time)
    protected TextView periodTime;
    @BindView(R.id.title_text)
    protected TextView titleText;
    @BindView(R.id.outlet_title)
    protected TextView outletTitle;
    @Inject
    protected T presenter;
    @Inject
    protected Navigator navigator;
    @BindView(R.id.toolbar_layout)
    protected AppBarLayout appBarLayout;

    protected abstract void setUpLists();

    protected void setUpToolbar() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (getActivity() != null) {
                if (Math.abs(verticalOffset) >= toolbar.getHeight())
                    ((MainView) getActivity()).hideBottomBar(true);
                else if (verticalOffset == 0)
                    ((MainView) getActivity()).showBottomBar(true);
            }
        });
        outletsIcon.setOnClickListener(view -> {
            Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Outlets button click"));
            navigator.showOutletsFragment(getActivity(), true);
        });
    }

    @OnClick(R.id.ic_forward)
    protected void onForwardClick() {
        Timber.i("select_date_range date_range next_date_range");
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Forward period click"));
        periodText.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_to_left));
        periodText.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_right));
        presenter.nextPeriod();
    }

    @OnClick(R.id.ic_backward)
    protected void onBackwardClick() {
        Timber.i("select_date_range date_range previous_date_range");
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Backward period click"));
        periodText.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_to_right));
        periodText.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_left));
        presenter.prevPeriod();
    }

    @Override
    public void onPeriodLengthChanged(int period) {
        presenter.nonCustomPeriodUnitSelected(period);
    }

    @Override
    public void showCustomCalendarDialog(long from, long to, String date, Integer startTime, Integer endTime) {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Show custom period selection window"));
        navigator.showCalendarDialog(getActivity(), date, from, to, startTime, endTime);
    }

    public void onCustomPeriodSelected(List<Date> dates, Integer startTime, Integer endTime) {
        presenter.customPeriodUnitSelected(dates, startTime, endTime);
    }

    @Override
    public void showSelectionWindow(int currentPeriod) {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Show period selection window"));
        navigator.showSelectPeriodDialog(currentPeriod, getActivity());
    }

    @Override
    public void updatePeriodText(String text, String year) {
        if (!((TextView) periodText.getCurrentView()).getText().toString().equals(text))
            periodText.setText(text);
    }

    @Override
    public void updatePeriodTime(String text) {
        if (!periodTime.getText().toString().equals(text))
            periodTime.setText(text);
        periodTime.setVisibility(text != null ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.period_text, R.id.period_time})
    void onPeriodTextClick() {
        presenter.handlePeriodUnitChange();
    }

    @Override
    public void setOutletName(String outlet) {
        if (outlet.equals("")) {
            outletTitle.setVisibility(View.GONE);
        } else {
            outletTitle.setVisibility(View.VISIBLE);
            outletTitle.setText(outlet);
        }
    }

    @Override
    public void showOutletTitleAndOutletsIcon() {
        outletsIcon.setVisibility(View.VISIBLE);
        outletTitle.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideOutletTitleAndOutletsIcon() {
        outletsIcon.setVisibility(View.INVISIBLE);
        outletTitle.setVisibility(View.GONE);
    }
}
