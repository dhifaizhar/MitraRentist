package id.rentist.mitrarentist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

public class CustomDatePickerRangeActivity extends AppCompatActivity {
    CalendarPickerView calendar;
    EditText edtStart, edtEnd;
    Date startDate;
    Date endDate;

    String startDay, startMonth, startYear;
    String endDay, endMonth, endYear;
    String startDateFormat, endDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_date_picker);
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today).inMode(CalendarPickerView.SelectionMode.RANGE);

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate = calendar.getSelectedDates().get(0);
                endDate = calendar.getSelectedDates().get(calendar.getSelectedDates().size()-1);
                startDay = String.valueOf(startDate.getDate());
                startMonth = String.valueOf(startDate.getMonth()+1);
                startYear = String.valueOf(startDate.getYear()-100);
                endDay = String.valueOf(endDate.getDate());
                endMonth = String.valueOf(endDate.getMonth()+1);
                endYear = String.valueOf(endDate.getYear()-100);

                if (startDay.length()<2) {
                    startDay = "0" + startDay;
                }
                if (startMonth.length()<2) {
                    startMonth = "0" + startMonth;
                }
                startYear = "20" + startYear;

                if (endDay.length()<2) {
                    endDay = "0" + endDay;
                }
                if (endMonth.length()<2) {
                    endMonth = "0" + endMonth;
                }
                endYear = "20" + endYear;

                startDateFormat = String.valueOf(startYear) + "-" +
                        String.valueOf(startMonth) + "-" +
                        String.valueOf(startDay);
                endDateFormat = String.valueOf(endYear) + "-" +
                        String.valueOf(endMonth)  + "-" +
                        String.valueOf(endDay);


                /*String toast = "Selected: " + startDate + endDate;
                Toast.makeText(CustomDatePickerRangeActivity.this, toast, LENGTH_SHORT).show();*/

                Intent returnIntent = new Intent();
                returnIntent.putExtra("startDate", startDateFormat);
                returnIntent.putExtra("endDate", endDateFormat);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
