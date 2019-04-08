package android_serialport_api;

import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

    private static final String TAG = "SerialPort";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        if (device.getAbsolutePath().indexOf("ttyMT") != -1) {
            try {
                //通过挂载到linux的方式，修改文件的操作权限
                String root = "su";//  /system/bin/
                String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
                Process su = Runtime.getRuntime().exec(root);
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "串口打开异常" + e.getMessage());
            }
        } else {
            //检查访问权限，如果没有读写权限，进行文件操作，修改文件访问权限
            if (!device.canRead() || !device.canWrite()) {
                Log.e(TAG, "串口没有读写权限，进行申请");
                try {
                    //通过挂载到linux的方式，修改文件的操作权限
                    String root = "/system/bin/su";
                    Process su = Runtime.getRuntime().exec(root);
                    String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
                    su.getOutputStream().write(cmd.getBytes());
                    if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                        throw new SecurityException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "串口打开异常：" + e.getMessage());
                }
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);

        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }

        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    // JNI(调用java本地接口，实现串口的打开和关闭)
    /**
     * @param path     串口设备的绝对路径
     * @param baudrate 波特率
     * @param flags    校验位
     */
    private native static FileDescriptor open(String path, int baudrate, int flags);
    public native void close();

    static {//加载jni下的C文件库
        System.loadLibrary("serial_port");
    }
}