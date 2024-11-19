package ticket.luckyticket.saler.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TicketInformation {
    private String documentId;         // Thêm trường documentId
    private String imagePath;          // Đường dẫn hình ảnh
    private String nameDaiTicket;      // Tên đài
    private String numberSixTicket;    // Sáu số vé
    private String numberTicketSale;   // Số vé muốn bán
    private Timestamp timeAdd;         // Thời gian thêm (kiểu Timestamp)
    private String userId;             // ID người dùng

    public TicketInformation(String imagePath, String nameDaiTicket, String numberSixTicket, String numberTicketSale, Timestamp timeAdd, String userId) {
        this.imagePath = imagePath;
        this.nameDaiTicket = nameDaiTicket;
        this.numberSixTicket = numberSixTicket;
        this.numberTicketSale = numberTicketSale;
        this.timeAdd = timeAdd;
        this.userId = userId;
    }

    public TicketInformation() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimeAdd() {
        return timeAdd;
    }

    public void setTimeAdd(Timestamp timeAdd) {
        this.timeAdd = timeAdd;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getNameDaiTicket() {
        return nameDaiTicket;
    }

    public void setNameDaiTicket(String nameDaiTicket) {
        this.nameDaiTicket = nameDaiTicket;
    }

    public String getNumberSixTicket() {
        return numberSixTicket;
    }

    public void setNumberSixTicket(String numberSixTicket) {
        this.numberSixTicket = numberSixTicket;
    }

    public String getNumberTicketSale() {
        return numberTicketSale;
    }

    public void setNumberTicketSale(String numberTicketSale) {
        this.numberTicketSale = numberTicketSale;
    }

    public String convertTimestampToString() {
        // Chuyển Timestamp thành Date
        Date date = timeAdd.toDate();
        // Định dạng Date thành chuỗi (ví dụ: "dd-MM-yyyy HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        // Trả về chuỗi ngày giờ đã định dạng
        return sdf.format(date);
    }
}
