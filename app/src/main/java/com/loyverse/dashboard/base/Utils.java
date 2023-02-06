package com.loyverse.dashboard.base;

import static com.loyverse.dashboard.core.DataModel.SHARED_PREFERENCES;
import static com.loyverse.dashboard.core.Server.DEFAULT_SO_TIME_OUT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.listener.Callback;
import com.loyverse.dashboard.base.server.ServerError;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.api.GetOwnerProfileResponse;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import timber.log.Timber;

public class Utils {
    public static final String DEVICE_TYPE = "android";
    public static final int DAY_PERIOD = 0;
    public static final int WEEK_PERIOD = 1;
    public static final int MONTH_PERIOD = 2;
    public static final int YEAR_PERIOD = 3;
    public static final int CUSTOM_PERIOD = 4;
    public static final double QUANTITY_DIVIDER = 1000d;
    public static final double FRACTIONAL_DIVIDER = 100d;
    public static final String WARES_KEY = "item";
    public static final String CATEGORIES_KEY = "category";
    public static final String EMPLOYEES_KEY = "employee";
    public static final String MONEY_FORMAT_KEY = "moneyFormat";
    public static final int SORTING_ACTIVE_COLOR = R.color.main_font_color;
    public static final int SORTING_PASSIVE_COLOR = R.color.light_main_font_color;
    public static final String ALL_PRODUCT_TYPE = "all";
    public static final String LOW_STOCK_TYPE = "low";
    public static final String OUT_OF_STOCK_TYPE = "out";
    public static final String SERVER_RESUL_OK = "ok";
    private static final int CIRCLE_SIZE = 120;
    private static final int WARE_IMAGE_SIZE = 120;
    public static final String COINS_SEPERATOR = ".";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static void makeActiveSortField(Context context, TextView view, DataModel.SortType sortType) {
        Drawable img = ContextCompat.getDrawable(context,
                sortType == DataModel.SortType.ASC ?
                        R.drawable.ic_arrow_upward :
                        R.drawable.ic_arrow_downward);
        if (isRtlMode(context)) {
            view.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else view.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
        view.setTextColor(ContextCompat.getColor(context, SORTING_ACTIVE_COLOR));

    }

    public static DataModel.SortType getDefaultSortTypeFor(DataModel.SortBy sortBy) {
        if (sortBy == DataModel.SortBy.NAME) {
            return DataModel.DEFAULT_SORT_BY_NAME_TYPE;
        }
        return DataModel.DEFAULT_SORT_BY_NET_TYPE;
    }

    public static void makeInactiveSortField(Context context, TextView view) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        view.setTextColor(ContextCompat.getColor(context, SORTING_PASSIVE_COLOR));
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        } catch (Exception e) {
            Timber.e(e);
        }
        builder.append("D.M.").append("Truerall").append("Fors");
        return MD5(builder.toString());
    }

    public static String MD5(String source) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(source.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
            return DEVICE_TYPE;
        }
    }

    public static String encryptWithSHA1andRSA(String text, String serverPEM) {
        try {
            String sha1Hex = stringToSha1(text);
            PublicKey pubKey = getPublicKeyFromPemFormat(serverPEM);
            if (pubKey != null) {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
                byte[] encryptedBytes = cipher.doFinal(sha1Hex.getBytes());
                return new String(Base64.encode(encryptedBytes, Base64.NO_WRAP));
            } else {
                return null;
            }
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public static String encryptWithRSA(String text, String serverPEM){
        try {
            PublicKey pubKey = getPublicKeyFromPemFormat(serverPEM);
            if (pubKey != null) {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
                byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
                return new String(Base64.encode(encryptedBytes, Base64.NO_WRAP));
            } else {
                return null;
            }
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private static PublicKey getPublicKeyFromPemFormat(String PEMString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(PEMString, Base64.DEFAULT)));
    }

    private static String stringToSha1(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.reset();
        md.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] hash = md.digest();
        return new String(Base64.encode(hash, Base64.NO_WRAP));
    }

    public static <T> void saveToSharedPreferences(Context context, T value, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (value instanceof String)
            editor.putString(key, (String) value);
        else if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public static <T> T readFromSharedPreferences(Context context, String key, Class<T> keyClass) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (keyClass.equals(String.class))
            return (T) sharedPref.getString(key, "");
        else if (keyClass.equals(Integer.class))
            return (T) ((Integer) sharedPref.getInt(key, 0));
        else if (keyClass.equals(Boolean.class))
            return (T) ((Boolean) sharedPref.getBoolean(key, true));
        else
            return null;
    }


    public static GetOwnerProfileResponse getMoneyFormat() {
        String json = readFromSharedPreferences(BaseApplication.getAppContext(), MONEY_FORMAT_KEY, String.class);
        if (json != null && !json.isEmpty())
            return new Gson().fromJson(json, GetOwnerProfileResponse.class);
        return null;
    }

    public static Boolean isDarkModeEnabled(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getBoolean("isDarkModeEnabled", false);
    }

    public static void removeFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

    public static Bitmap generateCircle(Context context, String color, String name) {
        int colorValue;
        if (color == null) {
//            String[] allColors = context.getResources().getStringArray(R.array.employees_palette);
//            int colorSegmentSize = Integer.MAX_VALUE / allColors.length;
//            int position = Math.abs(name == null ? 1 : name.hashCode() / colorSegmentSize);
//            colorValue = Color.parseColor(allColors[position]);
//            Log.e("=============", "========color111====" + allColors[position] + "  " + position);
            colorValue = Color.parseColor("#E0E0E0");
        } else {
            try {
                colorValue = Color.parseColor(color);
            } catch (IllegalArgumentException e) {
                Timber.e(e.getMessage() + color);
                colorValue = Color.parseColor(context.getResources().getStringArray(R.array.employees_palette)[0]);
            }
        }
        Bitmap output = Bitmap.createBitmap(CIRCLE_SIZE, CIRCLE_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(colorValue);

        //noinspection IntegerDivisionInFloatingPointContext
        canvas.drawCircle(CIRCLE_SIZE / 2, CIRCLE_SIZE / 2, CIRCLE_SIZE / 2, paint);
        return output;
    }

//    public static String formatNumber(double quantity) {
//        return formatNumber(null, quantity);
//    }

    public static String formatNumber(double quantity) {
        final GetOwnerProfileResponse goPR = getMoneyFormat();
        DecimalFormatSymbols symbols;
        if (goPR != null) {
            GetOwnerProfileResponse.MoneyFormat format = goPR.getMoneyFormat();
            return formatQuantity(format, quantity);

        } else {
            symbols = DecimalFormatSymbols.getInstance(EN_LOCALE);
            symbols.setGroupingSeparator(' ');
            symbols.setDecimalSeparator('.');
        }

        DecimalFormat formatter = new DecimalFormat("###,###.###", symbols);
        return formatter.format(quantity);
    }

    //FIXME: double -> long
    public static String formatSalesNumber(double amount) {
        final GetOwnerProfileResponse goPR = getMoneyFormat();
        if (goPR != null) {
            return formatMoneyAmount(goPR, amount);
        } else {
            //FIXME: divide amount with FRACTIONAL_DIVIDER
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(EN_LOCALE);
            symbols.setGroupingSeparator(','); // just on this realize for RTL format number
            symbols.setDecimalSeparator('.');
            DecimalFormat formatter = new DecimalFormat("###,##0.00", symbols);
            return formatter.format(amount);
        }
    }

    private static String formatMoneyAmount(
            GetOwnerProfileResponse goPR,
            double amount) {
        final GetOwnerProfileResponse.MoneyFormat moneyFormat = goPR.getMoneyFormat();
        final boolean isMinus = amount < 0D;

        if (isMinus)
            amount *= -1;

        String coins = "";

        final StringBuilder sb = new StringBuilder(32);
        final long temp = (long) amount;
        final double xCoins = (amount - temp);
        if (xCoins > 0D || goPR.getCashFractionDigits() > 0L) {
            coins = String.valueOf(String.valueOf(amount).contains("E") ? xCoins : amount);
            int indexOfDecimal = coins.indexOf(COINS_SEPERATOR);
            if (indexOfDecimal >= 0 && indexOfDecimal < coins.length())
                coins = coins.substring(indexOfDecimal);
            if (!moneyFormat.getDecSeparator().equals(COINS_SEPERATOR))
                coins = coins.replace(COINS_SEPERATOR, moneyFormat.getDecSeparator());
            if (coins.length() > goPR.getCashFractionDigits() + 1)
                coins = coins.substring(0, goPR.getCashFractionDigits().intValue() + 1);
            else {
                long left = goPR.getCashFractionDigits() + 1L - coins.length();
                int index = 0;
                while (index < left) {
                    coins += "0";
                    index++;
                }
            }
        }

        String money = String.valueOf((long) amount);
        if (money.length() > moneyFormat.getGrSeparator().getFirst()) {
            sb.insert(0, money.substring(money.length() - moneyFormat.getGrSeparator().getFirst()));
            money = money.substring(0, money.length() - moneyFormat.getGrSeparator().getFirst());
            sb.insert(0, moneyFormat.getGrSeparator().getSymbol());

            while (money.length() > moneyFormat.getGrSeparator().getOther()) {
                sb.insert(0, money.substring(money.length() - moneyFormat.getGrSeparator().getOther()));
                money = money.substring(0, money.length() - moneyFormat.getGrSeparator().getOther());
                sb.insert(0, moneyFormat.getGrSeparator().getSymbol());
            }
        }
        sb.insert(0, money);

//        final String currencySymbol = moneyFormat.getCurrency().getSymbol();
//        if (currencySymbol != null && !currencySymbol.isEmpty() && amount > 0D)
//            sb.insert(0, currencySymbol);

        if (isMinus && moneyFormat.getMinus().isOnTheLeft())
            sb.insert(0, "-");
        if (!coins.isEmpty() && coins.length() > 1)
            sb.append(coins);
        if (isMinus && !moneyFormat.getMinus().isOnTheLeft())
            sb.append("-");

        return sb.toString();
    }

    private static String formatQuantity(
            GetOwnerProfileResponse.MoneyFormat moneyFormat,
            double quantity) {

        final boolean isMinus = quantity < 0D;

        if (isMinus)
            quantity *= -1;

        String decimals = "";

        final StringBuilder sb = new StringBuilder(32);

        final long temp = (long) quantity;
        final double xCoins = (quantity - temp);
        if (xCoins > 0D) {
//            decimals = String.format(EN_LOCALE, "%.3f", quantity);
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(EN_LOCALE);
            DecimalFormat format = new DecimalFormat("##.###");
            decimals = format.format(quantity);
            Locale.setDefault(defaultLocale);
            int indexOfDecimal = decimals.indexOf(COINS_SEPERATOR);
            if (indexOfDecimal >= 0 && indexOfDecimal < decimals.length())
                decimals = decimals.substring(indexOfDecimal);
            if (!moneyFormat.getDecSeparator().equals(COINS_SEPERATOR))
                decimals = decimals.replace(COINS_SEPERATOR, moneyFormat.getDecSeparator());
        }

        String total = String.valueOf((long) quantity);
        if (total.length() > moneyFormat.getGrSeparator().getFirst()) {
            sb.insert(0, total.substring(total.length() - moneyFormat.getGrSeparator().getFirst()));
            total = total.substring(0, total.length() - moneyFormat.getGrSeparator().getFirst());
            sb.insert(0, moneyFormat.getGrSeparator().getSymbol());

            while (total.length() > moneyFormat.getGrSeparator().getOther()) {
                sb.insert(0, total.substring(total.length() - moneyFormat.getGrSeparator().getOther()));
                total = total.substring(0, total.length() - moneyFormat.getGrSeparator().getOther());
                sb.insert(0, moneyFormat.getGrSeparator().getSymbol());
            }
        }
        sb.insert(0, total);

        if (isMinus)
            sb.insert(0, "-");

        if (!decimals.isEmpty())
            sb.append(decimals);

        return sb.toString();
    }

    // TODO: 14.12.16 Consider making only one format function of large values

    /**
     * @param suffixes An suffix array of 5 items
     * @param value    for formatting
     * @return string string
     */
    public static String formatLargeNumberForDashboardChart(String[] suffixes, double value) {
        final int smallValueSuffixIndex = 0;
        final int eSuffixLength = 3;
        //Oh, this is thrilling touch of something higher than worldly logic
        final int specialBSValueMin = 1000;
        final int specialBSValueMax = 100000;
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(EN_LOCALE);
        final GetOwnerProfileResponse getOwnerProfileResponse = getMoneyFormat();
        final GetOwnerProfileResponse.MoneyFormat format = getOwnerProfileResponse.getMoneyFormat();

        if (value < 1 && value > -1) {
            if (value == 0)
                return "0";
            return String.format(Locale.US, "%." + getOwnerProfileResponse.getCashFractionDigits() + "f", value);
        }


        if (Math.abs(value) >= specialBSValueMin && Math.abs(value) < specialBSValueMax) {
//            symbols.setGroupingSeparator(format.getGrSeparator().getSymbol().charAt(0));
//            symbols.setDecimalSeparator(format.getDecSeparator().charAt(0));
//            DecimalFormat formatter = new DecimalFormat("###,###.###", symbols);
//            return formatter.format(value);
            return formatMoneyAmount(getOwnerProfileResponse, value);
        }

        DecimalFormat mFormat = new DecimalFormat("###E00", symbols);
        String r = mFormat.format(value);

        int numericValue1 = Character.getNumericValue(r.charAt(r.length() - 1));
        int numericValue2 = Character.getNumericValue(r.charAt(r.length() - 2));
        int index = Integer.parseInt(numericValue2 + "" + numericValue1) / 3;
        // TODO: 06.12.16 Refactor substring overhead (suffix)
        final String suffix = suffixes[index];
        r = r.replace(",", ".").substring(0, r.length() - eSuffixLength);

        double tmp;
        try {
            tmp = Double.parseDouble(r);
        } catch (NumberFormatException e) {
            //server sends number in a weird form
            tmp = Double.parseDouble(String.format(EN_LOCALE, "%." + getOwnerProfileResponse.getCashFractionDigits() + "f", value));
        }

        if (index > smallValueSuffixIndex)
            r = String.format(EN_LOCALE, "%.1f", tmp);
        else
            r = String.format(EN_LOCALE, "%.2f", tmp);

        if (index > smallValueSuffixIndex)
            while (isUselessNumber(r, r.length() - 1)) {
                r = r.substring(0, r.length() - 1);
            }

        String resultant = r + suffix;
        if (resultant.contains(COINS_SEPERATOR))
            resultant = resultant.replace(COINS_SEPERATOR, format.getDecSeparator());

        return resultant;
    }

    // TODO: 22.12.16 Move logic to a function above
    private static boolean isUselessNumber(String str, int charIndex) {
        if (charIndex < 0 || (str.indexOf(',') == -1 && str.indexOf('.') == -1))
            return false;

        char c = str.charAt(charIndex);
        return c == '0' || c == ',' || c == '.';
    }

    public static double formatCoinValue(long value) {
        return value / FRACTIONAL_DIVIDER;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static double calculatePercentageIncrease(long a, long b) {
        if (b == 0) {
            if (a > 0)
                return 100;
            else if (a < 0)
                return -100;
            else
                return 0;
        }

        return (a - b) / (b * 1D) * 100;
    }

    public static String wrapPercentageValue(double value) {
        final GetOwnerProfileResponse goPR = getMoneyFormat();
        String wrappedValue = "";
        if (value > 0D) {
            wrappedValue += "+";
        }
        wrappedValue += String.format(EN_LOCALE, "%.2f", value) + "%";

        long temp = (long) value;
        boolean toKeepDecimal = (value - temp) != 0D;
        if (toKeepDecimal)
            wrappedValue = wrappedValue.replace(COINS_SEPERATOR, goPR.getMoneyFormat().getDecSeparator());
        else if (wrappedValue.contains(COINS_SEPERATOR))
            wrappedValue = wrappedValue.substring(0, wrappedValue.indexOf(COINS_SEPERATOR)) + "%";
        return wrappedValue;
    }

    /**
     * @param context An activity, or Fragment where object is located or even some operation
     * @param action  Example: Some button click
     * @param info    An additional value, if it need
     * @return
     */
    public static String formatBreadCrumb(String context, String action, String info) {
        String str = context + ":\t" + action;
        if (!info.equals(""))
            str += "|" + info;
        return str;
    }

    public static String formatBreadCrumb(String context, String action) {
        return formatBreadCrumb(context, action, "");
    }


    public static String addPercentSymbol(String value) {
        return value + "%";
    }

    public static OkHttpClient createHttpClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        X509TrustManager trustManager = new X509TrustEverybodyManager();
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            javax.net.ssl.SSLSocketFactory sslSocketFactory = ctx.getSocketFactory();
            clientBuilder.sslSocketFactory(sslSocketFactory, trustManager);
        } catch (Exception e) {
            Timber.e(e);
        }
        clientBuilder.retryOnConnectionFailure(true).connectTimeout(DEFAULT_SO_TIME_OUT, TimeUnit.MILLISECONDS);
        return clientBuilder.build();
    }

    public static void loadRoundedImage(Context context, String imageLink,
                                        final ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .load(imageLink)
                .centerCrop()
                .override(Utils.WARE_IMAGE_SIZE, Utils.WARE_IMAGE_SIZE)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void loadRoundedImage(final Context context,
                                        final String imageLink,
                                        final ImageView imageView,
                                        final Callback callback) {

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.placeholder(R.drawable.splashscreenlogo);

        Glide.with(context)
                .asBitmap()
                .load(imageLink)
                .apply(options)
                .override(Utils.WARE_IMAGE_SIZE, Utils.WARE_IMAGE_SIZE)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }

                    @Override
                    public void setRequest(@Nullable Request request) {
                        super.setRequest(request);
                    }

                    @Override
                    public void setDrawable(Drawable drawable) {
                        super.setDrawable(drawable);
                        if (callback != null)
                            callback.onTaskPerformed();
                    }
                });
    }

    public static String getDarkThemeColorFor(String color) {

        switch (color != null ? color.toUpperCase() : "GREY") {

            case "PINK":
                return "#AD1457";
            case "ORANGE":
                return "#EF6C00";
            case "PURPLE":
                return "#6A1B9A";
            case "BLUE":
                return "#1565C0";
            case "RED":
                return "#C62828";
            case "GREEN":
                return "#2E7D32";
            case "LIME":
                return "#9E9D24";

            case "GREY":
            default:
                return "#616161";
        }

    }

    public static String getMessageForThrowable(Context context, Throwable throwable) {
        String message;
        if (throwable instanceof ServerError)
            message = context.getResources().getString(((ServerError) throwable).getErrorResource());
        else if (throwable instanceof UnknownHostException || throwable instanceof SocketTimeoutException)
            message = context.getResources().getString(R.string.no_connection);
        else
            message = throwable.getMessage();
        return message;
    }

    public static String[] getLargeValueFormatterSuffix(Context context) {
        return context.getResources().getStringArray(R.array.large_value_suffixes);
    }

    public static double getWaresEarningSum(WaresPeriodReportResponse.Wares item) {
        return 0;
    }

    public static void changeAppTheme(boolean isChecked) {
        if (isChecked)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @SuppressLint("NewApi")
    public static void setRTLText(final TextView textView, String value) {
        if (value.contains(" ")) {
            value = value.replace(" ", "&nbsp;");
            textView.setText(Html.fromHtml(value));
        } else
            textView.setText(value);

    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ALL_PRODUCT_TYPE, LOW_STOCK_TYPE, OUT_OF_STOCK_TYPE})
    public @interface ProductsFilterType {
    }

    private static final class X509TrustEverybodyManager implements X509TrustManager {
        private static final X509Certificate[] EMPTY_ACCEPTOR_ARRAY = new X509Certificate[0];

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return EMPTY_ACCEPTOR_ARRAY;
        }
    }

    public static final class CalculateProductPriceTotal {

        public static double getWaresEarningSum(WaresPeriodReportResponse.Wares item) {
            if (item.getVariations().isEmpty())
                return item.getAmount();

            return calculateVariantsEarningSum(item.getVariations());
        }

        public static double calculateVariantsEarningSum(List<WaresPeriodReportResponse.Variant> variants) {
            double earningSum = 0D;

            for (WaresPeriodReportResponse.Variant variant : variants) {
                earningSum += variant.getEarningSum();
            }
            return earningSum;
        }
    }

    public static boolean isPhoneLayout(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp < context.getResources().getInteger(R.integer.min_tablet_size_in_dp);
    }

    @SuppressLint("NewApi")
    public static boolean isRtlMode(Context context) {
        return context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public static Locale EN_LOCALE = new Locale("en", "US");
    public static NumberFormat enNumberFormat = NumberFormat.getInstance(EN_LOCALE);


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}