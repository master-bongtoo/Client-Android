package com.example.bongtoo.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class PhotoHelper {
    private static PhotoHelper instance = null;

    public static PhotoHelper getInstance() {
        if (instance == null) instance = new PhotoHelper();
        return instance;
    }
    public static void freeInstance() {
        instance = null;
    }

    private PhotoHelper() {}

    // 저장할 이미지 파일 이름 만들기
    public String getNewPhotoPath() {
        Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH) + 1;
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        int hh = calendar.get(Calendar.HOUR_OF_DAY);
        int mi = calendar.get(Calendar.MINUTE);
        int ss = calendar.get(Calendar.SECOND);

        String fileName = String.format("p%04d-%02d-%02d.jpg", yy, mm, dd);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if(!dir.exists()) dir.mkdir();

        String photoPath = dir.getAbsolutePath() + "/" + fileName;
        Log.d("[INFO]", "dir ->" + dir);
        return photoPath;
    }
    // 원본 이미지를 스마트폰 화면 크기로 줄이기 및 누운 이미지 세우기
    public Bitmap getThumb(Activity activity, String path) {
        // 1. 줄이기
        //이미지를 저장할 객체
        Bitmap bitmap = null;
        // 스마트폰 화면 해상도 얻기
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);  // displayMetrics에 저장?
        // 스마트폰의 가로, 세로 크기 얻기
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        // 긴 축을 골라내기
        int maxScale = deviceWidth;
        if(deviceWidth < deviceHeight) {
            maxScale = deviceHeight;
        }

        // 2. 세우기
        // bitmap 이미지로더의 옵션을 설정하기 위한 객체
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 이미지를 바로 로드하지 않고 사진 정보만 읽어오라고 설정
        options.inJustDecodeBounds = true;
        // bitmap 파일 읽어오기 - option에 의해서 사진 정보만 읽어들이게 된다.
        BitmapFactory.decodeFile(path, options);
        // 이미지의 가로, 세로 크기 읽기 - maxScale(더 긴 축)만 사용
        int fScale = options.outHeight;
        if(options.outHeight < options.outWidth) fScale = options.outWidth;

        // 3. Image Resizing
        if(maxScale < fScale) { // 이미지 길이가 다른 스마트폰보다 크면, 조절해주고
            // 이미지의 사이즈를 maxScale로 나누어서 샘플링 사이즈 계산
            // ex) 이미지의 긴 축이 2400px 이하일때, maxScale이 800이면 3으로 지정된다.
            // 이 때, 3의 의미는  1/3 크기를 나타낸다.
            int sampleSize = fScale / maxScale;
            // 새 bitmap option 생성
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inSampleSize = sampleSize;
            // 이미지 읽어오기
            bitmap = BitmapFactory.decodeFile(path, options1);
        } else { // 이미지 사이즈가 적당하면 그냥 읽음
            bitmap = BitmapFactory.decodeFile(path);
        }
        // 누운 이미지 세우기
        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL); // NORMAL = Default값
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = rotate(bitmap, exifDegree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /*  누운 이미지 세우기 */
    // exif정보중에서 orientation 값을 정수로 변환
    public int exifOrientationToDegrees(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }

    // 이미지를 회전시키기
    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();    // 이미지 수정 정보 관리 클래스
            matrix.setRotate(degrees, (float)bitmap.getWidth()/2, (float)bitmap.getHeight());
            Bitmap convertedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if(bitmap != convertedBitmap) {
                bitmap.recycle();
                bitmap = convertedBitmap;
            }
        }
        return bitmap;
    }

}