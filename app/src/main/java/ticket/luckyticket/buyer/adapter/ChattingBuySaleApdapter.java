package ticket.luckyticket.buyer.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.model.ChattingAB;
import ticket.luckyticket.databinding.ListChattingBinding;

public class ChattingBuySaleApdapter extends RecyclerView.Adapter<ChattingBuySaleApdapter.MyChattingBuySaleViewHolder> {
    private List<ChattingAB> chattingABList;
    private String currentUserId;
    public ChattingBuySaleApdapter(List<ChattingAB> chattingABList,String currentUserId) {
        this.chattingABList = chattingABList;
        this.currentUserId = currentUserId;
    }
    public void setChatList(List<ChattingAB> chatList) {
        this.chattingABList = chatList;
        notifyDataSetChanged(); // Cập nhật dữ liệu khi danh sách thay đổi
    }
    // Phương thức để cuộn xuống tin nhắn cuối cùng
    public void scrollToLastMessage(RecyclerView recyclerView) {
        if (getItemCount() > 0) {
            recyclerView.scrollToPosition(getItemCount() - 1); // Cuộn đến tin nhắn cuối cùng
        }
    }

    @NonNull
    @Override
    public MyChattingBuySaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListChattingBinding listChattingBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.list_chatting, parent, false);
        return new MyChattingBuySaleViewHolder(listChattingBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull MyChattingBuySaleViewHolder holder, int position) {
        ChattingAB chattingAB = chattingABList.get(position);
        // Gán model ChattingAB vào layout
        // Logic kiểm tra người gửi và người nhận
        boolean isMine = chattingAB.isMine();
        holder.listChattingBinding.setIsMine(isMine);
        holder.listChattingBinding.setChattingAB(chattingAB);
        holder.listChattingBinding.executePendingBindings();


    }

    @Override
    public int getItemCount() {
        return chattingABList != null ? chattingABList.size() : 0; // Tránh null pointer
    }



    public class MyChattingBuySaleViewHolder  extends RecyclerView.ViewHolder{
        private ListChattingBinding listChattingBinding;
        public MyChattingBuySaleViewHolder(ListChattingBinding listChattingBinding) {
            super(listChattingBinding.getRoot());
            this.listChattingBinding = listChattingBinding;
        }
    }
}
