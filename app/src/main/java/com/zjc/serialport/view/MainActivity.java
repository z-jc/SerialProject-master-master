package com.zjc.serialport.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zjc.serialport.R;
import com.zjc.serialport.event.SerialDataEvent;
import com.zjc.serialport.manager.SerialManager;
import com.zjc.serialport.util.DateUtil;
import com.zjc.serialport.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button mBtnOpen;
    private Button mBtnSend;
    private Button mBtnSet;
    private Button mBtnClear;
    private TextView mTvReadByteLength;
    private TextView mTvSendByteLength;
    private TextView mTvData;
    private EditText mEdSendData;
    private ScrollView mScrollView;

    private StringBuilder stringBuilder = new StringBuilder();
    private int sendByteLength = 0;//发送字节长度
    private int readByteLength = 0;//接收字节长度
    private boolean isSerialOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSet = (Button) findViewById(R.id.btn_set);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mTvReadByteLength = (TextView) findViewById(R.id.tv_read_byte_length);
        mTvSendByteLength = (TextView) findViewById(R.id.tv_send_byte_length);
        mTvData = (TextView) findViewById(R.id.tv_data);
        mScrollView = (ScrollView) findViewById(R.id.sv_data);
        mEdSendData = (EditText) findViewById(R.id.ed_send_data);

        mBtnOpen.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
        mBtnSet.setOnClickListener(this);
        mTvReadByteLength.setOnClickListener(this);
        mTvSendByteLength.setOnClickListener(this);
        mEdSendData.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open://打开串口
                if (isSerialOpen) {//false关闭,true打开
                    SerialManager.release();
                    mBtnOpen.setText("打开串口");
                    isSerialOpen = false;
                } else {
                    boolean isSerial = SerialManager.getInstance().open();
                    if (isSerial) {//打开成功
                        mBtnOpen.setText("关闭串口");
                        isSerialOpen = true;
                    } else {//打开失败
                        mBtnOpen.setText("打开串口");
                        isSerialOpen = true;
                        ToastUtil.showShortToast(MainActivity.this, "打开串口异常");
                    }
                }
                break;
            case R.id.btn_send://发送数据
                if (isSerialOpen) {
                    String data = mEdSendData.getText().toString().trim().replaceAll(" ", "");
                    SerialManager.sendSerialData(1, data);
                }
                break;
            case R.id.btn_set:
                sendByteLength = 0;
                readByteLength = 0;
                stringBuilder.delete(0, stringBuilder.length());
                mTvSendByteLength.setText("已发送(字节):0");
                mTvReadByteLength.setText("已接收(字节):0");
                SerialManager.release();
                startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                break;
            case R.id.btn_clear:
                sendByteLength = 0;
                readByteLength = 0;
                stringBuilder.delete(0, stringBuilder.length());
                mTvData.setText("");
                mTvSendByteLength.setText("已发送(字节):0");
                mTvReadByteLength.setText("已接收(字节):0");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSerialData(SerialDataEvent dataEvent) {
        String data = dataEvent.serialPortData;
        if (dataEvent.serialPortData.length() % 2 != 0) {
            data = "0" + data;
        }
        switch (dataEvent.code) {
            case 1://发送
                sendByteLength = sendByteLength + data.length() / 2;
                stringBuilder.append(DateUtil.getDate(DateUtil.ymdhms) + "\t\tSend:" + data + "\n");
                break;
            case 2://接收
                readByteLength = readByteLength + data.length() / 2;
                stringBuilder.append(DateUtil.getDate(DateUtil.ymdhms) + "\t\tRead:" + data + "\n");
                break;
        }
        mTvData.setText(stringBuilder.toString());
        mTvSendByteLength.setText("已发送(字节):" + sendByteLength);
        mTvReadByteLength.setText("已接收(字节):" + readByteLength);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                break;
            case KeyEvent.KEYCODE_HOME:
                this.finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendByteLength = 0;
        readByteLength = 0;
        stringBuilder.delete(0, stringBuilder.length());
        EventBus.getDefault().unregister(this);
        SerialManager.release();
    }
}