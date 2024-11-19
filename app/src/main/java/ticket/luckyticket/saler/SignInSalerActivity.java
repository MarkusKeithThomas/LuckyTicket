package ticket.luckyticket.saler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ticket.luckyticket.R;
import ticket.luckyticket.RegisterActivity;
import ticket.luckyticket.databinding.ActivitySignInSalerBinding;
import ticket.luckyticket.saler.service.LocationService;
import ticket.luckyticket.saler.viewmodel.SignInSalerViewModel;

public class SignInSalerActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private CheckBox rememberMeCheckbox;
    private SignInSalerViewModel signInSalerViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_saler);

        Button btnLogin = findViewById(R.id.btnSignIn_saler);
        TextView btnRegister = findViewById(R.id.sign_up_saler);
        TextView errorUser = findViewById(R.id.error_user_signin);
        edtEmail = findViewById(R.id.email_saler);
        edtPassword = findViewById(R.id.password_saler);
        rememberMeCheckbox = findViewById(R.id.remember_saler);

        signInSalerViewModel = new ViewModelProvider(this).get(SignInSalerViewModel.class);


        btnLogin.setOnClickListener(view -> {
            if (!edtEmail.getText().toString().isEmpty() && !edtPassword.getText().toString().isEmpty()) {
                signInSalerViewModel.signInEmailPassWor(edtEmail.getText().toString(), edtPassword.getText().toString());
                signInSalerViewModel.getErrorLiveData().observe(this, string -> {
                    errorUser.setText(string);
                    Log.d("signInSalerViewModel123",string);
                });
                // Lưu trạng thái "Remember Me" nếu checkbox được chọn
                saveRememberMePreference(rememberMeCheckbox.isChecked());
            } else {
                errorUser.setText("Không được bỏ trống.");
            }
        });
        signInSalerViewModel.getFireUser().observe(this,user ->{
            if (user!=null){
                Intent intent = new Intent(SignInSalerActivity.this,MainScreenSalerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(view ->{
            Intent intent = new Intent(SignInSalerActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


    }


    // Lưu trạng thái Remember Me vào SharedPreferences
    private void saveRememberMePreference(boolean isRemembered) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remember_me", isRemembered);
        editor.apply();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Kiểm tra trạng thái "Remember Me"
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean rememberMe = signInSalerViewModel.checkRememberMe(sharedPreferences);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && !rememberMe) {
            // Nếu không chọn "Remember Me", đăng xuất người dùng và đưa họ về màn hình đăng nhập
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, SignInSalerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (currentUser != null && rememberMe) {
            // Nếu đã đăng nhập và chọn "Remember Me", bắt đầu dịch vụ định vị
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        111);
            } else {
                startLocationService(); // Bắt đầu dịch vụ định vị nếu quyền đã được cấp
            }
        }
    }

    // Phương thức để khởi động Foreground Service
    private void startLocationService() {
        if (isGPSEnabled()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } else {
            Toast.makeText(this, "Please enable GPS to use location services", Toast.LENGTH_SHORT).show();
            // Điều hướng người dùng đến trang bật GPS
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Khởi động dịch vụ nếu quyền vị trí đã được cấp
                startLocationService();
            } else {
                // Xử lý khi người dùng từ chối quyền
                Log.d("SignInActivity", "Permission denied.");
                Toast.makeText(this, "Permission denied. Please grant location access to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}