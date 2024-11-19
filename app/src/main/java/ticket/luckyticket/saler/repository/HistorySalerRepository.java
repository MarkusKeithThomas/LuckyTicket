package ticket.luckyticket.saler.repository;

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

public class HistorySalerRepository {
    private MutableLiveData<List<TicketHistory>> ticketHistoryLiveData = new MutableLiveData<>();
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    public HistorySalerRepository() {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("TicketHistory");
    }

    public LiveData<List<TicketHistory>> getAllTicketHistoryForSaler(String salerId) {
        collectionReference.whereEqualTo("userSaleID", salerId)
                .orderBy("timeSaleBought", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("Repository", "Listen failed", e);
                        return;
                    }

                    List<TicketHistory> ticketHistoryList = new ArrayList<>();

                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentChange documentChange : snapshot.getDocumentChanges()) {
                            TicketHistory ticket = documentChange.getDocument().toObject(TicketHistory.class);
                            ticketHistoryList.add(ticket);
                        }

                        // Cập nhật LiveData với danh sách mới
                        ticketHistoryLiveData.setValue(ticketHistoryList);
                    } else {
                        // Nếu snapshot rỗng, cập nhật LiveData với danh sách trống
                        ticketHistoryLiveData.setValue(ticketHistoryList);
                    }
                });

        return ticketHistoryLiveData;
    }
}
