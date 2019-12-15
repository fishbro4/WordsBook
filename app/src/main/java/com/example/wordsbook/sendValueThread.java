package com.example.wordsbook;

import android.os.Handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class sendValueThread extends Thread {

    private Map<String,String> parameterMap;
    private Handler handler;

    /**
     * 传入参数集合，以及handler
     * @param map
     * @param handler
     */
    public sendValueThread(Map<String,String> map, Handler handler){
        this.parameterMap = map;
        this.handler = handler;
    }

    @Override
    public void run(){
        //获取连接的API类型
        String type = parameterMap.get("type");
        //预留接口，后续想要接入其他网站提供的API，可根据type进行分流
        switch (type){
            case "youdao":
                YouDao_Access();
                break;
        }
    }

    /**
     * 根据parameterMap中的参数
     * 接入有道API必备参数：
     * url:有道接入网址; APP_KEY:应用id; APP_SECRET:应用密匙
     * q:翻译文本; from:源语言; to:目标语言;
     */
    private void YouDao_Access(){
        //添加需要进行的操作
        try {
            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象

            String q = parameterMap.get("q");
            String salt = String.valueOf(System.currentTimeMillis());
            String curtime = String.valueOf(System.currentTimeMillis() / 1000);
            String signStr = parameterMap.get("APP_KEY") + truncate(q) + salt + curtime + parameterMap.get("APP_SECRET");
            String sign = getDigest(signStr);

            FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
            formBody.add("q",q);//传递键值对参数
            formBody.add("from",parameterMap.get("from"));
            formBody.add("to",parameterMap.get("to"));
            formBody.add("sign",sign);
            formBody.add("signType","v3");
            formBody.add("curtime",curtime);
            formBody.add("appKey",parameterMap.get("APP_KEY"));
            formBody.add("salt",salt);

//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
//            String jsonStr = "{q:"+q+",from:zh-CHS,to:en,sign:"
//                            + sign+",sigType:v3,curtime:"+curtime+",appKey:"
//                            +APP_KEY+",salt:"+salt+"}";//json数据.
//            RequestBody body = RequestBody.create(JSON,jsonStr);

            Request request = new Request.Builder()//创建Request 对象。
                    .url(parameterMap.get("url"))//请求接口
                    .addHeader("Charset","UTF-8")//添加头信息
                    .post(formBody.build())//传递表单请求体
//                  .post(body)//传递json请求体
                    .build();

            Call call = client.newCall(request);
            //同步请求，等待响应之后再进行处理
            Response response = call.execute();
            if ( !response.isSuccessful() ){
                //抛出错误
                handler.obtainMessage(1,"failure").sendToTarget();//发送失败消息到消息监听线程
                throw new IOException("Unexpected code " + response);
            }
            else {
                //System.out.println(response.body().string());//响应消息只能读取一次，之后会被刷新
                handler.obtainMessage(0,response.body().string() ).sendToTarget();//发送成功消息到消息监听线程
            }
//            //异步请求，创建子线程，在回调函数中进行处理——与同步调用call的不同方法
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    //...
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if(response.isSuccessful()){
//                        String result = response.body().string();
//                        System.out.println(result);
//                        //处理UI需要切换到UI线程处理
//                        handler.obtainMessage(0,"11233").sendToTarget();//发送消息到消息监听实例
//                    }
//                }
//            });
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * 有道提供的加密函数_sha256加密
     * @param string
     * @return
     */
    public static String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 有道获取文本
     * @param q
     * @return
     */
    public static String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        String result;
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}

