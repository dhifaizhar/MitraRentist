package id.rentist.mitrarentist.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mdhif on 31/07/2017.
 */

public class SessionManager {
    private Context context;
    private String position;
    private Integer positionInt;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor = null;

    public SessionManager(Context context){
        this.context = context;
    }

    public void setPreferences(String key, String value) {
        editor = context.getSharedPreferences("RentistSession", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setIntPreferences(String key, Integer value) {
        editor = context.getSharedPreferences("RentistSession", Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public String getPreferences(String key) {
        prefs = context.getSharedPreferences("RentistSession", Context.MODE_PRIVATE);
        position = prefs.getString(key, "");
        return position;
    }

    public Integer getIntPreferences(String key){
        prefs = context.getSharedPreferences("RentistSession", Context.MODE_PRIVATE);
        positionInt = prefs.getInt(key, 0);
        return positionInt;
    }

    public void clearPreferences(){
        prefs = context.getSharedPreferences("RentistSession", Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

}
