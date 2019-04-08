package com.zjc.serialport.bean;

/**
 * author cowards
 * created on 2018\12\6 0006
 **/
public class SerialBean extends BaseBean{
    public int code;
    public String value;

    public SerialBean(int code, String value) {
        this.code = code;
        this.value = value;
    }
}