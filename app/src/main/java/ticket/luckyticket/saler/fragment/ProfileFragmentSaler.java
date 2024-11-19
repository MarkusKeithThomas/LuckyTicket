package ticket.luckyticket.saler.fragment;

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
import ticket.luckyticket.databinding.FragmentProfileSalerBinding;
import ticket.luckyticket.saler.apdapter.RatingForSalerAdapter;
import ticket.luckyticket.saler.model.RatingForSaler;
import ticket.luckyticket.saler.viewmodel.ProfileFragmentSalerViewModel;
import ticket.luckyticket.saler.viewmodel.RatingForSalerViewModel;
import ticket.luckyticket.saler.viewmodel.RegisterViewModel;

public class ProfileFragmentSaler extends Fragment {
    private ProfileFragmentSalerViewModel profileFragmentSalerViewModel;
    private FragmentProfileSalerBinding fragmentProfileBinding;
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private RatingForSalerViewModel ratingForSalerViewModel;
    private RatingForSalerAdapter ratingForSalerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Khởi tạo DataBinding và inflate layout
        fragmentProfileBinding = FragmentProfileSalerBinding.inflate(inflater, container, false);
        // Lấy View từ DataBinding
        View view = fragmentProfileBinding.getRoot();
        recyclerView = fragmentProfileBinding.recyclerCommentsSaler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        ratingForSalerAdapter = new RatingForSalerAdapter();
        recyclerView.setAdapter(ratingForSalerAdapter);

        profileFragmentSalerViewModel = new ViewModelProvider(this).get(ProfileFragmentSalerViewModel.class);
        ratingForSalerViewModel = new ViewModelProvider(this).get(RatingForSalerViewModel.class);
        auth = FirebaseAuth.getInstance();
        String currentUser = auth.getUid();

        // Kiểm tra nếu người dùng đã đăng nhập
        if (currentUser != null){
            profileFragmentSalerViewModel.getOneUserSaler()
                    .observe(getViewLifecycleOwner(),userSaler -> {
                        fragmentProfileBinding.setUserSaler(userSaler);
                        Glide.with(requireContext())
                                .load(userSaler.getImage_Url_Avatar())
                                .fitCenter()
                                .circleCrop()  // Chuyển đổi ảnh thành hình tròn
                                .into(fragmentProfileBinding.imgAvatarSaler);  // Chèn ảnh vào ImageView của bạn
                    });

        } else {
            Toast.makeText(getActivity(), "Cần đăng nhập tài khoản.", Toast.LENGTH_SHORT).show();
        }
        ratingForSalerViewModel.getComment(currentUser).observe(getViewLifecycleOwner(), new Observer<List<RatingForSaler>>() {
            @Override
            public void onChanged(List<RatingForSaler> ratingForSalers) {
                ratingForSalerAdapter.updateData(ratingForSalers);
            }
        });

        return view;
    }


}