package com.hubbub.hubbub;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebViewActivity extends BaseActivity {
    WebView webview;
    OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        client = new OkHttpClient();
        webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            // TODO: more modern to use WebRequest.
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.contains(getString(R.string.github_static_host) + "/?code=")) {
                    String githubCode = Uri.parse(url).getQueryParameter("code");
                    requestAccessTokenAndSetResult(githubCode);
                    return true;
                }
                return false;
            }
        });

        webview.loadUrl(createAuthUrl());
    }

    // TODO (this is def a function that exists already)
    private String createAuthUrl() {
        String githubAuth = getString(R.string.github_authorize_html);
        String clientId = getString(R.string.github_id);
        String redirectUrl = getString(R.string.github_static_host);

        return githubAuth + "?" + "client_id=" + clientId + "&redirect_uri=" + redirectUrl;
    }

    private void run(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    private String makeRequestJson(String githubCode) {
        JSONObject jObject = new JSONObject();
        String clientId = getString(R.string.github_id);
        String clientSecret = getString(R.string.github_secret);

        try {
            jObject.put("client_id", clientId);
            jObject.put("client_secret", clientSecret);
            jObject.put("code", githubCode);
        } catch (JSONException e) {
            // TODO (fix this to not throw an error? Like why even)
            e.printStackTrace();
        }

        return jObject.toString();
    }


    private void requestAccessTokenAndSetResult(String githubCode) {
        String requestUrl = getString(R.string.github_access_token_html);
        String jsonString = makeRequestJson(githubCode);
        run(requestUrl, jsonString, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                setResult(RESULT_CANCELED);
                finish();
            }

            private String getAccessTokenFromString(String responseBodyString) {
                Pattern pattern = Pattern.compile("access_token=(.*?)&.*");
                Matcher matcher = pattern.matcher(responseBodyString);
                if (matcher.find()) {
                    return matcher.group(1);
                } else {
                    return "";
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                Intent data = new Intent();
                try {
                    data.putExtra("access_token", getAccessTokenFromString(response.body().string()));
                    setResult(RESULT_OK, data);
                } catch (IOException e) {
                    Toast.makeText(WebViewActivity.this,
                            "Error: could not fetch response body/ access_token from github.",
                            Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                } finally {
                    response.body().close();
                    finish();
                }
            }
        });
    }
}
