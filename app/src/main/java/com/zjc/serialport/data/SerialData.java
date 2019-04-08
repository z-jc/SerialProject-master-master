package com.zjc.serialport.data;

import android.util.Log;

import com.zjc.serialport.event.SerialDataEvent;

import org.greenrobot.eventbus.EventBus;

import android_serialport_api.SerialPortUtil;

/**
 * author cowards
 * created on 2018\10\17 0017
 **/
public class SerialData implements SerialPortUtil.SerialCallBack {

    private String TAG = getClass().getSimpleName();
    public static int MAIN_CODE = 1;

    public SerialData() {
        SerialPortUtil.setSerialCallBack(this);
    }

    /**
     * 发送成功
     *
     * @param code
     * @param serialPortData
     */
    @Override
    public void onSendData(int code, String serialPortData) {
        Log.e(TAG, "onSendData:" + serialPortData);
        EventBus.getDefault().post(new SerialDataEvent(1, serialPortData));
    }

    /**
     * 接收成功
     *
     * @param code
     * @param serialPortData
     */
    @Override
    public void onReadData(int code, String serialPortData) {
        Log.e(TAG, "onReadData:" + serialPortData);
        EventBus.getDefault().post(new SerialDataEvent(2, serialPortData));
    }
}