package ticket.luckyticket.saler.viewmodel;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ticket.luckyticket.R;
import ticket.luckyticket.saler.model.TicketInformation;
import ticket.luckyticket.saler.repository.AddNewTicketRepository;

public class SoldFragmentViewModel extends AndroidViewModel {
    private AddNewTicketRepository addNewTicketRepository;
    private LiveData<List<TicketInformation>> allTickets = new MutableLiveData<>();
    private Context context;

    public SoldFragmentViewModel(Application application) {
        super(application);
        this.context = application;
        this.addNewTicketRepository = new AddNewTicketRepository();
    }

    public LiveData<List<TicketInformation>> getAllTicketInformation(String userSalerId) {
        allTickets = addNewTicketRepository.getAllTicketInformation(userSalerId);
        return allTickets;
    }

//    public void deleteTicket(String documentID) {
//        soldFragmentRepository.deleteTicket(documentID);
//        refreshData();
//    }
//
//    public void deleteImage(String imageURL) {
//        soldFragmentRepository.deleteImage(imageURL);
//        refreshData();
//    }
//
    public void updateTicket(String documentID, String newTicketSaleRemain,String userSalerId) {
        addNewTicketRepository.updateTicket(documentID, newTicketSaleRemain);
        refreshData(userSalerId);
    }

    private void refreshData(String userSalerId) {
        allTickets = addNewTicketRepository.getAllTicketInformation(userSalerId);
    }
    // Phương thức hiển thị Dialog và chỉnh sửa thông tin
    public LiveData<String> showEditDialog(TicketInformation ticketInformation,Context context) {
        Log.d("showEditDialog", "User ID: ");

        // Tạo MutableLiveData để lưu trữ kết quả từ dialog
        MutableLiveData<String> resultLiveData = new MutableLiveData<>();

        // Sử dụng AlertDialog để hiển thị hộp thoại
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Số vé bạn hiện còn: " + ticketInformation.getNumberSixTicket()).setIcon(R.drawable.icon_buyer);

        // Danh sách các số mà người dùng có thể chọn
        String[] predefinedNumbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        AtomicReference<String> selectedNumber = new AtomicReference<>(ticketInformation.getNumberTicketSale());

        // Tìm lựa chọn mặc định
        int defaultSelectedIndex = -1;
        for (int i = 0; i < predefinedNumbers.length; i++) {
            if (predefinedNumbers[i].equals(ticketInformation.getNumberTicketSale())) {
                defaultSelectedIndex = i;
                break;
            }
        }

        // Hiển thị các lựa chọn
        builder.setSingleChoiceItems(predefinedNumbers, defaultSelectedIndex, (dialog, which) -> {
            selectedNumber.set(predefinedNumbers[which]);
        });

        // Nút OK để xác nhận lựa chọn
        builder.setPositiveButton("Cập Nhật", (dialog, which) -> {
            if (selectedNumber.get() != null) {
                // Đặt giá trị đã chọn vào MutableLiveData
                resultLiveData.setValue(selectedNumber.get());
            }
        });

        // Nút Cancel để hủy bỏ
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();

        // Trả lại LiveData để lắng nghe kết quả sau khi dialog được đóng
        return resultLiveData;
    }
}
