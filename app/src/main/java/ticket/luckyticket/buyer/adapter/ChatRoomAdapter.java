package ticket.luckyticket.buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.model.ChatRoom;
import ticket.luckyticket.diffutil.ChatRoomDiffCallback;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
private List<ChatRoom> chatRoomList;
private OnItemClickListenerRoom listener;


// Interface để lắng nghe sự kiện click
public interface OnItemClickListenerRoom {
    void onItemClickRoom(ChatRoom chatRoom);
}
    public void setOnItemClickListenerRoom(OnItemClickListenerRoom onItemClickListenerRoom){
        this.listener = onItemClickListenerRoom;
    }

    public ChatRoomAdapter(List<ChatRoom> chatRoomList) {
        this.chatRoomList = chatRoomList;
    }

    public void updateChatRooms(List<ChatRoom> newChatRooms) {
        ChatRoomDiffCallback diffCallback = new ChatRoomDiffCallback(this.chatRoomList, newChatRooms);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.chatRoomList.clear();
        this.chatRoomList.addAll(newChatRooms);
        diffResult.dispatchUpdatesTo(this);  // Cập nhật giao diện UI
    }



    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomList.get(position);
        holder.bind(chatRoom);
        holder.itemView.setAlpha(1); // Đặt mặc định alpha là 1

    }


    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

class ChatRoomViewHolder extends RecyclerView.ViewHolder {
    private TextView chatRoomIdText;

    public ChatRoomViewHolder(@NonNull View itemView) {
        super(itemView);
        chatRoomIdText = itemView.findViewById(R.id.chatRoomIdText);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getBindingAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClickRoom(chatRoomList.get(position));
                }
            }
        });
    }

    public void bind(ChatRoom chatRoom) {
        chatRoomIdText.setText(chatRoom.getSixNumberTicket()); // Hiển thị ticketSixNumber
    }
}
}
