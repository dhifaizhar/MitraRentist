package id.rentist.mitrarentist.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdhif on 31/10/2017.
 */

public class AdministrationArea {
    private Context context;
    private ArrayList<String> province;
    private Spinner provinceSpinner;

    private static final String TAG = "AdministrationArea";
    private static final String TOKEN = "secretissecret";

    public AdministrationArea(Context context){
        this.context = context;
        province = new ArrayList<String>();
    }

    public void getProvince(Spinner provinceSpinner){
        this.provinceSpinner = provinceSpinner;
        new getProvinceList().execute();
    }

    private class getProvinceList extends AsyncTask<String, String, String> {
        private String errorMsg, responseProvince;

        private getProvinceList() {}

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_PROVINCE;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String data) {
                    responseProvince = data;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Province Fetch Error : " + errorMsg);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", TOKEN);
                    return keys;
                }
            };
            try {
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return responseProvince;
        }

        @Override
        protected void onPostExecute(String prov) {
            try {
                JSONArray respArray = new JSONArray(prov);
                if(respArray.length() > 0){
                    for (int i = 0; i < respArray.length(); i++) {
                        JSONObject respObject = respArray.getJSONObject(i);
                        province.add(respObject.getString("province_name"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            provinceSpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, province));
        }
    }
}
