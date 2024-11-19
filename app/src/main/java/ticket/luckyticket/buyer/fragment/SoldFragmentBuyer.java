package ticket.luckyticket.buyer.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

import ticket.luckyticket.MapsActivity;
import ticket.luckyticket.R;
import ticket.luckyticket.buyer.adapter.BuyFragmentBuyerAdapter;
import ticket.luckyticket.buyer.viewmodelbuyer.BuyFragmentViewModel;
import ticket.luckyticket.saler.model.TicketInformation;
// Đây cần thực hiện 2 hành động? một tạo thông báo có muốn mua 1 lần nữa
public class SoldFragmentBuyer extends Fragment {
    private BuyFragmentBuyerAdapter buyFragmentBuyerAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BuyFragmentViewModel buyFragmentViewModel;
    private String userBuyId;
    private FloatingActionButton fabBuy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sold_buyer, container, false);

        // Setup Floating Action Button
        fabBuy = view.findViewById(R.id.fabBuyer);
        fabBuy.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), MapsActivity.class);
            startActivity(intent);
        });

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewBuy_fragment_buyer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_buyer);
        List<TicketInformation> ticketInformationList = new ArrayList<>();
        buyFragmentBuyerAdapter = new BuyFragmentBuyerAdapter(ticketInformationList);
        recyclerView.setAdapter(buyFragmentBuyerAdapter);

        // ViewModel setup
        buyFragmentViewModel = new ViewModelProvider(this).get(BuyFragmentViewModel.class);
        userBuyId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tải dữ liệu ban đầu
        loadTicketData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTicketList();
            }
        });
        setItemClickListener();
        // Setup SearchView để tìm kiếm dữ liệu
        SearchView searchView = view.findViewById(R.id.searchViewBuyer);
        setupSearchView(searchView);

        return view;
    }

    // Phương thức tải dữ liệu từ Firestore và cập nhật UI
    private void loadTicketData() {
        buyFragmentViewModel.getAllTicketInformationForYesterdayAndToday().observe(getViewLifecycleOwner(), ticketInformations -> {
            buyFragmentBuyerAdapter.updateTicketList(ticketInformations);
        });
    }

    // Phương thức làm mới danh sách vé
    private void refreshTicketList() {
        // Xóa tất cả observer cũ để tránh quan sát lại dữ liệu trùng lặp
        buyFragmentViewModel.getAllTicketInformationForYesterdayAndToday().removeObservers(getViewLifecycleOwner());
        // Quan sát lại dữ liệu mới sau khi làm mới
        loadTicketData();
        swipeRefreshLayout.setRefreshing(false);

    }

    // Phương thức thiết lập SearchView để tìm kiếm vé
    public void setupSearchView(SearchView searchView) {
        Handler handler = new Handler();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    // Gọi ViewModel để lọc dữ liệu
                    buyFragmentViewModel.filter(query).observe(getViewLifecycleOwner(), ticketInformations -> {
                        if (ticketInformations != null) {
                             Log.e("showEditDialogselectedNumber", "Gia tri ticketinformation: " + ticketInformations.get(0).getDocumentId());
                            buyFragmentBuyerAdapter.updateTicketList(ticketInformations);  // Cập nhật danh sách nếu có dữ liệu
                        } else {
                            buyFragmentBuyerAdapter.updateTicketList(new ArrayList<>());  // Làm rỗng danh sách nếu không có kết quả
                        }
                    });
                } else {
                    // Hiển thị tất cả dữ liệu nếu không có chuỗi tìm kiếm
                    buyFragmentViewModel.getAllTicketInformationForYesterdayAndToday().observe(getViewLifecycleOwner(), ticketInformations -> {
                        buyFragmentBuyerAdapter.updateTicketList(ticketInformations);
                    });
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacksAndMessages(null);  // Hủy các callback cũ
                handler.postDelayed(() -> {
                    if (!newText.isEmpty()) {
                        // Gọi ViewModel để lọc dữ liệu khi nhập liệu
                        buyFragmentViewModel.filter(newText).observe(getViewLifecycleOwner(), ticketInformations -> {
                            if (ticketInformations != null) {
                                buyFragmentBuyerAdapter.updateTicketList(ticketInformations);
                            } else {
                                buyFragmentBuyerAdapter.updateTicketList(new ArrayList<>());  // Làm rỗng danh sách nếu không có kết quả
                            }
                        });
                    } else {
                        // Hiển thị tất cả dữ liệu nếu chuỗi tìm kiếm bị xóa
                        buyFragmentViewModel.getAllTicketInformationForYesterdayAndToday().observe(getViewLifecycleOwner(), ticketInformations -> {
                            buyFragmentBuyerAdapter.updateTicketList(ticketInformations);
                        });
                    }
                }, 300);  // Trì hoãn 300ms để tránh xử lý liên tục
                return true;
            }
        });
    }

    // Thiết lập hành động click cho item trong RecyclerView
    private void setItemClickListener() {
        buyFragmentBuyerAdapter.setOnItemClickListener(ticketInformation -> {
            buyFragmentViewModel.getOneBuyerMoney(userBuyId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    buyFragmentViewModel.showEditDialog(ticketInformation, requireContext(), user, new Runnable() {
                        @Override
                        public void run() {
                            Log.e("showEditDialogselectedNumber", "Gia tri ticketinformation: " + ticketInformation.getDocumentId());
                            refreshTicketList();
                        }
                    });
                } else {
                    Log.d("SoldFragmentBuyer", "User is null");
                }
            });
        });
    }
}
