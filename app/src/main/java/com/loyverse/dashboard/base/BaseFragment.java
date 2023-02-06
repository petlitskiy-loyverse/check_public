package com.loyverse.dashboard.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.loyverse.dashboard.base.mvp.BaseView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment implements BaseView {

    //For ButterKnife
    private Unbinder unbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void hideLoadingDialog() {
        ((BaseView) getActivity()).hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        ((BaseView) getActivity()).showLoadingDialog();
    }

    @Override
    public void showToastOnException(Throwable throwable) {
        ((BaseView) getActivity()).showToastOnException(throwable);
    }
}
