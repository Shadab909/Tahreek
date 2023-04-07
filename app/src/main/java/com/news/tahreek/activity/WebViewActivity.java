package com.news.tahreek.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.news.tahreek.R;
import com.news.tahreek.utils.AdBlocker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {
    String myUrl;
    WebView mWebView;
    ImageButton back_btn , share_btn;
    SwipeRefreshLayout next_swipeContainer;
    LinearProgressIndicator myNextLPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Tahreek);
        setContentView(R.layout.activity_web_view);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_cancel_24);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D91604")));


        mWebView = findViewById(R.id.next_webView);
        back_btn = findViewById(R.id.back_btn);
        share_btn = findViewById(R.id.share_btn);
        next_swipeContainer = findViewById(R.id.next_swipeContainer);
        myNextLPI = findViewById(R.id.myNextLPI);
        next_swipeContainer.setOnRefreshListener(() -> mWebView.reload());
        myUrl = getIntent().getStringExtra("url");
        Objects.requireNonNull(getSupportActionBar()).setTitle(myUrl);
        AdBlocker.init(this);
//        darkLightMode();
        loadMyUrl(myUrl);
        goBack();
        share();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadMyUrl(String myUrl){
        mWebView.setWebViewClient(new myNextWebClient());
        mWebView.setWebChromeClient( new MyNextWebChromeClient());
        mWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        WebSettings webSettings=mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        String commonAgents = "Chrome/97.0.4692.87 Mozilla/95.2.0 Mobile Safari/15.2";
        webSettings.setUserAgentString(commonAgents);
        webSettings.setBuiltInZoomControls(true);
        mWebView.loadUrl(myUrl);
    }

    class myNextWebClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view,url,favicon);

        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){

            if(url.contains("youtube.com") || url.contains("play.google.com") || url.contains("google.com/maps")
                    || url.contains("facebook.com") || url.contains("twitter.com") || url.contains("instagram.com")){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            else if(url.startsWith("tel:")){
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                return true;
            }
            Objects.requireNonNull(getSupportActionBar()).setTitle(url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            myNextLPI.setVisibility(View.GONE);
            next_swipeContainer.setRefreshing(false);
        }
        @SuppressLint("WebViewClientOnReceivedSslError")
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

        private Map<String, Boolean> loadedUrls = new HashMap<>();
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }
            return ad ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);
        }

    }

    private void goBack(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mWebView.canGoBack()){
                    mWebView.goBack();
                }else{
                    Toast.makeText(WebViewActivity.this, "press back button to go back", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MyNextWebChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyNextWebChromeClient() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        public void onProgressChanged(WebView view, int newProgress) {
            myNextLPI.setVisibility(View.VISIBLE);
            myNextLPI.setProgress(newProgress);
        }
    }

    final void share(){
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, " Posted By ... : "+mWebView.getUrl());
                startActivity(Intent.createChooser(sharingIntent, "Share"));
            }
        });

    }

    @Override
    public void onBackPressed(){
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            myNextLPI.setVisibility(View.GONE);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWebView.destroy();
    }


    private void darkLightMode(){
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(mWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
            }
        }
    }
}