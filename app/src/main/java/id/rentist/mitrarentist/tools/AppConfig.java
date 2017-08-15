package id.rentist.mitrarentist.tools;

/**
 * Created by mdhif on 07/08/2017.
 */

public class AppConfig {
    private static String SERVER = "http://52.221.215.137:1337/";
    public static String URL_LOGIN = SERVER + "login/tenant/";
    public static  String URL_LIST_MOBIL = SERVER + "mobil/";
    public static  String URL_ADD_MOBIL = SERVER + "item/mobil/";
    public static  String URL_LIST_USER = SERVER + "tenant/child/";
    public static  String URL_ONGOING_RENT = SERVER + "order/history/";
}
