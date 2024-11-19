package ticket.luckyticket.saler.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RatingForSaler {
    private String contentComment;
    private String ratingComment;
    private String salerId;
    private Timestamp timeStamp;

    public RatingForSaler(){}

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSalerId() {
        return salerId;
    }

    public void setSalerId(String salerId) {
        this.salerId = salerId;
    }

    public String getContentComment() {
        return contentComment;
    }

    public void setContentComment(String contentComment) {
        this.contentComment = contentComment;
    }

    public String getRatingComment() {
        return ratingComment;
    }

    public void setRatingComment(String ratingComment) {
        this.ratingComment = ratingComment;
    }
    // Phương thức chuyển đổi Timestamp sang chuỗi định dạng
    public String convertTimestampToString() {
        // Chuyển Timestamp thành Date
        Date date = timeStamp.toDate();
        // Định dạng Date thành chuỗi (ví dụ: "dd-MM-yyyy HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        // Trả về chuỗi ngày giờ đã định dạng
        return sdf.format(date);
    }
}
