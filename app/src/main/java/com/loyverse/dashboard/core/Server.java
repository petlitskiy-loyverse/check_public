package com.loyverse.dashboard.core;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.server.ServerError;
import com.loyverse.dashboard.base.server.ServerResult;
import com.loyverse.dashboard.core.api.BaseResponsePOJO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class Server {
    public static final String PASSWORD_RSA_PUBLIC_KEY = BuildConfig.PASSWORD_RSA_PUBLIC_KEY;
    public static final int DEFAULT_SO_TIME_OUT = 30000;
    public static final int LIMIT = 50;
    private static final String SERVER_URL = BuildConfig.SERVER_URL;
    private final Gson jsonParser;
    private final OkHttpClient httpClient;

    public Server(Gson jsonParser, OkHttpClient httpClient) {
        Timber.d("constructor");
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
    }

    /**
     * this method is the only entry point to connect to server
     */
    public <Q, R extends BaseResponsePOJO> Subscription request(Q requestPojo, Class<R> responseClass, Action1<R> onNext, Action1<Throwable> onError) {
        return Single.fromCallable(() -> sendRequest(requestPojo, responseClass))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public <Q, R extends BaseResponsePOJO> R sendRequest(Q requestPojo, Class<R> responseClass) throws IOException {
        Timber.d("%s %s", requestPojo.getClass().getSimpleName(), jsonParser.toJson(requestPojo));
        JsonElement response = requestResponse(SERVER_URL, jsonParser.toJson(requestPojo));
        Timber.d("%s %s", responseClass.getSimpleName(), response);
        R object = jsonParser.fromJson(response, responseClass);
        if (!object.result.equals(ServerResult.OK.result)) {
            throw new ServerError(object.result);
        }
        return object;
    }

    private JsonElement requestResponse(String url, String postBody) throws IOException {
        Response response = null;
        try {
            response = httpClient.newCall(buildAndEncryptRequest(url, postBody)).execute();
            JsonParser parser = new JsonParser();
            JsonElement element;
            try {
                element = parser.parse(response.body().string());
                return element;
            } catch (JsonSyntaxException e) {
                Timber.e(response.body().string());
                throw new IOException(String.format("Server has returned %s", response.code()));
            }
        } finally {
            if (response != null)
                response.close();
        }
    }

    private Request buildAndEncryptRequest(String url, String postBody) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .url(url);
        ByteArrayOutputStream gzipedBuffer = new ByteArrayOutputStream();
        OutputStream zipper = new GZIPOutputStream(gzipedBuffer);
        zipper.write(postBody.getBytes("UTF-8"));
        zipper.close();
        requestBuilder
                .addHeader("Content-Encoding", "gzip")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gzipedBuffer.toByteArray()));
        return requestBuilder.build();
    }


}
