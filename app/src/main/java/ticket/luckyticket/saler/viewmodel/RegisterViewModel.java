package ticket.luckyticket.saler.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ticket.luckyticket.saler.model.User;
import ticket.luckyticket.saler.repository.RegisterRepository;

public class RegisterViewModel extends AndroidViewModel {
    private RegisterRepository registerRepository;
    private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>(new User());
    private final LiveData<Boolean> isUploadSuccessful;
    private final MutableLiveData<String> nameError = new MutableLiveData<>();
    private final MutableLiveData<String> emailError = new MutableLiveData<>();
    private final MutableLiveData<String> phoneError = new MutableLiveData<>();
    private final MutableLiveData<String> idCardError = new MutableLiveData<>();
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    private final MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();


    public RegisterViewModel(@NonNull Application application) {
        super(application);
        registerRepository = new RegisterRepository(application);
        isUploadSuccessful = registerRepository.getIsUploadSuccessful();
    }
    public LiveData<Boolean> getIsUploadSuccessful() {
        return isUploadSuccessful;
    }
    // Getter cho `user` để DataBinding có thể sử dụng
    public LiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void createUser(Uri uri, User user) {
        if (user != null && validateUserInput(user)){
            registerRepository.createUser(uri,user);
        }
    }
    // Getters for each error
    public LiveData<String> getNameError() {
        return nameError;
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<String> getPhoneError() {
        return phoneError;
    }

    public LiveData<String> getIdCardError() {
        return idCardError;
    }

    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    public LiveData<String> getConfirmPasswordError() {
        return confirmPasswordError;
    }
    // Method to validate user input and set error messages
    public boolean validateUserInput(User user) {
        boolean isValid = true;

        if (user.getName() == null || user.getName().isEmpty()) {
            nameError.setValue("Vui lòng điền tên.");
            isValid = false;
        } else {
            nameError.setValue(null);  // Clear error if valid
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            emailError.setValue("Vui lòng điền email.");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches()) {
            emailError.setValue("Email không đúng đinh dạng.");
            isValid = false;
        } else {
            emailError.setValue(null);
        }

        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            phoneError.setValue("Vui lòng điền số điện thoại.");
            isValid = false;
        } else {
            phoneError.setValue(null);
        }

        if (user.getIdCard() == null || user.getIdCard().isEmpty()) {
            idCardError.setValue("Vui lòng điền CCCD đúng.");
            isValid = false;
        } else {
            idCardError.setValue(null);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            passwordError.setValue("Vui lòng điền password.");
            isValid = false;
        } else if (user.getPassword().length() < 6) {
            passwordError.setValue("Password ít nhất có 6 kí tự.");
            isValid = false;
        } else {
            passwordError.setValue(null);
        }

        if (user.getRe_password() == null || !user.getRe_password().equals(user.getPassword())) {
            confirmPasswordError.setValue("Password cần điền giống nhau.");
            isValid = false;
        } else {
            confirmPasswordError.setValue(null);
        }
        return isValid;
    }
}

