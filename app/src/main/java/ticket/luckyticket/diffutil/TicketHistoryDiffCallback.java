package ticket.luckyticket.diffutil;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

import ticket.luckyticket.buyer.model.TicketHistory;

public class TicketHistoryDiffCallback extends DiffUtil.Callback {

    private final List<TicketHistory> oldList;
    private final List<TicketHistory> newList;

    public TicketHistoryDiffCallback(List<TicketHistory> oldList, List<TicketHistory> newList) {
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
        // Sử dụng Objects.equals() để so sánh một cách an toàn với null
        return Objects.equals(
                oldList.get(oldItemPosition).getDocumentIdTicketHistory(),
                newList.get(newItemPosition).getDocumentIdTicketHistory()
        );
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        TicketHistory oldTicket = oldList.get(oldItemPosition);
        TicketHistory newTicket = newList.get(newItemPosition);

        // Sử dụng Objects.equals() để so sánh tất cả các thuộc tính
        return Objects.equals(oldTicket.getTicketNumberSale(), newTicket.getTicketNumberSale()) &&
                Objects.equals(oldTicket.getNameDai(), newTicket.getNameDai()) &&
                Objects.equals(oldTicket.getTicketSixNumber(), newTicket.getTicketSixNumber()) &&
                Objects.equals(oldTicket.getUserSaleID(), newTicket.getUserSaleID()) &&
                Objects.equals(oldTicket.getTimeSaleBought(), newTicket.getTimeSaleBought()) &&
                Objects.equals(oldTicket.getImageURL(), newTicket.getImageURL());
    }
}
