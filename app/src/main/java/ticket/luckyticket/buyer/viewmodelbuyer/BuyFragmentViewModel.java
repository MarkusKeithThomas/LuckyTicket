package ticket.luckyticket.buyer.viewmodelbuyer;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.UUID;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.buyer.repository.BuyFragmentRepository;
import ticket.luckyticket.saler.model.TicketInformation;
import ticket.luckyticket.saler.model.User;
import ticket.luckyticket.saler.repository.AddNewTicketRepository;

public class BuyFragmentViewModel extends ViewModel {
    private BuyFragmentRepository buyFragmentRepository;
    private AddNewTicketRepository addNewTicketRepository;
    private final LiveData<List<TicketInformation>> allTicketInformation; // Dữ liệu toàn bộ vé

    public BuyFragmentViewModel() {
        buyFragmentRepository = new BuyFragmentRepository();
        addNewTicketRepository = new AddNewTicketRepository();
        allTicketInformation = buyFragmentRepository.getAllTicketInformationForYesterdayAndToday(); // Lấy dữ liệu vé từ repository
    }

    public LiveData<User> getOneBuyerMoney(String userId){
        return buyFragmentRepository.getOneBuyerMoney(userId);
    }

    // Thêm mới một vé
    public void addNewTicket(TicketHistory ticketHistory) {
        buyFragmentRepository.addNewTicket(ticketHistory);
        refreshData(); // Làm mới dữ liệu sau khi thêm vé mới
    }

    // Trả về LiveData cho tất cả thông tin vé
    public LiveData<List<TicketInformation>> getAllTicketInformationForYesterdayAndToday() {
        return buyFragmentRepository.getAllTicketInformationForYesterdayAndToday();
    }

    // Phương thức làm mới dữ liệu từ Firestore
    public void refreshData() {
        // Gọi phương thức từ repository để làm mới dữ liệu
        buyFragmentRepository.getAllTicketInformationForYesterdayAndToday();
    }
    public String[] remainTicket(int ticketType) {
        switch (ticketType) {
            case 1:
                return new String[]{"1"};
            case 2:
                return new String[]{"1", "2"};
            case 3:
                return new String[]{"1", "2", "3"};
            case 4:
                return new String[]{"1", "2", "3", "4"};
            case 5:
                return new String[]{"1", "2", "3", "4", "5"};
            case 6:
                return new String[]{"1", "2", "3", "4", "5", "6"};
            case 7:
                return new String[]{"1", "2", "3", "4", "5", "6", "7"};
            case 8:
                return new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
            case 9:
                return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
            case 10:
                return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
            case 11:
                return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
            case 12:
                return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
            default:
                System.out.println("Loại vé không hợp lệ");
                return new String[]{};
        }
    }
    // Phương thức hiển thị Dialog và chỉnh sửa thông tin
    public void showEditDialog(TicketInformation ticketInformation, Context context, User user,Runnable onSuccessCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Vé bạn đang xem: "+ticketInformation.getNumberSixTicket()).setIcon(R.drawable.icon_buyer);


        String[] predefinedNumbers = new String[0];
        int numberInitial = 0;

        try {
            numberInitial = Integer.parseInt(ticketInformation.getNumberTicketSale());
            predefinedNumbers = remainTicket(numberInitial);
        } catch (NumberFormatException e) {
            Log.e("showEditDialog", "Chuỗi không thể chuyển đổi thành số nguyên: " + e.getMessage());
        }

        if (predefinedNumbers.length == 0) {
            Toast.makeText(context, "Không có số vé nào để chọn", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] selectedNumber = {ticketInformation.getNumberTicketSale()};
        int defaultSelectedIndex = -1;

        for (int i = 0; i < predefinedNumbers.length; i++) {
            if (predefinedNumbers[i].equals(ticketInformation.getNumberTicketSale())) {
                defaultSelectedIndex = i;
                break;
            }
        }

        String[] finalPredefinedNumbers = predefinedNumbers;
        builder.setSingleChoiceItems(predefinedNumbers, defaultSelectedIndex, (dialog, which) -> {
            selectedNumber[0] = finalPredefinedNumbers[which];
        });

        // Xử lý khi người dùng bấm "Mua"
        int finalNumberInitial = numberInitial;
        builder.setPositiveButton("Mua", (dialog, which) -> {
            if (selectedNumber[0] != null) {
                int remainTicket = finalNumberInitial - Integer.parseInt(selectedNumber[0]);
                double moneyAcc = Double.parseDouble(user.getMoneyAccount());
                double moneyBuy = Double.parseDouble(selectedNumber[0]) * 10000;

                if (moneyAcc >= moneyBuy) {
                    // Tạo lịch sử mua vé
                    TicketHistory ticketHistory = new TicketHistory();
                    String documentId = UUID.randomUUID().toString();
                    ticketHistory.setDocumentIdTicketHistory(documentId);
                    ticketHistory.setImageURL(ticketInformation.getImagePath());
                    ticketHistory.setNameDai(ticketInformation.getNameDaiTicket());
                    ticketHistory.setTicketSixNumber(ticketInformation.getNumberSixTicket());
                    ticketHistory.setTicketNumberSale(selectedNumber[0]);
                    ticketHistory.setTimeSaleBought(Timestamp.now());
                    ticketHistory.setUserBoughtID(user.getUserId());
                    ticketHistory.setUserSaleID(ticketInformation.getUserId());
                    // Upload thông tin lịch sử
                    upLoadTicketHistory(ticketHistory);
                    // Cập nhật số tiền và số lượng vé
                    if (user.getUserId().equals(ticketInformation.getUserId())) {
                        addNewTicketRepository.updateTicketWithoutTime(ticketInformation.getDocumentId(), String.valueOf(remainTicket));
                    } else {
                        // Cập nhật tiền cho người mua và người bán
                        buyFragmentRepository.updateMoneyBuyer(user.getUserId(), moneyBuy);
                        buyFragmentRepository.updateMoneySaler(ticketInformation.getUserId(), moneyBuy);
                        // Cập nhật số lượng vé còn lại
                        //Log.e("showEditDialogselectedNumber", "Chuỗi không nguyên: " + ticketInformation.getDocumentId());
                        Log.e("showEditDialogselectedNumber", "Chuỗi khôn nguyên: " + String.valueOf(remainTicket));
                        addNewTicketRepository.updateTicketWithoutTime(ticketInformation.getDocumentId(), String.valueOf(remainTicket));
                    }

                    // Gọi callback sau khi mua thành công
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }

                } else {
                    Toast.makeText(context, "Số tiền bạn không đủ giao dịch.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public void upLoadTicketHistory(TicketHistory ticketHistory){
        buyFragmentRepository.upLoadTicketHistory(ticketHistory);
    }

    public LiveData<List<TicketInformation>> filter(String query) {
        if (query.isEmpty()) {
            // Nếu chuỗi tìm kiếm rỗng, hiển thị lại toàn bộ danh sách
            return getAllTicketInformationForYesterdayAndToday();  // Gọi lại để tải toàn bộ dữ liệu
        } else {
            return buyFragmentRepository.getDataByQuery(query);
        }
    }

}
