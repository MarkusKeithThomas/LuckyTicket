package ticket.luckyticket.buyer.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ticket.luckyticket.buyer.model.ChattingAB;
import ticket.luckyticket.buyer.model.TicketHistory;

public class ChattingBuySaleRepository {

    // Lưu trữ dữ liệu tin nhắn cho từng cuộc trò chuyện
    private final Map<String, MutableLiveData<List<ChattingAB>>> chatLiveDataMap = new HashMap<>();
    private final FirebaseDatabase firebaseDatabase;
    private MutableLiveData<TicketHistory> ticketHistoryLive = new MutableLiveData<>();
    private FirebaseFirestore db;
    private CollectionReference collectionReference;



    public ChattingBuySaleRepository() {
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://vesomayman-dd0d8.firebaseio.com/");
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("TicketHistory");

    }

    public LiveData<TicketHistory> getOneTicket(String documentIdTicketHistory) {
        collectionReference.whereEqualTo("documentIdTicketHistory", documentIdTicketHistory)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        // Log lỗi ra Logcat để dễ debug
                        Log.w("FirestoreError", "Listen failed.", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        // Lấy document đầu tiên (vì documentIdTicketHistory là duy nhất)
                        DocumentSnapshot document = snapshots.getDocuments().get(0);
                        TicketHistory ticketHistory1 = document.toObject(TicketHistory.class);
                        ticketHistoryLive.setValue(ticketHistory1);
                    } else {
                        // Xử lý trường hợp không có kết quả trả về
                        ticketHistoryLive.setValue(null);
                        Log.d("FirestoreData", "No such document with documentIdTicketHistory: " + documentIdTicketHistory);
                    }
                });
        return ticketHistoryLive;
    }

    // Tạo chatId duy nhất dựa trên hai userId
    private String createChatRoomId(String senderId,String ortheId, String docId) {
        if (senderId.compareTo(ortheId) < 0) {
            return senderId + "_" + ortheId + "_"+docId;
        } else {
            return ortheId + "_" + senderId + "_"+docId;
        }
    }

    // Gửi tin nhắn trong cuộc trò chuyện giữa hai người dùng
    public void sendMessage(String saleId,String buyId, String messageContent,String docId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String senderId = null;
        if (firebaseAuth.getCurrentUser() != null) {
            senderId = firebaseAuth.getCurrentUser().getUid();
        } else {
            Log.w("ChattingBuySaleRepository", "User chưa đăng nhập. senderId là null.");
            // Xử lý khi người dùng chưa đăng nhập, chẳng hạn thông báo hoặc chuyển đến màn hình đăng nhập
        }
        String chatRoomId = createChatRoomId(saleId,buyId,docId);
        DatabaseReference messageRef = firebaseDatabase.getReference("chats").child(chatRoomId).child("messages");


        // Tạo một khóa duy nhất cho tin nhắn
        String messageId = messageRef.push().getKey();
        if (messageId == null){
            Log.d("messageId", "null");
        } else {
            Log.d("messageId", "Co"+messageId);

        }
        //String senderId1 = getCurrentUserId();

        if (messageContent != null && !messageContent.trim().isEmpty() ) {
            if (messageId != null) {
                // Tạo đối tượng tin nhắn
                ChattingAB chattingAB = new ChattingAB(chatRoomId,senderId, messageContent, System.currentTimeMillis());
                Log.d("ChattingBuySaleRepository", "Database path: " + messageRef.child(messageId).getPath().toString());


                // Lưu tin nhắn vào Firebase
                Log.d("ChattingBuySaleRepository", "Bắt đầu gửi tin nhắn với messageId: " + messageId);
                try {
                    messageRef.child(messageId).setValue(chattingAB)
                            .addOnSuccessListener(aVoid -> Log.d("ChattingBuySaleRepository", "Tin nhắn đã được gửi thành công firebase."))
                            .addOnFailureListener(e -> Log.w("ChattingBuySaleRepository", "Lỗi khi gửi tin nhắn", e));
                } catch (Exception e) {
                    Log.e("ChattingBuySaleRepository", "Exception khi gửi tin nhắn", e);
                }

                Log.d("ChattingBuySaleRepository", "Hoàn tất gọi setValue cho tin nhắn");


            } else {
                Log.w("Chat", "Không thể tạo ID tin nhắn.");
            }
        } else {
            Log.w("ChattingBuySaleRepository", "Nội dung tin nhắn trống hoặc chỉ chứa khoảng trắng.");
        }
    }

    // Phương thức để lấy danh sách tin nhắn và trả về MutableLiveData
    public MutableLiveData<List<ChattingAB>> getMessages(String senderId,String ortheId,String docId) {

        // Tạo ID chat theo hai UID để đảm bảo duy nhất
        String chatId = senderId.compareTo(ortheId) < 0 ? senderId + "_" + ortheId + "_" + docId : ortheId + "_" + senderId + "_" + docId;
        DatabaseReference chatRef = firebaseDatabase.getReference("chats").child(chatId).child("messages");

        // Kiểm tra xem chatId đã có trong Map chưa, nếu chưa thì tạo mới
        if (!chatLiveDataMap.containsKey(chatId)) {
            chatLiveDataMap.put(chatId, new MutableLiveData<>());
        }

        MutableLiveData<List<ChattingAB>> listMutableLiveData = chatLiveDataMap.get(chatId);
        List<ChattingAB> chattingList = new ArrayList<>();

        // Lắng nghe thay đổi liên tục từ Firebase
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chattingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChattingAB chattingAB = dataSnapshot.getValue(ChattingAB.class);
                    if (chattingAB != null) {
                        chattingList.add(chattingAB);
                    }
                }
                // Cập nhật dữ liệu cho LiveData khi có thay đổi
                listMutableLiveData.setValue(chattingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Chat", "Lỗi khi lấy tin nhắn", error.toException());
            }
        });
        return listMutableLiveData;
    }


}
