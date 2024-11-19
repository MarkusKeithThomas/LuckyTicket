package ticket.luckyticket.buyer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import ticket.luckyticket.R;
import ticket.luckyticket.saler.viewmodel.MainScreenSalerViewModel;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private TextView toolbarTime;
    private MainScreenSalerViewModel mainScreenSalerViewModel;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.webview);
        // Tìm Toolbar và thiết lập
        Toolbar toolbar = findViewById(R.id.toolbar_buyer_webview);
        setSupportActionBar(toolbar);
        // Tìm TextView để hiển thị thời gian
        toolbarTime = toolbar.findViewById(R.id.toolbar_time_webview);
        imageView = findViewById(R.id.image_close);
        mainScreenSalerViewModel = new ViewModelProvider(this).get(MainScreenSalerViewModel.class);
        toolbarTime.setText(mainScreenSalerViewModel.getDayOfWeek()+", "+mainScreenSalerViewModel.getCurrentDay());

        imageView.setOnClickListener(view ->{
            Intent intent = new Intent(WebViewActivity.this,MainScreenBuyerActivity.class);
            startActivity(intent);
            finish();
        });


        // Cấu hình WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Kích hoạt JavaScript nếu trang yêu cầu

        // Đảm bảo rằng trang web được mở trong ứng dụng thay vì trình duyệt
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Chèn JavaScript để ẩn các thành phần không mong muốn
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('#header-id').style.display='none';" + // Ẩn header có id là 'header-id'
                        "document.querySelector('.site-footer').style.display='none';" + // Ẩn footer có class là 'site-footer'
                        "document.querySelector('.ad-banner').style.display='none';" + // Ẩn quảng cáo có class là 'ad-banner'
                        "})()");
            }
        });

        // Tải trang web
        webView.loadUrl("https://xsmn.mobi/xo-so-truc-tiep.html");

    }

    // Xử lý khi người dùng nhấn nút Back
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Quay lại trang trước nếu có
        } else {
            super.onBackPressed(); // Đóng ứng dụng nếu không còn trang nào để quay lại
        }
    }
}