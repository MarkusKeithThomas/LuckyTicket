package ticket.luckyticket.buyer.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.saler.model.TicketInformation;
import ticket.luckyticket.saler.model.User;

public class BuyFragmentRepository {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private CollectionReference collectionReferenceTicketHistory;
    private CollectionReference collectionReferenceUser;
    private CollectionReference dbMoneyOneBuyer;
    private FirebaseStorage storage;

    private List<TicketInformation> ticketInformationList = new ArrayList<>(); // Danh sách hiện tại

    public BuyFragmentRepository() {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("TicketInformation");
        collectionReferenceTicketHistory = db.collection("TicketHistory");
        collectionReferenceUser = db.collection("User");
        storage = FirebaseStorage.getInstance();
    }

    public LiveData<TicketInformation>  getOneTicket(String documentID){
        MutableLiveData<TicketInformation> mutableLiveData = new MutableLiveData<>();
        collectionReference.document(documentID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    TicketInformation ticketInformation = document.toObject(TicketInformation.class);
                    mutableLiveData.setValue(ticketInformation);
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
        return mutableLiveData;
    }

    public void addNewTicket(TicketHistory ticketHistory) {
        collectionReference.add(ticketHistory)
                .addOnSuccessListener(task -> {
                    Log.d("Repository", "TicketHistory added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Error adding ticket", e);
                });
    }

    public void deleteTicket(String documentID) {
        collectionReference.document(documentID).delete()
                .addOnSuccessListener(task -> {
                    Log.d("Repository", "Ticket deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Error deleting ticket", e);
                });
    }

    public void deleteImage(String imageUrl) {
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        imageRef.delete()
                .addOnSuccessListener(task -> {
                    Log.d("Repository", "Image deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Error deleting image", e);
                });
    }

    public void editTicket(String documentID, TicketInformation ticketInformation) {
        collectionReference.document(documentID).set(ticketInformation)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Repository", "Ticket updated with ID: " + documentID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Error updating ticket", e);
                });
    }

    public void updateTicket(String documentID, String newTicketSaleRemain ) {
        // Sử dụng một Map để lưu trữ các trường cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("numberTicketSale", newTicketSaleRemain);  // Trường dữ liệu bạn đã có

        // Thực hiện cập nhật
        collectionReference.document(documentID).update(updates)
                .addOnSuccessListener(task -> {
                    Log.d("Repository", "Ticket updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Error updating ticket", e);
                });
    }


    // Phương thức lấy tất cả dữ liệu từ Firestore
    public LiveData<List<TicketInformation>> getAllTicketInformationForYesterdayAndToday() {
        MutableLiveData<List<TicketInformation>> mutableLiveData = new MutableLiveData<>();
        List<TicketInformation> ticketInformationList = new ArrayList<>();

        // Lấy thời gian bắt đầu của hôm qua
        Timestamp yesterdayStart = getStartOfYesterday(Timestamp.now());

        // Lấy thời gian bắt đầu của ngày mai (kết thúc của ngày hôm nay)
        Timestamp tomorrowStart = getStartOfNextDay(Timestamp.now());

        collectionReference
                .orderBy("timeAdd", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("timeAdd", yesterdayStart)
                .whereLessThan("timeAdd", tomorrowStart)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("Repository", "Listen failed", e);
                        return;
                    }

                    if (snapshot != null) {
                        for (DocumentChange documentChange : snapshot.getDocumentChanges()) {
                            TicketInformation ticket = documentChange.getDocument().toObject(TicketInformation.class);
                            switch (documentChange.getType()) {
                                case ADDED:
                                    ticketInformationList.add(ticket);  // Thêm mới
                                    break;
                                case MODIFIED:
                                    int modifiedIndex = findTicketIndexById(ticket.getDocumentId());
                                    if (modifiedIndex != -1) {
                                        ticketInformationList.set(modifiedIndex, ticket);
                                    }
                                    break;
                                case REMOVED:
                                    int removedIndex = findTicketIndexById(ticket.getDocumentId());
                                    if (removedIndex != -1) {
                                        ticketInformationList.remove(removedIndex);
                                    }
                                    break;
                            }
                        }
                        mutableLiveData.postValue(ticketInformationList); // Cập nhật dữ liệu
                    }
                });

        return mutableLiveData;
    }

    // Hàm để lấy thời gian bắt đầu của hôm qua
    private Timestamp getStartOfYesterday(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.toDate().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // Lùi về một ngày trước
        return new Timestamp(new Date(calendar.getTimeInMillis()));
    }

    // Hàm để lấy thời gian bắt đầu của ngày mai (kết thúc của ngày hôm nay)
    private Timestamp getStartOfNextDay(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.toDate().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);  // Chuyển sang ngày mai
        return new Timestamp(new Date(calendar.getTimeInMillis()));
    }


    // Tìm vị trí của vé dựa vào documentId
    private int findTicketIndexById(String documentId) {
        for (int i = 0; i < ticketInformationList.size(); i++) {
            if (ticketInformationList.get(i).getDocumentId().equals(documentId)) {
                return i;
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }
    public void upLoadTicketHistory(TicketHistory ticketHistory) {
        collectionReferenceTicketHistory.add(ticketHistory).addOnSuccessListener(task -> {
        }).addOnFailureListener(e -> {

        });
    }
    public void updateMoneySaler(String salerId, double deductionAmount) {
        // Chuyển đổi deductionAmount thành số nguyên để thực hiện phép cộng tiền
        double deduction = deductionAmount;

        // Truy vấn để lấy tài liệu người bán dựa trên salerId
        collectionReferenceUser.document(salerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        // Lấy giá trị tiền hiện tại từ tài liệu
                        DocumentSnapshot document = task.getResult();
                        String currentMoney = document.getString("moneyAccount");
                        double currentMoneyDouble = Double.parseDouble(currentMoney);


                        if (currentMoneyDouble > 0) {
                            // Tính số tiền mới sau khi trừ
                            double newMoney = currentMoneyDouble + deduction;


                            // Kiểm tra nếu số tiền không âm
                            if (newMoney < 0) {
                                Log.w("FirestoreUpdate", "Số tiền không moi.");
                                return;
                            }
                            String newMoneyString = String.valueOf(newMoney);

                            // Cập nhật giá trị mới cho trường moneyAccount
                            collectionReferenceUser.document(salerId)
                                    .update("moneyAccount", newMoneyString)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FirestoreUpdate", "moneyAccount updated successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("FirestoreUpdate", "Error updating moneyAccount", e);
                                    });
                        } else {
                            Log.w("FirestoreUpdate", "Không tìm thấy trường moneyAccount.");
                        }
                    } else {
                        Log.w("FirestoreUpdate", "Không tìm thấy tài liệu với salerId: " + salerId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreUpdate", "Error retrieving document", e);
                });
    }
    public void updateMoneyBuyer(String buyerId, double deductionAmount) {
        // Chuyển đổi deductionAmount thành số nguyên để thực hiện phép trừ tiền
        double deduction = deductionAmount;

        // Truy vấn để lấy tài liệu người bán dựa trên salerId
        collectionReferenceUser.document(buyerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        // Lấy giá trị tiền hiện tại từ tài liệu
                        DocumentSnapshot document = task.getResult();
                        String currentMoney = document.getString("moneyAccount");
                        double currentMoneyDouble = Double.parseDouble(currentMoney);

                        if (currentMoneyDouble > 0) {
                            // Tính số tiền mới sau khi trừ
                            double newMoney = currentMoneyDouble - deduction;
                            Log.w("currentMoneyDouble", "Số tiền không moi." + newMoney);


                            // Kiểm tra nếu số tiền không âm
                            if (newMoney < 0) {
                                Log.w("FirestoreUpdate", "Số tiền không đủ giao dịch.");
                                return;
                            }
                            String newMoneyString = String.valueOf(newMoney);

                            // Cập nhật giá trị mới cho trường moneyAccount
                            collectionReferenceUser.document(buyerId)
                                    .update("moneyAccount", newMoneyString)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FirestoreUpdate", "moneyAccount updated successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("FirestoreUpdate", "Error updating moneyAccount", e);
                                    });
                        } else {
                            Log.w("FirestoreUpdate", "Không tìm thấy trường moneyAccount.");
                        }
                    } else {
                        Log.w("FirestoreUpdate", "Không tìm thấy tài liệu với salerId: " + buyerId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreUpdate", "Error retrieving document", e);
                });
    }
    public LiveData<User> getOneBuyerMoney(String buyerId) {
        MutableLiveData<User> userSalerLiveData = new MutableLiveData<>();

        // Truy vấn Firestore để lấy thông tin người dùng dựa trên buyerId
        collectionReferenceUser.document(buyerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Chuyển đổi document thành đối tượng UserSaler
                        User userBuyer = documentSnapshot.toObject(User.class);
                        // Cập nhật LiveData với dữ liệu mới
                        userSalerLiveData.setValue(userBuyer);
                    } else {
                        Log.w("Firestore", "No such document found!");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting document", e);
                });

        // Trả về LiveData để có thể quan sát
        return userSalerLiveData;
    }
    public LiveData<List<TicketInformation>> getDataByQuery(String query){

        MutableLiveData<List<TicketInformation>> ticketInformationMutableLiveData = new MutableLiveData<>();
        String filterPattern = query.toLowerCase().trim();
        // Lọc dữ liệu trực tiếp từ Firestore
        collectionReference
                .orderBy("timeAdd", Query.Direction.DESCENDING)  // Sắp xếp theo thời gian giảm dần
                .limit(1000)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<TicketInformation> filteredTicketList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            TicketInformation ticket = document.toObject(TicketInformation.class);
                            if (ticket.getNumberSixTicket() != null && ticket.getNumberSixTicket().endsWith(filterPattern)) {
                                filteredTicketList.add(ticket);
                            }
                        }

                        ticketInformationMutableLiveData.postValue(filteredTicketList);  // Thêm vé phù hợp vào danh sách
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
        return ticketInformationMutableLiveData;
    }
}
