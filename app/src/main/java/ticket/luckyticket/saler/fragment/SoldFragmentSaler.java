package ticket.luckyticket.saler.fragment;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import ticket.luckyticket.R;
import ticket.luckyticket.saler.AddNewTicketActivity;
import ticket.luckyticket.saler.apdapter.SoldFragmentSalerAdapter;
import ticket.luckyticket.saler.viewmodel.SoldFragmentViewModel;
import ticket.luckyticket.saler.model.TicketInformation;
import ticket.luckyticket.saler.viewmodel.AddNewTicketViewModel;

public class SoldFragmentSaler extends Fragment {
    private RecyclerView recyclerView;
    private SoldFragmentSalerAdapter soldFragmentSalerAdapter;
    private SoldFragmentViewModel soldFragmentViewModel;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId;
    private FirebaseAuth auth;
    private AddNewTicketViewModel addNewTicketViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sold_saler, container, false);
        fab = rootView.findViewById(R.id.fab);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Lấy userID của người dùng đang đăng nhập
            Log.d("UserID", "User ID: " + userId);
        } else {
            // Người dùng chưa đăng nhập hoặc đã đăng xuất
            Log.d("UserID", "No user is logged in");
        }
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        fab.setOnClickListener(view ->{
            Intent intent = new Intent(getActivity(), AddNewTicketActivity.class);
            startActivity(intent);
        });

        //Thieets lap recylerview
        recyclerView = rootView.findViewById(R.id.recyclerViewSold_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        soldFragmentSalerAdapter = new SoldFragmentSalerAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(soldFragmentSalerAdapter);


        //SoldFragmentViewModelFactory factory = new SoldFragmentViewModelFactory(userId);
        soldFragmentViewModel = new ViewModelProvider(this).get(SoldFragmentViewModel.class);
        addNewTicketViewModel = new ViewModelProvider(this).get(AddNewTicketViewModel.class);
        String userSalerId = auth.getCurrentUser().getUid();


        // Lắng nghe sự kiện click vào CardView
        soldFragmentSalerAdapter.setOnItemClickListener(ticketInformation -> {
            soldFragmentViewModel.showEditDialog(ticketInformation,getContext()).observe(getViewLifecycleOwner(), string -> {
                addNewTicketViewModel.updateTicket(ticketInformation.getDocumentId(),string);
                soldFragmentViewModel.getAllTicketInformation(ticketInformation.getUserId()).removeObservers(getViewLifecycleOwner());
                soldFragmentViewModel.getAllTicketInformation(ticketInformation.getUserId())
                        .observe(getViewLifecycleOwner(),ticketInformations -> {
                        soldFragmentSalerAdapter.updateTicketList(ticketInformations);});
            });
        });
        if (auth.getCurrentUser() != null) {
            soldFragmentViewModel.getAllTicketInformation(userSalerId).observe(getViewLifecycleOwner(),
                    new Observer<List<TicketInformation>>() {
                        @Override
                        public void onChanged(List<TicketInformation> ticketInformations) {
                            soldFragmentSalerAdapter.updateTicketList(ticketInformations);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Cần đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
        }


        // Thiết lập hành động khi vuốt để làm mới
        swipeRefreshLayout.setOnRefreshListener(() -> {
            soldFragmentViewModel.getAllTicketInformation(userSalerId).observe(getViewLifecycleOwner(), ticketInformations -> {
                soldFragmentSalerAdapter.updateTicketList(ticketInformations);
                swipeRefreshLayout.setRefreshing(false);
            });
        });

//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }


//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getBindingAdapterPosition();
//                Context context = recyclerView.getContext();
//                TicketInformation ticketInformation = soldFragmentSalerAdapter.getItemPositionAt(position);
//                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
//                    soldFragmentSalerAdapter.removeItemTicket(position);
//                    soldFragmentViewModel.deleteImage(ticketInformation.getImagePath());
//                    soldFragmentViewModel.deleteTicket(ticketInformation.getDocumentId());
//                    soldFragmentViewModel.getAllTicketInformation().removeObservers(getViewLifecycleOwner());
//                    soldFragmentViewModel.getAllTicketInformation().observe(getViewLifecycleOwner(), new Observer<List<TicketInformation>>() {
//                        @Override
//                        public void onChanged(List<TicketInformation> ticketInformations) {
//                            cardViewSoldFragmentAdapter.updateTicketList(ticketInformations);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//
//                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                    View itemView = viewHolder.itemView;
//                    Paint p = new Paint();
//                    p.setColor(Color.RED);
//                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), p);
//                }
//            }
//        });
//
//        //Ganws Itemhelpper
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//        fab.setOnClickListener(view -> {
//            Intent intent = new Intent(getActivity(), AddNewTicketActivity.class);
//            startActivity(intent);
//        });

       return rootView;
    }







}