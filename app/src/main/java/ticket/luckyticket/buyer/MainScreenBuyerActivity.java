package ticket.luckyticket.buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import ticket.luckyticket.OptionActivity;
import ticket.luckyticket.R;
import ticket.luckyticket.buyer.fragment.ChattingFragmentBuyer;
import ticket.luckyticket.buyer.fragment.HistoryFragmentBuyer;
import ticket.luckyticket.buyer.fragment.HomeFragmentBuyer;
import ticket.luckyticket.buyer.fragment.ProfileFragmentBuyer;
import ticket.luckyticket.buyer.fragment.SoldFragmentBuyer;
import ticket.luckyticket.saler.MainScreenSalerActivity;
import ticket.luckyticket.saler.fragment.HomeFragmentSaler;
import ticket.luckyticket.saler.service.LocationService;
import ticket.luckyticket.saler.viewmodel.MainScreenSalerViewModel;
import ticket.luckyticket.saler.viewmodel.ProfileFragmentSalerViewModel;
import ticket.luckyticket.saler.viewmodel.SignInSalerViewModel;

public class MainScreenBuyerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private MainScreenSalerViewModel mainScreenSalerViewModel;
    private SignInSalerViewModel signInSalerViewModel;

    private TextView toolbarTime;

    private FragmentManager fragmentManager;
    private Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private int currentFragmentId = R.id.nav_home_buyer;
    private DrawerLayout drawerLayout;
    private ProfileFragmentSalerViewModel profileFragmentSalerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_buyer);
        signInSalerViewModel = new ViewModelProvider(this).get(SignInSalerViewModel.class);
        profileFragmentSalerViewModel = new ViewModelProvider(this).get(ProfileFragmentSalerViewModel.class);
        // Tìm Toolbar và thiết lập
        Toolbar toolbar = findViewById(R.id.toolbar_buyer);
        setSupportActionBar(toolbar);
        // Tìm TextView để hiển thị thời gian
        toolbarTime = toolbar.findViewById(R.id.toolbar_time);
        // Thiết lập drawerLayout
        drawerLayout = findViewById(R.id.drawer_layout_buyer);
        // Set up NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view_buyer);
        navigationView.setNavigationItemSelectedListener(this);
        // thiết lặp các view cho avatar
        View headerView = navigationView.getHeaderView(0);
        ImageView imageViewAvatar = headerView.findViewById(R.id.image_avatar_drawer);
        TextView nameView = headerView.findViewById(R.id.text_user_name_drawer);
        profileFragmentSalerViewModel.getOneUserSaler().observe(this, user -> {
            if (user != null) {
                if (user.getName() != null) {
                    nameView.setText(user.getName());
                } else {
                    nameView.setText("Tên người dùng");  // Hiển thị tên mặc định nếu user name là null
                }

                if (user.getImage_Url_Avatar() != null) {
                    Glide.with(this)
                            .load(user.getImage_Url_Avatar())
                            .fitCenter()
                            .circleCrop()  // Chuyển đổi ảnh thành hình tròn
                            .into(imageViewAvatar);  // Chèn ảnh vào ImageView của bạn
                } else {
                    imageViewAvatar.setImageResource(R.drawable.icon_buyer);  // Sử dụng ảnh mặc định nếu URL ảnh đại diện là null
                }
            } else {
                Log.w("Profile", "User data is null.");
            }
        });

        // Toggle button for opening and closing the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mainScreenSalerViewModel = new ViewModelProvider(this).get(MainScreenSalerViewModel.class);

        toolbarTime.setText(mainScreenSalerViewModel.getDayOfWeek()+", "+mainScreenSalerViewModel.getCurrentDay());
        fragmentManager = getSupportFragmentManager();
        //Thiết lập các Fragment
        fragmentMap.put(R.id.nav_home_buyer, new HomeFragmentBuyer());
        fragmentMap.put(R.id.nav_chatting_buyer, new ChattingFragmentBuyer());
        fragmentMap.put(R.id.nav_history_buyer, new HistoryFragmentBuyer());
        fragmentMap.put(R.id.nav_profile_buyer,new ProfileFragmentBuyer());
        fragmentMap.put(R.id.nav_buyer, new SoldFragmentBuyer());
        // Add các Fragment vào FragmentManager và hide chúng, ngoại trừ Fragment mặc định
        for (Map.Entry<Integer, Fragment> entry : fragmentMap.entrySet()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, entry.getValue());
            if (entry.getKey() != currentFragmentId) {
                fragmentTransaction.hide(entry.getValue());
            }
            fragmentTransaction.commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_buyer);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId != currentFragmentId) {
                showFragment(itemId);
                currentFragmentId = itemId;
            }
            return true;
        });

        // Set mục mặc định khi mở Activity
        bottomNavigationView.setSelectedItemId(R.id.nav_home_buyer);
    }
    private void showFragment(int fragmentId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Hide Fragment hiện tại
        Fragment currentFragment = fragmentMap.get(currentFragmentId);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        // Show Fragment mới
        Fragment newFragment = fragmentMap.get(fragmentId);
        if (newFragment != null) {
            fragmentTransaction.show(newFragment);
        }

        fragmentTransaction.commit();

    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // Đóng Drawer nếu nó đang mở
        } else {
            super.onBackPressed(); // Nếu Drawer đã đóng, sử dụng hành vi mặc định
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragmentSaler()).commit();
        } else if (id == R.id.item_settings) {
            // Xử lý khi chọn Settings
        } else if (id == R.id.item_logout) {
            // Xử lý khi chọn Logout
            signInSalerViewModel.signOut();
            Intent intent = new Intent(MainScreenBuyerActivity.this, OptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() !=  null){
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        } else {
            // Nếu không có người dùng, chuyển hướng về màn hình đăng nhập
            Intent intent = new Intent(this, SignInBuyerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}