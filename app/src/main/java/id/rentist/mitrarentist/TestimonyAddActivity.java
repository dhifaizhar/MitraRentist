package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import id.rentist.mitrarentist.tools.SessionManager;

public class TestimonyAddActivity extends AppCompatActivity {
    private SessionManager sm;
    private Intent iTesti;

    SimpleRatingBar cleanliness, neatness, honesty, comunication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimony_add);

        sm = new SessionManager(getApplicationContext());

        cleanliness = (SimpleRatingBar) findViewById(R.id.t_cleanliness_rating);
        neatness = (SimpleRatingBar) findViewById(R.id.t_neatness_rating);
        honesty = (SimpleRatingBar) findViewById(R.id.t_honesty_rating);
        comunication = (SimpleRatingBar) findViewById(R.id.t_comunication_rating);

        cleanliness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanliness.setPressedFillColor(getResources().getColor(R.color.colorPrice));
            }
        });
    }
}
