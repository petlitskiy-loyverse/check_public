package com.loyverse.dashboard.mvp.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.BaseFragment;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.MainView;
import com.loyverse.dashboard.base.mvp.ProductsPresenter;
import com.loyverse.dashboard.base.mvp.ProductsView;
import com.loyverse.dashboard.base.products.ProductFilterAdapter;
import com.loyverse.dashboard.base.products.ProductItem;
import com.loyverse.dashboard.base.products.ProductsAdapter;
import com.loyverse.dashboard.base.sales.CardItemDecoration;
import com.loyverse.dashboard.base.sales.EndlessScrollListener;
import com.loyverse.dashboard.core.Navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class ProductsFragment extends BaseFragment implements ProductsView {

    public static final String TAG = "ProductsFragment";
    public static final int SEARCH_DELAY = 700;

    @BindView(R.id.ic_outlets)
    protected ImageView outletsIcon;
    @BindView(R.id.products)
    RecyclerView productsRecycleView;
    @BindView(R.id.product_filter_type_title)
    TextView productFilterTypeTitle;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.outlet_title)
    TextView outletTitle;
    @BindView(R.id.product_toolbar_margin_view)
    View toolbarMarginView; //  Add-hoc solution
    @BindView(R.id.toolbar_content)
    View toolbarContent;
    @BindView(R.id.search_container)
    View searchContainer;
    @BindView(R.id.search_view)
    EditText searchView;
    @BindView(R.id.search_close)
    View searchCloseButton;
    @BindView(R.id.search_clear)
    View searchClear;
    @BindView(R.id.ic_search)
    ImageView searchIcon;
    ProductsAdapter adapter;

    List<String> filterList = new ArrayList<>();

    @Inject
    ProductsPresenter<ProductsView> presenter;
    @Inject
    Navigator navigator;
    CompositeSubscription textSubscription = new CompositeSubscription();
    SearchTextWatcher textWatcher = new SearchTextWatcher();
    private EndlessScrollListener scrollListener;
    private ListPopupWindow productFilterPopupWindow;
    private ProductFilterAdapter filterAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((BaseApplication) getActivity().getApplication()).getActivityComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.bind(this);
        setUpFields();
        presenter.setUp();
    }

    @Override
    public void onDestroyView() {
        unsubscribeSearchSubscription();
        presenter.unbind(this);
        hideKeyBoard();
        super.onDestroyView();
    }

    private void unsubscribeSearchSubscription() {
        textSubscription.clear();
    }

    private void setUpFields() {
        outletsIcon.setOnClickListener(view -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Outlets button click"));
            navigator.showOutletsFragment(getActivity(), false);
        });
        adapter = new ProductsAdapter();

        productsRecycleView.setHasFixedSize(true);
        productsRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || presenter.isStateOfSearch())
                    ((MainActivity) getActivity()).hideBottomBar(true);
                else
                    ((MainActivity) getActivity()).showBottomBar(true);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        productsRecycleView.setLayoutManager(manager);
        productsRecycleView.setAdapter(adapter);

        productsRecycleView.setItemAnimator(null);

        if(!Utils.isPhoneLayout(requireContext())){
            productsRecycleView.addItemDecoration(new CardItemDecoration(requireContext()));
        } else {
            swipeRefreshLayout.setBackground(getContext().getResources().getDrawable(R.color.white));
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Product list refresh"));
            presenter.loadData(0);
            scrollListener.resetState();
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        scrollListener = new EndlessScrollListener(manager) {
            @Override
            public void onLoadMore(int totalItemsCount) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    Timber.v(Utils.formatBreadCrumb(TAG, "Product list load more"));
                    presenter.loadData(totalItemsCount);
                }

            }
        };
        productsRecycleView.addOnScrollListener(scrollListener);
        toolbar.setOnClickListener(v -> onProductTypeFilterTitleClick());
        setUpSearch();
        setUpProductFilter();
    }

    private void setUpSearch() {
        searchIcon.setOnClickListener((view -> showSearch()));
        searchCloseButton.setOnClickListener((view -> hideSearch()));
        // Search text changed listener
        searchView.addTextChangedListener(textWatcher);
        // Clear search text when clear button is tapped
        searchClear.setOnClickListener(v -> searchView.setText(""));
    }

    private void setUpProductFilter() {
        productFilterPopupWindow = new ListPopupWindow(getContext());
        productFilterPopupWindow.setAnimationStyle(android.R.style.Widget_DropDownItem_Spinner);

        filterAdapter = new ProductFilterAdapter(getContext());
        filterList.clear();
        filterList.add(Utils.ALL_PRODUCT_TYPE);
        filterList.add(Utils.LOW_STOCK_TYPE);
        filterList.add(Utils.OUT_OF_STOCK_TYPE);

        for (int i = 0; i < filterList.size(); i++) {
            filterAdapter.add(getFilterNameByType(filterList.get(i)));
        }
        filterAdapter.setSelectedItem(0);

        productFilterPopupWindow.setAdapter(filterAdapter);
        productFilterPopupWindow.setAnchorView(toolbar);
        productFilterPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            @Utils.ProductsFilterType String filterType = filterList.get(position);
            Timber.i("filter_by_stock_alert");
            presenter.onProductFilterChange(filterType);
            productFilterPopupWindow.dismiss();
        });
        productFilterPopupWindow.setModal(true);
        productFilterPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private String getFilterNameByType(String type) {
        int stringId = R.string.product_filter_type_all;
        if (type.equals(Utils.LOW_STOCK_TYPE)) {
            stringId = R.string.product_filter_type_low_stock;
        } else if (type.equals(Utils.OUT_OF_STOCK_TYPE)) {
            stringId = R.string.product_filter_type_empty_stock;
        }
        return getResources().getString(stringId);
    }

    public void onProductTypeFilterTitleClick() {
        if (!presenter.isStateOfSearch())
            presenter.onProductTypeFilterTitleClick();
    }

    @Override
    public void showProductFilterTypeDialog(@Utils.ProductsFilterType String filterType) {
        Timber.v(Utils.formatBreadCrumb(TAG, "showProductFilterTypeDialog"));
        int selectedPosition = filterList.indexOf(filterType);
        filterAdapter.setSelectedItem(selectedPosition);
        productFilterPopupWindow.show();
    }

    public void setProductFilterTypeTitle(@Utils.ProductsFilterType String filterType) {
        String title = getFilterNameByType(filterType);
        productFilterTypeTitle.setText(title);
    }

    @Override
    public void addToProductList(List<ProductItem> productList) {
        hideLoadingDialog();
        adapter.addProductItemList(productList);
    }

    @Override
    public void clearProductList() {
        scrollListener.resetState();
        adapter.clearProductItemList();
    }

    @Override
    public void showOutletTitleAndOutletsIcon() {
        outletsIcon.setVisibility(View.VISIBLE);
        outletTitle.setVisibility(View.VISIBLE);
        //For centring title
        int targetWidth = (int) getResources().getDimension(R.dimen.material_icon_button_size);
        toolbarMarginView.getLayoutParams().width = 2 * targetWidth;
    }

    @Override
    public void hideOutletTitleAndOutletsIcon() {
        outletsIcon.setVisibility(View.GONE);
        outletTitle.setVisibility(View.GONE);
        //For centring title
        toolbarMarginView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.material_icon_button_size);
    }

    @Override
    public void showSearch() {
        ((MainView) getActivity()).hideBottomBar(false);

        presenter.setStateOfSearch(true);
        searchView.setText(presenter.getSearchedProductName());
        textSubscription.add(textWatcher.getObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(s1 -> s1.trim())
                .debounce(SEARCH_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s1 -> {
                    Timber.d(s1);
                    presenter.setSearchedProductName(s1);
                    presenter.loadData(0);
                }, throwable -> {
                })); // TODO: 13.01.17 Add error handler
        //separate subscriber that handles searchClear visibility(should be hidden when no symbols are entered)
        textSubscription.add(textWatcher.getObservable()
                .subscribe(s1 -> {
                    if (s1.isEmpty()) searchClear.setVisibility(View.GONE);
                    else searchClear.setVisibility(View.VISIBLE);
                }));
        toolbarContent.setVisibility(View.GONE);
        searchContainer.setVisibility(View.VISIBLE);
        ViewCompat.jumpDrawablesToCurrentState(searchContainer);

        if (searchView.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void hideSearch() {
        Timber.e("here");
        presenter.setStateOfSearch(false);
        presenter.setSearchedProductName("");
        presenter.loadData(0);

        unsubscribeSearchSubscription();
        toolbarContent.setVisibility(View.VISIBLE);
        ViewCompat.jumpDrawablesToCurrentState(toolbarContent);
        searchContainer.setVisibility(View.GONE);
        hideKeyBoard();
        //timeout, waiting for keyboard to disappear
        new Handler().postDelayed(() ->
        {
            if (getActivity() != null)
                ((MainView) getActivity()).showBottomBar(true);
        }, 400);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
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
    public void scrollToTheTop() {
        if (productsRecycleView != null)
            productsRecycleView.smoothScrollToPosition(0);
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
    public boolean onBackPressed() {
        if (presenter.isStateOfSearch()) {
            hideSearch();
            return false;
        }

        return true;
    }

    private class SearchTextWatcher implements TextWatcher {
        PublishSubject<String> subject = PublishSubject.create();

        public Observable<String> getObservable() {
            return subject;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            subject.onNext(editable.toString());
        }
    }
}
