package com.example.bongtoo.helper;

import java.util.Calendar;

public class DateTimeHelper {
    // 싱글톤 객체 설정 시작
    private static DateTimeHelper instance = null;

    public static DateTimeHelper getInstance() {
        if(instance == null) instance = new DateTimeHelper();
        return instance;
    }

    public static void freeInstance() {
        instance = null;
    }

    private DateTimeHelper() {}

    // 싱글톤 객체 설정 끝

    // 현재 날짜를 배열로 리턴
    public int[] getDate() {
        Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH) + 1;
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        int[] result = {yy, mm, dd};

        return result;
    }

    public String getStringDate() {
        Calendar calendar = Calendar.getInstance();
        String MM = null;
        String DD = null;
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH) + 1;
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        if (mm < 10) {
            MM = "0" + mm;
        }
        if (dd < 10) {
            DD = "0" + dd;
        }

        return yy +"년 " + MM + "월 " + DD + "일";
    }
}