package ticket.luckyticket.buyer.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.databinding.ListInformationTicketBinding;
import ticket.luckyticket.diffutil.TicketDiffCallback;
import ticket.luckyticket.saler.model.TicketInformation;

public class BuyFragmentBuyerAdapter extends RecyclerView.Adapter<BuyFragmentBuyerAdapter.MyBuyFragmentViewHolderBuyer> {
    private List<TicketInformation> ticketInformationList;
    private OnItemClickListener listener; // Interface cho sự kiện click

    public BuyFragmentBuyerAdapter(List<TicketInformation> ticketInformationList) {
        this.ticketInformationList = ticketInformationList;
    }

    @NonNull
    @Override
    public MyBuyFragmentViewHolderBuyer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListInformationTicketBinding listInformationTicketBinding = ListInformationTicketBinding.inflate(layoutInflater, parent, false);
        return new MyBuyFragmentViewHolderBuyer(listInformationTicketBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBuyFragmentViewHolderBuyer holder, int position) {
        TicketInformation ticketInformation = ticketInformationList.get(position);
        holder.bind(ticketInformation);
    }

    @Override
    public int getItemCount() {
        return ticketInformationList.size();
    }

    // Phương thức cập nhật danh sách trong Adapter sử dụng DiffUtil để so sánh dữ liệu
    public void updateTicketList(List<TicketInformation> newTicketInformationList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TicketDiffCallback(this.ticketInformationList, newTicketInformationList));
        this.ticketInformationList.clear();
        this.ticketInformationList.addAll(newTicketInformationList);  // Cập nhật danh sách mới
        diffResult.dispatchUpdatesTo(this);  // Thông báo cho RecyclerView cập nhật
        // Thiết lập lại sự kiện click cho các item sau khi danh sách thay đổi
        notifyDataSetChanged();
    }

    // Interface để lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(TicketInformation ticketInformation);
    }


    // Cài đặt sự kiện click vào item
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    // ViewHolder cho từng item
    public class MyBuyFragmentViewHolderBuyer extends RecyclerView.ViewHolder {
        private ListInformationTicketBinding listInformationTicketBinding;

        public MyBuyFragmentViewHolderBuyer(ListInformationTicketBinding listInformationTicketBinding) {
            super(listInformationTicketBinding.getRoot());
            this.listInformationTicketBinding = listInformationTicketBinding;

            // Thiết lập sự kiện click vào từng item
            listInformationTicketBinding.getRoot().setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(ticketInformationList.get(position));
                }
            });
        }

        // Bind dữ liệu vào từng item
        public void bind(TicketInformation ticketInformation) {
            listInformationTicketBinding.setTicketinformationcard(ticketInformation);

            // Sử dụng Glide để tải ảnh vào ImageView
            Glide.with(itemView.getContext())
                    .load(ticketInformation.getImagePath())
                    .fitCenter()
                    .placeholder(R.drawable.baseline_terrain_24)
                    .error(R.drawable.baseline_terrain_24)
                    .into(listInformationTicketBinding.imageTicket);

            // Hiển thị thời gian
            listInformationTicketBinding.timeAddTicket.setText("Đã Đăng: " + ticketInformation.convertTimestampToString());

            // Thực hiện binding
            listInformationTicketBinding.executePendingBindings();
        }
    }
}
