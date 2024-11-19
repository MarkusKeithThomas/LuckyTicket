package ticket.luckyticket.diffutil;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import ticket.luckyticket.buyer.model.ChatRoom;

public class ChatRoomDiffCallback extends DiffUtil.Callback {

    private final List<ChatRoom> oldList;
    private final List<ChatRoom> newList;

    public ChatRoomDiffCallback(List<ChatRoom> oldList, List<ChatRoom> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getChatRoomId().equals(newList.get(newItemPosition).getChatRoomId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ChatRoom oldChatRoom = oldList.get(oldItemPosition);
        ChatRoom newChatRoom = newList.get(newItemPosition);

        // So sánh các thuộc tính cụ thể để xác định sự khác biệt
        return oldChatRoom.getSixNumberTicket().equals(newChatRoom.getSixNumberTicket()) &&
                oldChatRoom.getChatRoomId().equals(newChatRoom.getChatRoomId());
    }


}


