package com.example.dongxing.cameraplayer;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DownloadFtp implements Runnable {

    String HOSTNAME = "155.230.118.51";
    Integer PORT = 21;
    String USERNAME = "admin";
    String PASSWORD = "#media2018#";
    Bitmap editpic;
    FTPClient ftpClient;
    String fileName, Lastvalue= "20190228105938353.png";
    String localPath = "/data/data/com.example.dongxing.cameraplayer/cache";
    Handler handler;
    FTPFile[] files;
    public DownloadFtp(Handler handler){

        this.handler = handler;

    }

    @Override
    public void run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        try{
            while(true){
             ftpClient = new FTPClient();
            ftpClient.connect(HOSTNAME, PORT);
            ftpClient.login(USERNAME, PASSWORD);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory("/HDD1/CollabApp/editdata/clientA");

            Log.d("DownloadFtp", "stopThread = " + GolbalVar.stopThread);

                try {
                    files = ftpClient.listFiles();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fileName=getFileName(files);
                Log.d("DownloadFtp", "filename = " + getFileName(files));
                if(!fileName.equals(Lastvalue) ) {
                    Log.d("DownloadFtp1", "if is run  " );
                    Lastvalue = fileName;

                    File localFile = new File(localPath + File.separatorChar + fileName);
                    OutputStream os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(fileName, os);
//          start send msg
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = fileName;
                    handler.sendMessage(msg);
//            end send msg
                    os.close();

                }

                try {
                    destroyObject(ftpClient);
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }





        }catch (IOException e) {
            Log.e("DownloadFtp", "Exception: "+Log.getStackTraceString(e));
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

    public String getFileName( FTPFile[] files){
        String Filename="null.png";
        if( files!=null&&files.length!=0){

            Filename = files[files.length-1].getName();

        }


        return Filename;
    }
}
