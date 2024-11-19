package ticket.luckyticket.diffutil;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import ticket.luckyticket.saler.model.TicketInformation;

public class TicketDiffCallback extends DiffUtil.Callback {

    private final List<TicketInformation> oldList;
    private final List<TicketInformation> newList;

    public TicketDiffCallback(List<TicketInformation> oldList, List<TicketInformation> newList) {
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
        // Kiểm tra dựa trên Document ID hoặc một thuộc tính duy nhất khác
        return oldList.get(oldItemPosition).getDocumentId().equals(newList.get(newItemPosition).getDocumentId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Kiểm tra dựa trên các thuộc tính có thể thay đổi
        TicketInformation oldItem = oldList.get(oldItemPosition);
        TicketInformation newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);  // Sử dụng phương thức equals để so sánh tất cả các thuộc tính
    }
}
