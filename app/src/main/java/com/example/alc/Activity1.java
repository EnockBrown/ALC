package com.example.alc;

import android.app.ActionBar;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Activity1 extends AppCompatActivity {

    private WebView webView;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        toolbar();




        webView=findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        WebSettings w =webView.getSettings();
        w.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();//to ignore ssl certificate errors
            }
        });
         webView.loadUrl("https://andela.com/alc/");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void toolbar() {
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("About ALC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //  Tools.setSystemBarColor(this, R.color.colorPrimary);
    }
}
