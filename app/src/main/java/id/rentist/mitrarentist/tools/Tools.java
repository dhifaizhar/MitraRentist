package id.rentist.mitrarentist.tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.enums.EPickType;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String dateFormat(String date){
        SimpleDateFormat currentFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(currentFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        return dateFormat.format(c.getTime());
    }

    public static String datePriceAdvanceFormat(String date){
        SimpleDateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(currentFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return dateFormat.format(c.getTime());
    }

    public static String dateCreatedFormat(String date){
        SimpleDateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(currentFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, dd MMM yyyy");

        return dateFormat.format(c.getTime());
    }

    // Date +1. exp: 2017-10-20 --> 21 Oct 2017
    public static String dateFineFormat(String date){
        SimpleDateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(currentFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        return dateFormat.format(c.getTime());
    }

    // Hour +7. exp: 2017-10-20T12:30:30 --> 19:30:30, 20 Oct 2017
    public static String dateHourFormat(String date){
        String subDate = date.substring(0, 19);
        SimpleDateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(currentFormat.parse(subDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.HOUR_OF_DAY, 7);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, dd MMM yyyy");

        return dateFormat.format(c.getTime());
    }

    public static String getLongitude(String LatLong){
        String pattern = "([^(,a-z]*),([^a-z,)]*)";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(LatLong);
        m.find();
        return  m.group(2);
    }

    public static String getLatitude(String LatLong){
        String pattern = "([^(,a-z]*),([^a-z,)]*)";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(LatLong);
        m.find();
        return  m.group(1);
    }

    // IMAGE : get string for upload
    public static String getStringImage(Bitmap bmp){
        if(bmp != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Log.e("BITMAPWidthHeight", bmp.getHeight() + "|" + bmp.getWidth());
            Log.e("BITMAPSize", String.valueOf(bmp.getRowBytes() * bmp.getHeight()));
            Bitmap resized = null;
            if (bmp.getHeight() > 1024){
                if (bmp.getHeight() > 2500){
                    if(bmp.getHeight() > 4000) {
                        resized = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.25), (int) (bmp.getHeight() * 0.25), true);
                    }else{
                        resized = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.4), (int) (bmp.getHeight() * 0.4), true);
                    }
                }
            }else {
                resized = Bitmap.createScaledBitmap(bmp, 1024, 1024, true);
            }
            
            int size = resized.getRowBytes() * resized.getHeight();
            Log.e("BITMAP2WidthHeight", resized.getHeight() + "|" + resized.getWidth());
            Log.e("BITMAPReSize", String.valueOf(size));

            if (size > 1000000){
                if (size > 10000000) {
                    if (size > 30000000) {
                        Log.e("COMPRESSBig", "YES");
                        resized.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                    }else{
                        Log.e("COMPRESSUnderBig", "YES");
                        resized.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    }
                }else{
                    Log.e("COMPRESSSmall", "YES");
                    resized.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                }
            }else{
                Log.e("COMPRESS", "NO");
                resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }else{
            return null;
        }
    }

    public static String getStringImageView(Bitmap bmp, ImageView imageview){
        if(bmp != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Log.e("BITMAPWidthHeight", bmp.getHeight() + "|" + bmp.getWidth());
            Log.e("BITMAPSize", String.valueOf(bmp.getRowBytes() * bmp.getHeight()));
            Bitmap resized;
            if (bmp.getHeight() > 1024){
                if (bmp.getHeight() > 2500) {
                    if (bmp.getHeight() > 4000) {
                        resized = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.25), (int) (bmp.getHeight() * 0.25), true);
                    } else {
                        resized = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.4), (int) (bmp.getHeight() * 0.4), true);
                    }
                }else{
                    resized = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.7), (int) (bmp.getHeight() * 0.7), true);
                }
            }else{
                resized = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
            }

            int size = resized.getRowBytes() * resized.getHeight();
            Log.e("BITMAP2WidthHeight", resized.getHeight() + "|" + resized.getWidth());
            Log.e("BITMAPReSize", String.valueOf(size));

            if (size > 1000000){
                if (size > 5000000) {
                    if (size > 10000000) {
                        if (size > 30000000) {
                            Log.e("COMPRESSBig", "YES");
                            resized.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                        } else {
                            Log.e("COMPRESSUnderBig", "YES");
                            resized.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        }
                    } else {
                        Log.e("COMPRESSSmallUP", "YES");
                        resized.compress(Bitmap.CompressFormat.JPEG, 35, baos);
                    }
                }else {
                    Log.e("COMPRESSSmall", "YES");
                    resized.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                }
            }else{
                Log.e("COMPRESS", "NO");
                resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

            imageview.setImageBitmap(resized);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }else{
            return null;
        }
    }

    public static void deleteImage(final ImageButton button, final ImageView image, Context context){
        AlertDialog.Builder showAlert = new AlertDialog.Builder(context);
        showAlert.setMessage("Hapus gambar ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                image.setImageResource(R.drawable.add_picture);
                button.setVisibility(View.GONE);
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        AlertDialog alertDialog = showAlert.create();
        alertDialog.show();
    }

    public static PickSetup imagePickerSetup(){
        PickSetup setup = new PickSetup()
                .setTitle("Pilih Gambar")
                .setProgressText("Loading ...")
                .setCancelText("Batal")
                .setFlip(true)
                .setMaxSize(500)
                .setPickTypes( EPickType.CAMERA, EPickType.GALLERY)
                .setIconGravity(Gravity.LEFT)
                .setButtonOrientation(LinearLayoutCompat.HORIZONTAL)
                .setSystemDialog(false);
        return setup;
    }
}
