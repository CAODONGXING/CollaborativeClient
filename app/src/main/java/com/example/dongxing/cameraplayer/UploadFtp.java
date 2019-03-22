package com.example.dongxing.cameraplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dongxing on 2019-01-22.
 */

public class UploadFtp  implements Runnable{

    String HOSTNAME = "155.230.118.51";
    Integer PORT = 21;
    //     String FilePackage = "F:\\Programming_Package\\IMG_3733_1.JPG";
    String USERNAME = "admin";
    String PASSWORD = "#media2018#";
    FormatTools Tools;
    Bitmap frame;
    FTPClient ftpClient;

    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        //创建一个FtpClient对象
        Tools=FormatTools.getInstance();
        SimpleDateFormat timesdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        ftpClient = new FTPClient();
        //创建ftp连接。默认是21端口
        try {
            Boolean status;
            ftpClient.connect(HOSTNAME, PORT);
            //登录ftp服务器，使用用户名和密码
            status= ftpClient.login(USERNAME, PASSWORD);
            //上传文件。
            //读取本地文件
//            Drawable dar= ResourcesCompat.getDrawable(getResources(),R.drawable.lena,null);
//            frame = MainActivity.getJpegFrame();
//            Log.e("MainActivity", "frame="+frame.toString());

//            while(true) {
                frame = geteditfromloccal();
                if(frame!=null) {
                    InputStream inputStream = Tools.Bitmap2InputStream(frame,100);
                    //设置上传的路径
                    status = ftpClient.changeWorkingDirectory("/HDD1/CollabApp/editdata/clientA");
                    ftpClient.enterLocalPassiveMode();

                    //修改上传文件的格式
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    //第一个参数：服务器端文档名
                    //第二个参数：上传文档的inputStream
                    String FileTime = timesdf.format(new Date()).toString();//获取系统时间

                    Log.e("MainActivity", "filename=" + FileTime);
                    status = ftpClient.storeFile(FileTime + ".png", inputStream);
                }
//            }

            try {
                //关闭连接
                destroyObject(ftpClient);
            } catch (Exception e) {
                e.printStackTrace();
            }



//            Log.e("MainActivity", "No Exception");
        } catch (IOException e) {
            Log.e("MainActivity", "Exception: "+Log.getStackTraceString(e));
        }

    }

    public void destroyObject(FTPClient ftpClient) throws Exception {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            // 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
            try {
                ftpClient.disconnect();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    public Bitmap geteditfromloccal(){
        Bitmap editpic = null;
        try {
            FileInputStream fs = new FileInputStream("/data/data/com.example.dongxing.cameraplayer/cache/send_image.png");
            editpic  = BitmapFactory.decodeStream(fs);

        }catch (Exception e){
            Log.d("UploadFtp", "error to get local edit");
        }
        return editpic;
    };
}
