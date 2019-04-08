package com.zjc.serialport.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.zjc.serialport.R;
import com.zjc.serialport.util.SharedPreferencesUtil;

public class ConfigActivity extends Activity implements View.OnClickListener {

    private Button mBtnDevice;
    private Button mBtnBaud;
    private Button mBtnSave;

    private AlertDialog alertDialog;

    final String[] itemDevice = {"/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3", "/dev/ttyS4"};
    final String[] itemBaud = {"9600", "19200", "38400", "57600", "115200"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        initView();
    }

    private void initView() {
        mBtnDevice = (Button) findViewById(R.id.btn_device);
        mBtnBaud = (Button) findViewById(R.id.btn_baud);
        mBtnSave = (Button) findViewById(R.id.btn_save);

        mBtnDevice.setOnClickListener(this);
        mBtnBaud.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_device:
                showList(itemDevice, 1);
                break;
            case R.id.btn_baud:
                showList(itemBaud, 2);
                break;
            case R.id.btn_save:
                finish();
                break;
        }
    }

    public void showList(final String[] items, final int code) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("请选择以下配置信息");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                switch (code) {
                    case 1://设备名
                        SharedPreferencesUtil.getInstance(ConfigActivity.this).setString("serialName", items[index]);
                        mBtnDevice.setText(items[index]);
                        break;
                    case 2://波特率
                        SharedPreferencesUtil.getInstance(ConfigActivity.this).setString("serialPort", items[index]);
                        mBtnBaud.setText(items[index]);
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = alertBuilder.create();
        alertDialog.show();
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

}