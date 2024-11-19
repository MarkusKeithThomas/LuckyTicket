package ticket.luckyticket.buyer.model;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class ChattingAB {
        private String chatRoomId;  // ID của phòng chat
        private String senderId;  // ID của người gửi tin nhắn
        //    private String receiverId;  // ID của người nhận tin nhắn
        private String messageContent;  // Nội dung tin nhắn
        private long timestamp;  // Thời gian tin nhắn được gửi
        private String documentIdTicketHistory; // Tạo id cho cuộc trò chuyện để lọc sau này
        private String id; // ID duy nhất của mỗi đối tượng ChattingAB
        private boolean isMine; // Biến xác định tin nhắn có phải của người dùng hiện tại hay không




        // Constructor với đầy đủ tham số
        public ChattingAB(String chatId,String senderId, String messageContent, long timestamp) {
            this.chatRoomId = chatId;
            this.senderId = senderId;
            this.messageContent = messageContent;
            this.timestamp = timestamp;
        }

        // Constructor mặc định
        public ChattingAB() {}

        // Getter và Setter cho tất cả các thuộc tính
        public String getChatRoomId() {
            return chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getDocumentIdTicketHistory() {
            return documentIdTicketHistory;
        }

        public void setDocumentIdTicketHistory(String documentIdTicketHistory) {
            this.documentIdTicketHistory = documentIdTicketHistory;
        }

        // Hàm chuyển đổi timestamp thành giờ định dạng "HH:mm"
        public String convertTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = new Date(timestamp);
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        }

        //Kiểm tra nếu tin nhắn là của người dùng hiện tại
        public boolean isMine() {
            if (senderId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                return true;
            }
            return false;
        }

        public void setMine(boolean isMine) {
            this.isMine = isMine;
        }


        // Thêm phương thức getId()
        public String getId() {
            return id;
        }

        // Ghi đè phương thức equals() và hashCode() nếu cần thiết
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChattingAB that = (ChattingAB) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
}
