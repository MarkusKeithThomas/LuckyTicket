package ticket.luckyticket.saler.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ticket.luckyticket.saler.model.RatingForSaler;
import ticket.luckyticket.saler.model.User;
import ticket.luckyticket.saler.repository.RatingForSalerRepository;

public class RatingForSalerViewModel extends ViewModel {
    private RatingForSalerRepository ratingForSalerRepository;
    public RatingForSalerViewModel(){
        ratingForSalerRepository = new RatingForSalerRepository();
    }

    public LiveData<User> getInformationOneSaler(String salerId){
        return ratingForSalerRepository.getInformationOneSaler(salerId);
    }
    public void sendComment(RatingForSaler ratingForSaler){
        ratingForSalerRepository.sendComment(ratingForSaler);
    }
    public LiveData<List<RatingForSaler>> getComment(String userId){
        return ratingForSalerRepository.getComments(userId);
    }
    public void updateMoneySaler(String salerId, String deductionAmount) {
        ratingForSalerRepository.updateMoneySaler(salerId,deductionAmount);
    }
    public void updateMoneyForBuyer(String userId, String deductionAmount) {
        ratingForSalerRepository.updateMoneyBuyer(userId,deductionAmount);
    }
    public void updateRatingForSaler(String rating, String salerId) {
        ratingForSalerRepository.updateRatingForSaler(rating,salerId);
    }
}
