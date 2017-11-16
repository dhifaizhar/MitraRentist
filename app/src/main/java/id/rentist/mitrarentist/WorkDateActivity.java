package id.rentist.mitrarentist;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.decorator.EventDecorator;
import id.rentist.mitrarentist.decorator.HighlightWeekendsDecorator;
import id.rentist.mitrarentist.decorator.MySelectorDecorator;
import id.rentist.mitrarentist.decorator.OneDayDecorator;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class WorkDateActivity extends AppCompatActivity implements OnDateSelectedListener {
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private SessionManager sm;
    Intent iWork;

    AsyncTask mWorkTask = null;
    MaterialCalendarView calendarView;
    List<CalendarDay> dayEvent;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private static final String TAG = "WorkActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_date);
        setTitle("Jadwal Operasional");

        iWork = getIntent();
        sm = new SessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setArrowColor(0xFF00AEEE);
        calendarView.setSelectionColor(0xFF00AEEE);
        calendarView.setOnDateChangedListener(this);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        Calendar instance = Calendar.getInstance();
        calendarView.setSelectedDate(instance.getTime());

        Calendar instance1 = Calendar.getInstance();
        instance1.set(instance1.get(Calendar.YEAR), Calendar.JANUARY, 1);

        Calendar instance2 = Calendar.getInstance();
        instance2.set(instance2.get(Calendar.YEAR), Calendar.DECEMBER, 31);

        calendarView.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();

        calendarView.addDecorators(
                new MySelectorDecorator(this),
                new HighlightWeekendsDecorator(),
                oneDayDecorator
        );

        mWorkTask = new getDateEvent().execute();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(getApplicationContext(),
                        "Selected Date:\n" + "Day = " + date.getDay() + "\n" + "Month = " + date.getMonth() + "\n" + "Year = " + date.getMonth(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        oneDayDecorator.setDate(date.getDate());
        widget.invalidateDecorators();
    }

    private class getDateEvent extends AsyncTask<String, String, String> {
        private String errorMsg, responseEvent;

        private getDateEvent() {}

        @Override
        protected String doInBackground(String... voids) {
            String URL = AppConfig.URL_VIEW_EVENT;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseEvent = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Event Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", String.valueOf(sm.getIntPreferences("id_tenant")));
                    keys.put("id_asset", String.valueOf(iWork.getIntExtra("id_asset",0)));
                    keys.put("id_asset_category", iWork.getStringExtra("id_asset_category"));
                    return keys;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    // Posting parameters to login url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", TOKEN);
                    return keys;
                }
            };

            try {
                requestQueue.add(stringRequest);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e(TAG, responseEvent);
            return responseEvent;
        }

        @Override
        protected void onPostExecute(String event) {
            if (event != null) {
                try {
                    JSONObject eventObject = new JSONObject(event);
                    JSONArray eventArray = new JSONArray(eventObject.getString("transaction"));

                    ArrayList<CalendarDay> dates = new ArrayList<>();
                    for (int i = 0; i < eventArray.length(); i++) {
                        JSONObject arrayObject = eventArray.getJSONObject(i);

                        try{
                            Date date = format.parse(arrayObject.getString("start_date"));
                            CalendarDay day = CalendarDay.from(date);
                            dates.add(day);
                        }catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    dayEvent = dates;
                    calendarView.addDecorator(new EventDecorator(Color.RED, dayEvent));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Tidak ada event.",
                        Toast.LENGTH_LONG).show();
            }

            mWorkTask = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }
}
