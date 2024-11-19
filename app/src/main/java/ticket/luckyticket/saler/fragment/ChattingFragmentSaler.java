package ticket.luckyticket.saler.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.BuyTicketActivity;
import ticket.luckyticket.buyer.adapter.ChatRoomAdapter;
import ticket.luckyticket.buyer.model.ChatRoom;
import ticket.luckyticket.buyer.viewmodelbuyer.ChatRoomViewModel;

public class ChattingFragmentSaler extends Fragment {

    private ChatRoomViewModel chatRoomViewModel;
    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    private String userId;
    private FirebaseAuth auth;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting_saler, container, false);
        Log.d("ChattingFragmentBuyer123", "Received ticket information list of size: ");

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_chatroom_saler);

        recyclerView = view.findViewById(R.id.recyclerViewChatRooms_saler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        chatRoomAdapter = new ChatRoomAdapter(new ArrayList<>());
        recyclerView.setAdapter(chatRoomAdapter);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userId = currentUser.getUid();
        // Khởi tạo ViewModel
        chatRoomViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        // Tải dữ liệu ngay khi tạo Fragment để hiển thị ngay lập tức
        chatRoomViewModel.updateChatRoomDataSaler(userId);
        // Lắng nghe sự thay đổi từ LiveData
        chatRoomViewModel.getRoomChatSaler().observe(getViewLifecycleOwner(), chatRooms -> {
            if (chatRooms != null) {
                chatRoomAdapter.updateChatRooms(chatRooms); // Cập nhật adapter với dữ liệu mới
            }
        });

        // Xử lý làm mới dữ liệu với SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            chatRoomViewModel.updateChatRoomDataSaler(userId);  // Làm mới dữ liệu
            swipeRefreshLayout.setRefreshing(false); // Tắt hiệu ứng làm mới sau khi hoàn thành
        });
        chatRoomAdapter.setOnItemClickListenerRoom(new ChatRoomAdapter.OnItemClickListenerRoom() {
            @Override
            public void onItemClickRoom(ChatRoom chatRoom) {
                Intent intent = new Intent(getContext(), BuyTicketActivity.class);
                intent.putExtra("DocID",chatRoom.getDocId());
                startActivity(intent);
            }
        });

        return view;
    }
}