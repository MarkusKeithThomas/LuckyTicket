package ticket.luckyticket.buyer.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.buyer.WebViewActivity;
import ticket.luckyticket.buyer.adapter.HomeFragmentBuyerAdapter;
import ticket.luckyticket.saler.model.MainScreenSalerProvince;
import ticket.luckyticket.saler.viewmodel.MainScreenSalerViewModel;

public class HomeFragmentBuyer extends Fragment {
    private RecyclerView recyclerView;
    private HomeFragmentBuyerAdapter homeFragmentBuyerAdapter;
    private MainScreenSalerViewModel mainScreenSalerViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_buyer, container, false);

        // Thiết lập RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewHome_buyer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        // Thiết lập ViewModel và Adapter
        mainScreenSalerViewModel = new ViewModelProvider(requireActivity()).get(MainScreenSalerViewModel.class);
        mainScreenSalerViewModel.getAllTicket().observe(getViewLifecycleOwner(), new Observer<List<MainScreenSalerProvince>>() {
            @Override
            public void onChanged(List<MainScreenSalerProvince> mainScreenSalerProvinceList) {
                if (mainScreenSalerProvinceList != null && !mainScreenSalerProvinceList.isEmpty()) {
                    // Thiết lập Adapter khi dữ liệu thay đổi
                    homeFragmentBuyerAdapter = new HomeFragmentBuyerAdapter(mainScreenSalerProvinceList,mainScreenSalerProvince ->{
                        if (mainScreenSalerProvince.getTitleTicket() != null) {
                            Intent intent = new Intent(getActivity(), WebViewActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "Title is null", Toast.LENGTH_SHORT).show();
                        }
                    });
                    recyclerView.setAdapter(homeFragmentBuyerAdapter);

                }
            }
        });

        return view;
    }
}