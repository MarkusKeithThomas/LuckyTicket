package ticket.luckyticket.buyer.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import ticket.luckyticket.buyer.model.TicketHistory;

public class HistoryFragmentBuyerRepository {
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private MutableLiveData<List<TicketHistory>> mutableLiveData = new MutableLiveData<>();
    private List<TicketHistory> ticketHistoryList = new ArrayList<>(); // Danh sách hiện tại

    public HistoryFragmentBuyerRepository() {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("TicketHistory");
    }

    public LiveData<List<TicketHistory>> getListTicketHistoryLiveData(String userID) {
        // Lắng nghe các thay đổi từ Firestore theo userID và cập nhật dữ liệu theo thời gian thực
        collectionReference.whereEqualTo("userBoughtID", userID)
                .orderBy("timeSaleBought", Query.Direction.DESCENDING)  // Sắp xếp theo thời gian giảm dần
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("Repository", "Listen failed", e);
                        return;
                    }

                    if (snapshot != null) {
                        ticketHistoryList.clear();  // Xóa danh sách cũ trước khi cập nhật
                        for (DocumentChange documentChange : snapshot.getDocumentChanges()) {
                            TicketHistory ticket = documentChange.getDocument().toObject(TicketHistory.class);
                            ticket.setDocumentIdTicketHistory(documentChange.getDocument().getId());

                            switch (documentChange.getType()) {
                                case ADDED:
                                    ticketHistoryList.add(ticket);  // Thêm mới
                                    break;
                                case MODIFIED:
                                    // Tìm và cập nhật vé đã chỉnh sửa
                                    int modifiedIndex = findTicketIndexById(ticket.getDocumentIdTicketHistory());
                                    if (modifiedIndex != -1) {
                                        ticketHistoryList.set(modifiedIndex, ticket);
                                    }
                                    break;
                                case REMOVED:
                                    // Xóa vé đã bị xóa
                                    int removedIndex = findTicketIndexById(ticket.getDocumentIdTicketHistory());
                                    if (removedIndex != -1) {
                                        ticketHistoryList.remove(removedIndex);
                                    }
                                    break;
                            }
                        }
                        mutableLiveData.setValue(ticketHistoryList); // Cập nhật LiveData với danh sách mới
                    }
                });

        return mutableLiveData;
    }

    // Tìm vị trí của vé dựa vào documentId
    private int findTicketIndexById(String documentId) {
        for (int i = 0; i < ticketHistoryList.size(); i++) {
            if (ticketHistoryList.get(i).getDocumentIdTicketHistory().equals(documentId)) {
                return i;
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }
}
