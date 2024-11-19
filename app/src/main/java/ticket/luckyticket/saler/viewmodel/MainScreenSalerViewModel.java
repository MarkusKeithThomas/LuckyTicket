package ticket.luckyticket.saler.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ticket.luckyticket.R;
import ticket.luckyticket.saler.model.MainScreenSalerProvince;

public class MainScreenSalerViewModel extends ViewModel {
    private MutableLiveData<List<MainScreenSalerProvince>> ticketLuckySalerList;
    private MutableLiveData<MainScreenSalerProvince> selectTicketLuckySaler;



    public MainScreenSalerViewModel() {
        this.ticketLuckySalerList = new MutableLiveData<>();
        this.selectTicketLuckySaler = new MutableLiveData<>();
        loadTicket();
    }

    private void loadTicket() {
        List<MainScreenSalerProvince> ticketsList = new ArrayList<>();
        String dayOfWeek = getDayOfWeek();
        // Nạp dữ liệu dựa trên ngày hiện tại
        ticketsList.addAll(getTicketsForDay(dayOfWeek));

        // Nạp dữ liệu cho ngày tiếp theo (hoặc một logic nào đó nếu cần)
        ticketsList.addAll(getTicketsForNextDay(dayOfWeek));

        // Cập nhật LiveData
        ticketLuckySalerList.setValue(ticketsList);
    }

    private List<MainScreenSalerProvince> getTicketsForDay(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Thứ 2":
                return createTicketList(
                        new int[]{R.drawable.cardview_t2camau, R.drawable.cardview_t2tphcm, R.drawable.carview_t2dongthap},
                        new String[]{"Cà Mau", "TPHCM", "Đồng Tháp"},
                        dayOfWeek
                );
            case "Thứ 3":
                return createTicketList(
                        new int[]{R.drawable.cardview_t3baclieu, R.drawable.cardview_t3bentre, R.drawable.cardview_t3vungtau},
                        new String[]{"Bạc Liêu", "Bến Tre", "Vũng Tàu"},
                        dayOfWeek
                );
            case "Thứ 4":
                return createTicketList(
                        new int[]{R.drawable.cardview_t4cantho, R.drawable.cardview_t4dongnai, R.drawable.cardview_t4soctrang},
                        new String[]{"Cần Thơ", "Đồng Nai", "Sóc Trăng"},
                        dayOfWeek
                );
            case "Thứ 5":
                return createTicketList(
                        new int[]{R.drawable.cardview_t5binhthuan, R.drawable.cardview_t5tayninh, R.drawable.cardview_t5angiang},
                        new String[]{"Bình Thuận", "Tây Ninh", "An Giang"},
                        dayOfWeek
                );
            case "Thứ 6":
                return createTicketList(
                        new int[]{R.drawable.cardview_t6binhduong, R.drawable.cardview_t6travinh, R.drawable.cardview_t6vinhlong},
                        new String[]{"Bình Dương", "Trà Vinh", "Vĩnh Long"},
                        dayOfWeek
                );
            case "Thứ 7":
                return createTicketList(
                        new int[]{R.drawable.cardview_t7binhphuoc, R.drawable.cardview_t7haugiang, R.drawable.cardview_t7longan, R.drawable.cardview_t7tphcm},
                        new String[]{"Bình Phước", "Hậu Giang", "Long An", "TPHCM"},
                        dayOfWeek
                );
            case "Chủ Nhật":
                return createTicketList(
                        new int[]{R.drawable.cardview_cndalat, R.drawable.cardview_cnkiengiang, R.drawable.cardview_cntiengiang},
                        new String[]{"Đà Lạt", "Kiên Giang", "Tiền Giang"},
                        dayOfWeek
                );
            default:
                return new ArrayList<>();
        }
    }

    private List<MainScreenSalerProvince> getTicketsForNextDay(String currentDayOfWeek) {
        String nextDayOfWeek = getNextDayOfWeek(currentDayOfWeek);
        return getTicketsForDay(nextDayOfWeek);
    }

    private List<MainScreenSalerProvince> createTicketList(int[] images, String[] titles, String dayOfWeek) {
        List<MainScreenSalerProvince> ticketList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            ticketList.add(new MainScreenSalerProvince(images[i], titles[i], dayOfWeek));
        }
        return ticketList;
    }

    private String getNextDayOfWeek(String currentDayOfWeek) {
        switch (currentDayOfWeek) {
            case "Thứ 2":
                return "Thứ 3";
            case "Thứ 3":
                return "Thứ 4";
            case "Thứ 4":
                return "Thứ 5";
            case "Thứ 5":
                return "Thứ 6";
            case "Thứ 6":
                return "Thứ 7";
            case "Thứ 7":
                return "Chủ Nhật";
            case "Chủ Nhật":
                return "Thứ 2";
            default:
                return "Không xác định";
        }
    }

    public String getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "Thứ 2";
            case Calendar.TUESDAY:
                return "Thứ 3";
            case Calendar.WEDNESDAY:
                return "Thứ 4";
            case Calendar.THURSDAY:
                return "Thứ 5";
            case Calendar.FRIDAY:
                return "Thứ 6";
            case Calendar.SATURDAY:
                return "Thứ 7";
            case Calendar.SUNDAY:
                return "Chủ Nhật";
            default:
                return "Không xác định";
        }
    }

    public LiveData<List<MainScreenSalerProvince>> getAllTicket() {
        return ticketLuckySalerList;
    }
    public String getCurrentDay(){
        // Hiển thị ngày trên Toolbar
        Calendar calendar = Calendar.getInstance();
        //cardProvinceViewModel.getDayOfWeek(calendar);
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        return currentDate;
    }
}
