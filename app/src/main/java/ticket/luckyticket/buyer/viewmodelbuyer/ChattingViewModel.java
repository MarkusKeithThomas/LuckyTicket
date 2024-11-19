package ticket.luckyticket.buyer.viewmodelbuyer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ticket.luckyticket.buyer.model.ChattingAB;
import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.buyer.repository.ChattingBuySaleRepository;

public class ChattingViewModel extends AndroidViewModel {
    private ChattingBuySaleRepository chattingBuySaleRepository;
    private MutableLiveData<List<ChattingAB>> messagesLiveData;

    public ChattingViewModel(@NonNull Application application) {
        super(application);
        chattingBuySaleRepository = new ChattingBuySaleRepository();
    }

    // Gửi tin nhắn giữa hai người dùng
    public void sendMessage(String senderId,String ortherId, String textMessage,String sixNumberTicket) {
        chattingBuySaleRepository.sendMessage(senderId,ortherId,textMessage,sixNumberTicket);
    }

    // Lấy danh sách tin nhắn giữa hai người dùng và quan sát dữ liệu
    public LiveData<List<ChattingAB>> getMessages(String saleId, String boughtId, String docId) {
        String chatId = saleId.compareTo(boughtId) < 0 ? saleId + "_" + boughtId + "_"+docId : boughtId + "_" + saleId + "_" + docId;

        // Chỉ gọi Firebase để lấy tin nhắn nếu messagesLiveData chưa có dữ liệu
        if (messagesLiveData == null) {
            messagesLiveData = new MutableLiveData<>();
            messagesLiveData = chattingBuySaleRepository.getMessages(saleId,boughtId,docId);
        }
        return messagesLiveData;
    }
    public LiveData<TicketHistory> getOneTicket(String documentIdTicketHistory){
        return chattingBuySaleRepository.getOneTicket(documentIdTicketHistory);
    }

}
