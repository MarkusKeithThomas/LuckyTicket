package ticket.luckyticket.saler.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import ticket.luckyticket.saler.model.User;

public class ProfileFragmentSalerRepository {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private MutableLiveData<User> userSalerMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorGetFireUser = new MutableLiveData<>();
    public ProfileFragmentSalerRepository(){
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("User");
        auth = FirebaseAuth.getInstance();
    }
    public LiveData<User> getOneUserSaler() {
        String userSalerId = auth.getCurrentUser().getUid();
        if (userSalerId != null) {
            // Lắng nghe liên tục các thay đổi từ Firestore
            collectionReference.document(userSalerId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                // Xử lý lỗi nếu có
                                errorGetFireUser.setValue(e.getMessage());
                                return;
                            }

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                // Chuyển đổi DocumentSnapshot thành đối tượng User
                                User user = documentSnapshot.toObject(User.class);
                                userSalerMutableLiveData.setValue(user);
                            } else {
                                // Tài liệu không tồn tại hoặc bị xóa
                                userSalerMutableLiveData.setValue(null);
                            }
                        }
                    });
        }
        return userSalerMutableLiveData;
    }

}
