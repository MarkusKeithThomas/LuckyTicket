package ticket.luckyticket.saler.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import android.location.Location;
import android.util.Log;


import ticket.luckyticket.saler.model.SalerMap;
import ticket.luckyticket.saler.model.TicketInformation;
import ticket.luckyticket.saler.repository.SalerMapRepository;

public class SalerMapViewModel extends AndroidViewModel {
    private SalerMapRepository salerMapRepository;
    private MutableLiveData<Location> userLocationLiveData = new MutableLiveData<>();

    public SalerMapViewModel(@NonNull Application application) {
        super(application);
        salerMapRepository = new SalerMapRepository();
    }
    public LiveData<List<TicketInformation>> getAllTicketForOneSaler(String userId){
        return salerMapRepository.getAllTicketForOneSaler(userId);
    }

    public LiveData<List<SalerMap>> getAllSalerMap() {
        return salerMapRepository.getAllSalerMap();
    }

    // Cập nhật LiveData vị trí và lưu vào Firebase
    public void updateLocation(String userId,double latitude, double longitude, boolean isOnline) {
        salerMapRepository.updateSalerLocation(userId, latitude, longitude, true); // Lưu vào Firebase
    }
}
