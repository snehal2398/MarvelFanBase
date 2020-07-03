package com.snehal2398.marvel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;



public class FeedWeb extends AppCompatActivity {
    WebView webView;
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_web);
       progress=(ProgressBar)findViewById(R.id.progress);
       webView =(WebView)findViewById(R.id.feedwebview);
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
      Bundle b=getIntent().getExtras();
       String id=b.getString("id");
        webView.loadUrl(id);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new Browser());
        webView.setWebChromeClient(new MyWebClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            progress.setVisibility(View.VISIBLE);
            setTitle("Loading...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.GONE);
                setTitle(view.getTitle());
            }
        });
    }
    class Browser
            extends WebViewClient
    {
        Browser() {}

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
        {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }

    public class MyWebClient
            extends WebChromeClient
    {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyWebClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (FeedWeb.this == null) {
                return null;
            }
            return BitmapFactory.decodeResource(FeedWeb.this.getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)FeedWeb.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            FeedWeb.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            FeedWeb.this.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = FeedWeb.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = FeedWeb.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)FeedWeb.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            FeedWeb.this.getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }
}
