package ticket.luckyticket.buyer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.databinding.ItemTicketHistoryBinding;
import ticket.luckyticket.diffutil.TicketHistoryDiffCallback;
import ticket.luckyticket.myinterface.OnItemTicketHistory;

public class HistoryFragmentBuyerAdapter extends RecyclerView.Adapter<HistoryFragmentBuyerAdapter.HistoryFragmentBuyerViewHolder> {
    private List<TicketHistory> ticketHistoryList;
    private Context context;
    private OnItemTicketHistory onItemTicketHistory;

    public HistoryFragmentBuyerAdapter(List<TicketHistory> ticketHistoryList, Context context,OnItemTicketHistory onItemTicketHistory) {
        this.ticketHistoryList = ticketHistoryList;
        this.context = context;
        this.onItemTicketHistory = onItemTicketHistory;
    }

    public void updateTicketHistory(List<TicketHistory> newTicket) {
        TicketHistoryDiffCallback diffCallback = new TicketHistoryDiffCallback(this.ticketHistoryList, newTicket);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.ticketHistoryList.clear();
        this.ticketHistoryList.addAll(newTicket);
        diffResult.dispatchUpdatesTo(this);  // Cập nhật giao diện UI
    }

    @NonNull
    @Override
    public HistoryFragmentBuyerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemTicketHistoryBinding binding = ItemTicketHistoryBinding.inflate(layoutInflater, parent, false);
        return new HistoryFragmentBuyerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryFragmentBuyerViewHolder holder, int position) {
        TicketHistory ticketHistory = ticketHistoryList.get(position);
        holder.bind(ticketHistory);
    }

    @Override
    public int getItemCount() {
        return ticketHistoryList.size();
    }

    public class HistoryFragmentBuyerViewHolder extends RecyclerView.ViewHolder {
        private ItemTicketHistoryBinding binding;

        public HistoryFragmentBuyerViewHolder(ItemTicketHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v ->{
                if (onItemTicketHistory != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemTicketHistory.onItemClick(ticketHistoryList.get(position));  // Gửi sự kiện click ra ngoài với dữ liệu ticketHistory
                    }
                }
            });

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
