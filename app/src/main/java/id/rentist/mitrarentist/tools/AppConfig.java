package id.rentist.mitrarentist.tools;

/**
 * Created by mdhif on 07/08/2017.
 */

public class AppConfig {
    private static String SERVER = "http://52.221.215.137:1337/";
    public static String URL_LOGIN = SERVER + "login/tenant/";
    public static  String URL_LIST_MOBIL = SERVER + "list/item/1/";
    public static  String URL_ADD_MOBIL = SERVER + "item/mobil/";
    public static  String URL_EDIT_STATUS_MOBIL = SERVER + "item/status/1/";
    public static  String URL_LIST_MOTOR = SERVER + "list/item/2/";
    public static  String URL_LIST_YACHT = SERVER + "list/item/3/";
    public static  String URL_LIST_USER = SERVER + "tenant/child/";
    public static  String URL_DETAIL_USER = SERVER + "view/child/";
    public static  String URL_ADD_USER = SERVER + "register/tenant/child/";
    public static  String URL_ONGOING_RENT = SERVER + "order/history/";
    public static  String URL_ADD_POLICY = SERVER + "kebijakan/";
    public static  String URL_LIST_TESTIMONY = SERVER + "list/testimony/";
    public static  String URL_WITHDRAWAL = SERVER + "withdrawal/request/";
    public static  String URL_LIST_DRIVER = SERVER + "list/driver/";
    public static  String URL_DETAIL_DRIVER = SERVER + "view/driver/";
    public static  String URL_ADD_DRIVER = SERVER + "tenant/driver/";
    public static  String URL_EDIT_DRIVER = SERVER + "update/driver/";
    public static  String URL_HISTORY_TRANS = SERVER + "order/history/";
    public static  String URL_LIST_KEBIJAKAN = SERVER + "list/kebijakan/";
}
