package com.loyverse.dashboard.mvp.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseActivity;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.core.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WebActivity extends BaseActivity {

    @BindView(R.id.web)
    WebView webView;

    private String URL;
    private Unbinder unbinder;

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        unbinder = ButterKnife.bind(this);
        ((BaseApplication) getApplication()).getActivityComponent().inject(this);

        Intent inIntent = this.getIntent();
        if (inIntent != null && inIntent.hasExtra(Navigator.URL_KEY)) {
            URL = inIntent.getStringExtra(Navigator.URL_KEY);
        } else
            this.finish();

        webView.setWebViewClient(new MyClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URL);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null)
            unbinder.unbind();

        super.onDestroy();
    }

    @OnClick(R.id.ic_back)
    void onBackClick() {
        this.finish();
    }

    @OnClick(R.id.ic_next)
    void onNextClick() {
        navigator.openExternalBrowserFor(this, URL);
    }


    private class MyClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showLoadingDialog();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideLoadingDialog();
        }
    }
}
