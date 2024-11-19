package ticket.luckyticket.saler.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import ticket.luckyticket.saler.model.User;
import ticket.luckyticket.saler.repository.ProfileFragmentSalerRepository;

public class ProfileFragmentSalerViewModel extends ViewModel {
    private ProfileFragmentSalerRepository profileFragmentSalerRepository;
    public  ProfileFragmentSalerViewModel(){
        profileFragmentSalerRepository = new ProfileFragmentSalerRepository();
    }
    public LiveData<User> getOneUserSaler(){
        return profileFragmentSalerRepository.getOneUserSaler();
    }

}
