package com.loyverse.dashboard.mvp.views;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.PeriodUtils;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.chart.DashboardChart;
import com.loyverse.dashboard.base.chart.MultiLangLargeValueFormatter;
import com.loyverse.dashboard.base.chart.PeriodAxisValueFormatter;
import com.loyverse.dashboard.base.mvp.ReportsPresenter;
import com.loyverse.dashboard.base.mvp.ReportsView;
import com.loyverse.dashboard.base.sales.BaseSalesAdapter;
import com.loyverse.dashboard.base.sales.BaseSalesItem;
import com.loyverse.dashboard.base.sales.BaseToolbarFragment;
import com.loyverse.dashboard.base.sales.DividerItemDecoration;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.api.CategoriesReportResponse;
import com.loyverse.dashboard.core.api.EarningsReportResponse;
import com.loyverse.dashboard.core.api.MerchantsReportResponse;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class ReportsFragment extends BaseToolbarFragment<ReportsPresenter<ReportsView>> implements ReportsView {

    public static final String TAG = "ReportsFragment";

    private static final int DASHBOARD_CHART_DEFAULT_VALUE = 50;
    private static final int DASHBOARD_CHART_LONG_ANIMATION = 375;
    private static final int DASHBOARD_CHART_SHORT_ANIMATION = 150;

    private static final int EARNINGS_CHART_GRID_COLOR = R.color.earnings_chart_grid_color;
    private static final int EARNINGS_CHART_BORDER_COLOR = R.color.earnings_chart_border_color;
    private static final int EARNINGS_CHART_AXIS_COLOR = R.color.earnings_chart_axis_color;
    private static final int EARNINGS_CHART_AXIS_ZERO_COLOR = R.color.earnings_chart_axis_zero_color;
    private static final int EARNINGS_CHART_BAR_COLOR = R.color.earnings_chart_bar_color;
    private static final int EARNINGS_CHART_LABEL_COLOR = R.color.earnings_chart_label_color;
    private static final float EARNINGS_CHART_AXIS_WIDTH = 1F;
    private static final float EARNINGS_CHART_BORDER_WIDTH = 0.5F;
    private static final int EARNINGS_CHART_SELECTED_BAR_OPACITY = 31;

    @BindView(R.id.product_card)
    ViewGroup productCard;

    @BindView(R.id.top_products_list)
    RecyclerView topProductsRecyclerView;
    private BaseSalesAdapter topProductsAdapter;

    @BindView(R.id.product_header)
    ViewGroup productHeader;


    @BindView(R.id.categories_card)
    ViewGroup categoriesCard;

    @BindView(R.id.top_categories_list)
    RecyclerView topCategoriesRecyclerView;
    private BaseSalesAdapter topCategoriesAdapter;

    @BindView(R.id.categories_header)
    ViewGroup categoriesHeader;

    @BindView(R.id.employees_card)
    ViewGroup employeesCard;

    @BindView(R.id.top_employees_list)
    RecyclerView topEmployeesRecyclerView;
    private BaseSalesAdapter topEmployeesAdapter;

    @BindView(R.id.employees_header)
    ViewGroup employeesHeader;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.earnings_chart)
    BarChart earningsChart;

    @BindView(R.id.earnings_metrics)
    View metrics;

    @BindView(R.id.main_total_sales_chart)
    DashboardChart totalSalesChart;

    @BindView(R.id.main_total_sales_chart_percentage_value)
    TextView totalSalesPercentageValue;

    @BindView(R.id.main_operations_chart)
    DashboardChart operationsChart;

    @BindView(R.id.main_operations_chart_percentage_value)
    TextView operationsPercentageValue;

    @BindView(R.id.main_avg_ticket_chart)
    DashboardChart avgTicketChart;

    @BindView(R.id.main_avg_ticket_chart_percentage_value)
    TextView avgTicketPercentageValue;

    @BindView(R.id.reports_nested_scroll)
    NestedScrollView nestedScrollView;

    private int y = 0;
    private volatile boolean initialized = false;

    private String[] largeValueSuffixes;

    boolean isRightToLeft = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((BaseApplication) requireActivity().getApplication()).getActivityComponent().inject(this);
        super.onCreate(savedInstanceState);

        largeValueSuffixes = Utils.getLargeValueFormatterSuffix(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialized = false;
        isRightToLeft = getResources().getBoolean(R.bool.is_right_to_left);
        setUpLists();
        setUpToolbar();
        setUpEarningsView();
        productCard.setOnClickListener((v) -> navigateToSaleFragment(Utils.WARES_KEY));
        categoriesCard.setOnClickListener((v) -> navigateToSaleFragment(Utils.CATEGORIES_KEY));
        employeesCard.setOnClickListener((v) -> navigateToSaleFragment(Utils.EMPLOYEES_KEY));
        presenter.bind(this);
        //hack to prevent bottomBar reacting to old scroll position when returning from another tab
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                initialized = true;
            }
        }, 1000);
    }

    private void navigateToSaleFragment(String key) {
        Timber.v(Utils.formatBreadCrumb(TAG, "Sales card click", key));
        navigator.showSalesFragment(getActivity(), key);
    }

    @Override
    public void onDestroyView() {
        presenter.unbind(this);
        super.onDestroyView();
    }

    private void setUpEarningsView() {
        metrics.setOnClickListener(view -> onSalesSummaryTitleClick());

        Context context = requireContext();
        final int gridColor = ContextCompat.getColor(context, EARNINGS_CHART_GRID_COLOR);
        final int borderColor = ContextCompat.getColor(context, EARNINGS_CHART_BORDER_COLOR);
        final int axisColor = ContextCompat.getColor(context, EARNINGS_CHART_AXIS_COLOR);
        final int zeroColor = ContextCompat.getColor(context, EARNINGS_CHART_AXIS_ZERO_COLOR);
        final int labelColor = ContextCompat.getColor(context, EARNINGS_CHART_LABEL_COLOR);

        earningsChart.setDrawBarShadow(false);
        earningsChart.setBorderColor(borderColor);
        earningsChart.setBorderWidth(EARNINGS_CHART_BORDER_WIDTH);
        earningsChart.setDrawBorders(true);
        earningsChart.setFitBars(true);
        earningsChart.getDescription().setEnabled(false);
        earningsChart.setMaxVisibleValueCount(24);
        earningsChart.setPinchZoom(false);
        earningsChart.setDrawGridBackground(false);
        if (isRightToLeft) {
            earningsChart.getAxisRight().setEnabled(true);
            earningsChart.getAxisLeft().setEnabled(false);
        } else {
            earningsChart.getAxisRight().setEnabled(false);
            earningsChart.getAxisLeft().setEnabled(true);
        }
        earningsChart.setDoubleTapToZoomEnabled(false);
        earningsChart.setScaleYEnabled(false);
        earningsChart.setHighlightFullBarEnabled(false);
        earningsChart.setDrawValueAboveBar(false);
        earningsChart.getLegend().setEnabled(false);

        IAxisValueFormatter xAxisFormatter = new PeriodAxisValueFormatter();
        XAxis xAxis = earningsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(axisColor);
        xAxis.setAxisLineWidth(EARNINGS_CHART_AXIS_WIDTH);
        xAxis.setGridColor(gridColor);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1.0f);
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MultiLangLargeValueFormatter(largeValueSuffixes);
        YAxis leftAxis = isRightToLeft ? earningsChart.getAxisRight() : earningsChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisLineColor(axisColor);
        leftAxis.setZeroLineColor(zeroColor);
        leftAxis.setAxisLineWidth(EARNINGS_CHART_AXIS_WIDTH);
        leftAxis.setGridColor(gridColor);

        XYMarkerView mv = new XYMarkerView(getContext(), xAxisFormatter);
        mv.setChartView(earningsChart);
        earningsChart.getXAxis().setTextColor(labelColor);
        if (isRightToLeft)
            earningsChart.getAxisRight().setTextColor(labelColor);
        else
            earningsChart.getAxisLeft().setTextColor(labelColor);
        earningsChart.setMarker(mv);
    }

    private void setGraphData(EarningsReportResponse.EarningsRow[] earningsRows, String divider) {
        ((PeriodAxisValueFormatter) earningsChart.getXAxis().getValueFormatter()).setDivider(divider, earningsRows);
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < earningsRows.length; i++) {
            values.add(new BarEntry(i, (float) Utils.formatCoinValue(earningsRows[i].earningsSum)));
        }
        BarDataSet set1;

        if (earningsChart.getData() != null && earningsChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) earningsChart.getData().getDataSetByIndex(0);
            set1.setLabel(PeriodUtils.getLegendByDivider(requireActivity().getApplicationContext(), divider));
            set1.setValues(values);
            if (isRightToLeft)
                normalizeYAxis(set1, earningsChart.getAxisRight());
            else
                normalizeYAxis(set1, earningsChart.getAxisLeft());
            earningsChart.getData().notifyDataChanged();
            earningsChart.notifyDataSetChanged();
            earningsChart.animateXY(300, 500);
        } else {
            set1 = new BarDataSet(values, PeriodUtils.getLegendByDivider(requireActivity().getApplicationContext(), divider));
            set1.setColor(ContextCompat.getColor(requireContext(), EARNINGS_CHART_BAR_COLOR));
            set1.setHighLightAlpha(EARNINGS_CHART_SELECTED_BAR_OPACITY);
            if (isRightToLeft)
                normalizeYAxis(set1, earningsChart.getAxisRight());
            else
                normalizeYAxis(set1, earningsChart.getAxisLeft());

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setDrawValues(false);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            earningsChart.setData(data);
            earningsChart.getData().notifyDataChanged();
            earningsChart.notifyDataSetChanged();
            earningsChart.invalidate();
            earningsChart.animateXY(300, 500);
        }
    }

    private void normalizeYAxis(BarDataSet set, YAxis axis) {
        if (set.getYMax() == 0 && set.getYMin() == 0)
            axis.setAxisMaximum(axis.getLabelCount());
        else
            axis.resetAxisMaximum();

        if (set.getYMin() >= 0) {
            axis.setAxisMinimum(0);
            axis.setDrawZeroLine(true);
        } else {
            axis.resetAxisMinimum();
            axis.setDrawZeroLine(false);
        }
    }

    @Override
    public void updateProductsReport(List<WaresPeriodReportResponse.Wares> newItems) {
        swipeRefreshLayout.setRefreshing(false);
        if (newItems.size() == 0) {
            productCard.setVisibility(View.GONE);
        } else productCard.setVisibility(View.VISIBLE);
        topProductsAdapter.updateData((List<BaseSalesItem>) (List<?>) newItems);
        restoreScrollPosition();
    }

    @Override
    public void updateCategoriesReport(List<CategoriesReportResponse.Categories> newItems) {
        swipeRefreshLayout.setRefreshing(false);
        if (newItems.size() == 0) {
            categoriesCard.setVisibility(View.GONE);
        } else categoriesCard.setVisibility(View.VISIBLE);
        topCategoriesAdapter.updateData((List<BaseSalesItem>) (List<?>) newItems);
        restoreScrollPosition();
    }

    @Override
    public void updateEmployeesReport(List<MerchantsReportResponse.Report> newItems) {
        swipeRefreshLayout.setRefreshing(false);
        if (newItems.size() == 0) {
            employeesCard.setVisibility(View.GONE);
        } else employeesCard.setVisibility(View.VISIBLE);
        topEmployeesAdapter.updateData((List<BaseSalesItem>) (List<?>) newItems);
        restoreScrollPosition();
    }

    // TODO: 12.12.16 Add-hoc solution for overriding jumping of scroll view, when elements become visible after gone state
    private void restoreScrollPosition() {
        nestedScrollView.scrollTo(0, y);
    }

    @Override
    public void clearLists() {
        earningsChart.getXAxis().setDrawLabels(false);
        setGraphData(new EarningsReportResponse.EarningsRow[0], PeriodUtils.DIVIDER_HOUR);
        topProductsAdapter.clearData();
        topCategoriesAdapter.clearData();
        topEmployeesAdapter.clearData();
    }

    @Override
    public void updateEarningsView(EarningsReportResponse.EarningsRow[] earningsRows, String divider) {
        earningsChart.getXAxis().setDrawLabels(true);
        setGraphData(earningsRows, divider);
    }

    private float getDashboardChartValue(double baseValue) {
        if (baseValue > 100) {
            return 100;
        } else if (baseValue < -100) {
            return 0;
        }
        return (float) (baseValue * 0.5F + DASHBOARD_CHART_DEFAULT_VALUE);
    }

    @Override
    public void updateOperationChart(DataModel.DashboardChartData chartData) {
        operationsChart.setIndicatorText(String.valueOf(chartData.getTotalValue()));
        setUpDashboardChartValueAnimation(operationsChart, getDashboardChartValue(chartData.getPercentageDifference()));
        operationsPercentageValue.setText(Utils.wrapPercentageValue(chartData.getPercentageDifference()));
    }

    private void setUpDashboardChartValueAnimation(DashboardChart chart, float value) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(chart, "value", chart.getValue(), value);
        animation.setDuration(getDashboardChartAnimationDuration(chart, value));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            animation.setInterpolator(new DecelerateInterpolator());
        }
        animation.start();
    }

    private long getDashboardChartAnimationDuration(DashboardChart chart, float value) {
        int magicDelta = 10;
        if (Math.abs(chart.getValue() - value) > DASHBOARD_CHART_DEFAULT_VALUE - magicDelta)
            return DASHBOARD_CHART_LONG_ANIMATION;
        else
            return DASHBOARD_CHART_SHORT_ANIMATION;
    }

    @Override
    public void updateTotalSalesChart(DataModel.DashboardChartData chartData) {
        totalSalesChart.setIndicatorText(Utils.formatLargeNumberForDashboardChart(
                largeValueSuffixes,
                Utils.formatCoinValue(chartData.getTotalValue())));
        setUpDashboardChartValueAnimation(totalSalesChart, getDashboardChartValue(chartData.getPercentageDifference()));
        totalSalesPercentageValue.setText(Utils.wrapPercentageValue(chartData.getPercentageDifference()));
    }

    @Override
    public void updateAverageTicketChart(DataModel.DashboardChartData chartData) {
        avgTicketChart.setIndicatorText(Utils.formatLargeNumberForDashboardChart(
                largeValueSuffixes,
                Utils.formatCoinValue(chartData.getTotalValue())));
        setUpDashboardChartValueAnimation(avgTicketChart, getDashboardChartValue(chartData.getPercentageDifference()));
        avgTicketPercentageValue.setText(Utils.wrapPercentageValue(chartData.getPercentageDifference()));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setUpLists() {
        Context context = requireContext();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.loadDataForSelectedPeriod();
            y = nestedScrollView.getScrollY();
        });
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!initialized)
                return;
            if (scrollY - oldScrollY > 0) {
                ((MainActivity) requireActivity()).hideBottomBar(true);
            } else {
                ((MainActivity) requireActivity()).showBottomBar(true);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        DividerItemDecoration dividerDecoration = new DividerItemDecoration(context);
        topProductsRecyclerView.addItemDecoration(dividerDecoration);
        topCategoriesRecyclerView.addItemDecoration(dividerDecoration);
        topEmployeesRecyclerView.addItemDecoration(dividerDecoration);

        topProductsRecyclerView.setOnTouchListener(new RecyclerViewClickDelegate(productCard));
        topCategoriesRecyclerView.setOnTouchListener(new RecyclerViewClickDelegate(categoriesCard));
        topEmployeesRecyclerView.setOnTouchListener(new RecyclerViewClickDelegate(employeesCard));

        topProductsRecyclerView.setNestedScrollingEnabled(false);
        topCategoriesRecyclerView.setNestedScrollingEnabled(false);
        topEmployeesRecyclerView.setNestedScrollingEnabled(false);

        topProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topEmployeesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        topProductsAdapter = new BaseSalesAdapter();
        ((TextView) productHeader.findViewById(R.id.header_text)).setText(R.string.top_5_products);
        topCategoriesAdapter = new BaseSalesAdapter();
        ((TextView) categoriesHeader.findViewById(R.id.header_text)).setText(R.string.top_5_categories);
        topEmployeesAdapter = new BaseSalesAdapter();
        ((TextView) employeesHeader.findViewById(R.id.header_text)).setText(R.string.top_5_employees);

        topProductsRecyclerView.setAdapter(topProductsAdapter);
        topCategoriesRecyclerView.setAdapter(topCategoriesAdapter);
        topEmployeesRecyclerView.setAdapter(topEmployeesAdapter);
    }

    @Override
    protected void setUpToolbar() {
        super.setUpToolbar();
        titleText.setText(getResources().getString(R.string.tab_reports));
        periodText.setFactory(() -> {
            TextView switcherTextView = new TextView(requireActivity().getApplicationContext());
            switcherTextView.setTextSize((getResources().getDimension(R.dimen.period_text_size) / getResources().getDisplayMetrics().density));
            switcherTextView.setTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.white));
            switcherTextView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            switcherTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            return switcherTextView;
        });
        backIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showToastOnException(Throwable throwable) {
        super.showToastOnException(throwable);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onForwardClick() {
        y = nestedScrollView.getScrollY();
        super.onForwardClick();
    }

    @Override
    protected void onBackwardClick() {
        y = nestedScrollView.getScrollY();
        super.onBackwardClick();
    }

    @Override
    public void showLoadingDialog() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingDialog() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @OnClick(R.id.sales_summary_card_title)
    public void onSalesSummaryTitleClick() {
        Timber.v(Utils.formatBreadCrumb(TAG, "Sales summary card click"));
        navigator.showSalesSummaryFragment(getActivity());
    }

    @Override
    public void scrollToTheTop() {
        if (nestedScrollView != null) {
            nestedScrollView.smoothScrollTo(0, 0);
        }
    }

    private class RecyclerViewClickDelegate implements View.OnTouchListener {
        private final View delegateView;

        public RecyclerViewClickDelegate(View delegateView) {
            this.delegateView = delegateView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            delegateView.onTouchEvent(event);
            return true;
        }
    }
}