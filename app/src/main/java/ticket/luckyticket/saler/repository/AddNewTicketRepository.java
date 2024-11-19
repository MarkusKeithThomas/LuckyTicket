package ticket.luckyticket.saler.repository;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ticket.luckyticket.saler.AddNewTicketActivity;
import ticket.luckyticket.saler.model.TicketInformation;

public class AddNewTicketRepository {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ExecutorService executorServiceUpLoadImage;
    private String userId;
    private MutableLiveData<String> errorUpload = new MutableLiveData<>();
    private List<TicketInformation> ticketInformationList = new ArrayList<>(); // Danh sách hiện tại

    public AddNewTicketRepository(){
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("TicketInformation");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        executorServiceUpLoadImage = Executors.newSingleThreadExecutor();
        userId = firebaseAuth.getCurrentUser().getUid();
    }
    public void updateTicket(String documentID, String newTicketSaleRemain) {
        collectionReference.document(documentID).update(
                "numberTicketSale", newTicketSaleRemain,
                        "timeAdd", Timestamp.now() // Bạn có thể cập nhật thời gian hiện tại hoặc giữ thời gian cũ
                )
                .addOnSuccessListener(task -> {
                    Log.d("Repository", "Ticket updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Error updating ticket", e);
                });
    }
    public void updateTicketWithoutTime(String documentID, String newTicketSaleRemain) {
        collectionReference.document(documentID).update(
                        "numberTicketSale", newTicketSaleRemain)
                .addOnSuccessListener(task -> {
                    Log.d("Repository", "Ticket updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Error updating ticket", e);
                });
    }




    public void uploadImageTicketInformationToFirebase(Uri uri, TicketInformation ticketInformation) {
        if (uri != null && uri.getLastPathSegment() != null) {
            StorageReference imageRef = storageReference.child("/images/" + uri.getLastPathSegment());
            executorServiceUpLoadImage.execute(() -> {
                imageRef.putFile(uri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                String imageUrL = downloadUri.toString();
                                if (ticketInformation != null) {
                                    // Gán đường dẫn ảnh vào ticketInformation
                                    ticketInformation.setImagePath(imageUrL);

                                    // Tạo tài liệu trong Firestore với documentID tự động
                                    collectionReference.add(ticketInformation)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful() && task.getResult() != null) {
                                                    // Lấy documentID tự động từ Firestore
                                                    String documentId = task.getResult().getId();
                                                    // Cập nhật documentID vào đối tượng ticketInformation
                                                    ticketInformation.setDocumentId(documentId);

                                                    // Cập nhật lại tài liệu với documentID đã được gán
                                                    collectionReference.document(documentId)
                                                            .set(ticketInformation)
                                                            .addOnSuccessListener(aVoid -> {
                                                                errorUpload.setValue("Gửi vé thành công.");
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                errorUpload.setValue("Lỗi quá trình cập nhật.");
                                                            });
                                                } else {
                                                    errorUpload.setValue("Lỗi quá trình tải.");
                                                }
                                            });
                                } else {
                                    // Xử lý trường hợp người dùng chưa đăng nhập
                                    errorUpload.setValue("Bạn cần đăng nhập.");
                                }
                            });
                        });
            });
        } else {
            // Xử lý trường hợp URI không hợp lệ
            errorUpload.setValue("Ảnh vé số không được trống.");
        }
    }

    public LiveData<String> errorLoadTicket(){
        return errorLoadTicket();
    }
    public LiveData<List<TicketInformation>> getAllTicketInformation(String userSalerID) {
        MutableLiveData<List<TicketInformation>> mutableLiveData = new MutableLiveData<>();

        collectionReference.whereEqualTo("userId",userSalerID)
                .orderBy("timeAdd", Query.Direction.DESCENDING) // Sắp xếp theo thời gian mới nhất (giảm dần)
                .addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("getAllTicketInformationRepository", "Listen failed", e);
                return;
            }

            if (snapshot != null) {
                ticketInformationList.clear();  // Xóa danh sách cũ
                for (DocumentChange documentChange : snapshot.getDocumentChanges()) {
                    TicketInformation ticket = documentChange.getDocument().toObject(TicketInformation.class);
                    ticket.setDocumentId(documentChange.getDocument().getId());

                    switch (documentChange.getType()) {
                        case ADDED:
                            ticketInformationList.add(ticket);  // Thêm mới
                            break;
                        case MODIFIED:
                            // Tìm và cập nhật vé đã chỉnh sửa
                            int modifiedIndex = findTicketIndexById(ticket.getDocumentId());
                            if (modifiedIndex != -1) {
                                ticketInformationList.set(modifiedIndex, ticket);
                            }
                            break;
                        case REMOVED:
                            // Xóa vé đã bị xóa
                            int removedIndex = findTicketIndexById(ticket.getDocumentId());
                            if (removedIndex != -1) {
                                ticketInformationList.remove(removedIndex);
                            }
                            break;
                    }
                }
                mutableLiveData.setValue(ticketInformationList); // Cập nhật dữ liệu
            } else {
                if (snapshot != null && snapshot.isEmpty()) {
                    ticketInformationList.clear();  // Xóa dữ liệu cũ
                    mutableLiveData.setValue(ticketInformationList); // Trả về danh sách trống
                }
            }
        });

        return mutableLiveData;
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


}

