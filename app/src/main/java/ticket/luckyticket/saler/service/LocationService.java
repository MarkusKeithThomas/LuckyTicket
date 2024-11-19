package ticket.luckyticket.saler.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ticket.luckyticket.OptionActivity;
import ticket.luckyticket.R;
import ticket.luckyticket.saler.SignInSalerActivity;
import ticket.luckyticket.saler.repository.SalerMapRepository;
import ticket.luckyticket.saler.viewmodel.SalerMapViewModel;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private SalerMapViewModel salerMapViewModel;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Khởi tạo locationRequest như biến thành viên
        locationRequest = new LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(10000)
                .build();
        salerMapViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(SalerMapViewModel.class);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    startForegroundServiceWithNotification();
                    return; // Kết thúc nếu không có vị trí nào được cập nhật
                };
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                if(currentUser !=  null){
                    for (Location location : locationResult.getLocations()) {
                        //Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
                        //Log.d(TAG, "UserId "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                        salerMapViewModel.updateLocation(FirebaseAuth.getInstance().getCurrentUser().getUid()
                                ,location.getLatitude()
                                ,location.getLongitude()
                                ,true);

                }
                    // Thêm code xử lý vị trí tại đây nếu cần
                } else {
                    // Nếu người dùng chưa đăng nhập, chuyển về màn hình đăng nhập từ Service
                    Intent intent = new Intent(LocationService.this, OptionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Cờ để mở Activity từ Service
                    startActivity(intent);
                    stopSelf(); // Dừng dịch vụ nếu không có người dùng đăng nhập
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Quyền vị trí chưa được cấp.");
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void startForegroundServiceWithNotification() {
        String channelId = "location_channel";
        String channelName = "Location Service";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Intent mở `SignInSalerActivity` khi nhấn vào thông báo
        Intent notificationIntent = new Intent(this, SignInSalerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Location Service")
                .setContentText("Đang theo dõi vị trí của bạn")
                .setSmallIcon(R.drawable.ic_my_location)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundServiceWithNotification();
        startLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
