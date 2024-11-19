package ticket.luckyticket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import ticket.luckyticket.buyer.SignInBuyerActivity;
import ticket.luckyticket.databinding.ActivityRegisterBinding;
import ticket.luckyticket.saler.SignInSalerActivity;
import ticket.luckyticket.saler.model.User;
import ticket.luckyticket.saler.viewmodel.RegisterViewModel;
import ticket.luckyticket.saler.viewmodel.SignInSalerViewModel;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding activityRegisterBinding;
    private RegisterViewModel registerViewModel;
    private SignInSalerViewModel signInSalerViewModel;
    private static final int PERMISSION_REQUEST_CODE = 11;
    private static final int PICK_IMAGE_REQUEST = 22;
    private ImageView imageView;
    private Uri imageUri;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup DataBinding
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        signInSalerViewModel = new ViewModelProvider(this).get(SignInSalerViewModel.class);
        activityRegisterBinding.setViewmodel(registerViewModel);
        activityRegisterBinding.setLifecycleOwner(this);

        // Setup click listener for ImageView to select image
        imageView = activityRegisterBinding.imageAvatar;
        progressBar = activityRegisterBinding.progressBarRegister;
        progressBar.setVisibility(View.GONE);
        observeViewModel();

        imageView.setOnClickListener(view -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                // Nếu API từ Android 13 trở lên, yêu cầu quyền mới
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
                } else {
                    openGallery(); // Mở gallery nếu quyền đã được cấp
                }
            } else {
                // Đối với các phiên bản Android thấp hơn API 33, sử dụng quyền cũ
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    openGallery(); // Mở gallery nếu quyền đã được cấp
                }
            }

        });
        // Register button listener
        activityRegisterBinding.btnRegister.setOnClickListener(v -> {
            // Kiểm tra xem người dùng đã chọn ảnh chưa
            if (imageUri != null) {
                User user = registerViewModel.getUserMutableLiveData().getValue();
                if (user != null) {
                    boolean isValid = registerViewModel.validateUserInput(user); // Kiểm tra tính hợp lệ
                    if (isValid) { // Chỉ tiếp tục nếu tất cả dữ liệu đều hợp lệ
                        registerViewModel.createUser(imageUri, user);
                        Log.d("RegisterActivity123", "Selected URI: " + imageUri.toString());
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();

            }

        });
        activityRegisterBinding.tvLogin.setOnClickListener(view ->{
            Intent intent = new Intent(RegisterActivity.this, OptionActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Mở gallery để chọn ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");  // Chỉ cho phép chọn ảnh
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Xử lý kết quả của yêu cầu quyền truy cập
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Xử lý khi người dùng chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                activityRegisterBinding.btnRegister.setVisibility(View.VISIBLE);
                imageView.setImageURI(imageUri);  // Hiển thị ảnh đã chọn trên ImageView
            } else {
                Log.d("RegisterActivity123", "Image URI is null");
                Toast.makeText(this, "Failed to select image. Please try again.", Toast.LENGTH_SHORT).show();
            }

        }

    }
    private void observeViewModel() {
        registerViewModel.getIsUploadSuccessful().observe(this, isSuccessful -> {
            if (Boolean.TRUE.equals(isSuccessful)) {
                // Upload thành công, chuyển sang Activity khác
                Intent intent = new Intent(RegisterActivity.this, OptionActivity.class);
                startActivity(intent);
                finish();  // Đóng Activity hiện tại nếu không cần quay lại
            }
        });
        registerViewModel.getNameError().observe(this, error -> activityRegisterBinding.errorName.setText(error));
        registerViewModel.getEmailError().observe(this, error -> activityRegisterBinding.errorEmail.setText(error));
        registerViewModel.getPasswordError().observe(this, error -> activityRegisterBinding.errorPassword.setText(error));
        registerViewModel.getConfirmPasswordError().observe(this, error -> activityRegisterBinding.errorRepassword.setText(error));
        registerViewModel.getPhoneError().observe(this, error -> activityRegisterBinding.errorPhone.setText(error));
        registerViewModel.getIdCardError().observe(this,error ->{activityRegisterBinding.errorCccd.setText(error);});
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        signInSalerViewModel.signOut();
    }
}