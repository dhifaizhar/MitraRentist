package id.rentist.mitrarentist;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class WorkDateActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    TextView startDate, endDate;
    Button btnstartDate, btnendDate;
    private int jam, menit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_date);
        setTitle("Jadwal Operasional");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        startDate = (TextView) findViewById(R.id.startDate);
        btnstartDate = (Button) findViewById(R.id.btn_start_date);
        btnstartDate.setOnClickListener(this);

        endDate = (TextView) findViewById(R.id.endDate);
        btnendDate = (Button) findViewById(R.id.btn_end_date);
        btnendDate.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iEditWork = new Intent(WorkDateActivity.this, DashboardActivity.class);
                startActivity(iEditWork);
                Toast.makeText(getApplicationContext()," Jadwal Diubah", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        final Calendar calendar;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            jam = calendar.get(Calendar.HOUR_OF_DAY);
            menit = calendar.get(Calendar.MINUTE);
        }

        if (v == btnstartDate) {

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startDate.setText(String.format("%02d:%02d", hourOfDay, minute));
                }
            }, jam, menit, false);
            timePickerDialog.show();

        }else if(v == btnendDate){

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    endDate.setText(String.format("%02d:%02d", hourOfDay, minute));
                }
            }, jam, menit, false);
            timePickerDialog.show();

        }
    }
}
