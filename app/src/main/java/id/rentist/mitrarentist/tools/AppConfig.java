package id.rentist.mitrarentist.tools;

/**
 * Created by mdhif on 07/08/2017.
 */

public class AppConfig {
    private static String SERVER = "http://52.221.215.137:1337/";
//    private static String SERVER = "http://192.168.100.11:1337/";

    public static String URL_LOGIN = SERVER + "login/tenant/";
    public static String URL_RESET_PASSWORD = SERVER + "reset/mitra/";
    public static String URL_ACTIVATION = SERVER + "tenant/active/";
    public static String URL_DASHBOARD_DATA = SERVER + "dashboard/";

    public static  String URL_LIST_ASSET = SERVER + "list/item/";
    public static  String URL_ADD_MOBIL = SERVER + "item/mobil/";
    public static  String URL_ADD_MOTOR = SERVER + "item/motor/";
    public static  String URL_EDIT_STATUS_MOBIL = SERVER + "item/status/1/";
    public static  String URL_LIST_MOBIL = SERVER + "list/item/1/";
    public static  String URL_LIST_MOTOR = SERVER + "list/item/2/";
    public static  String URL_LIST_YACHT = SERVER + "list/item/3/";
    public static  String URL_UPDATE_ASSET = SERVER + "update/item/";
    public static  String URL_DELETE_ASSET = SERVER + "delete/item/";

    public static String URL_TRANSACTION_NEW = SERVER + "order/received/";
    public static String URL_TRANSACTION_DROP = SERVER + "transaction/ongoing/";

    public static  String URL_LIST_USER = SERVER + "tenant/child/";
    public static  String URL_DETAIL_USER = SERVER + "view/child/";
    public static  String URL_ADD_USER = SERVER + "register/tenant/child/";
    public static String URL_UPDATE_TENANT = SERVER + "update/mitra/";
    public static  String URL_DELETE_USER = SERVER + "delete/child/";

    public static  String URL_ONGOING_RENT = SERVER + "order/history/";
    public static  String URL_ADD_POLICY = SERVER + "kebijakan/";
    public static  String URL_UPDATE_POLICY = SERVER + "update/kebijakan/";
    public static  String URL_DELETE_POLICY = SERVER + "delete/kebijakan/";

    public static  String URL_LIST_VOUCHER = SERVER + "voucher/list/";
    public static  String URL_ADD_VOUCHER = SERVER + "tenant/voucher/";
    public static  String URL_UPDATE_VOUCHER = SERVER + "update/voucher/";
    public static  String URL_DELETE_VOUCHER = SERVER + "delete/voucher/";

    public static  String URL_DOMPET = SERVER + "list/request/";
    public static  String URL_LIST_TESTIMONY = SERVER + "list/testimony/";
    public static  String URL_WITHDRAWAL = SERVER + "withdrawal/request/";
    public static  String URL_HISTORY_WITHDRAWAL = SERVER + "list/withdrawal/";

    public static  String URL_LIST_DRIVER = SERVER + "list/driver/";
    public static  String URL_DETAIL_DRIVER = SERVER + "view/driver/";
    public static  String URL_ADD_DRIVER = SERVER + "tenant/driver/";
    public static  String URL_EDIT_DRIVER = SERVER + "update/driver/";
    public static  String URL_DELETE_DRIVER = SERVER + "delete/driver/";

    public static  String URL_HISTORY_TRANS = SERVER + "order/history/";
    public static  String URL_HISTORY_SALDO = SERVER + "finance/history/";
    public static  String URL_LIST_KEBIJAKAN = SERVER + "list/kebijakan/";

    public static  String URL_CRITIC_SUGGESTION = SERVER + "critics/";

}
