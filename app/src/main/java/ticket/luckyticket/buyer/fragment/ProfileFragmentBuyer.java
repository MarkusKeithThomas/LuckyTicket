package ticket.luckyticket.buyer.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.databinding.FragmentProfileBuyerBinding;
import ticket.luckyticket.databinding.FragmentProfileSalerBinding;
import ticket.luckyticket.saler.apdapter.RatingForSalerAdapter;
import ticket.luckyticket.saler.model.RatingForSaler;
import ticket.luckyticket.saler.viewmodel.ProfileFragmentSalerViewModel;
import ticket.luckyticket.saler.viewmodel.RatingForSalerViewModel;
import ticket.luckyticket.saler.viewmodel.RegisterViewModel;

public class ProfileFragmentBuyer extends Fragment {
    private ProfileFragmentSalerViewModel profileFragmentSalerViewModel;
    private FragmentProfileBuyerBinding fragmentProfileBuyerBinding;
    private FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Khởi tạo DataBinding và inflate layout
        fragmentProfileBuyerBinding = FragmentProfileBuyerBinding.inflate(inflater, container, false);
        // Lấy View từ DataBinding
        View view = fragmentProfileBuyerBinding.getRoot();
        profileFragmentSalerViewModel = new ViewModelProvider(this).get(ProfileFragmentSalerViewModel.class);
        auth = FirebaseAuth.getInstance();
        String currentUser = auth.getUid();
        // Kiểm tra nếu người dùng đã đăng nhập
        if (currentUser != null) {
            profileFragmentSalerViewModel.getOneUserSaler()
                    .observe(getViewLifecycleOwner(), userSaler -> {
                        if (userSaler != null) {
                            // Đặt dữ liệu cho user
                            fragmentProfileBuyerBinding.setUserBuyer(userSaler);

                            // Kiểm tra URL ảnh đại diện trước khi sử dụng Glide
                            if (userSaler.getImage_Url_Avatar() != null) {
                                Glide.with(requireContext())
                                        .load(userSaler.getImage_Url_Avatar())
                                        .fitCenter()
                                        .circleCrop()  // Chuyển đổi ảnh thành hình tròn
                                        .into(fragmentProfileBuyerBinding.imgAvatarBuyer);  // Chèn ảnh vào ImageView của bạn
                            } else {
                                // Sử dụng ảnh mặc định nếu URL ảnh đại diện là null
                                fragmentProfileBuyerBinding.imgAvatarBuyer.setImageResource(R.drawable.avatar);
                            }
                        } else {
                            // Thông báo nếu userSaler là null
                            Toast.makeText(getActivity(), "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Cần đăng nhập tài khoản.", Toast.LENGTH_SHORT).show();
        }



        return view;
    }
}