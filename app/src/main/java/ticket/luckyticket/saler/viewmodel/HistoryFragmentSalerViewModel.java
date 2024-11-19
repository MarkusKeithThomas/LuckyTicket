package ticket.luckyticket.saler.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.saler.repository.HistorySalerRepository;

public class HistoryFragmentSalerViewModel extends ViewModel {
    private HistorySalerRepository historySalerRepository;
    private LiveData<List<TicketHistory>> listMutableLiveData = new MutableLiveData<>();
    public HistoryFragmentSalerViewModel(){
        historySalerRepository = new HistorySalerRepository();
    }
    public LiveData<List<TicketHistory>> getAllTicketHistoryForSaler(String salerId){
        return historySalerRepository.getAllTicketHistoryForSaler(salerId);
    }
    // Phương thức làm mới dữ liệu từ Firestore
    public LiveData<List<TicketHistory>> refreshTicketHistory(String userId) {
        return listMutableLiveData = historySalerRepository.getAllTicketHistoryForSaler(userId);
    }
}
