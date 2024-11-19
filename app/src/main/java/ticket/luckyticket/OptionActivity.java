package ticket.luckyticket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import ticket.luckyticket.buyer.MainScreenBuyerActivity;
import ticket.luckyticket.buyer.SignInBuyerActivity;
import ticket.luckyticket.saler.MainScreenSalerActivity;
import ticket.luckyticket.saler.SignInSalerActivity;

public class OptionActivity extends AppCompatActivity {
    private ImageView imageView, image_buyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        imageView = findViewById(R.id.image_saler);
        image_buyer = findViewById(R.id.image_buyer);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent intent = new Intent(OptionActivity.this, MainScreenSalerActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(OptionActivity.this, SignInSalerActivity.class);
                    startActivity(intent);
                }

            }
        });

        image_buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent intent = new Intent(OptionActivity.this, MainScreenBuyerActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(OptionActivity.this, SignInBuyerActivity.class);
                    startActivity(intent);
                }

            }
        });



    }
}