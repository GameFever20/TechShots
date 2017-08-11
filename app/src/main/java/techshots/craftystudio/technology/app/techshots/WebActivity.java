package techshots.craftystudio.technology.app.techshots;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        String url =getIntent().getStringExtra("newsUrl");

        WebView webView =(WebView)findViewById(R.id.webActivity_webview);
        webView.loadUrl(url);

    }
}
