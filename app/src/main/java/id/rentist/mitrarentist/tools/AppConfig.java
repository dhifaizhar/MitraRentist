package id.rentist.mitrarentist.tools;

/**
 * Created by mdhif on 07/08/2017.
 */

public class AppConfig {
    private static String SERVER = "https://api.rentist.id:8443/";
//    private static String SERVER = "http://192.168.100.49:1337/";
//
    private static String ASSETS_SERVER = "https://assets.rentist.id/";
    public static String URL_IMAGE_PROFIL = ASSETS_SERVER + "images/";
    public static String URL_IMAGE_ASSETS = ASSETS_SERVER + "assets/";
    public static String URL_IMAGE_DOCUMENTS = ASSETS_SERVER + "documents/";

    public static String URL_LOGIN = SERVER + "login/tenant/";
    public static String URL_REGISTER = SERVER + "register/tenant/";
    public static String URL_VERIFY_EMAIL = SERVER + "verify/email/";
    public static String URL_VERIFY_PHONE = SERVER + "verify/phone/";
    public static String URL_RESET_PASSWORD = SERVER + "reset/mitra/";
    public static String URL_ACTIVATION = SERVER + "tenant/active/";
    public static String URL_RESEND_CODE = SERVER + "mitra/resend/";
    public static String URL_DASHBOARD_DATA = SERVER + "dashboard/";

    public static String URL_LIST_ASSET = SERVER + "list/item/";
    public static String URL_VIEW_CAR = SERVER + "view/mobil/";
    public static String URL_VIEW_MOTOR = SERVER + "view/motor/";
    public static String URL_VIEW_YACHT = SERVER + "view/yacht/";
    public static String URL_VIEW_MEDIC = SERVER + "view/medical/";
    public static String URL_VIEW_PHOTOGRAPHY = SERVER + "view/photography/";
    public static String URL_VIEW_TOYS = SERVER + "view/toys/";
    public static String URL_VIEW_ADVENTURE = SERVER + "view/watersport/";
    public static String URL_VIEW_MATERNITY = SERVER + "view/maternity/";
    public static String URL_VIEW_ELECTRONIC = SERVER + "view/electronic/";
    public static String URL_VIEW_BICYCLE = SERVER + "view/bicycle/";
    public static String URL_VIEW_OFFICE = SERVER + "view/officetools/";
    public static String URL_VIEW_EVENT = SERVER + "schedule/view/";
    public static String URL_SET_EVENT = SERVER + "schedule/asset/";
    public static String URL_DELETE_EVENT = SERVER + "schedule/delete/";

    public static String URL_PRICE_CHECK = SERVER + "price/check/";

    public static String URL_MOBIL = SERVER + "item/mobil/";
    public static String URL_MOTOR = SERVER + "item/motor/";
    public static String URL_YACHT = SERVER + "item/yacht/";
    public static String URL_MEDIC = SERVER + "item/medical/";
    public static String URL_PHOTOGRAPHY = SERVER + "item/photography/";
    public static String URL_TOYS = SERVER + "item/toys/";
    public static String URL_ADVENTURE = SERVER + "item/watersport/";
    public static String URL_MATERNITY = SERVER + "item/maternity/";
    public static String URL_ELECTRONIC = SERVER + "item/electronic/";
    public static String URL_BICYCLE = SERVER + "item/bicycle/";
    public static String URL_OFFICE = SERVER + "item/office/";
    public static String URL_FASHION = SERVER + "item/fashion/";

    public static String URL_DELETE_MOBIL =SERVER + "delete/mobil/";
    public static String URL_DELETE_MOTOR =SERVER + "delete/motor/";
    public static String URL_DELETE_YACHT = SERVER + "delete/yacht/";
    public static String URL_DELETE_MEDIC = SERVER + "delete/medical/";
    public static String URL_DELETE_PHOTOGRAPHY = SERVER + "delete/photography/";
    public static String URL_DELETE_TOYS = SERVER + "delete/toys/";
    public static String URL_DELETE_ADVENTURE = SERVER + "delete/watersport/";
    public static String URL_DELETE_MATERNITY = SERVER + "delete/maternity/";
    public static String URL_DELETE_ELECTRONIC = SERVER + "delete/electronic/";
    public static String URL_DELETE_BICYCLE = SERVER + "delete/bicycle/";
    public static String URL_DELETE_OFFICE = SERVER + "delete/office/";
    public static String URL_DELETE_FASHION = SERVER + "delete/fashion/";

