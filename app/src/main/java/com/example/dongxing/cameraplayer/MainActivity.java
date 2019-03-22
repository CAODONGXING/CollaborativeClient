package com.example.dongxing.cameraplayer;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    FrameLayout Container;
    DrawView drawview;
    Handler handler;
    ImageView get_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Container = (FrameLayout) findViewById(R.id.Container);
        DisplayMetrics dispalyMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dispalyMetrics);
        get_pic = (ImageView)findViewById(R.id.get_pic);
        handler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what) {
                    case 1:
                        String filename = (String) msg.obj;
                        Log.d("MainActivity","filename in hander ="+filename);
                        get_pic.setBackground(ScrToBitmapDrawable(filename));
                        break;
                    default:
                        break;
                }
            }

        };
        drawview = new DrawView(this, dispalyMetrics.widthPixels, dispalyMetrics.heightPixels, handler);
//----start enter ip
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("please enter ip");    //设置对话框标题
        builder.setIcon(android.R.drawable.btn_star);   //设置对话框标题前的图标
        final EditText edit = new EditText(this);
        edit.setText("192.168.0.");
        builder.setView(edit);
        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String ip = edit.getText().toString();
                WebView webview = (WebView) findViewById(R.id.web_view);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.setWebViewClient(new WebViewClient());
//        webview.loadUrl("http://192.168.43.1:8080");
                webview.loadUrl("http://"+ip+":8080");
                Container.addView(drawview);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();

//----end enter ip

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GolbalVar.stopThread = true ;

    }

    public BitmapDrawable ScrToBitmapDrawable(String filename){
        FileInputStream fs1 = null;
        try {
            fs1 = new FileInputStream("/data/data/com.example.dongxing.cameraplayer/cache/"+filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmapfromMain1  = BitmapFactory.decodeStream(fs1);
        BitmapDrawable bd1=new BitmapDrawable(bitmapfromMain1);
        return bd1;
    }
}
