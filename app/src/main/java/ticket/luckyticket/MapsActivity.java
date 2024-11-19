package ticket.luckyticket;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MapsActivity", "Connection to Google Play Services failed: " + connectionResult.getErrorMessage());
    }

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Lấy bản đồ từ SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapsActivity", "SupportMapFragment is null. Check layout file.");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            Log.d("MapsActivity", "Google Map is ready.");
        } else {
            Log.e("MapsActivity", "Google Map failed to load.");
        }
        // Thiết lập vị trí mặc định (ví dụ: Thành phố Hồ Chí Minh)
        LatLng hoChiMinh = new LatLng(10.762622, 106.660172);
        mMap.addMarker(new MarkerOptions().position(hoChiMinh).title("Hồ Chí Minh"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hoChiMinh, 10));
    }

}
