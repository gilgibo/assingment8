package com.example.gibo.test8;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import java.net.URL;
import java.util.ArrayList;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    EditText et;
    WebView webView;
    ListView listView;
    ProgressDialog dialog;
    Animation ani;
    LinearLayout line;
    ArrayList<DataInfo> info = new ArrayList<DataInfo>();
    ArrayList<String> data = new ArrayList<String>();
    ArrayList<String> urldata = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText)findViewById(R.id.et);
        webView = (WebView)findViewById(R.id.webview);
        line = (LinearLayout)findViewById(R.id.line);


        listView = (ListView)findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(info.get(position).GetUrl());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("즐겨찾기 삭제").setPositiveButton("삭제 하시겠습니까?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.remove(position);
                        info.remove(position);
                        urldata.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }).show();
                return false;
            }
        });


        webView.addJavascriptInterface(new JavaScriptMethods(), "MyApp");
        dialog = new ProgressDialog(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);


        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                et.setText(url);
            }

        });


        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if( newProgress >= 100) dialog.dismiss();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }

        });


        ani = AnimationUtils.loadAnimation(this, R.anim.translate_top);
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                line.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,"즐겨찾기추가");
        menu.add(0,2,0,"즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1){
            webView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);

            webView.loadUrl("file:///android_asset/wwww/urladd.html");
            line.setAnimation(ani);
            ani.start();
        }
        if(item.getItemId() == 2){
            webView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }


    Handler myhandler = new Handler();


    class JavaScriptMethods {


        @JavascriptInterface
        public boolean judge(String url){
            if(urldata.contains(url))
                return false;
            else
                return true;
        }

        @JavascriptInterface
        public void addinfo(final String name, final String url ){
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    if( info.size() == 0 || !urldata.contains(url)) {
                        info.add(new DataInfo(name, url));
                        urldata.add(url);
                        data.add("사이트 명 : " + name  + "     URL : " +  url);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @JavascriptInterface
        public void urlvis(){
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    line.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void onClick(View v){
        if(v.getId() == R.id.bt){
            webView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            webView.loadUrl(et.getText().toString());
        }
    }

}
