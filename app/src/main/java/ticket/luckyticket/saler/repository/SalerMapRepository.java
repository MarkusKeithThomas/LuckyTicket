package ticket.luckyticket.saler.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ticket.luckyticket.saler.model.SalerMap;
import ticket.luckyticket.saler.model.TicketInformation;

public class SalerMapRepository {
    DatabaseReference dbreal = FirebaseDatabase.getInstance("https://vesomayman-dd0d8.firebaseio.com/").getReference("Salers");
    private MutableLiveData<List<SalerMap>> salerListLiveData;
    private MutableLiveData<List<TicketInformation>> ticketLiveData;
    private FirebaseFirestore db;
    public SalerMapRepository() {
        // Tham chiếu tới node "Drivers" trong Firebase
        salerListLiveData = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        ticketLiveData = new MutableLiveData<>();
    }
    //Phuong thức lấy các danh sách vé bán của một id cụ thể
    public LiveData<List<TicketInformation>> getAllTicketForOneSaler(String userId){
        db.collection("TicketInformation").whereEqualTo("userId",userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            return;
                        }
                        if (value == null) {
                            return;
                        }

                        List<TicketInformation> ticketInformationList = new ArrayList<>();
                        // Lặp qua từng thay đổi trong các document
                        for (DocumentSnapshot document : value.getDocuments()) {
                            if (document.exists()) {
                                String documentId = document.getId();
                                String nameDaiTicket = document.getString("nameDaiTicket");
                                String numberSixTicket = document.getString("numberSixTicket");
                                String numberTicketSale = document.getString("numberTicketSale");
                                String imagePath = document.getString("imagePath");
                                Timestamp timeAdd = document.getTimestamp("timeAdd");
                                TicketInformation ticketInformation1 = new TicketInformation(imagePath,nameDaiTicket,numberSixTicket,numberTicketSale,timeAdd,userId);
                                ticketInformation1.setDocumentId(documentId);
                                ticketInformationList.add(ticketInformation1);
                                //Log.d("ticketInformationList?", "data" + numberSixTicket);


                            }
                        }
                        ticketLiveData.setValue(ticketInformationList);

                    }

                });
        return ticketLiveData;
    }

    // Phương thức lấy danh sách tất cả người bán hàng đang online
    public LiveData<List<SalerMap>> getAllSalerMap() {
        // Truy vấn những saler có isOnline = true
        Query query = dbreal.orderByChild("isOnline").equalTo(true);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SalerMap> onlineSalers = new ArrayList<>();
                for (DataSnapshot salerSnapshot : snapshot.getChildren()) {
                    // Lấy dữ liệu từ Firebase và gán vào đối tượng SalerMap
                    boolean isOnline = salerSnapshot.child("isOnline").getValue(Boolean.class);
                    double latitude = salerSnapshot.child("latitude").getValue(Double.class);
                    double longitude = salerSnapshot.child("longitude").getValue(Double.class);
                    String userId = salerSnapshot.child("userId").getValue(String.class);

                    if (isOnline = true && isOnline) {  // Kiểm tra nếu isOnline là true
                        SalerMap salerMap = new SalerMap(userId, latitude, longitude, isOnline);
                        onlineSalers.add(salerMap);
                    }
                }
                // Cập nhật dữ liệu vào LiveData
                salerListLiveData.setValue(onlineSalers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read salers: " + error.getMessage());
            }
        });
        return salerListLiveData;
    }
    public void updateSalerLocation(String salerId, double latitude, double longitude, boolean isOnline) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("userId", salerId);
        updates.put("latitude", latitude);
        updates.put("longitude", longitude);
        updates.put("isOnline", isOnline);

        Log.d("SalerMapRepository", "Latitude: " + longitude + ", Longitude: " + latitude + "userId " + salerId);

        dbreal.child(salerId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("SalerMapRepository", "Đã cập nhật vị trí và trạng thái người bán thành công."))
                .addOnFailureListener(e -> Log.e("SalerMapRepository", "Lỗi khi cập nhật vị trí người bán: " + e.getMessage()));
        Log.d("SalerMapRepositorys", "Latitude: " + longitude + ", Longitude: " + latitude + "userId " + salerId);

    }


    }

