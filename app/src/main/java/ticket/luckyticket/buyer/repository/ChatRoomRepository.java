package ticket.luckyticket.buyer.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ticket.luckyticket.buyer.model.ChatRoom;

public class ChatRoomRepository {
    private FirebaseFirestore db;
    private CollectionReference collectionChatRoom;
    private MutableLiveData<List<ChatRoom>> chatRoomsLiveData = new MutableLiveData<>();

    public ChatRoomRepository() {
        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();
        // Khởi tạo CollectionReference
        collectionChatRoom = db.collection("TicketHistory");

    }

    // Lắng nghe sự thay đổi của dữ liệu trong Firestore theo thời gian thực
    public LiveData<List<ChatRoom>> getRoomChat(String userId) {
        if (!userId.isEmpty()) {
            // Thực hiện query để lấy các phòng chat của user
            collectionChatRoom.whereEqualTo("userBoughtID", userId)
                    .orderBy("timeSaleBought", Query.Direction.DESCENDING)  // Sắp xếp theo trường thời gian giảm dần
                    .limit(50)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Firestore Error", "Listen failed.", e);
                                return;
                            }

                            if (snapshots != null) {
                                List<ChatRoom> chatRooms = new ArrayList<>();
                                if (!snapshots.isEmpty()) {
                                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                                        ChatRoom chatRoom = document.toObject(ChatRoom.class);
                                        if (chatRoom != null) {
                                            String documentIdTicketHistory = document.getString("documentIdTicketHistory");
                                            String userBoughtID = document.getString("userBoughtID");
                                            String userSaleID = document.getString("userSaleID");
                                            String chatId = userSaleID.compareTo(userBoughtID) < 0 ? userSaleID + "_" + userBoughtID + "_" + documentIdTicketHistory : userBoughtID + "_" + userSaleID + "_" + documentIdTicketHistory;
                                            String ticketSixNumber = document.getString("ticketSixNumber");
                                            ChatRoom chatRoom1 = new ChatRoom(chatId, documentIdTicketHistory, ticketSixNumber);
                                            chatRooms.add(chatRoom1);
                                        }
                                    }
                                }
                                // Cập nhật MutableLiveData với danh sách phòng chat mới hoặc danh sách trống
                                chatRoomsLiveData.setValue(chatRooms);
                            }

                        }
                    });
        } else {
            if (userId == null || userId.isEmpty()) {
                Log.e("UserIdError", "userId is empty, cannot listen to data");
                chatRoomsLiveData.setValue(new ArrayList<>());  // Trả về danh sách trống nếu userId rỗng
                return chatRoomsLiveData;
            }

            Log.e("UserIdError", "userId is empty, cannot listen to data");
        }
        return chatRoomsLiveData;

    }
    public LiveData<List<ChatRoom>> getRoomChatSaler(String userId) {
        if (!userId.isEmpty()) {
            // Thực hiện query để lấy các phòng chat của user
            collectionChatRoom.whereEqualTo("userSaleID", userId)
                    .orderBy("timeSaleBought", Query.Direction.DESCENDING)  // Sắp xếp theo trường thời gian giảm dần
                    .limit(50)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Firestore Error", "Listen failed.", e);
                                return;
                            }

                            if (snapshots != null) {

                                List<ChatRoom> chatRooms = new ArrayList<>();
                                if (!snapshots.isEmpty()) {
                                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                                        ChatRoom chatRoom = document.toObject(ChatRoom.class);

                                        if (chatRoom != null) {
                                            String documentIdTicketHistory = document.getString("documentIdTicketHistory");
                                            String userBoughtID = document.getString("userBoughtID");
                                            String userSaleID = document.getString("userSaleID");
                                            String chatId = userSaleID.compareTo(userBoughtID) < 0 ? userSaleID + "_" + userBoughtID + "_" + documentIdTicketHistory : userBoughtID + "_" + userSaleID + "_" + documentIdTicketHistory;
                                            String ticketSixNumber = document.getString("ticketSixNumber");
                                            ChatRoom chatRoom1 = new ChatRoom(chatId, documentIdTicketHistory, ticketSixNumber);
                                            chatRooms.add(chatRoom1);
                                        }
                                    }
                                }
                                // Cập nhật MutableLiveData với danh sách phòng chat mới hoặc danh sách trống
                                chatRoomsLiveData.setValue(chatRooms);
                            }

                        }
                    });
        } else {
            if (userId == null || userId.isEmpty()) {
                Log.e("UserIdError", "userId is empty, cannot listen to data");
                chatRoomsLiveData.setValue(new ArrayList<>());  // Trả về danh sách trống nếu userId rỗng
                return chatRoomsLiveData;
            }

            Log.e("UserIdError", "userId is empty, cannot listen to data");
        }
        return chatRoomsLiveData;

    }
}
