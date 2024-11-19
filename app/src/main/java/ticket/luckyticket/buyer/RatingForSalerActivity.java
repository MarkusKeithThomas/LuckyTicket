package ticket.luckyticket.buyer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ticket.luckyticket.R;
import ticket.luckyticket.databinding.ActivityRatingForSalerBinding;
import ticket.luckyticket.saler.apdapter.RatingForSalerAdapter;
import ticket.luckyticket.saler.model.RatingForSaler;
import ticket.luckyticket.saler.viewmodel.RatingForSalerViewModel;

public class RatingForSalerActivity extends AppCompatActivity {

    private RatingForSalerViewModel ratingForSalerViewModel;
    private ActivityRatingForSalerBinding activityRatingForSalerBinding;
    private RatingForSaler ratingForSaler;
    private RatingForSalerAdapter ratingForSalerAdapter;
    private List<RatingForSaler> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private String userSalerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSalerId = getIntent().getStringExtra("userSalerId");
        activityRatingForSalerBinding = DataBindingUtil.setContentView(this,R.layout.activity_rating_for_saler);
        ratingForSalerViewModel = new ViewModelProvider(this).get(RatingForSalerViewModel.class);

        ratingForSaler = new RatingForSaler();

        recyclerView = activityRatingForSalerBinding.recyclerComments;
        ratingForSalerAdapter = new RatingForSalerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(ratingForSalerAdapter); // Đặt adapter trước khi lấy dữ liệu


        ratingForSalerViewModel.getInformationOneSaler(userSalerId)
                .observe(this, userSaler -> {
                    if (userSaler != null) {
                        // Set the user data to the binding
                        activityRatingForSalerBinding.setSalerInformation(userSaler);

                        // Load the avatar image
                        Glide.with(this)
                                .load(userSaler.getImage_Url_Avatar())
                                .fitCenter()
                                .circleCrop()
                                .into(activityRatingForSalerBinding.imgAvatarCommentActivity);
                    } else {
                        // Load the avatar image
                        Glide.with(this)
                                .load(R.drawable.icon_profile)
                                .fitCenter()
                                .circleCrop()
                                .into(activityRatingForSalerBinding.imgAvatarCommentActivity);
                    }
                });
        activityRatingForSalerBinding.buttonSendCommentProfile.setOnClickListener(v -> {
            showEditDialog(ratingForSaler);
        });

        ratingForSalerViewModel.getComment(userSalerId).observe(this, new Observer<List<RatingForSaler>>() {
            @Override
            public void onChanged(List<RatingForSaler> ratingForSalerList) {
                if (ratingForSalerList != null && !ratingForSalerList.isEmpty()) {
                    // Cập nhật dữ liệu vào adapter
                    ratingForSalerAdapter.updateData(ratingForSalerList);
                } else {
                    // Xử lý trường hợp không có dữ liệu
                    Toast.makeText(RatingForSalerActivity.this, "Không có đánh giá nào", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    private void showEditDialog(RatingForSaler ratingForSaler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đánh giá độ hài lòng của quý khách: ");

        // Khoi tao ban dau co 10 phan tu
        Integer[] predefinedNumbers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] predefinedNumbersStr = new String[predefinedNumbers.length];

        // Convert Integer[] to String[] for setSingleChoiceItems
        for (int i = 0; i < predefinedNumbers.length; i++) {
            predefinedNumbersStr[i] = String.valueOf(predefinedNumbers[i]);
        }

        AtomicReference<Integer> selectedNumber = new AtomicReference<>(10);

        // Tim lua chon mac dinh
        int defaultSelectedIndex = -1;
        for (int i = 0; i < predefinedNumbers.length; i++) {
            if (predefinedNumbers[i] == 10) {
                defaultSelectedIndex = i;
                break;
            }
        }

        // Check if the predefined list is empty
        if (predefinedNumbers.length == 0) {
            Toast.makeText(this, "Bạn chưa chọn điểm.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Correct the setSingleChoiceItems to use String[] predefinedNumbersStr
        builder.setSingleChoiceItems(predefinedNumbersStr, defaultSelectedIndex, (dialog, which) -> {
            selectedNumber.set(predefinedNumbers[which]);  // Use the original Integer array to get the number
        });

        builder.setPositiveButton("Đánh giá", (dialog, which) -> {
            if (selectedNumber.get() != null) {
                // Update the ratingForSaler object with the selected rating and other info
                ratingForSaler.setSalerId(userSalerId);
                ratingForSaler.setRatingComment(selectedNumber.get().toString()); // Corrected to use selectedNumber.get()
                ratingForSaler.setContentComment(activityRatingForSalerBinding.editTextMessage.getText().toString());
                ratingForSaler.setTimeStamp(Timestamp.now());  // Lấy thời gian hiện tại dưới dạng theo dinh dang firebase
                // Send the comment to Firebase
                ratingForSalerViewModel.sendComment(ratingForSaler);
                ratingForSalerViewModel.updateRatingForSaler(selectedNumber.get().toString(),userSalerId);
                activityRatingForSalerBinding.editTextMessage.getText().clear();

                // Display success message
                Toast.makeText(this, "Đánh giá thành công " + selectedNumber.get(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

}