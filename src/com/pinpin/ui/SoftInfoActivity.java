package com.pinpin.ui;

import com.pinpin.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class SoftInfoActivity extends Activity {
private WebView webView;
private TextView titleview;
private Button backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soft_info);             
        init();
        backbtn=(Button)findViewById(R.id.back_btn1);
		 backbtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							  Intent intent = new Intent(SoftInfoActivity.this,AboutActivity.class);
							  startActivity(intent);
							  finish();
						}
					});
    }
    
    private void init(){
        webView = (WebView) findViewById(R.id.webView1);
        titleview = (TextView)findViewById(R.id.title_soft);
        WebSettings webSettings = webView.getSettings();  
        webView.getSettings().getJavaScriptEnabled();
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webSettings.setDefaultTextEncodingName("utf-8");
        //WebView加载web资源
        Log.d("tag","myboy!!!");
        webView.loadUrl("file:///android_asset/youpin_info.html");
        Log.d("tag","mygirl!!!");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
 
       webView.setWebViewClient(new WebViewClient(){
           @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
             view.loadUrl(url);
            return true;
        }
       });
       webView.setWebChromeClient(new WebChromeClient(){
           @Override
           public void onReceivedTitle(WebView view, String title) {
               titleview.setText(title);//a textview
           }
       }); 
       webView.setWebChromeClient(new WebChromeClient(){
                   @Override
                   public void onProgressChanged(WebView view, int newProgress) {
                       //get the newProgress and refresh progress bar
                   }
               });
       
    }
}