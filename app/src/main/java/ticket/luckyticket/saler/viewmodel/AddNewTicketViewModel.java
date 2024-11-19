package ticket.luckyticket.saler.viewmodel;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ticket.luckyticket.saler.model.TicketInformation;
import ticket.luckyticket.saler.repository.AddNewTicketRepository;

public class AddNewTicketViewModel extends ViewModel {
    private AddNewTicketRepository addNewTicketRepository;
    public AddNewTicketViewModel(){
        addNewTicketRepository = new AddNewTicketRepository();
    }
    public void uploadImageTicketInformationToFirebase(Uri uri, TicketInformation ticketInformation){
        addNewTicketRepository.uploadImageTicketInformationToFirebase(uri,ticketInformation);
    }
    public void updateTicket(String documentId,String newTicketSaleRemain){
        addNewTicketRepository.updateTicket(documentId,newTicketSaleRemain);
    }
    public List<String> getItemsBasedOnDay() {
        List<String> items = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                items.add("TPHCM");
                items.add("Đồng Tháp");
                items.add("Cà Mau");
                break;
            case Calendar.TUESDAY:
                items.add("Bến Tre");
                items.add("Vũng Tàu");
                items.add("Bạc Liêu");
                break;
            case Calendar.WEDNESDAY:
                items.add("Đồng Nai");
                items.add("Cần Thơ");
                items.add("Sóc Trăng");
                break;
            case Calendar.THURSDAY:
                items.add("Tây Ninh");
                items.add("An Giang");
                items.add("Bình Thuận");
                break;
            case Calendar.FRIDAY:
                items.add("Vĩnh Long");
                items.add("Bình Dương");
                items.add("Trà Vinh");
                break;
            case Calendar.SATURDAY:
                items.add("TPHCM");
                items.add("Long An");
                items.add("Bình Phước");
                items.add("Hậu Giang");
                break;
            case Calendar.SUNDAY:
                items.add("Tiền Giang");
                items.add("Kiên Giang");
                items.add("Đà Lạt");
                break;
            default:
                items.add("Không có nội dung");
                break;
        }

        return items;
    }
}
