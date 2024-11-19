package ticket.luckyticket.saler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.saler.model.TicketInformation;
import ticket.luckyticket.saler.viewmodel.AddNewTicketViewModel;
import ticket.luckyticket.saler.viewmodel.MainScreenSalerViewModel;

public class AddNewTicketActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_STORAGE_PERMISSION = 111;
    private MainScreenSalerViewModel mainScreenSalerViewModel;
    private AddNewTicketViewModel addNewTicketViewModel;
    private TextView toolbarTime;
    private EditText edtNumberTicket;
    private String soVeBan, tenDai;
    private ImageView imageTicket;
    private Button btnsaveBackhome, btnContinueInput;
    private ProgressBar progressBar;
    private Uri imageUri;
    private String userSalerId;
    private TicketInformation ticketInformation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_ticket);
        mainScreenSalerViewModel = new ViewModelProvider(this).get(MainScreenSalerViewModel.class);
        addNewTicketViewModel = new ViewModelProvider(this).get(AddNewTicketViewModel.class);
        userSalerId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Đảm bảo userSalerId đã được khởi tạo

        // Tìm Toolbar và thiết lập
        Toolbar toolbar = findViewById(R.id.toolbar_saler);
        setSupportActionBar(toolbar);
        // Tìm TextView để hiển thị thời gian
        toolbarTime = toolbar.findViewById(R.id.toolbar_time);
        toolbarTime.setText(mainScreenSalerViewModel.getDayOfWeek()+" "+mainScreenSalerViewModel.getCurrentDay());
        imageTicket = findViewById(R.id.image_ticket);
        edtNumberTicket = findViewById(R.id.edt_number_ticket);
        progressBar = findViewById(R.id.progress_circular);
        btnsaveBackhome = findViewById(R.id.btnsave_backhome);
        btnContinueInput = findViewById(R.id.btncontinute_input);


        // Khởi tạo các thành phần giao diện
        Spinner spinnerTenDai = findViewById(R.id.spinner_tendai);
        Spinner spinnerSoVeBan = findViewById(R.id.spinner_sove);

        // Thiết lập spinner
        List<String> itemsTenDai = addNewTicketViewModel.getItemsBasedOnDay();
        String[] itemsSoVeBan = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

        ArrayAdapter<String> adapterTenDai = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsTenDai);
        adapterTenDai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTenDai.setAdapter(adapterTenDai);

        ArrayAdapter<String> adapterSoVeBan = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsSoVeBan);
        adapterSoVeBan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSoVeBan.setAdapter(adapterSoVeBan);

        // Lắng nghe sự kiện spinner
        spinnerTenDai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tenDai = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddNewTicketActivity.this, "Vui Lòng Chọn Nội Dung.", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerSoVeBan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soVeBan = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddNewTicketActivity.this, "Vui Lòng Chọn Nội Dung.", Toast.LENGTH_SHORT).show();
            }
        });
//        checkAndRequestReadStoragePermission();

        // Lấy đường dẫn hình ảnh từ Intent
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        String numberSauSo = intent.getStringExtra("numberSauSo");
        if (numberSauSo != null && numberSauSo.length() == 6) {
            edtNumberTicket.setText(numberSauSo);
        } else {
            edtNumberTicket.setText("999999");
        }
        if (imagePath != null) {
            File file = new File(imagePath);
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            imageTicket.setImageURI(imageUri); // Đặt hình ảnh từ URI
        }

        handleEvents();


    }
//    private void checkAndRequestReadStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                // Nếu chưa có quyền, yêu cầu quyền
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_STORAGE_PERMISSION);
//            } else {
//                // Quyền đã được cấp, thực hiện thao tác
//                accessFile();
//            }
//        } else {
//            // Android dưới API 23, quyền đã có sẵn
//            accessFile();
//        }
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_READ_STORAGE_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Người dùng đã cấp quyền, thực hiện thao tác
//                accessFile();
//            } else {
//                // Người dùng từ chối cấp quyền
//                Log.d("PermissionError", "Quyền truy cập bộ nhớ bị từ chối");
//                Toast.makeText(this, "Quyền truy cập camera hoặc bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show();  // Thêm thông báo lỗi ở đây
//                 }
//        }
//    }
//    private void accessFile() {
//        // Logic thực hiện sau khi đã được cấp quyền
//        Toast.makeText(this, "Truy cập thành công vào bộ nhớ!", Toast.LENGTH_SHORT).show();
//        // Thực hiện thao tác với tệp tại đây
//    }
    private void handleEvents( ) {
        imageTicket.setOnClickListener(v -> {
            Log.d("ClickHere", "Ban Da Click Roi");
            Intent intent = new Intent(AddNewTicketActivity.this, ReconNumberActivity.class);
            startActivity(intent);
        });

        btnsaveBackhome.setOnClickListener(v -> {
            ticketInformation = new TicketInformation("",tenDai,edtNumberTicket.getText().toString(),soVeBan, Timestamp.now(),userSalerId);
            if (imageUri != null && !edtNumberTicket.getText().toString().isEmpty() && tenDai != null && soVeBan != null) {
                addNewTicketViewModel.uploadImageTicketInformationToFirebase(imageUri,ticketInformation);
                Intent intent = new Intent(AddNewTicketActivity.this, MainScreenSalerActivity.class);
                // Xóa AddNewTicketActivity khỏi ngăn xếp
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();  // Kết thúc Activity sau khi lưu xong
            } else {
                Toast.makeText(AddNewTicketActivity.this, "Cần nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        btnContinueInput.setOnClickListener(v -> {
            ticketInformation = new TicketInformation("",tenDai,edtNumberTicket.getText().toString(),soVeBan, Timestamp.now(),userSalerId);
            if (imageUri != null && !edtNumberTicket.getText().toString().isEmpty() && tenDai != null && soVeBan != null) {
                addNewTicketViewModel.uploadImageTicketInformationToFirebase(imageUri,ticketInformation);
                Intent intent = new Intent(AddNewTicketActivity.this, ReconNumberActivity.class);
                // Xóa AddNewTicketActivity khỏi ngăn xếp
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();  // Kết thúc Activity sau khi lưu xong
            } else {
                Toast.makeText(AddNewTicketActivity.this, "Cần nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

}