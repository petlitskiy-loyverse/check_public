package com.loyverse.dashboard.base.mvp;

public interface BaseView {

    void hideLoadingDialog();

    void showLoadingDialog();

    void showToastOnException(Throwable throwable);

}