package id.rentist.mitrarentist.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Nugroho Tri Pambud on 10/23/2017.
 */

public class PricingTools {

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

//        String pattern = "\\s*\"range_name\"\\s*:\"\\s*([^\"]*)[^:]*:\"(\\d*-\\d*-\\d*)[^\"]*[^:]*:\"(\\d*-\\d*-\\d*)[^\"]*[^:]*:(\\d*)";
//
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(string);
//
//        while (m.find()) {
//            prop = new HashMap<>();
//            prop.put("range_name", m.group(1));
//            prop.put("start_date", m.group(2));
//            prop.put("end_date", m.group(3));
//            prop.put("price", m.group(4));
//            arr.add(prop);
//        }

        try {
            JSONArray nPriceArray = new JSONArray(string);
            if (nPriceArray.length() > 0) {
                for (int i = 0; i < nPriceArray.length(); i++) {
                    JSONObject priceObject = nPriceArray.getJSONObject(i);
                    JSONObject nPrice = new JSONObject();
                    nPrice.put("range_name", priceObject.getString("range_name"));
                    nPrice.put("start_date", priceObject.getString("start_date"));
                    nPrice.put("end_date", priceObject.getString("end_date"));
                    nPrice.put("price", priceObject.getString("price"));

                    arr.add(nPrice);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  arr;
    }

    public static List PriceStringToArrayCar(String string){
        Map<String, Object> prop;
        List<Object> arr = new ArrayList<>();

//        String pattern = "\\s*\"range_name\"\\s*:\"\\s*([^\"]*)[^:]*:\"(\\d*-\\d*-\\d*)[^\"]*[^:]*:\"(\\d*-\\d*-\\d*)[^\"]*[^:]*:(\\d*)[^:]*:[^:]*:(\\d*)";
//
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(string);
//
//        while (m.find()) {
//            prop = new HashMap<>();
//            prop.put("range_name", m.group(1));
//            prop.put("start_date", m.group(2));
//            prop.put("end_date", m.group(3));
//            prop.put("price", m.group(4));
//            prop.put("price_with_driver", m.group(5));
//            arr.add(prop);
//        }

        try {
            JSONArray nPriceArray = new JSONArray(string);
            if (nPriceArray.length() > 0) {
                for (int i = 0; i < nPriceArray.length(); i++) {
                    JSONObject priceObject = nPriceArray.getJSONObject(i);
                    JSONObject nPrice = new JSONObject();
                    nPrice.put("range_name", priceObject.getString("range_name"));
                    nPrice.put("start_date", priceObject.getString("start_date"));
                    nPrice.put("end_date", priceObject.getString("end_date"));
                    nPrice.put("price", priceObject.getString("price"));
                    nPrice.put("price_with_driver", priceObject.getString("price_with_driver"));

                    arr.add(nPrice);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return  arr;
    }

    public static Integer PriceMinFee(Integer price, Integer fee){
        return price - (price/100*fee);
    }


}
