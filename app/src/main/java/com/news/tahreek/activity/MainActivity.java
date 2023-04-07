package com.news.tahreek.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.splashscreen.SplashScreen;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.news.tahreek.R;
import com.news.tahreek.utils.AdBlocker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    LinearLayout lang_layout;
    Button lang_btn;
    WebView myWebView;
    TextView header ;
    LinearLayout website_layout;
    Button website_btn;
    LinearProgressIndicator myLPI;
    SwipeRefreshLayout mySwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Tahreek);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lang_btn = findViewById(R.id.lang_btn);
        lang_layout = findViewById(R.id.lang_layout);
        website_btn = findViewById(R.id.web_btn);
        website_layout = findViewById(R.id.website_layout);
        myWebView = findViewById(R.id.webView);
        header = findViewById(R.id.header);
        myLPI = findViewById(R.id.web_page_progress);
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        header.setText(website_btn.getText().toString());
        mySwipeRefreshLayout.setOnRefreshListener(() -> myWebView.reload());
        AdBlocker.init(this);
//        darkLightMode();
        setWebsite(lang_btn.getText().toString(),website_btn.getText().toString());

        lang_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LanguagePopUpMenu(R.menu.language_popup_menu);
//                setWebsite(lang_btn.getText().toString(),website_btn.getText().toString());
            }
        });

        website_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lang_btn.getText().toString().equals("Hindi")){
                    WebsitePopUpMenu(R.menu.website_popup_menu_hindi);
                }else{
                    WebsitePopUpMenu(R.menu.website_popup_menu_english);
                }
                header.setText(website_btn.getText().toString());
//                setWebsite(lang_btn.getText().toString(),website_btn.getText().toString());
            }
        });


    }

    private void LanguagePopUpMenu(int position){
        PopupMenu popup = new PopupMenu(MainActivity.this, lang_layout);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(position, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                lang_btn.setText(item.getTitle());
                setWebsite(lang_btn.getText().toString(),website_btn.getText().toString());
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    private void WebsitePopUpMenu(int position){
        PopupMenu popup = new PopupMenu(MainActivity.this, website_layout);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(position, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                website_btn.setText(item.getTitle());
                setWebsite(lang_btn.getText().toString(),website_btn.getText().toString());
                header.setText(website_btn.getText().toString());
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    private void setWebsite(String language , String website){
        if (language.equals("Hindi")){
            switch (website) {
                case "Siyasat Daily":
                    loadMyUrl("https://hindi.siasat.com/");
                    break;
                case "NDTV":
                    loadMyUrl("https://ndtv.in/topic/indian-muslim");
                    break;
                case "News 24":
                    loadMyUrl("https://news24online.com/amp/");
                    break;
                case "The Wire":
                    loadMyUrl("https://m.thewirehindi.com/");
                    break;
                case "Quint Hindi":
                    loadMyUrl("https://hindi.thequint.com/");
                    break;
            }
        }else{
            switch (website) {
                case "Muslim Mirror":
                    loadMyUrl("https://muslimmirror.com/eng/");
                    break;
                case "Siyasat Daily":
                    loadMyUrl("https://www.siasat.com/");
                    break;
                case "Milli Gazette":
                    loadMyUrl("https://www.milligazette.com/");
                    break;
                case "The Cognate":
                    loadMyUrl("https://thecognate.com/");
                    break;
                case "NDTV":
                    loadMyUrl("https://www.ndtv.com/topic/muslims-in-india");
                    break;
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadMyUrl(String myUrl){
        myWebView.setWebViewClient(new myWebClient());
        myWebView.setWebChromeClient( new MyWebChromeClient());
        myWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        WebSettings webSettings=myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        String commonAgents = "Chrome/97.0.4692.87 Mozilla/95.2.0 Mobile Safari/15.2";
        webSettings.setUserAgentString(commonAgents);
        webSettings.setBuiltInZoomControls(true);
        myWebView.loadUrl(myUrl);
    }

    @Override
    public void onBackPressed(){
        if (myWebView.canGoBack()) {
            myWebView.goBack();
            myLPI.setVisibility(View.GONE);
        }
        else{
            new AlertDialog.Builder(this) //alert the person knowing they are about to close
                    .setTitle("EXIT")
                    .setMessage("Are you sure. You want to close this app?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyWebChromeClient() {
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
            myLPI.setVisibility(View.VISIBLE);
            myLPI.setProgress(newProgress);
        }
    }

     class myWebClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view,url,favicon);

        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){

            if(url.contains("play.google.com") || url.contains("google.com/maps")
                    || url.contains("facebook.com") || url.contains("twitter.com") || url.contains("instagram.com")){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            else if(url.startsWith("tel:")){
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                return true;
            }
            Intent intent = new Intent(MainActivity.this,WebViewActivity.class);
            intent.putExtra("url",url);
            startActivity(intent);
//            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            myLPI.setVisibility(View.GONE);
            mySwipeRefreshLayout.setRefreshing(false);
        }
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

    private void darkLightMode(){
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(myWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
            }
        }
    }




}