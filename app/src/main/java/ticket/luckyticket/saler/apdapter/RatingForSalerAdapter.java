package ticket.luckyticket.saler.apdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ticket.luckyticket.R;
import ticket.luckyticket.databinding.ListCommentBinding;
import ticket.luckyticket.saler.model.RatingForSaler;

public class RatingForSalerAdapter extends RecyclerView.Adapter<RatingForSalerAdapter.RatingForSalerViewHolder>{
    private List<RatingForSaler> ratingForSalerList;

    public RatingForSalerAdapter() {
        this.ratingForSalerList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RatingForSalerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListCommentBinding listCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.list_comment, parent, false);
        return new RatingForSalerViewHolder(listCommentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingForSalerViewHolder holder, int position) {
        RatingForSaler ratingForSaler = ratingForSalerList.get(position);
        holder.listCommentBinding.setRatingSaler(ratingForSaler);
    }

    @Override
    public int getItemCount() {
        return ratingForSalerList.size();
    }
    // Phương thức này để cập nhật dữ liệu mới
    public void updateData(List<RatingForSaler> newRatingForSalerList) {
        this.ratingForSalerList.clear(); // Xóa danh sách cũ
        this.ratingForSalerList.addAll(newRatingForSalerList); // Thêm danh sách mới
        notifyDataSetChanged(); // Thông báo cho adapter rằng dữ liệu đã thay đổi
    }


    public class RatingForSalerViewHolder extends RecyclerView.ViewHolder{
        private ListCommentBinding listCommentBinding;
        public RatingForSalerViewHolder(ListCommentBinding listCommentBinding) {
            super(listCommentBinding.getRoot());
            this.listCommentBinding = listCommentBinding;
        }
    }
    public String convertTimeStampToDate(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date(timeStamp);
        return sdf.format(date);
    }
}
