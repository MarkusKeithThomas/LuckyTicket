package ticket.luckyticket.buyer.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.RatingForSalerActivity;
import ticket.luckyticket.buyer.adapter.HistoryFragmentBuyerAdapter;
import ticket.luckyticket.buyer.viewmodelbuyer.HistoryFragmentBuyerViewModel;

public class HistoryFragmentBuyer extends Fragment {
    private HistoryFragmentBuyerViewModel historyFragmentBuyerViewModel;
    private RecyclerView recyclerView;
    private HistoryFragmentBuyerAdapter historyFragmentBuyerAdapter;
    private String userId;
    private FirebaseAuth auth;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_buyer, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_history_buyer);

        recyclerView = view.findViewById(R.id.recyclerViewTicketHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        historyFragmentBuyerAdapter = new HistoryFragmentBuyerAdapter(new ArrayList<>(), getContext(), ticketHistory -> {
            Intent intent = new Intent(getActivity(), RatingForSalerActivity.class);
            intent.putExtra("userSalerId",ticketHistory.getUserSaleID());
            startActivity(intent);

        });
        recyclerView.setAdapter(historyFragmentBuyerAdapter);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userId = currentUser.getUid();

        // Khởi tạo ViewModel
        historyFragmentBuyerViewModel = new ViewModelProvider(this).get(HistoryFragmentBuyerViewModel.class);

        // Lắng nghe sự thay đổi từ LiveData
        historyFragmentBuyerViewModel.getTicketHistoryLiveData(userId).observe(getViewLifecycleOwner(), ticketHistories -> {
            if (ticketHistories != null) {
                historyFragmentBuyerAdapter.updateTicketHistory(ticketHistories); // Cập nhật adapter với dữ liệu mới
            }
        });

        // Xử lý làm mới dữ liệu với SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            historyFragmentBuyerViewModel.refreshTicketHistory(userId);  // Làm mới dữ liệu
            swipeRefreshLayout.setRefreshing(false); // Tắt hiệu ứng làm mới sau khi hoàn thành
        });
        return view;
    }
}