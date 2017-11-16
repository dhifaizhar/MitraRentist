package id.rentist.mitrarentist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CalendarView;
import android.widget.Toast;

public class WorkDateActivity extends AppCompatActivity {
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_date);
        setTitle("Jadwal Operasional");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setVerticalScrollBarEnabled(false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                Toast.makeText(getApplicationContext(),
                        "Selected Date:\n" + "Day = " + i2 + "\n" + "Month = " + i1 + "\n" + "Year = " + i,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }
}
