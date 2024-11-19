package ticket.luckyticket.buyer.model;

import java.util.Objects;

public class ChatRoom {
    private String chatRoomId;
    private String docId; // Thêm thuộc tính để lưu tên của phòng chat
    private String sixNumberTicket;

    public ChatRoom() {
        // Constructor mặc định cho Firebase
    }

    public ChatRoom(String chatRoomId, String docId,String sixNumberTicket) {
        this.chatRoomId = chatRoomId;
        this.docId = docId;
        this.sixNumberTicket = sixNumberTicket;
    }

    public String getSixNumberTicket() {
        return sixNumberTicket;
    }

    public void setSixNumberTicket(String sixNumberTicket) {
        this.sixNumberTicket = sixNumberTicket;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return chatRoomId.equals(chatRoom.chatRoomId) &&
                docId.equals(chatRoom.docId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId, docId);
    }
}
