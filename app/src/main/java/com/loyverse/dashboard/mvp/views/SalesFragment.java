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
import com.loyverse.dashboard.base.mvp.SalesPresenter;
import com.loyverse.dashboard.base.mvp.SalesView;
import com.loyverse.dashboard.base.sales.BaseSalesAdapter;
import com.loyverse.dashboard.base.sales.BaseSalesClassWrapper;
import com.loyverse.dashboard.base.sales.BaseSalesItem;
import com.loyverse.dashboard.base.sales.BaseToolbarFragment;
import com.loyverse.dashboard.base.sales.EndlessScrollListener;
import com.loyverse.dashboard.base.sales.CardItemDecoration;
import com.loyverse.dashboard.base.sales.SalesItemDividerDecoration;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Navigator;

import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static com.loyverse.dashboard.base.Utils.CATEGORIES_KEY;
import static com.loyverse.dashboard.base.Utils.EMPLOYEES_KEY;
import static com.loyverse.dashboard.base.Utils.WARES_KEY;

public class SalesFragment extends BaseToolbarFragment<SalesPresenter<SalesView>> implements SalesView {

    public static final String TAG = "SalesFragment";

    @BindView(R.id.sales_list)
    protected RecyclerView salesRecyclerView;
    BaseSalesAdapter adapter;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private EndlessScrollListener scrollListener;

    public static SalesFragment newInstance(String key) {
        SalesFragment salesFragment = new SalesFragment();
        Bundle args = new Bundle();
        args.putString(Navigator.TITLE_KEY, key);
        salesFragment.setArguments(args);
        return salesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getActivity() != null)
            ((BaseApplication) getActivity().getApplication()).getActivityComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpLists();
        setUpToolbar();
        adapter.updateSorting(new BaseSalesClassWrapper.Sorting(DataModel.SortBy.NET, DataModel.SortType.DESC, getSortFieldName()));
        adapter.updateTotal(0D);
        presenter.bind(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbind(this);
        super.onDestroyView();
    }

    @Override
    protected void setUpLists() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Refresh sales list"));
            presenter.refreshData();
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        salesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    ((MainActivity) getActivity()).hideBottomBar(true);
                else
                    ((MainActivity) getActivity()).showBottomBar(true);
            }
        });
        salesRecyclerView.setItemAnimator(null);
        salesRecyclerView.setHasFixedSize(true);
        salesRecyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        salesRecyclerView.setLayoutManager(manager);
        salesRecyclerView.addItemDecoration(new SalesItemDividerDecoration(requireContext()));
        if (!Utils.isPhoneLayout(requireContext())) {
            salesRecyclerView.addItemDecoration(new CardItemDecoration(requireContext()));
        }
        adapter = new BaseSalesAdapter((sortBy, sortType) -> {
            Timber.i("column_sorting");
            if (sortBy == DataModel.SortBy.NAME) {
                Timber.v(Utils.formatBreadCrumb(TAG, "Toggle name sort"));
                presenter.onNameSortChange();
            } else {
                Timber.v(Utils.formatBreadCrumb(TAG, "Toggle net sort"));
                presenter.onNetSortChange();
            }
        });

        salesRecyclerView.setAdapter(adapter);

        scrollListener = new EndlessScrollListener(manager) {
            @Override
            public void onLoadMore(int totalItemsCount) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    Timber.v(Utils.formatBreadCrumb(TAG, "Load more into sales list"));
                    presenter.loadNext();
                }
            }
        };
        salesRecyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    protected void setUpToolbar() {
        super.setUpToolbar();
        switch (getListKey()) {
            case WARES_KEY:
                titleText.setText(getResources().getString(R.string.products_sales));
                break;
            case EMPLOYEES_KEY:
                titleText.setText(getResources().getString(R.string.employees_sales));
                break;
            case CATEGORIES_KEY:
                titleText.setText(getResources().getString(R.string.categories_sales));
                break;
        }
        periodText.setFactory(() -> {
            TextView switcherTextView = new TextView(getContext().getApplicationContext());
            switcherTextView.setTextSize((getResources().getDimension(R.dimen.period_text_size) / getResources().getDisplayMetrics().density));
            switcherTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
            switcherTextView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            switcherTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            return switcherTextView;
        });
        backIcon.setOnClickListener(view -> getActivity().onBackPressed());
    }

    private String getSortFieldName() {
        switch (getListKey()) {
            case WARES_KEY:
                return getResources().getString(R.string.product);
            case EMPLOYEES_KEY:
                return getResources().getString(R.string.name);
            case CATEGORIES_KEY:
                return getResources().getString(R.string.category);
        }
        return null;
    }

    @Override
    public void updateSortingView(DataModel.SortType sortType, DataModel.SortBy sortBy) {
        adapter.updateSorting(new BaseSalesClassWrapper.Sorting(sortBy, sortType, getSortFieldName()));
    }

    @Override
    public void addToData(List<BaseSalesItem> newItems, double total) {
        adapter.addData(newItems);
        adapter.updateTotal(total);
    }

    @Override
    public String getListKey() {
        return getArguments().getString(Navigator.TITLE_KEY);
    }

    @Override
    public void clearList() {
        adapter.clearData();
        scrollListener.resetState();
    }

    @Override
    public void showLoadingDialog() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingDialog() {
        swipeRefreshLayout.setRefreshing(false);
    }

}