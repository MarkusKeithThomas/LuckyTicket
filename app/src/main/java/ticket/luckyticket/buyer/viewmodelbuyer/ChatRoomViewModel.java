package ticket.luckyticket.buyer.viewmodelbuyer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ticket.luckyticket.buyer.model.ChatRoom;
import ticket.luckyticket.buyer.repository.ChatRoomRepository;

public class ChatRoomViewModel extends ViewModel {
    private final ChatRoomRepository chatRoomRepository;
    private final MediatorLiveData<List<ChatRoom>> chatRoomLiveList = new MediatorLiveData<>();
    private final MediatorLiveData<List<ChatRoom>> chatRoomLiveListSaler = new MediatorLiveData<>();

    public ChatRoomViewModel() {
        chatRoomRepository = new ChatRoomRepository();  // Khởi tạo Repository
    }

    // Trả về LiveData để UI quan sát
    public LiveData<List<ChatRoom>> getRoomChat() {
        return chatRoomLiveList;
    }

    // Phương thức cập nhật dữ liệu từ Firestore
    public void updateChatRoomData(String userId) {
        if (userId != null && !userId.isEmpty()) {
            LiveData<List<ChatRoom>> source = chatRoomRepository.getRoomChat(userId);
            chatRoomLiveList.addSource(source, chatRooms -> {
                chatRoomLiveList.setValue(chatRooms); // Cập nhật dữ liệu
                chatRoomLiveList.removeSource(source); // Loại bỏ sau khi có dữ liệu
            });
        }
    }

    // Trả về LiveData để UI quan sát
    public LiveData<List<ChatRoom>> getRoomChatSaler() {
        return chatRoomLiveListSaler;
    }

    // Phương thức cập nhật dữ liệu từ Firestore
    public void updateChatRoomDataSaler(String userId) {
        if (userId != null && !userId.isEmpty()) {
            LiveData<List<ChatRoom>> source = chatRoomRepository.getRoomChatSaler(userId);
            chatRoomLiveListSaler.addSource(source, chatRooms -> {
                chatRoomLiveListSaler.setValue(chatRooms); // Cập nhật dữ liệu
                chatRoomLiveListSaler.removeSource(source); // Loại bỏ sau khi có dữ liệu
            });
        }
    }
}
