package ticket.luckyticket.saler.apdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.saler.model.TicketInformation;

public class SalerMapAdapter extends RecyclerView.Adapter<SalerMapAdapter.MySalerMapViewHolder> {
    private List<TicketInformation> list;
    public SalerMapAdapter(List<TicketInformation> list){
        this.list = list;

    }
    // Interface để lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(TicketInformation ticketInformation);
    }



    @NonNull
    @Override
    public MySalerMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_ticket_information, parent, false);
        return new MySalerMapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySalerMapViewHolder holder, int position) {
        TicketInformation ticketInformation = list.get(position);
        holder.textViewSoVe.setText("Số Vé: "+ticketInformation.getNumberSixTicket());
        holder.textViewVeConLai.setText("Vé Còn: "+ticketInformation.getNumberTicketSale()+" vé");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MySalerMapViewHolder extends RecyclerView.ViewHolder{
        TextView textViewSoVe, textViewVeConLai;
        public MySalerMapViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSoVe = itemView.findViewById(R.id.tvNumberTicket);
            textViewVeConLai = itemView.findViewById(R.id.tvNumberTicketSale);
        }
    }
    // Thêm phương thức để cập nhật danh sách
    public void updateList(List<TicketInformation> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
    }
}
