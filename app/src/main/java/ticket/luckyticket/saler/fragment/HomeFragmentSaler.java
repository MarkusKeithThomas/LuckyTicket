package ticket.luckyticket.saler.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import ticket.luckyticket.saler.AddNewTicketActivity;
import ticket.luckyticket.saler.apdapter.HomeFragmentAdapter;
import ticket.luckyticket.saler.model.MainScreenSalerProvince;
import ticket.luckyticket.saler.viewmodel.MainScreenSalerViewModel;

public class HomeFragmentSaler extends Fragment {
    private RecyclerView recyclerView;
    private HomeFragmentAdapter homeFragmentAdapter;
    private MainScreenSalerViewModel mainScreenSalerViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_saler, container, false);
        // Thiết lập RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Thiết lập ViewModel và Adapter
        mainScreenSalerViewModel = new ViewModelProvider(requireActivity()).get(MainScreenSalerViewModel.class);
        mainScreenSalerViewModel.getAllTicket().observe(getViewLifecycleOwner(), new Observer<List<MainScreenSalerProvince>>() {
            @Override
            public void onChanged(List<MainScreenSalerProvince> mainScreenSalerProvinces) {
                if (mainScreenSalerProvinces != null && !mainScreenSalerProvinces.isEmpty()) {
                    homeFragmentAdapter = new HomeFragmentAdapter(mainScreenSalerProvinces,mainScreenSalerProvince -> {
                        Intent intent = new Intent(getActivity(), AddNewTicketActivity.class);
                        startActivity(intent);

                    });
                    recyclerView.setAdapter(homeFragmentAdapter);

                }

            }

        });












        return view;
    }
}