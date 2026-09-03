package com.flat.trustnoone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Thin offline shell. The entire game is app/src/main/assets/index.html.
 * There is no network code here and no INTERNET permission in the manifest.
 */
public class MainActivity extends Activity {

    private WebView web;
    private long lastBackPress = 0L;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pass-the-phone games should not sleep mid-round.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // used only for prefs and saved names
        s.setMediaPlaybackRequiresUserGesture(false); // Web Audio click sounds
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        s.setTextZoom(100);                    // ignore system font scaling; layout is fixed

        web.setWebViewClient(new WebViewClient());
        web.setBackgroundColor(0xFF07060A);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.setLongClickable(false);
        web.setHapticFeedbackEnabled(false);

        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    /**
     * Hardware back: first press hands control to the game (window.__androidBack
     * returns to the main menu). If the game is already at the menu it returns
     * false, and a second press within two seconds exits.
     */
    @Override
    public void onBackPressed() {
        web.evaluateJavascript(
            "(function(){try{return window.__androidBack?window.__androidBack():false;}catch(e){return false;}})()",
            new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if ("true".equals(value)) return;   // the game handled it
                    long now = System.currentTimeMillis();
                    if (now - lastBackPress < 2000) {
                        finish();
                    } else {
                        lastBackPress = now;
                        Toast.makeText(MainActivity.this, R.string.exit_hint, Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override protected void onPause()  { super.onPause();  if (web != null) web.onPause(); }
    @Override protected void onResume() { super.onResume(); if (web != null) web.onResume(); }
    @Override protected void onDestroy() {
        if (web != null) { web.destroy(); web = null; }
        super.onDestroy();
    }
}
