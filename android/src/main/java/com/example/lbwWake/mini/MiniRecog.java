package com.example.lbwWake.mini;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.lbwWake.BaiduConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class MiniRecog implements EventListener {
    private EventManager asr;
    public Context context;
    private final MethodChannel channel;
    protected BaiduConfig config;
    public MiniRecog(Context ct, MethodChannel listener, BaiduConfig c){
        context = ct;
        this.config = c;
        this.channel = listener;
        asr = EventManagerFactory.create(context, "asr");
        asr.registerListener(this);

    }
    public void start(){
        Map<String, Object> params = new HashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START;
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.APP_KEY, config.getAppKey());
        params.put(SpeechConstant.APP_ID, config.getAppId());
        params.put(SpeechConstant.SECRET, config.getSecretKey());
        String json = new JSONObject(params).toString();
        asr.send(event, json, null, 0, 0);
    }
    public void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    public void pause() {
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params == null || params.isEmpty()) {
                return;
            }
            if (params.contains("\"nlu_result\"")) {
                // 一句话的语义解析结果
                if (length > 0 && data.length > 0) {
                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            } else if (params.contains("\"partial_result\"")) {
                // 一句话的临时识别结果
                logTxt += ", 临时识别结果：" + params;
                try {
                    JSONObject jsonObj = new JSONObject(params);
                    String st = jsonObj.getString("best_result");
                    channel.invokeMethod("temporary", st);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }  else if (params.contains("\"final_result\""))  {
                // 一句话的最终识别结果
                logTxt += ", 最终识别结果：" + params;
                try {
                    JSONObject jsonObj = new JSONObject(params);
                    String st = jsonObj.getString("best_result");
                    channel.invokeMethod("end", st);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }  else {
                // 一般这里不会运行
                logTxt += " ;params :" + params;
                if (data != null) {
                    logTxt += " ;data length=" + data.length;
                }
            }
        }else {
            // 识别开始，结束，音量，音频数据回调
            if (params != null && !params.isEmpty()){
                logTxt += " ;params :" + params;
            }
            if (data != null) {
                logTxt += " ;data length=" + data.length;
            }
        }
    }
}
