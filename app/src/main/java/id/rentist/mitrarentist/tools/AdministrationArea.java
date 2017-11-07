package id.rentist.mitrarentist.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    private SessionManager sm;
    private ArrayList<String> province, city, distric, village;
    private Map<Integer,String> provinceMap, cityMap, districMap, villageMap;
    private Spinner provinceSpinner, citySpinner, districSpinner, villageSpinner;
    private EditText idProv, idCity;

    private static final String TAG = "AdministrationArea";
    private static final String TOKEN = "secretissecret";

    public AdministrationArea(Context context, Spinner provinceSpinner, Spinner citySpinner, EditText idProv, EditText idCity){
        this.context = context;

        this.sm = new SessionManager(context);
        this.province = new ArrayList<String>();
        this.city = new ArrayList<String>();
        this.distric = new ArrayList<String>();
        this.village = new ArrayList<String>();

        this.provinceSpinner = provinceSpinner;
        this.citySpinner = citySpinner;

        this.idProv = idProv;
        this.idCity = idCity;
    }

    public void getProvince(){
        provinceSpinner.setEnabled(false);
        provinceSpinner.setClickable(false);
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
                    String[] provinceArray = new String[respArray.length()];
                    provinceMap = new HashMap<Integer,String>();
                    for (int i = 0; i < respArray.length(); i++) {
                        JSONObject respObject = respArray.getJSONObject(i);
                        provinceMap.put(i,respObject.getString("id"));
                        provinceArray[i] = respObject.getString("province_name");
                    }

                    ArrayAdapter<String> provinceAdapter =new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, provinceArray);
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    provinceSpinner.setAdapter(provinceAdapter);

                    provinceSpinner.setEnabled(true);
                    provinceSpinner.setClickable(true);

                    provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            idProv.setText(provinceMap.get(provinceSpinner.getSelectedItemPosition()));
                            getCity(provinceMap.get(provinceSpinner.getSelectedItemPosition()));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            provinceSpinner.setSelection(sm.getIntPreferences("province"));
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getCity(String provId){
        citySpinner.setEnabled(false);
        citySpinner.setClickable(false);
        new getCityList(provId).execute();
    }

    private class getCityList extends AsyncTask<String, String, String> {
        private String provinceId, errorMsg, responseCity;

        private getCityList(String provinceId) {
            this.provinceId = provinceId;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_CITY;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String data) {
                    responseCity = data;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "City Fetch Error : " + errorMsg);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", TOKEN);
                    return keys;
                }

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_province", provinceId);
                    return keys;
                }
            };
            try {
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return responseCity;
        }

        @Override
        protected void onPostExecute(String cit) {
            try {
                JSONArray respArray = new JSONArray(cit);
                if(respArray.length() > 0){
                    String[] cityArray = new String[respArray.length()];
                    cityMap = new HashMap<Integer,String>();
                    for (int i = 0; i < respArray.length(); i++) {
                        JSONObject respObject = respArray.getJSONObject(i);
                        cityMap.put(i,respObject.getString("id"));
                        cityArray[i] = respObject.getString("city_name");
                        //city.add(respObject.getString("city_name"));
                    }

                    ArrayAdapter<String> cityAdapter =new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, cityArray);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityAdapter);

                    citySpinner.setEnabled(true);
                    citySpinner.setClickable(true);

                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            idCity.setText(cityMap.get(citySpinner.getSelectedItemPosition()));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            citySpinner.setSelection(sm.getIntPreferences("city"));
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getDistric(Spinner districSpinner, String cityId){
        this.districSpinner = districSpinner;
        new getDistricList(cityId).execute();
    }

    private class getDistricList extends AsyncTask<String, String, String> {
        private String cityId, errorMsg, responseDistric;

        private getDistricList(String cityId) {
            this.cityId = cityId;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DISTRIC;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String data) {
                    responseDistric = data;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Distric Fetch Error : " + errorMsg);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", TOKEN);
                    return keys;
                }

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_city", cityId);
                    return keys;
                }
            };
            try {
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return responseDistric;
        }

        @Override
        protected void onPostExecute(String dist) {
            try {
                JSONArray respArray = new JSONArray(dist);
                if(respArray.length() > 0){
                    for (int i = 0; i < respArray.length(); i++) {
                        JSONObject respObject = respArray.getJSONObject(i);
                        distric.add(respObject.getString("distric_name"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            districSpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, distric));
        }
    }

    public void getVillage(Spinner villageSpinner, String districId){
        this.villageSpinner = villageSpinner;
        new getVillageList(districId).execute();
    }

    private class getVillageList extends AsyncTask<String, String, String> {
        private String districId, errorMsg, responseVillage;

        private getVillageList(String districId) {
            this.districId = districId;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_VILLAGE;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String data) {
                    responseVillage = data;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Village Fetch Error : " + errorMsg);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", TOKEN);
                    return keys;
                }

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_distric", districId);
                    return keys;
                }
            };
            try {
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return responseVillage;
        }

        @Override
        protected void onPostExecute(String vill) {
            try {
                JSONArray respArray = new JSONArray(vill);
                if(respArray.length() > 0){
                    for (int i = 0; i < respArray.length(); i++) {
                        JSONObject respObject = respArray.getJSONObject(i);
                        village.add(respObject.getString("village_name"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            villageSpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, village));
        }
    }
}
