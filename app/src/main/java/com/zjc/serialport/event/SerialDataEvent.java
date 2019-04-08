package com.zjc.serialport.event;

/**
 * author cowards
 * created on 2019\3\6 0006
 **/
public class SerialDataEvent {
    public int code;    //1：发送，2:接收
    public String serialPortData;

    public SerialDataEvent(int code, String serialPortData) {
        this.code = code;
        this.serialPortData = serialPortData;
    }
}