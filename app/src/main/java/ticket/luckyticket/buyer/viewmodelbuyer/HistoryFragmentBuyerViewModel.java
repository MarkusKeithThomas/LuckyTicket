package ticket.luckyticket.buyer.viewmodelbuyer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.buyer.repository.HistoryFragmentBuyerRepository;

public class HistoryFragmentBuyerViewModel extends ViewModel {
    private HistoryFragmentBuyerRepository historyFragmentBuyerRepository;
    private LiveData<List<TicketHistory>> ticketHistoryLiveData = new MutableLiveData<>();

    public HistoryFragmentBuyerViewModel() {
        historyFragmentBuyerRepository = new HistoryFragmentBuyerRepository();  // Khởi tạo Repository
    }

    // Trả về LiveData để UI quan sát
    public LiveData<List<TicketHistory>> getTicketHistoryLiveData(String userId) {
        return historyFragmentBuyerRepository.getListTicketHistoryLiveData(userId);
    }

    // Phương thức làm mới dữ liệu từ Firestore
    public void refreshTicketHistory(String userId) {
        ticketHistoryLiveData = historyFragmentBuyerRepository.getListTicketHistoryLiveData(userId);
    }
}
