package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import util.ByteUtil;

/**
 * Created by Administrator on 2018/5/31.
 */
public class SerialPortUtil {

    private static String TAG = "SerialPortUtil";
    /**
     * 标记当前串口状态(true:打开,false:关闭)
     **/
    public static boolean isFlagSerial1 = false;
    public static SerialPort serialPort = null;
    public static InputStream inputStream = null;
    public static OutputStream outputStream = null;
    public static Thread receiveThread = null;
    public static SerialCallBack serialCallBack;
    private static int code;

    public static void setSerialCallBack(SerialCallBack callBack) {
        serialCallBack = callBack;
    }

    /**
     * 打开串口
     *
     * @param device   串口设备文件
     * @param baudrate 波特率，一般是9600
     */
    public static boolean open(String device, int baudrate) {
        if (isFlagSerial1) {
            return isFlagSerial1;
        }
        try {
            serialPort = new SerialPort(new File(device), baudrate, 8);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            receiveCopy();
            isFlagSerial1 = true;
        } catch (IOException e) {
            e.printStackTrace();
            isFlagSerial1 = false;
        }
        return isFlagSerial1;
    }

    /**
     * 关闭串口
     */
    public static boolean close() {
        boolean isClose = false;
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            isClose = true;
            isFlagSerial1 = false;//关闭串口时，连接状态标记为false
        } catch (IOException e) {
            e.printStackTrace();
            isClose = false;
        }
        return isClose;
    }

    /**
     * 发送16进制串口指令
     */
    public static void sendString(int codes, String data) {
        code = codes;
        if (!isFlagSerial1) {
            return;
        }
        try {
            outputStream.write(ByteUtil.hex2byte(data));
            outputStream.flush();
            if (serialCallBack != null) {
                serialCallBack.onSendData(code, data);
            }
            Log.e(TAG, "App--->串口:" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收串口数据的方法
     */
    public static void receiveCopy() {
        if (receiveThread != null && !isFlagSerial1) {
            return;
        }
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (isFlagSerial1) {
                    try {
                        byte[] readData = new byte[1024];
                        if (inputStream == null) {
                            return;
                        }
                        int size = inputStream.read(readData);
                        if (size > 0 && isFlagSerial1) {
                            try {
                                String receStr = ByteUtil.byteToStr(readData, size);//拼接好的数据转字串
                                Log.e(TAG, "原始数据:" + receStr);
                                if (serialCallBack != null) {
                                    serialCallBack.onReadData(code, receStr);
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                Log.e(TAG, "串口数据读取异常：" + e.toString());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receiveThread.start();
    }

    /**
     * 串口数据回调
     */
    public interface SerialCallBack {
        void onSendData(int code, String sendData);

        void onReadData(int code, String readData);
    }
}