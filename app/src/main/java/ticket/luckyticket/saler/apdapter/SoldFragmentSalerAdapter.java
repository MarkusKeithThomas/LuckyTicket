package ticket.luckyticket.saler.apdapter;

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
import ticket.luckyticket.databinding.ListInformationTicketBinding;
import ticket.luckyticket.diffutil.TicketDiffCallback;
import ticket.luckyticket.myinterface.OnItemClickListener;
import ticket.luckyticket.saler.fragment.SoldFragmentSaler;
import ticket.luckyticket.saler.model.TicketInformation;

public class SoldFragmentSalerAdapter extends RecyclerView.Adapter<SoldFragmentSalerAdapter.MySoldFragmentViewHolder> {
    private List<TicketInformation> ticketInformationList;
    private Context context;
    private OnItemClickListener listener;


    public SoldFragmentSalerAdapter(List<TicketInformation> ticketInformationList, Context context) {
        this.ticketInformationList = ticketInformationList;
        this.context = context;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MySoldFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListInformationTicketBinding listInformationTicketBinding = ListInformationTicketBinding
                .inflate(layoutInflater, parent, false);
        return new MySoldFragmentViewHolder(listInformationTicketBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MySoldFragmentViewHolder holder, int position) {
        TicketInformation ticketInformation = ticketInformationList.get(position);
        holder.bind(ticketInformation);
    }

    @Override
    public int getItemCount() {
        return ticketInformationList.size();
    }

    // Phương thức update danh sách với DiffUtil
    public void updateTicketList(List<TicketInformation> newTicketInformationList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TicketDiffCallback(this.ticketInformationList, newTicketInformationList));
        this.ticketInformationList.clear();
        this.ticketInformationList.addAll(newTicketInformationList);
        // Sử dụng DiffUtil để chỉ cập nhật những item thay đổi
        diffResult.dispatchUpdatesTo(this);
    }

    public TicketInformation getItemPositionAt(int position) {
        return ticketInformationList.get(position);
    }

    public void removeItemTicket(int position) {
        ticketInformationList.remove(position);
        notifyItemRemoved(position);
    }


    public class MySoldFragmentViewHolder extends RecyclerView.ViewHolder {
        private ListInformationTicketBinding listInformationTicketBinding;

        public MySoldFragmentViewHolder(ListInformationTicketBinding listInformationTicketBinding) {
            super(listInformationTicketBinding.getRoot());
            this.listInformationTicketBinding = listInformationTicketBinding;
            // Xử lý sự kiện click trên itemView (CardView)
            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(ticketInformationList.get(position)); // Gọi hàm onItemClick
                }
            });
        }


        public void bind(TicketInformation ticketInformation) {
            listInformationTicketBinding.setTicketinformationcard(ticketInformation);
            // Chuyển đổi Timestamp thành chuỗi định dạng
            Timestamp timestamp = ticketInformation.getTimeAdd();
            if (timestamp != null) {
                listInformationTicketBinding.timeAddTicket.setText("Đã Đăng: " + ticketInformation.convertTimestampToString()); // Hiển thị thời gian định dạng
            }            // Sử dụng Glide để tải ảnh vào ImageView trong item
            Glide.with(context)
                    .load(ticketInformation.getImagePath())
                    .fitCenter()
                    .placeholder(R.drawable.baseline_terrain_24) // Hình ảnh khi đang tải
                    .error(R.drawable.baseline_terrain_24) // Hình ảnh nếu tải thất bại
                    .into(listInformationTicketBinding.imageTicket);
            listInformationTicketBinding.executePendingBindings();
        }


    }
}
