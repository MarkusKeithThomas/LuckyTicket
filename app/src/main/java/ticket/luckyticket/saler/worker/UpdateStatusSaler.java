package ticket.luckyticket.saler.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateStatusSaler extends Worker {
    public UpdateStatusSaler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Lấy userId từ input data hoặc SharedPreferences
        String salerId = getInputData().getString("userId");
        Log.d("WorkerHer123","checkxemntn"+salerId);

        if (salerId != null) {
            DatabaseReference salerRef = FirebaseDatabase.getInstance().getReference("Salers");

            Map<String, Object> updates = new HashMap<>();
            updates.put("latitude", 0);
            updates.put("longitude", 0);
            updates.put("isOnline", false);

            salerRef.child(salerId).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> System.out.println("Saler attributes updated successfully."))
                    .addOnFailureListener(e -> System.err.println("Failed to update saler attributes: " + e.getMessage()));

        }
        return Result.success();    }
}
