package com.loyverse.dashboard.mvp.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.BaseFragment;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.SettingsPresenter;
import com.loyverse.dashboard.base.mvp.SettingsView;
import com.loyverse.dashboard.core.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class SettingsFragment extends BaseFragment implements SettingsView {

    public static final String TAG = "SettingsFragment";

    @BindView(R.id.item_setting_value)
    SwitchCompat stockNotificationValue;

    @BindView(R.id.item_use_darktheme_value)
    SwitchCompat item_use_darktheme_value;

    @BindView(R.id.account_email)
    TextView accountEmail;

    @BindView(R.id.settings_wrapper)
    CardView settingsWrapper;

    @Inject
    SettingsPresenter<SettingsView> presenter;

    @Inject
    Navigator navigator;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((BaseApplication) getActivity().getApplication()).getActivityComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.bind(this);
        stockNotificationValue.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    Timber.i(isChecked ? "low_stock_notification_turn_on" : "low_stock_notification_turn_off");
                    presenter.onStockNotificationSettingChange(isChecked, Utils.getDeviceId(getContext()));
                });

        item_use_darktheme_value.setChecked(Utils.isDarkModeEnabled(getActivity()));

        item_use_darktheme_value.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    Utils.saveToSharedPreferences(getActivity(), isChecked,"isDarkModeEnabled");
                    Utils.changeAppTheme(isChecked);
                });
    }

    @OnClick(R.id.logout_button)
    public void onLogoutButtonClick() {
        Timber.v(Utils.formatBreadCrumb(TAG, "Logout button click"));
        presenter.onLogout();
    }

    @OnClick(R.id.btnPrivacy)
    public void onPrivacyClick() {
        navigator.openWebpage(getActivity(), Navigator.PRIVACY_LINK);
    }

    @OnClick(R.id.btnTerms)
    public void onTermsClick() {
        navigator.openWebpage(getActivity(), Navigator.TERMS_LINK);
    }

    @Override
    public void setStockNotificationSetting(boolean value) {
        stockNotificationValue.setChecked(value);
    }

    @Override
    public void setAccountEmail(String email) {
        accountEmail.setText(email);
    }

    @Override
    public void showLogoutDialog() {
        Timber.v(Utils.formatBreadCrumb(TAG, "Show logout dialog"));
        navigator.showLogoutDialog(getActivity());
    }

    @Override
    public void logout() {
        Timber.v(Utils.formatBreadCrumb(TAG, "Success logout"));
        navigator.showLoginScreen(getActivity());
    }

    @Override
    public void onDestroyView() {
        presenter.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onLogoutConfirmation() {
        presenter.onLogoutConfirmation();
    }

    @Override
    public void toggleNotificationSetting(boolean enable) {
        Timber.v(Utils.formatBreadCrumb(TAG, "Change notification settings", String.valueOf(enable)));
        stockNotificationValue.setChecked(enable);
    }

    @Override
    public void setNotificationSettingVisibility(boolean visible) {
        if (visible)
            settingsWrapper.setVisibility(View.VISIBLE);
        else
            settingsWrapper.setVisibility(View.GONE);
    }
}
