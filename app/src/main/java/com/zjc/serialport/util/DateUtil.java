package com.zjc.serialport.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/6/7.
 */

public class DateUtil {

    public static String y_m_d_h_m_s = "yyyy_MM_dd_HH_mm_ss";
    public static String ymdhms = "yyyy:MM:dd HH:mm:ss";
    public static String hms = "HH:mm:ss";
    public static String hm = "HH:mm";

    /**
     * 获取当前时间
     *
     * @param type
     * @return
     */
    public static String getDate(String type) {
        SimpleDateFormat formatter = new SimpleDateFormat(type);
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 获取当前时间毫秒值
     */
    public static String getTime() {
        return String.valueOf(System.currentTimeMillis());
    }

}