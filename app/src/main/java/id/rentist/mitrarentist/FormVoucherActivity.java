package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FormVoucherActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnStartDate, btnEndDate;
    EditText startDate, endDate;
    private int mYear, mMonth, mDay;
//    private CalendarPickerView calendar;

    @TargetApi(Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_voucher);
        setTitle("Tambah Voucher");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        // Daterangepicker
//        Calendar nextYear = Calendar.getInstance();
//        nextYear.add(Calendar.YEAR, 1);
//
//        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
//        Date today = new Date();
//        calendar.init(today, nextYear.getTime())
//                .withSelectedDate(today)
//                .inMode(CalendarPickerView.SelectionMode.RANGE);
//        calendar.highlightDates(getHolidays());


        btnStartDate=(Button)findViewById(R.id.btn_start_date);
        startDate=(EditText)findViewById(R.id.vou_startDate);

        Spinner asCategorySpin=(Spinner) findViewById(R.id.vou_spinner);
        String asCategory = asCategorySpin.getSelectedItem().toString();
//        btnEndDate=(Button)findViewById(R.id.btn_end_date);
//        endDate=(EditText)findViewById(R.id.vou_endDate);

        btnStartDate.setOnClickListener(this);
//        btnEndDate.setOnClickListener(this);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_calendar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id){
//            case R.id.action_settings:
//                return true;
//            case R.id.action_next:
//                ArrayList<Date> selectedDates = (ArrayList<Date>)calendar
//                        .getSelectedDates();
//                Toast.makeText(FormVoucherActivity.this, selectedDates.toString(),
//                        Toast.LENGTH_LONG).show();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnStartDate) {

//            // Get Current Date
//            final Calendar c = Calendar.getInstance();
//            mYear = c.get(Calendar.YEAR);
//            mMonth = c.get(Calendar.MONTH);
//            mDay = c.get(Calendar.DAY_OF_MONTH);
//
//
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                    new DatePickerDialog.OnDateSetListener() {
//
//                        @Override
//                        public void onDateSet(DatePicker view, int year,
//                                              int monthOfYear, int dayOfMonth) {
//
//                            startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//
//                        }
//                    }, mYear, mMonth, mDay);
//            datePickerDialog.show();
            Intent iKebijakan = new Intent(FormVoucherActivity.this, SampleTimesSquareActivity.class);
            startActivity(iKebijakan);
        }

//        } else if (v == btnEndDate) {
//            // Get Current Date
//            final Calendar c = Calendar.getInstance();
//            mYear = c.get(Calendar.YEAR);
//            mMonth = c.get(Calendar.MONTH);
//            mDay = c.get(Calendar.DAY_OF_MONTH);
//
//
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                    new DatePickerDialog.OnDateSetListener() {
//
//                        @Override
//                        public void onDateSet(DatePicker view, int year,
//                                              int monthOfYear, int dayOfMonth) {
//
//                            endDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//
//                        }
//                    }, mYear, mMonth, mDay);
//            datePickerDialog.show();
//        }
    }

//    @TargetApi(Build.VERSION_CODES.N)
//    public ArrayList<Date> getHolidays() {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
//        String dateInString = "21-04-2015";
//        Date date = null;
//        try {
//            date = sdf.parse(dateInString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        ArrayList<Date> holidays = new ArrayList<>();
//        holidays.add(date);
//        return holidays;
//    }
}
