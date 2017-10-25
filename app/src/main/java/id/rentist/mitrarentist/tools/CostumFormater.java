package id.rentist.mitrarentist.tools;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.rentist.mitrarentist.R;

/**
 * Created by Nugroho Tri Pambud on 10/23/2017.
 */

public class CostumFormater {

    public static String PriceFormat(int value){
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        return formatter.format(value) + " IDR";
    }

    public static String PriceStringFormat(String value){
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        return formatter.format(Integer.parseInt(value)) + " IDR";
    }

    public static List PriceStringToArray(String string){
        Map<String, Object> prop;
        List<Object> arr = new ArrayList<>();

        String pattern = "\\s*\"range_name\"\\s*:\"\\s*([^\"]*)[^:]*:\"(\\d*-\\d*-\\d*)[^\"]*[^:]*:\"(\\d*-\\d*-\\d*)[^\"]*[^:]*:(\\d*)";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(string);

        while (m.find()) {
            prop = new HashMap<>();
            prop.put("range_name", m.group(1));
            prop.put("start_date", m.group(2));
            prop.put("end_date", m.group(3));
            prop.put("price", m.group(4));
            arr.add(prop);
        }

        return  arr;
    }

    public static Integer PriceMinFee(Integer price, Integer fee){
        return price - (price/100*fee);
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
