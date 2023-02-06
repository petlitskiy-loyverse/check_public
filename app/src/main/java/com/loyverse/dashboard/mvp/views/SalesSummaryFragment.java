package com.loyverse.dashboard.mvp.views;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.SalesSummaryPresenter;
import com.loyverse.dashboard.base.mvp.SalesSummaryView;
import com.loyverse.dashboard.base.sales.BaseToolbarFragment;
import com.loyverse.dashboard.base.sales.CardItemDecoration;
import com.loyverse.dashboard.base.sales.SalesSummaryAdapter;
import com.loyverse.dashboard.core.api.EarningsReportResponse;

import butterknife.BindView;
import timber.log.Timber;

public class SalesSummaryFragment extends BaseToolbarFragment<SalesSummaryPresenter<SalesSummaryView>> implements SalesSummaryView {

    public static final String TAG = "SalesSummaryFragment";

    @BindView(R.id.sales_summary_list)
    RecyclerView salesSummaryRecyclerView;
    SalesSummaryAdapter salesSummaryAdapter;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getActivity() != null)
            ((BaseApplication) getActivity().getApplication()).getActivityComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Refresh sales summary list"));
            salesSummaryAdapter.clearData();
            presenter.loadData();
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        setUpLists();
        setUpToolbar();
        presenter.bind(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbind(this);
        super.onDestroyView();
    }

    @Override
    protected void setUpLists() {
        salesSummaryRecyclerView.setHasFixedSize(false);
        salesSummaryRecyclerView.setItemAnimator(null);

        salesSummaryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (getActivity() != null)
                    if (dy > 0)
                        ((MainActivity) getActivity()).hideBottomBar(true);
                    else
                        ((MainActivity) getActivity()).showBottomBar(true);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        salesSummaryRecyclerView.setLayoutManager(manager);
        salesSummaryAdapter = new SalesSummaryAdapter(getActivity());
        salesSummaryRecyclerView.setAdapter(salesSummaryAdapter);
        if (!Utils.isPhoneLayout(requireContext())) {
            salesSummaryRecyclerView.addItemDecoration(new CardItemDecoration(requireContext()));
        }
    }

    @Override
    protected void setUpToolbar() {
        super.setUpToolbar();
        periodText.setFactory(() -> {
            TextView switcherTextView = null;
            if (getActivity() != null) {
                switcherTextView = new TextView(getActivity().getApplicationContext());
                switcherTextView.setTextSize((getResources().getDimension(R.dimen.period_text_size) / getResources().getDisplayMetrics().density));
                switcherTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
                switcherTextView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                switcherTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            return switcherTextView;
        });
        titleText.setText(getResources().getString(R.string.sales_summary));
        backIcon.setOnClickListener(view -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });
    }

    @Override
    public void showLoadingDialog() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingDialog() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateData(
        EarningsReportResponse.TotalValues totalValues,
        EarningsReportResponse.HideFields hideFields
    ) {
        hideLoadingDialog();
        //Set flags before updating data, otherwise adapter will use old flags
        salesSummaryAdapter.setHideFields(hideFields);
        salesSummaryAdapter.updateData(totalValues);
    }
}
