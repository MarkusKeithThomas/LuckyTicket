package ticket.luckyticket.saler.repository;

import android.util.Log;

import androidx.annotation.Nullable;
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

import ticket.luckyticket.saler.model.RatingForSaler;
import ticket.luckyticket.saler.model.User;

public class RatingForSalerRepository {
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private CollectionReference collectionReferenceSendComment;

    public RatingForSalerRepository(){
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("User");
        collectionReferenceSendComment =  firestore.collection("RatingForSaler");
    }

    public LiveData<User> getInformationOneSaler(String salerId){
        MutableLiveData<User> liveData = new MutableLiveData<>();
        collectionReference.document(salerId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null){
                            User userSaler = value.toObject(User.class);
                            liveData.setValue(userSaler);
                        }

                    }
                });
        return liveData;
    }
    public void sendComment(RatingForSaler ratingForSaler){
        // Tạo tài liệu mới với ID tự động được tạo bởi Firestore
        collectionReferenceSendComment.document()
                .set(ratingForSaler)
                .addOnSuccessListener(task -> {
                    // Xử lý khi gửi thành công
                    Log.d("Firestore", "Comment sent successfully with auto-generated ID");
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi gửi thất bại
                    Log.e("Firestore", "Failed to send comment", e);
                });
    }

    public LiveData<List<RatingForSaler>> getComments(String salerId) {
        MutableLiveData<List<RatingForSaler>> liveData = new MutableLiveData<>();

        collectionReferenceSendComment
                .whereEqualTo("salerId", salerId) // Truy vấn tất cả comment của Saler
                .orderBy("timeStamp", Query.Direction.DESCENDING) // Sắp xếp theo thời gian mới nhất
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        if (value != null) {
                            List<RatingForSaler> commentList = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                RatingForSaler ratingForSaler = documentSnapshot.toObject(RatingForSaler.class);
                                commentList.add(ratingForSaler);
                            }
                            liveData.setValue(commentList);
                        }
                    }
                });
        return liveData;
    }
    public void updateRatingForSaler (String ratingSaler,String salerId){
        double rating = Double.parseDouble(ratingSaler);
        collectionReference.document(salerId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().exists()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String currentRating = documentSnapshot.getString("rating");
                        double currentRatingDouble = Double.parseDouble(currentRating);
                        double newRatingSalerDouble = (currentRatingDouble+rating)/2;
                        String newRatingSalerString = String.valueOf(newRatingSalerDouble);
                        collectionReference.document(salerId)
                                .update("rating",newRatingSalerString)
                                .addOnSuccessListener(aVoid ->{

                                });

                    }
                });


    }
    public void updateMoneySaler(String salerId, String deductionAmount) {
        // Chuyển đổi deductionAmount thành số nguyên để thực hiện phép trừ
        double deduction = Integer.parseInt(deductionAmount);

        // Truy vấn để lấy tài liệu người bán dựa trên salerId
        collectionReference.document(salerId)
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
                            collectionReference.document(salerId)
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
    public void updateMoneyBuyer(String userId, String deductionAmount) {
        // Chuyển đổi deductionAmount thành số nguyên để thực hiện phép trừ
        double deduction = Integer.parseInt(deductionAmount);

        // Truy vấn để lấy tài liệu người bán dựa trên salerId
        collectionReference.document(userId)
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

                            // Kiểm tra nếu số tiền không âm
                            if (newMoney < 0) {
                                Log.w("FirestoreUpdate", "Số tiền không đủ giao dịch.");
                                return;
                            }
                            String newMoneyString = String.valueOf(newMoney);

                            // Cập nhật giá trị mới cho trường moneyAccount
                            collectionReference.document(userId)
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
                        Log.w("FirestoreUpdate", "Không tìm thấy tài liệu với salerId: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreUpdate", "Error retrieving document", e);
                });
    }
}
