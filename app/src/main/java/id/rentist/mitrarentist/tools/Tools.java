package id.rentist.mitrarentist.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import id.rentist.mitrarentist.FormAdventureAsetActivity;
import id.rentist.mitrarentist.FormBicycleAsetActivity;
import id.rentist.mitrarentist.FormCarAsetActivity;
import id.rentist.mitrarentist.FormElectronicAsetActivity;
import id.rentist.mitrarentist.FormFashionAsetActivity;
import id.rentist.mitrarentist.FormMaternityAsetActivity;
import id.rentist.mitrarentist.FormMedicAsetActivity;
import id.rentist.mitrarentist.FormMotorcycleAsetActivity;
import id.rentist.mitrarentist.FormOfficeAsetActivity;
import id.rentist.mitrarentist.FormPhotographyAsetActivity;
import id.rentist.mitrarentist.FormToysAsetActivity;
import id.rentist.mitrarentist.FormYachtAsetActivity;
import id.rentist.mitrarentist.R;

public class Tools {
    private static float getAPIVerison() {

        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {
            Log.e("", "erro ao recuperar a versão da API" + e.getMessage());
        }

        return f.floatValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void systemBarLolipop(Activity act){
        if (getAPIVerison() >= 5.0) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /**
     *  Making notification bar transparent
     * */
    public static void changeStatusBarColor(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0xf009ddd);
        }
    }

    public static int getGridSpanCount(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth  = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.recycler_item_size);
        return Math.round(screenWidth / cellWidth);
    }
    public static int getGridExplorerCount(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth  = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.explorer_item_size);
        return Math.round(screenWidth / cellWidth);
    }

    public static int dpToPx(int dp, Context ctx ) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(int px, Context ctx) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static String getIdCategoryURL(String category ){
        String URL = "";
        switch (category) {
            case "1": URL = AppConfig.URL_MOBIL; break;
            case "2": URL = AppConfig.URL_MOTOR; break;
            case "3": URL = AppConfig.URL_YACHT; break;
            case "4": URL = AppConfig.URL_MEDIC; break;
            case "5": URL = AppConfig.URL_PHOTOGRAPHY; break;
            case "6": URL = AppConfig.URL_TOYS; break;
            case "7": URL = AppConfig.URL_ADVENTURE; break;
            case "8": URL = AppConfig.URL_MATERNITY; break;
            case "9": URL = AppConfig.URL_ELECTRONIC; break;
            case "10": URL = AppConfig.URL_BICYCLE; break;
            case "11": URL = AppConfig.URL_OFFICE; break;
            case "12": URL = AppConfig.URL_OFFICE; break;
        }
        return URL;
    }

    public static Class getCategoryForm(String category ){
        Class activity = FormCarAsetActivity.class;;
        switch (category) {
            case "1": activity = FormCarAsetActivity.class; break;
            case "2": activity = FormMotorcycleAsetActivity.class; break;
            case "3": activity = FormYachtAsetActivity.class;  break;
            case "4": activity = FormMedicAsetActivity.class;  break;
            case "5": activity = FormPhotographyAsetActivity.class;  break;
            case "6": activity = FormToysAsetActivity.class;  break;
            case "7": activity = FormAdventureAsetActivity.class;  break;
            case "8": activity = FormMaternityAsetActivity.class;  break;
            case "9": activity = FormElectronicAsetActivity.class;  break;
            case "10": activity = FormBicycleAsetActivity.class;  break;
            case "11": activity = FormOfficeAsetActivity.class;  break;
            case "12": activity = FormFashionAsetActivity.class;  break;
        }
        return activity;
    }

    public static void setSpinnerValue(String value, Spinner spinner, int entries, Context c){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(c, entries, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.select_dialog_item);
        spinner.setAdapter(adapter);
        if (!value.equals(null)) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }
}
