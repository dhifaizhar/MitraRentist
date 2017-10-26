package id.rentist.mitrarentist.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

/**
 * Created by Nugroho Tri Pambud on 8/30/2017.
 */

public class DriverDialogFragment extends DialogFragment {
    Context context;
    private SessionManager sm;

    private static final String TAG = "DriverFragment";
    private static final String TOKEN = "secretissecret";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pilih Driver")
                    .setItems(R.array.driver_entries, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                    });

            driverGetList();
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void driverGetList() {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LIST_DRIVER + sm.getPreferences("id_tenant"), new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("error", response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONArray responseJson = jObj.getJSONArray("driver");
                            driverParseData(responseJson);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Driver Fetch Error : " +  error.toString());
                Toast.makeText(context, "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
        queue.add(strReq);
    }

    private void driverParseData(JSONArray responseJson) {

    }
}

