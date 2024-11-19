package ticket.luckyticket.saler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ticket.luckyticket.R;
import ticket.luckyticket.saler.analysis.NumberRecognizer;

public class ReconNumberActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ProgressBar progressBar;
    private Button captureButton;
    private ImageView imageView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private Handler handler;
    private TessBaseAPI tessBaseAPI;
    private final int CAMERA_PERMISSION_CODE = 1001;

    static {
        if (!OpenCVLoader.initDebug()) {
            throw new RuntimeException("OpenCV initialization failed");
        }
    }
    NumberRecognizer numberRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recon_number);

        previewView = findViewById(R.id.previewView);
        captureButton = findViewById(R.id.capture_button);
        imageView = findViewById(R.id.image);
        progressBar = findViewById(R.id.progress_circular);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkCameraPermission()) {
                startCamera();
            } else {
                requestCameraPermission();
            }
        } else {
            startCamera();
        }




        captureButton.setOnClickListener(view -> takePhoto());

        cameraExecutor = Executors.newSingleThreadExecutor();
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Khởi tạo Tesseract với tệp traineddata
        tessBaseAPI = new TessBaseAPI();
        numberRecognizer = new NumberRecognizer(tessBaseAPI);

        try {
            String tessDataPath = getFilesDir() + "/";
            copyTessDataFiles(tessDataPath);  // Phương thức này sẽ sao chép tệp traineddata vào thư mục đích

            if (!tessBaseAPI.init(tessDataPath, "eng")) {
                Log.e("MainActivity", "Tesseract initialization failed with path: " + tessDataPath);
                //Toast.makeText(this, "Tesseract initialization failed", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.d("MainActivity", "Tesseract initialized successfully");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Tesseract initialization error: " + e.getMessage());
            //Toast.makeText(this, "Tesseract initialization failed", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void copyTessDataFiles(String tessDataPath) {
        try {
            File dir = new File(tessDataPath + "tessdata");
            if (!dir.exists() && !dir.mkdirs()) {
                Log.e("MainActivity", "Failed to create tessdata directory");
                return;
            }
            String fileName = "eng.traineddata";
            String pathToDataFile = tessDataPath + "tessdata/" + fileName;
            File dataFile = new File(pathToDataFile);
            if (!dataFile.exists()) {
                try (InputStream in = getAssets().open("tessdata/" + fileName);
                     OutputStream out = new FileOutputStream(pathToDataFile)) {

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                    Log.d("MainActivity", "Tessdata file copied to: " + pathToDataFile);
                }
            } else {
                Log.d("MainActivity", "Tessdata file already exists at: " + pathToDataFile);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error copying tessdata files: " + e.getMessage());
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        // Kiểm tra nếu người dùng đã từ chối quyền trước đó và hiển thị một lời giải thích
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera permission is needed to use this feature", Toast.LENGTH_SHORT).show();
        }

        // Yêu cầu quyền
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            boolean cameraGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean readStorageGranted = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;
            boolean writeStorageGranted = grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED;

            if (cameraGranted && readStorageGranted && writeStorageGranted) {
                // Tất cả các quyền đã được cấp
                startCamera();
            } else {
                // Quyền bị từ chối
                Toast.makeText(this, "Quyền truy cập camera hoặc bộ nhớ đã bị từ chối.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    private void takePhoto() {
        if (imageCapture == null) return;
        progressBar.setVisibility(View.VISIBLE);


        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {

                // Bước 2: Xoay và cắt hình ảnh
                Bitmap rotatedAndCroppedBitmap = rotateAndCropImage(image);

                if (rotatedAndCroppedBitmap != null) {
                    // Bước 3: Hiển thị hình ảnh đã cắt
                    imageView.setImageBitmap(rotatedAndCroppedBitmap);
                    cameraExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            String imagePath = saveBitmap(rotatedAndCroppedBitmap);
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                String numberSauSo = numberRecognizer.recognizeNumbers(rotatedAndCroppedBitmap);
                                Intent intent = new Intent(ReconNumberActivity.this,AddNewTicketActivity.class);
                                intent.putExtra("numberSauSo",numberSauSo);
                                intent.putExtra("imagePath",imagePath);
                                startActivity(intent);
                                finish();
                            });
                        }
                    });


                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(ReconNumberActivity.this, "Lỗi khi chụp ảnh. Hãy Thử lại: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CameraXApp", "Error capturing image", exception);
            }
        });
    }

    private Bitmap rotateAndCropImage(ImageProxy image) {
        Bitmap bitmap1 = imageProxyToBitmap(image);
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
        bitmap1.recycle();  // Giải phóng bitmap ban đầu sau khi xoay xong

        return cropBitmap(rotatedBitmap);
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        double widthDrop = imageView.getWidth() * 1.8;
        int heightDrop = imageView.getWidth();
        int x = (int) (centerX - (widthDrop / 2));
        int y = centerY - (heightDrop / 2);

        int cropWidth = Math.min((int) widthDrop, bitmap.getWidth() - x);
        int cropHeight = Math.min(heightDrop, bitmap.getHeight() - y);

        return Bitmap.createBitmap(bitmap, x, y, cropWidth, cropHeight);
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private String saveBitmap(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir, "CROPPED_IMG_" + timeStamp + ".jpg");

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("CameraXApp", "Error saving cropped image", e);
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng cameraExecutor nếu nó chưa bị đóng
        if (cameraExecutor != null && !cameraExecutor.isShutdown()) {
            cameraExecutor.shutdown();
        }

        // Dừng và kết thúc Tesseract API
        if (tessBaseAPI != null) {
            tessBaseAPI.end();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Xóa ảnh khỏi ImageView khi quay lại Activity này
        imageView.setImageBitmap(null);
    }
}