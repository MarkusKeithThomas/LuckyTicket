package ticket.luckyticket.saler.viewmodel;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ticket.luckyticket.saler.repository.SalerMapRepository;


public class SignInSalerViewModel extends ViewModel {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    public SignInSalerViewModel(){
        salerMapRepository = new SalerMapRepository();
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    public LiveData<FirebaseUser> getFireUser(){
        return userLiveData;
    }
    private SalerMapRepository salerMapRepository;

    public void signInEmailPassWor(String email,String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    userLiveData.postValue(mAuth.getCurrentUser());
                } else {
                    errorLiveData.setValue("Vui lòng kiểm tra lại email và password.");
                }
            }
        });
    }
    // Đặt trạng thái Remember Me vào LiveData để Activity xử lý
    public boolean checkRememberMe(SharedPreferences sharedPreferences) {
        boolean isRemembered = sharedPreferences.getBoolean("remember_me", false);
        return isRemembered;
    }
    public void signOut() {
        salerMapRepository.updateSalerLocation(FirebaseAuth.getInstance().getCurrentUser().getUid()
                ,0
                ,0
                ,false);
        FirebaseAuth.getInstance().signOut();
    }

}
