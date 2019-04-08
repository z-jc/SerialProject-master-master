package com.zjc.serialport.manager;

import android.os.SystemClock;
import android.util.Log;
import com.zjc.serialport.app.SerialApp;
import com.zjc.serialport.data.SerialData;
import com.zjc.serialport.util.SharedPreferencesUtil;
import com.zjc.serialport.bean.SerialBean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android_serialport_api.SerialPortUtil;

/**
 * author cowards
 * created on 2018\10\17 0017
 **/
public class SerialManager {

    private static String TAG = "SerialManager2";
    private static volatile boolean isSerialFlag = false;
    private static volatile SerialManager instance = null;
    private static BlockingQueue<SerialBean> queue = new LinkedBlockingQueue<>();           //发送的数据放入队列中,防止并发
    private static boolean isConsumer2 = false;

    /**
     * 波特率,9600...
     *
     * @return
     */
    public static int getBaud() {
        return Integer.parseInt(SharedPreferencesUtil.getInstance(SerialApp.mApplicationContext).getString("serialPort"));
    }

    /**
     * 串口设备,ttyS0,ttyS1...
     *
     * @return
     */
    public static String getSerialDeviceName() {
        return SharedPreferencesUtil.getInstance(SerialApp.mApplicationContext).getString("serialName");
    }

    private SerialManager() {
    }

    public boolean open() {
        if (queue == null) {
            queue = new LinkedBlockingQueue<>();
        }
        Log.e(TAG, "当前打开的串口是:" + getSerialDeviceName() + ",波特率:" + getBaud());
        boolean isSerial = SerialPortUtil.open(getSerialDeviceName(), getBaud());
        if (isSerial) {
            isSerialFlag = true;
            new SerialData();
            isConsumer2 = true;
            ThreadPoolManager.getSingleInstance().execute(new SerialConsumer());
            Log.e(TAG, "串口打开成功");
        } else {
            isSerialFlag = false;
            Log.e(TAG, "串口打开失败");
        }
        return isSerialFlag;
    }

    /**
     * 单一实例
     */
    public static SerialManager getInstance() {
        if (instance == null) {
            synchronized (SerialManager.class) {
                if (instance == null) {
                    instance = new SerialManager();
                }
            }
        }
        return instance;
    }

    /**
     * 发送数据到串口
     */
    public static void sendSerialData(int code, String data) {
        try {
            if (queue != null) {
                queue.put(new SerialBean(code, data));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "数据添加过多：" + e.toString());
        }
    }

    /**
     * 队列中取出数据发送到串口
     */
    class SerialConsumer implements Runnable {
        @Override
        public void run() {
            while (isConsumer2) {
                if (!queue.isEmpty()) {
                    try {
                        SerialBean bean = queue.take();
                        sendSerialData(bean.code, bean.value);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SystemClock.sleep(300);
            }
        }

        private void sendSerialData(int code, String data) {
            SerialPortUtil.sendString(code, data);
        }
    }

    /**
     * 关闭串口
     */
    public static void close() {
        isSerialFlag = false;
        if (queue != null) {
            queue.clear();
            queue = null;
        }
        SerialPortUtil.close();
    }

    /**
     * 释放单例
     */
    public static void release() {
        isConsumer2 = false;
        if (instance != null) {
            close();
            instance = null;
        }
    }
}