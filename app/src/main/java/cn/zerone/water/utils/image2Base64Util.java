package cn.zerone.water.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class image2Base64Util {


    // 将本地图片url转成Base64格式
    public String getBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    // 将网络上的图片转成Base64格式（这样的格式能够用ImageView显示出来）
    public String encodeImageToBase64(String imgUrl){
        HttpURLConnection conn = null;
        String base64 = "";
        try {
            URL url = new URL(imgUrl);

            conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            //关闭输入流
            inStream.close();
            byte[] data = outStream.toByteArray();
            //对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            base64 = encoder.encode(data);
            //返回Base64编码过的字节数组字符串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    // 获取文件路径中的文件名(xxx.jpg)，直接将url路径插入数据库会失效
    public String getPicName(String path){
        Pattern pattern = Pattern.compile("[0-9][0-9].+");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find())
            path = matcher.group(0);
        System.out.println("AAAAAAAAAAAAAAAAA"+ matcher.group(0));
        return path;
    }
}
