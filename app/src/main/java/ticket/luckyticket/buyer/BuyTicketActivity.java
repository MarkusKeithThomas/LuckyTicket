package ticket.luckyticket.buyer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.adapter.ChattingBuySaleApdapter;
import ticket.luckyticket.buyer.model.ChattingAB;
import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.buyer.viewmodelbuyer.ChattingViewModel;
import ticket.luckyticket.databinding.ActivityBuyTicketBinding;

public class BuyTicketActivity extends AppCompatActivity {

    private ActivityBuyTicketBinding activityBuyTicketBinding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userIdBought;
    private RecyclerView recyclerView;
    private ChattingBuySaleApdapter chattingBuySaleApdapter;
    private ChattingViewModel chattingViewModel;
    private String ticketHistoryID;
    private String userIdSale;
    private String sixNumberTicket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBuyTicketBinding = DataBindingUtil.setContentView(this, R.layout.activity_buy_ticket);
        userIdBought = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Khởi tạo userId ở đây
        if (userIdBought == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu người dùng chưa đăng nhập
            return;
        }
        //Nhaanj du lieu tu fragment
        Intent intent = getIntent();
        ticketHistoryID = intent.getStringExtra("DocID");
        Log.d("BuyTicketActivity123", "Received ticket information list of size: "+ticketHistoryID);
        if (ticketHistoryID == null || ticketHistoryID.isEmpty()) {
            Toast.makeText(this, "Document ID not found", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có Document ID
        } else {
            setupSendMessage();
            setupRecyclerView();
        }


    }


    private void setupRecyclerView() {
        recyclerView = activityBuyTicketBinding.recyclerViewMessages;
        // Thiết lập LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo Adapter và gán vào RecyclerView
        chattingBuySaleApdapter = new ChattingBuySaleApdapter(new ArrayList<>(),userIdBought);
        recyclerView.setAdapter(chattingBuySaleApdapter);


    }
    private void observeMessages(String saleId,String buyId,String docId) {
        if (userIdSale != null && sixNumberTicket != null) { // Kiểm tra chắc chắn hai biến này đã có giá trị
            chattingViewModel.getMessages(
                    saleId,
                    buyId,
                    docId).observe(this, new Observer<List<ChattingAB>>() {
                @Override
                public void onChanged(List<ChattingAB> chatMessages) {
                    chattingBuySaleApdapter.setChatList(chatMessages);
                    chattingBuySaleApdapter.scrollToLastMessage(recyclerView);
                }
            });
        } else {
            Log.e("BuyTicketActivity123", "userIdA or sixNumberTicket is null");
        }
    }

    private void setupSendMessage() {
        chattingViewModel = new ViewModelProvider(this).get(ChattingViewModel.class);
        chattingViewModel.getOneTicket(ticketHistoryID).observe(this, new Observer<TicketHistory>() {
            @Override
            public void onChanged(TicketHistory ticketHistory) {
                if (ticketHistory.getDocumentIdTicketHistory() != null) {
                    Log.d("BuyTicketActivity123", "Kiem tra thong tin ticketHistory: "+ticketHistory.getNameDai());
                    Log.d("BuyTicketActivity123", "Kiem tra thong tin ticketHistory: "+ticketHistory.getTimeSaleBought());


                    Glide.with(getApplicationContext())
                            .load(ticketHistory.getImageURL())
                            .fitCenter()
                            .placeholder(R.drawable.baseline_terrain_24)
                            .error(R.drawable.baseline_terrain_24)
                            .into(activityBuyTicketBinding.imageTicket);
                    activityBuyTicketBinding.nameDaiTicket.setText("Đài: " + ticketHistory.getNameDai());
                    activityBuyTicketBinding.numberSixTicket.setText("Số Vé: " + ticketHistory.getTicketSixNumber());
                    activityBuyTicketBinding.numberTicketSale.setText("Vé Đã Mua: " + ticketHistory.getTicketNumberSale() + " Vé");
                    userIdSale = ticketHistory.getUserSaleID();
                    sixNumberTicket = ticketHistory.getTicketSixNumber();
                    sendMessage(ticketHistory);
                    // Gọi phương thức để lắng nghe và hiển thị tin nhắn
                    observeMessages(ticketHistory.getUserSaleID(),
                            ticketHistory.getUserBoughtID(),
                            ticketHistory.getDocumentIdTicketHistory());

                    // Lấy trường timestamp từ document
                    Timestamp timestamp = ticketHistory.getTimeSaleBought(); // Thay "timestampField" bằng tên trường
                    if (timestamp != null) {
                        // Chuyển đổi Timestamp thành Date
                        Date date = timestamp.toDate();
                        // Định dạng Date thành chuỗi theo định dạng mong muốn
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        String formattedDate = dateFormat.format(date);
                        activityBuyTicketBinding.timeSaleTicket.setText("Đã Bán Vào: " + formattedDate);
                    }
                }
            }
        });

    }
    public void sendMessage(TicketHistory ticketHistory){
        activityBuyTicketBinding.buttonSend.setOnClickListener(view ->{
            String msg = activityBuyTicketBinding.editTextMessage.getText().toString();
            if (!msg.isEmpty()) { // Kiểm tra nếu tin nhắn không rỗng
                chattingViewModel.sendMessage(
                        ticketHistory.getUserSaleID(),
                        ticketHistory.getUserBoughtID(),
                        msg,
                        ticketHistory.getDocumentIdTicketHistory()
                );
                activityBuyTicketBinding.editTextMessage.getText().clear(); // Xóa nội dung sau khi gửi
            }
        });
    }
}