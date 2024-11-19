package ticket.luckyticket.saler.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainScreenSalerProvince {
    private int imagePath;
    private String titleTicket;
    private String dayWeek;
    private String timeNow;
    private String dateOfYear;


    public MainScreenSalerProvince(int imagePath, String titleTicket, String dayWeek) {
        this.imagePath = imagePath;
        this.titleTicket = titleTicket;
        this.dayWeek = dayWeek;
        this.timeNow = getCurrentTime();
        this.dateOfYear = getCurrentDate();
    }



    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitleTicket() {
        return titleTicket;
    }

    public void setTitleTicket(String titleTicket) {
        this.titleTicket = titleTicket;
    }

    public String getDayWeek() {
        return dayWeek;
    }

    public void setDayWeek(String dayWeek) {
        this.dayWeek = dayWeek;
    }

    public String getTimeNow() {
        return timeNow;
    }

    public void setTimeNow(String timeNow) {
        this.timeNow = timeNow;
    }

    public String getDateOfYear() {
        return dateOfYear;
    }

    public void setDateOfYear(String dateOfYear) {
        this.dateOfYear = dateOfYear;
    }

    public String getCurrentTime (){
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormatter.format(new Date());
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormatter.format(new Date());
    }
}
