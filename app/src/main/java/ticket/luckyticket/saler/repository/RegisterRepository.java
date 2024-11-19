package ticket.luckyticket.saler.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ticket.luckyticket.saler.model.User;

public class RegisterRepository {
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private MutableLiveData<String> errorLiveData;
    private final MutableLiveData<Boolean> isUploadSuccessful = new MutableLiveData<>();

    private Context context;
    public RegisterRepository(Context context){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        this.context = context;
        errorLiveData = new MutableLiveData<>();
    }
    public LiveData<Boolean> getIsUploadSuccessful() {
        return isUploadSuccessful;
    }
    public void createUser(Uri uri, User user) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.d("RegisterRepository", "User registered with UID: " + currentUser.getUid());
                    uploadImageToFirebase(uri, user, currentUser.getUid());  // Gọi hàm upload ảnh và lưu dữ liệu vào Firestore
                } else {
                    Log.e("RegisterRepository", "Current user is null after registration.");
                }
            } else {
                errorLiveData.setValue(task.getException().getMessage());  // Đưa thông báo lỗi vào LiveData để Activity quan sát
                Log.e("FirebaseAuth", "Error creating user: " + task.getException().getMessage());
            }
        });
    }
    public void uploadImageToFirebase(Uri uri, User user, String userId) {
        isUploadSuccessful.setValue(false);  // Đặt mặc định là false khi bắt đầu upload
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        StorageReference avatarRef = FirebaseStorage.getInstance().getReference()
                                .child("avatar/" + System.currentTimeMillis() + ".jpg");

                        UploadTask uploadTask = avatarRef.putBytes(data);
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            avatarRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                String downloadUrl = downloadUri.toString();
                                Log.d("FirebaseStorage", "Upload successful, Download URL: " + downloadUrl);
                                // Tiếp tục xử lý nếu cần, ví dụ lưu URL vào Firestore
                                user.setImage_Url_Avatar(downloadUrl);  // Thiết lập URL của ảnh đại diện
                                user.setPassword("");  // Xóa mật khẩu để đảm bảo bảo mật
                                user.setUserId(userId);

                                firebaseFirestore.collection("User")
                                        .document(userId)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "User info saved successfully.");
                                            isUploadSuccessful.setValue(true);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Failed to save user info: " + e.getMessage());
                                        });

                                Log.d("FirebaseStorage", "Upload successful, Download URL: " + downloadUrl);
                            });

                        }).addOnFailureListener(e -> {
                            Log.e("FirebaseStorage", "Upload failed: " + e.getMessage());
                        });


                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

//        if (uri != null) {
//            Log.d("Uri123", uri.toString());
//            try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
//                StorageReference avatarRef = FirebaseStorage.getInstance().getReference()
//                        .child("avatar/" + System.currentTimeMillis() + ".jpg");
//
//                UploadTask uploadTask = avatarRef.putStream(inputStream);
//                uploadTask.addOnSuccessListener(taskSnapshot -> {
//                    avatarRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
//                        String downloadUrl = downloadUri.toString();
//                        user.setImage_Url_Avatar(downloadUrl);  // Thiết lập URL của ảnh đại diện
//                        user.setPassword("");  // Xóa mật khẩu để đảm bảo bảo mật
//                        user.setUserId(userId);
//
//                        firebaseFirestore.collection("User")
//                                .document(userId)
//                                .set(user)
//                                .addOnSuccessListener(aVoid -> {
//                                    Log.d("Firestore", "User info saved successfully.");
//                                })
//                                .addOnFailureListener(e -> {
//                                    Log.e("Firestore", "Failed to save user info: " + e.getMessage());
//                                });
//
//                        Log.d("FirebaseStorage", "Upload successful, Download URL: " + downloadUrl);
//                    });
//                }).addOnFailureListener(e -> {
//                    Log.e("FirebaseStorage", "Upload failed: " + e.getMessage());
//                });
//
//            } catch (FileNotFoundException e) {
//                Log.e("RegisterActivity", "File not found: " + e.getMessage());
//            } catch (IOException e) {
//                Log.e("RegisterActivity", "Error closing input stream: " + e.getMessage());
//            }
//        } else {
//            Log.e("RegisterActivity", "No URI found");
//        }
//    }

    public LiveData<String> getErrorFireBase(){
        return errorLiveData;
    }










}
