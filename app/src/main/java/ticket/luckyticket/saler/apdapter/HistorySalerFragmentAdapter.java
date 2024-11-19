package ticket.luckyticket.saler.apdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.databinding.ItemTicketHistoryBinding;

public class HistorySalerFragmentAdapter extends RecyclerView.Adapter<HistorySalerFragmentAdapter.HistorySalerViewHolder> {
    private List<TicketHistory> ticketHistoryList = new ArrayList<>();
    private Context context;

    public HistorySalerFragmentAdapter(List<TicketHistory> ticketHistoryList,Context context) {
        this.ticketHistoryList = ticketHistoryList;
        this.context = context;
    }
    public void updateData(List<TicketHistory> newTicketHistory) {
        this.ticketHistoryList.clear(); // Xóa danh sách cũ
        this.ticketHistoryList.addAll(newTicketHistory); // Thêm danh sách mới
        notifyDataSetChanged(); // Thông báo cho adapter rằng dữ liệu đã thay đổi
    }

    @NonNull
    @Override
    public HistorySalerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemTicketHistoryBinding binding = ItemTicketHistoryBinding.inflate(layoutInflater, parent, false);

        return new HistorySalerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorySalerViewHolder holder, int position) {
        TicketHistory ticketHistory = ticketHistoryList.get(position);
        holder.bind(ticketHistory);
    }

    @Override
    public int getItemCount() {
        return ticketHistoryList.size();
    }

    public class HistorySalerViewHolder extends RecyclerView.ViewHolder{
        private ItemTicketHistoryBinding binding;
        public HistorySalerViewHolder(ItemTicketHistoryBinding itemTicketHistoryBinding) {
            super(itemTicketHistoryBinding.getRoot());
            this.binding = itemTicketHistoryBinding;
        }
        public void bind(TicketHistory ticketHistory) {
            binding.setTickethistory(ticketHistory);

            // Chuyển đổi Timestamp thành chuỗi định dạng
            Timestamp timestamp = ticketHistory.getTimeSaleBought();
            if (timestamp != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(timestamp.toDate());
                binding.timeSaleTicket.setText("Đã Bán: " + formattedDate);
            }

            // Sử dụng Glide để tải ảnh vào ImageView
            Glide.with(context)
                    .load(ticketHistory.getImageURL())
                    .fitCenter()
                    .placeholder(R.drawable.baseline_terrain_24)
                    .error(R.drawable.baseline_terrain_24)
                    .into(binding.imageTicket);

            binding.executePendingBindings();
        }
    }

}
