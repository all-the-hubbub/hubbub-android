package com.hubbub.hubbub;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sgoldblatt on 5/14/17.
 */

public class BeaconViewActivity extends BaseActivity {
    private static final String TAG = "BeaconViewActivity";

    WebView webview;
    String slotId;
    String topicId;
    String topicName;

    private String getUrlString(){
        String staticString = getString(R.string.beacon_url);
        Uri uri = Uri.parse(staticString)
                .buildUpon()
                .appendQueryParameter("slotId", slotId)
                .appendQueryParameter("topicId", topicId)
                .appendQueryParameter("topicName", topicName)
                .build();
        return uri.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        slotId = getIntent().getStringExtra("slotId");
        topicId = getIntent().getStringExtra("topicId");
        topicName= getIntent().getStringExtra("topicName");

        webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient() {
            // TODO: more modern to use WebRequest.
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                Log.d(TAG, url);
                return true;
            }
        });

        Log.d(TAG, getUrlString());
        webview.loadUrl(getUrlString());
    }
}
