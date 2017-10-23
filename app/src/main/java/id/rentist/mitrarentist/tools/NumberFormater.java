package id.rentist.mitrarentist.tools;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Nugroho Tri Pambud on 10/23/2017.
 */

public class NumberFormater {

    public static String PriceFormat(int value){
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        return formatter.format(value) + " IDR";
    }

    public static String PriceStringFormat(String value){
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        return formatter.format(Integer.parseInt(value)) + " IDR";
    }
}
