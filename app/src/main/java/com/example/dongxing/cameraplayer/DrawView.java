package com.example.dongxing.cameraplayer;

/**
 * Created by Dongxing on 2019-01-09.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

public class DrawView extends View {

    float preX;
    float preY;
    private Path path;
    public Paint paint = null;
    static Bitmap cacheBitmap = null;
    Canvas cacheCanvas = null;
    private Timer timer;
    private static UploadFtp uploadftp ;
    private static DownloadFtp downloadftp ;
//    private Handler handler;
//    private static Thread uploadftpThread = new Thread(uploadftp);
    public DrawView(Context context, int width, int height, Handler handler) {
        super(context);
//        this.handler=handler;
        uploadftp=new UploadFtp();
        downloadftp = new DownloadFtp(handler);
        cacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        cacheCanvas=new Canvas();
        path=new Path();
        cacheCanvas.setBitmap(cacheBitmap);
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        paint.setDither(true);
//        优化路径平滑
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);


    }
    public  static Bitmap getcacheBitmap(){
        return cacheBitmap;
    }
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                invalidate();
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(preX - x) < 3 && Math.abs(preY - y) < 3) {
                } else {
                    path.quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2);
                    preX = x ;
                    preY = y ;
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
               cacheCanvas.drawPath(path, paint);
//                cacheCanvas.drawBitmap(cacheBitmap,0,0,paint);
//               path.reset();
                Thread uploadftpThread = new Thread(uploadftp);
                if (!uploadftpThread.isAlive()) uploadftpThread.start();

                Thread downloadftpThread = new Thread(downloadftp);
                if (!downloadftpThread.isAlive()) downloadftpThread.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        saveBitmapFile(cacheBitmap,"/data/data/com.example.dongxing.cameraplayer/cache/send_image.png");
                        Log.d("DrawView", "保存图片成功");
                    }
                }).start();

                break;

        }
        invalidate();

        return true;

    }


    public void onDraw(Canvas canvas) {
        Log.d("DrawView", "DrawView=Run");
//        Paint bmpPaint =new Paint();
        canvas.drawPath(path,paint);
//        canvas.drawBitmap(cacheBitmap,0,0,paint);

//        File outputImage =new File("/data/data/com.example.testlocalnetwork/cache/send_image.png");
//        try {
//            if(outputImage.exists()){
//
//            }
//            else outputImage.createNewFile();
//
//            cacheBitmap2=cacheBitmap;
//
//        }catch (IOException e){
//            e.printStackTrace();
//
//        }




    }

    public static File saveBitmapFile(Bitmap bitmap, String filepath){

        File file=new File(filepath);//将要保存图片的路径
        try {
            if(file.exists()){

            }else file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            Log.d("DrawView", "bos="+bos.toString());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
//Log.d("DrawView", "bos="+bos.toString());
}


