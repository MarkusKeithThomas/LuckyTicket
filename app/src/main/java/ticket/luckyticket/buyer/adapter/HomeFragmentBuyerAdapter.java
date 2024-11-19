package ticket.luckyticket.buyer.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ticket.luckyticket.databinding.ListTicketCardBinding;
import ticket.luckyticket.myinterface.MyClickEvent;
import ticket.luckyticket.saler.model.MainScreenSalerProvince;

public class HomeFragmentBuyerAdapter extends RecyclerView.Adapter<HomeFragmentBuyerAdapter.MyCardViewProvince> {
    private List<MainScreenSalerProvince> mainScreenSalerProvinceList;
    private MyClickEvent myClickEvent;

    public HomeFragmentBuyerAdapter(List<MainScreenSalerProvince> mainScreenSalerProvinceList, MyClickEvent myClickEvent) {
        this.mainScreenSalerProvinceList = mainScreenSalerProvinceList;
        this.myClickEvent = myClickEvent;
    }

    @NonNull
    @Override
    public MyCardViewProvince onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListTicketCardBinding listTicketCardBinding = ListTicketCardBinding.inflate(layoutInflater, parent, false);
        return new MyCardViewProvince(listTicketCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCardViewProvince holder, int position) {
        MainScreenSalerProvince mainScreenSalerProvince = mainScreenSalerProvinceList.get(position);
        holder.bind(mainScreenSalerProvince);
    }

    @Override
    public int getItemCount() {
        return mainScreenSalerProvinceList.size();
    }

    public class MyCardViewProvince extends RecyclerView.ViewHolder {
        ListTicketCardBinding listTicketCardBinding;
        private Handler handler;
        private Runnable runnable;

        public MyCardViewProvince(ListTicketCardBinding listTicketCardBinding) {
            super(listTicketCardBinding.getRoot());
            this.listTicketCardBinding = listTicketCardBinding;
            this.handler = new Handler();
        }

        public void bind(MainScreenSalerProvince mainScreenSalerProvince) {
            listTicketCardBinding.setMainScreenSalerProvince(mainScreenSalerProvince);
            listTicketCardBinding.imageCardDai.setImageResource(mainScreenSalerProvince.getImagePath());
            listTicketCardBinding.executePendingBindings();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && myClickEvent != null ){
                        myClickEvent.onCardViewListener(listTicketCardBinding.getMainScreenSalerProvince());
                    }
                }
            });

            // Thiết lập Runnable để cập nhật thời gian
            runnable = new Runnable() {
                @Override
                public void run() {
                    // Cập nhật thời gian mới
                    mainScreenSalerProvince.setTimeNow(mainScreenSalerProvince.getCurrentTime());
                    listTicketCardBinding.setMainScreenSalerProvince(mainScreenSalerProvince);
                    listTicketCardBinding.executePendingBindings();

                    // Gọi lại runnable sau 10 giây (10000 milliseconds)
                    handler.postDelayed(this, 10000);
                }
            };

            // Bắt đầu Runnable
            handler.post(runnable);
        }

        // Dừng cập nhật khi không cần thiết để tránh rò rỉ bộ nhớ
        public void stopUpdatingTime() {
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull MyCardViewProvince holder) {
        super.onViewRecycled(holder);
        // Dừng cập nhật thời gian khi ViewHolder bị tái chế
        holder.stopUpdatingTime();
    }
}
