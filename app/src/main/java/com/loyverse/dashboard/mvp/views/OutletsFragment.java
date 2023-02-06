package com.loyverse.dashboard.mvp.views;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.BaseFragment;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.multishop.OutletAdapter;
import com.loyverse.dashboard.base.mvp.OutletsPresenter;
import com.loyverse.dashboard.base.mvp.OutletsView;
import com.loyverse.dashboard.core.api.Outlet;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

public class OutletsFragment extends BaseFragment implements OutletsView, OutletAdapter.OnOutletSelectedListener {
    public final static String TAG = "OutletsFragment";
    public final static String INCLUDE_PERIOD_OUTLETS = "includePeriodOutlet";

    @BindView(R.id.outlet_list)
    protected RecyclerView outletList;
    @Inject
    protected OutletsPresenter<OutletsView> presenter;
    @BindView(R.id.ic_back)
    ImageView backIcon;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    OutletAdapter adapter = new OutletAdapter();


    public static OutletsFragment newInstance(boolean includePeriodOutlets) {
        OutletsFragment outletsFragment = new OutletsFragment();
        Bundle args = new Bundle();
        args.putBoolean(INCLUDE_PERIOD_OUTLETS, includePeriodOutlets);
        outletsFragment.setArguments(args);
        return outletsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((BaseApplication) getActivity().getApplication()).getActivityComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outlets, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFields();

        presenter.bind(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbind(this);
        super.onDestroyView();
    }

    private void setUpFields() {
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadData());

        outletList.setHasFixedSize(false);
        outletList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setListener(this);
        outletList.setAdapter(adapter);
        outletList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    ((MainActivity) getActivity()).hideBottomBar(true);
                else
                    ((MainActivity) getActivity()).showBottomBar(true);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        backIcon.setOnClickListener(view -> getActivity().onBackPressed());
    }

    @Override
    public void updateData(List<Outlet> outlets, List<Outlet> selectedOutlets) {
        hideLoadingDialog();
        adapter.updateData(outlets, selectedOutlets);
    }

    @Override
    public void cleanData() {
        adapter.clearData();
    }

    @Override
    public void close() {
        getActivity().onBackPressed();
    }

    @Override
    public boolean isIncludedPeriodOutlets() {
        return getArguments().getBoolean(INCLUDE_PERIOD_OUTLETS);
    }

    @Override
    public void OnOutletSelected(Outlet outlet) {
        Timber.v(Utils.formatBreadCrumb(TAG, "Outlet selected", outlet.name + ", " + String.valueOf(outlet.id)));
        presenter.onOutletSelected(outlet);
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
