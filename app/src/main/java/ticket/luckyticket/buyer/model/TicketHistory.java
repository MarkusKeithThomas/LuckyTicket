package ticket.luckyticket.buyer.model;

import com.google.firebase.Timestamp;

public class TicketHistory {
    private String userBoughtID;
    private String userSaleID;
    private String ticketSixNumber;
    private String ticketNumberSale;
    private Timestamp timeSaleBought;
    private String imageURL;
    private String nameDai;
    private String documentIdTicketHistory;         // Thêm trường documentId


    public TicketHistory(String userBoughtID, String userSaleID, String ticketSixNumber, String ticketNumberSale, Timestamp timeSaleBought, String imageURL, String nameDai) {
        this.userBoughtID = userBoughtID;
        this.userSaleID = userSaleID;
        this.ticketSixNumber = ticketSixNumber;
        this.ticketNumberSale = ticketNumberSale;
        this.timeSaleBought = timeSaleBought;
        this.imageURL = imageURL;
        this.nameDai = nameDai;
    }
    public TicketHistory(){}

    public String getDocumentIdTicketHistory() {
        return documentIdTicketHistory;
    }

    public void setDocumentIdTicketHistory(String documentIdTicketHistory) {
        this.documentIdTicketHistory = documentIdTicketHistory;
    }

    public String getUserBoughtID() {
        return userBoughtID;
    }

    public void setUserBoughtID(String userBoughtID) {
        this.userBoughtID = userBoughtID;
    }

    public String getUserSaleID() {
        return userSaleID;
    }

    public void setUserSaleID(String userSaleID) {
        this.userSaleID = userSaleID;
    }

    public String getTicketSixNumber() {
        return ticketSixNumber;
    }

    public void setTicketSixNumber(String ticketSixNumber) {
        this.ticketSixNumber = ticketSixNumber;
    }

    public String getTicketNumberSale() {
        return ticketNumberSale;
    }

    public void setTicketNumberSale(String ticketNumberSale) {
        this.ticketNumberSale = ticketNumberSale;
    }

    public Timestamp getTimeSaleBought() {
        return timeSaleBought;
    }

    public void setTimeSaleBought(Timestamp timeSaleBought) {
        this.timeSaleBought = timeSaleBought;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNameDai() {
        return nameDai;
    }

    public void setNameDai(String nameDai) {
        this.nameDai = nameDai;
    }
}
