package cn.zerone.water.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zero on 2018/11/29.
 */

public class Https {
    public static final String CMD_LOGIN="login";

    public static <T>  void baseJSONObject(final Observer<JSONObject> observer, final String cmd, final JSONObject json){
//        System.out.println("request:"+"cmd:"+cmd+",json:"+json);
        Observable<T> oble = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                System.out.println("传递参数为：~~~~~~~~~" + json.toJSONString());
                Response response = post(cmd, json.toJSONString());
                System.out.println(response);
                if(response!=null&&response.code()==200){
                    String json = response.body().string();
                    System.out.println("baseJSONObject:"+json);
                    e.onNext((T) JSON.parseObject(json));
                    e.onComplete();
                }else{
                    e.onError(new IOException());
                }

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }
    public static   void baseJSONArray(final Observer<JSONArray> observer, final String cmd, final JSONObject json){
        Observable oble = Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JSONArray> e) throws Exception {
                Response response = post(cmd, json.toJSONString());
                System.out.println(response);
                if(response.code() == 200){
                    String json = response.body().string();
                    System.out.println(cmd);
                    System.out.println("baseJSONArray:"+json);
                    e.onNext(JSON.parseArray(json));
                    e.onComplete();
                }else{
                    throw new IOException();
                }

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }

    public static void baseString(Observer<String> observer, final String cmd, final JSONObject json) {
        System.out.println("request:"+"cmd:"+cmd+",json:"+json);
        Observable oble = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Response response = post(cmd, json.toJSONString());
                System.out.println("ppppppp" + response);
                int code = response.code();
                if(code == 200){
                    String json = response.body().string();
                    System.out.println("baseString:"+json);
                    e.onNext(json);
                    e.onComplete();
                }else{
                    System.out.println("request:"+cmd+"code:"+code+"string:"+response.body().string());
                    throw new IOException();
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }

    public static Response post(String cmd, String json){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = null;
        try {
            FormBody.Builder  builder = new FormBody.Builder();
            builder.add("Cmd",cmd);
            String md5 = cmd+json+"d8sd19k09d1dc6";
            builder.add("Md5","");
            builder.add("Json",json);
            body = builder.build();
            System.out.println("body  :  " + body.toString() + "    llllll   " + body.contentLength() );

            return okHttpClient.newCall(new Request.Builder().url("http://47.105.187.185:8011/Service/AppService.asmx").post(body).build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