    public static String URL_TRANSACTION_NEW = SERVER + "order/received/";
    public static String URL_TRANSACTION = SERVER + "order/history/";
    public static String URL_TRANSACTION_CONFIRM = SERVER + "order/confirm/";
    public static String URL_TRANSACTION_DROP = SERVER + "transaction/ongoing/";
    public static String URL_TRANSACTION_TAKE = SERVER + "transaction/completed/";

    public static String URL_LIST_USER = SERVER + "tenant/child/";
    public static String URL_DETAIL_USER = SERVER + "view/child/";
    public static String URL_ADD_USER = SERVER + "register/tenant/child/";
    public static String URL_UPDATE_TENANT = SERVER + "update/mitra/";
    public static String URL_DELETE_USER = SERVER + "delete/child/";
    public static String URL_MEMBER_PROFILE = SERVER + "detail/member/";

    public static String URL_ADD_POLICY = SERVER + "kebijakan/";
    public static String URL_UPDATE_POLICY = SERVER + "update/kebijakan/";
    public static String URL_DELETE_POLICY = SERVER + "delete/kebijakan/";

    public static String URL_LIST_VOUCHER = SERVER + "voucher/list/";
    public static String URL_ADD_VOUCHER = SERVER + "tenant/voucher/";
    public static String URL_UPDATE_VOUCHER = SERVER + "update/voucher/";
    public static String URL_DELETE_VOUCHER = SERVER + "delete/voucher/";
    public static String URL_VOUCHER_CATALOG = SERVER + "tenant/voucher/catalog/";
    public static String URL_VOUCHER_PURCHASE = SERVER + "tenant/purchase/voucher/";

    public static String URL_DOMPET = SERVER + "list/request/";
    public static String URL_WITHDRAWAL = SERVER + "withdrawal/request/";
    public static String URL_HISTORY_WITHDRAWAL = SERVER + "list/withdrawal/";
    public static String URL_HISTORY_SALDO = SERVER + "finance/history/";

    public static String URL_LIST_DRIVER = SERVER + "list/driver/";
    public static String URL_DETAIL_DRIVER = SERVER + "view/driver/";
    public static String URL_ADD_DRIVER = SERVER + "tenant/driver/";
    public static String URL_EDIT_DRIVER = SERVER + "update/driver/";
    public static String URL_DELETE_DRIVER = SERVER + "delete/driver/";
    public static String URL_ASSIGN_DRIVER = SERVER + "assign/driver/";

    public static String URL_FEATURE_NAME = SERVER + "category/feature/";
    public static String URL_LIST_FEATURE = SERVER + "list/additional/";
    public static String URL_FEATURE = SERVER + "item/feature/";
    public static String URL_DELETE_FEATURE = SERVER + "delete/feature/";
    public static String URL_ITEM_FEATURE = SERVER + "item/additional/";

    public static String URL_TESTIMONY_SUBMIT = SERVER + "testimony/submit/";
    public static String URL_LIST_KEBIJAKAN = SERVER + "list/kebijakan/";

    public static String URL_CRITIC_SUGGESTION = SERVER + "critics/";
    public static String URL_LIST_COMPLAIN = SERVER + "list/complain/";
    public static String URL_LIST_TESTIMONY = SERVER + "list/testimony/";
    public static String URL_COMPLAIN = SERVER + "complain/";

    public static String URL_PROVINCE = SERVER + "place/province/";
    public static String URL_CITY = SERVER + "place/city/";
    public static String URL_DISTRIC = SERVER + "place/distric/";
    public static String URL_VILLAGE = SERVER + "place/village/";

    public static String URL_LIST_DELIVERY_PRICE = SERVER + "list/delivery/";
    public static String URL_DELIVERY_PRICE = SERVER + "create/delivery/";

    public static String URL_SETUP_CATEGORY = SERVER + "tenant/setup/";

}
