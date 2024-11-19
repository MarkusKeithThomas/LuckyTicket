package ticket.luckyticket.saler.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.model.TicketHistory;
import ticket.luckyticket.saler.apdapter.HistorySalerFragmentAdapter;
import ticket.luckyticket.saler.viewmodel.HistoryFragmentSalerViewModel;

public class HistoryFragmentSaler extends Fragment {
    private HistorySalerFragmentAdapter historySalerFragmentAdapter;
    private RecyclerView recyclerView;
    private List<TicketHistory> ticketHistoryList;
    private HistoryFragmentSalerViewModel historyFragmentSalerViewModel;
    private FirebaseAuth auth;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_saler, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewTicketHistory_saler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_history_saler);

        ticketHistoryList = new ArrayList<>();
        historySalerFragmentAdapter = new HistorySalerFragmentAdapter(ticketHistoryList,getContext());
        recyclerView.setAdapter(historySalerFragmentAdapter);
        historyFragmentSalerViewModel = new ViewModelProvider(this).get(HistoryFragmentSalerViewModel.class);
        if (auth.getCurrentUser() != null) {
            String salerId = auth.getCurrentUser().getUid();
            historyFragmentSalerViewModel.getAllTicketHistoryForSaler(salerId)
                    .observe(getViewLifecycleOwner(), new Observer<List<TicketHistory>>() {
                        @Override
                        public void onChanged(List<TicketHistory> ticketHistories) {
                            historySalerFragmentAdapter.updateData(ticketHistories);
                        }
                    });
        } else {
            Log.w("HistoryFragment", "User not logged in.");
            // Xử lý trường hợp chưa đăng nhập (ví dụ: chuyển hướng đến màn hình đăng nhập)
        }
        // Xử lý làm mới dữ liệu với SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            historyFragmentSalerViewModel.refreshTicketHistory(auth.getCurrentUser().getUid());  // Làm mới dữ liệu
            swipeRefreshLayout.setRefreshing(false); // Tắt hiệu ứng làm mới sau khi hoàn thành
        });

        return view;
    }
}