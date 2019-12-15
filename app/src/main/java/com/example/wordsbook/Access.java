package com.example.wordsbook;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Access {
    //消息响应标识
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    private static final int MSG_WAIT = 2;
    //参数容器
    private Map<String,String> map_parameters;
    private JSONObject json_return;
    //消息响应完成标识，三状态：等待、失败、完成
    private int completed;
    //进程间消息传递句柄
    private Handler mHandler;

    public Access(){
        this.map_parameters = new HashMap<String,String>();
        this.completed = MSG_WAIT;//设置消息响应为等待
    }

    /**
     * 设置有道翻译实例参数
     * @param url
     * @param key
     * @param secret
     */
    public void YouDao_SetInstance(String url,String key,String secret){
        //设置接入类型--有道youdao接入
        this.map_parameters.put("type","youdao");

        this.map_parameters.put("url",url);
        this.map_parameters.put("APP_KEY",key);
        this.map_parameters.put("APP_SECRET",secret);
    }

    /**
     * 设置有道翻译参数
     * @param q
     * @param from
     * @param to
     */
    public void YouDao_SetTranslation(String q, String from, String to){
        this.map_parameters.put("q",q);
        this.map_parameters.put("from",from);
        this.map_parameters.put("to",to);
    }



    public JSONObject getConnect(){//此方法在主线程中进行
        //启动消息监听线程实例
        new Message_Receiver().start();
        //开启API请求线程，使用带参数的Runnable生成,传入handler进行线程间的响应，传入map获得参数
        new sendValueThread(this.map_parameters,mHandler).start();

        //主线程等待消息响应完成，失去效率
        while ( this.completed != MSG_SUCCESS ){
            if( this.completed == MSG_FAILURE ){
                Log.w("warning","消息请求返回失败，查看详细情况");
                return null;
            }
            continue;
        }
        //消息成功返回，返回对象
        return this.json_return;

    }

    private class Message_Receiver extends Thread{
        @Override
        public void run() {
            //建立消息循环的步骤
            Looper.prepare();//1、初始化Looper
            mHandler = new Handler(){//2、绑定handler到CustomThread实例的Looper对象
                public void handleMessage (Message msg) {//3、定义处理消息的方法
                    switch(msg.what) {
                        case MSG_SUCCESS:
                            //使用fastjson进行json字符串与json对象的相互转换
                            String res = (String) msg.obj;
                            json_return = JSONObject.parseObject(res);
                            //map_return.put("response",(String) msg.obj);
                            completed = MSG_SUCCESS ;//设置消息响应为完成
                            //结束线程循环
                            this.getLooper().quit();
                            break;
                        case MSG_FAILURE:
                            json_return = null;//设置
                            completed = MSG_FAILURE;//设置消息响应为失败
                            //结束线程循环
                            this.getLooper().quit();
                            break;
                    }
                }
            };
            Looper.loop();//4、启动消息循环
        }
    }
}
