package com.defensadelatierra.miniinvaders;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
    private WebView webView;
    private FrameLayout splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#090e1c"));

        webView = new WebView(this);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                hideSplash();
            }
        });
        webView.loadUrl("file:///android_asset/index.html");
        root.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // Splash overlay
        splash = new FrameLayout(this);
        splash.setBackgroundColor(Color.parseColor("#090e1c"));
        ImageView logo = new ImageView(this);
        logo.setImageResource(getResources().getIdentifier("ic_launcher", "drawable", getPackageName()));
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(240, 240);
        lp.gravity = android.view.Gravity.CENTER;
        splash.addView(logo, lp);

        ProgressBar pb = new ProgressBar(this);
        FrameLayout.LayoutParams plp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        plp.gravity = android.view.Gravity.CENTER | android.view.Gravity.BOTTOM;
        plp.bottomMargin = 120;
        splash.addView(pb, plp);

        root.addView(splash, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        setContentView(root);

        // Fallback hide splash
        new Handler(Looper.getMainLooper()).postDelayed(this::hideSplash, 2500);
    }

    private void hideSplash() {
        if (splash != null && splash.getVisibility() == View.VISIBLE) {
            splash.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                splash.setVisibility(View.GONE);
            }).start();
        }
    }

    private void hideSystemUI() {
        View decor = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decor.setSystemUiVisibility(flags);
        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decor.setSystemUiVisibility(flags);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        webView.evaluateJavascript("if(typeof stopMusic==='function')stopMusic();", null);
        webView.pauseTimers();
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
    }

    @Override
    protected void onStop() {
        webView.evaluateJavascript("if(typeof stopMusic==='function')stopMusic();", null);
        webView.pauseTimers();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        webView.evaluateJavascript("if(typeof stopMusic==='function')stopMusic();", null);
        webView.destroy();
        super.onDestroy();
    }
}