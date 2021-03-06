package cn.zerone.water.utils;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.baidu.platform.comapi.newsearch.EngineParams.HttpMethod.GET;


/**
 * created by qhk
 * on 2019/5/11
 */
public class HttpUtil {
    // 接口调用的url
    public static final String ADVANCED_URL = "http://47.105.187.185:8011/api1/";

    public static void baseJSONArray(final Observer<JSONArray> observer, final String cmd, final RequestBody requestBody) {
        Observable oble = Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JSONArray> e) throws Exception {
                Response response = post(cmd, requestBody);
                if (response.code() == 200) {
                    String json = response.body().string();
                    e.onNext(JSON.parseArray(json));
                    e.onComplete();
                } else {
                    throw new IOException();
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }

    public static void baseString(Observer<String> observer, final String cmd, final RequestBody requestBody) {
        Observable oble = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Response response = post(cmd, requestBody);
                int code = response.code();
                if (code == 200) {
                    String json = response.body().string();
                    Log.i("myTag", "json" + json);
                    e.onNext(json);
                    e.onComplete();
                } else {
                    throw new IOException(
                    );
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }

    public static void baseGetString(Observer<String> observer, final String cmd, final String userId, final String date) {
        Observable oble = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Response response = Get(cmd, userId, date);
                int code = response.code();
                if (code == 200) {
                    String json = response.body().string();
                    Log.i("myTag", "json" + json);
                    e.onNext(json);
                    e.onComplete();
                } else {
                    throw new IOException(
                    );
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }


    public static <T> void baseJSONObject(final Observer<JSONObject> observer, final String cmd, final RequestBody requestBody) {
        Observable<T> oble = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                Response response = post(cmd, requestBody);
                if (response != null && response.code() == 200) {
                    String json = response.body().string();
                    e.onNext((T) JSON.parseObject(json));
                    e.onComplete();
                } else {
                    e.onError(new IOException());
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }
    public static <T> void baseJSONObjectLive(final Observer<JSONObject> observer, final String url, final RequestBody requestBody) {
        Observable<T> oble = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                Response response = post(url, requestBody);
                if (response != null && response.code() == 0) {
                    String json = response.body().string();
                    e.onNext((T) JSON.parseObject(json));
                    e.onComplete();
                } else {
                    e.onError(new IOException());
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }
    //post请求服务端接口
    public static Response post(String url1, RequestBody requestBody) {
        String url = url1;//实际url
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //无需观察者模式的JSONObject接口
    public static <T> JSONObject baseJSONObject(final String cmd, final RequestBody requestBody) {
        Response response = post(cmd, requestBody);
        if (response != null && response.code() == 200) {
            try {
                String json = response.body().string();
                JSONObject jsonObject = JSON.parseObject(json);
                return jsonObject;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    //无需观察者模式的JSONObjectArray接口
    public static <T> JSONArray baseJSONArray(final String cmd, final RequestBody requestBody) {
        Response response = post(cmd, requestBody);
        if (response != null && response.code() == 200) {
            try {
                String json = response.body().string();
                JSONArray objects = JSON.parseArray(json);
                return objects;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;

    }
    //get请求服务端接口
    public static Response Get(String cmd, String userId, String date){
        String url = ADVANCED_URL + cmd + "?" + "UserId=" + userId + "&Date=" + date;//实际url
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
        }

    }

