package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.NumberTextWatcherForThousand;
import id.rentist.mitrarentist.tools.PricingTools;
import id.rentist.mitrarentist.tools.SessionManager;
import id.rentist.mitrarentist.tools.Tools;

public class FormMotorcycleAsetActivity extends AppCompatActivity {
    private Activity currentActivity = FormMotorcycleAsetActivity.this;
    private int subCategotyArray = R.array.motorcycle_subcategory_entries;
    private int layout = R.layout.activity_form_motorcycle_aset;
    private int cat_num = 2;
    private String URL = AppConfig.URL_MOTOR;
    private String fee_cat = "fee_motor";

    String[] asset_category;
    private AsyncTask mAddAssetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    Intent iFormAsset;

    TextView aName, aType, aPlat, aPlatStart, aPlatEnd, aMinDayRent, aAddress, aNoRangka, aNoMesin;//, aYear;
    Integer idAsset;
    String aLatitude, aLongitude, aRentPackage, tenant, category,
            aDeliveryMethod, aRentReq;
    CheckBox aAssurace, aDelivery, aPickup;
    RadioGroup aTransmisionGroup, aRentReqGroup;
    RadioButton aTransmisionButton,  aBasic, aVerified, aSmartCon;;
    Button btnImgUpload;
    Spinner subcategory, aMerk, aColor, aFuel, aEngCap, aYear;

    private int PICK_LOCATION_REQUEST = 10;
    private static final String TAG = "FormAssetActivity";
    private static final String TOKEN = "secretissecret";

    Button bSaveButton;

    //Delivery & Deposit
    String aIdDelivery = "", aDepositStatus;
    RadioGroup aDeliveryMethodGroup, aDepositGroup;
    RadioButton rPickup, rDelivery, aLogistic, rbDepositYes, rbDepositNo;
    LinearLayout rDeliveryPrice, rDeposit, rDepositValue;
    TextView detDeliveryPrice;
    EditText aDepositValue;
    private int SET_DELIVERY_PRICE = 15;

    //Image Initial
    String[] imagesArray = {AppConfig.URL_IMAGE_ASSETS + "default.png"};
    int img = 0;
    ImageView aImgSTNK, aMainImage, aSecondImage, aThirdImage, aFourthImage, aFifthImage;
    ImageButton btnCamSTNK, btnFileSTNK, delSecondImage, delThirdImage, delFourthImage, delFifthImage;
    RelativeLayout conSecondImage, conThirdImage, conFourthImage, conFifthImage;
    String isiimage, imgStringMain = "", imgStringSecond = "", imgStringThird = "", imgStringFourth = "", imgStringFifth = "",
            imgStringSTNK = "";
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_IMAGE_SECOND_REQUEST = 2;
    private int PICK_IMAGE_THIRD_REQUEST = 3;
    private int PICK_IMAGE_FOURTH_REQUEST = 4;
    private int PICK_IMAGE_FIFTH_REQUEST = 5;
    private int PICK_IMAGE_STNK_REQUEST = 6;
    private int CAMERA_REQUEST = 7;

    // Price Initial
    ArrayList<String> pricingArray = new ArrayList<String>();
    String aFeeMsg, priceStatus="";
    private int fee = 0, ap = 0;
    EditText aBasicPrice, aAdvancePrice, aAdvancePrice2, aAdvancePrice3, aAdvancePrice4;
    TextView  btnAdvancePrice, aBasicPriceDisp, aAdvancePriceDisp, aAdvancePriceDisp2, aAdvancePriceDisp3, aAdvancePriceDisp4,
            aStartDate, aStartDate2, aStartDate3, aStartDate4,
            aEndDate, aEndDate2, aEndDate3, aEndDate4,
            aDispText, aDispText2, aDispText3, aDispText4, aDispText5;
    Spinner aRangName, aRangName2, aRangName3, aRangName4;
    LinearLayout conAdvancePrice, conAdvancePrice2, conAdvancePrice3, conAdvancePrice4;
    private int PICK_DATE_REQUEST = 11;
    private int PICK_DATE_REQUEST2 = 12;
    private int PICK_DATE_REQUEST3 = 13;
    private int PICK_DATE_REQUEST4 = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        asset_category = getApplicationContext().getResources().getStringArray(R.array.asset_category_entries);
        setTitle("Aset " + asset_category[cat_num-1]);

        iFormAsset = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        contentcontrol();
    }

    private void contentcontrol() {
        aTransmisionGroup = (RadioGroup) findViewById(R.id.transmission_group);
        subcategory = (Spinner) findViewById(R.id.as_subcat_spinner);
        aMerk = (Spinner) findViewById(R.id.as_merk);
        aName = (TextView) findViewById(R.id.as_name);
        aType = (TextView) findViewById(R.id.as_type);
        aPlat = (TextView) findViewById(R.id.as_plat);
        aYear = (Spinner) findViewById(R.id.as_year);
        aColor = (Spinner) findViewById(R.id.as_colour);
        aEngCap = (Spinner) findViewById(R.id.as_engcap);
        aPlat = (TextView) findViewById(R.id.as_plat);
        aFuel = (Spinner) findViewById(R.id.as_fuel);
        aPlatStart = (TextView) findViewById(R.id.as_plat_start);
        aPlatEnd = (TextView) findViewById(R.id.as_plat_end);
        aMinDayRent = (TextView) findViewById(R.id.as_min_rent_day);
        aAssurace = (CheckBox) findViewById(R.id.as_ck_assurance);
        aStartDate = (TextView) findViewById(R.id.as_start_date);
        aDelivery = (CheckBox) findViewById(R.id.as_ck_delivery);
        aPickup = (CheckBox) findViewById(R.id.as_ck_pickup);
        aAddress = (TextView) findViewById(R.id.as_address);
        aRentReqGroup = (RadioGroup) findViewById(R.id.rent_req_group);
        aBasic = (RadioButton) findViewById(R.id.r_basic);
        aVerified = (RadioButton) findViewById(R.id.r_verified);
        aSmartCon = (RadioButton) findViewById(R.id.r_smart_con);
        aNoRangka = (TextView) findViewById(R.id.as_no_rangka);
        aNoMesin = (TextView) findViewById(R.id.as_no_mesin);

        conSecondImage = (RelativeLayout) findViewById(R.id.con_second_image);
        conThirdImage = (RelativeLayout) findViewById(R.id.con_third_image);
        conFourthImage = (RelativeLayout) findViewById(R.id.con_fourth_image);
        conFifthImage = (RelativeLayout) findViewById(R.id.con_fifth_image);
        aImgSTNK = (ImageView) findViewById(R.id.stnk_image);
        aMainImage = (ImageView) findViewById(R.id.main_image);
        aSecondImage = (ImageView) findViewById(R.id.second_image);
        aThirdImage = (ImageView) findViewById(R.id.third_image);
        aFourthImage = (ImageView) findViewById(R.id.fourth_image);
        aFifthImage = (ImageView) findViewById(R.id.fifth_image);

        delSecondImage = (ImageButton) findViewById(R.id.delete_second_image);
        delThirdImage = (ImageButton) findViewById(R.id.delete_third_image);
        delFourthImage = (ImageButton) findViewById(R.id.delete_fourth_image);
        delFifthImage = (ImageButton) findViewById(R.id.delete_fifth_image);
        btnFileSTNK = (ImageButton) findViewById(R.id.btn_photo);
        btnCamSTNK = (ImageButton) findViewById(R.id.btn_camera);
        btnCamSTNK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        //Get Location
        aAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                aAddress.setClickable(false);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(FormMotorcycleAsetActivity.this);
                    startActivityForResult(intent, PICK_LOCATION_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        aMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser("main");
            }
        });
        aSecondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser("second");
            }
        });
        aThirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser("third");
            }
        });
        aFourthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser("fourth");
            }
        });
        aFifthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser("fifth");
            }
        });

        btnFileSTNK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser("STNK");
            }
        });

        delSecondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.deleteImage(delSecondImage, aSecondImage, currentActivity);
                imgStringSecond = iFormAsset.getStringExtra("action").equals("update") ? "default.png" : "";
            }
        });

        delThirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.deleteImage(delThirdImage, aThirdImage, currentActivity);
                imgStringThird = iFormAsset.getStringExtra("action").equals("update") ? "default.png" : "";
            }
        });

        delFourthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.deleteImage(delFourthImage, aFourthImage, currentActivity);
                imgStringFourth = iFormAsset.getStringExtra("action").equals("update") ? "default.png" : "";
            }
        });

        delFifthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.deleteImage(delFifthImage, aFifthImage, currentActivity);
                imgStringFifth = iFormAsset.getStringExtra("action").equals("update") ? "default.png" : "";
            }
        });

        //Deliver & Deposit
        aDeliveryMethodGroup = (RadioGroup) findViewById(R.id.delivery_group);
        rPickup = (RadioButton) findViewById(R.id.r_pickup);
        rDelivery = (RadioButton) findViewById(R.id.r_deliver);
        rDeliveryPrice = (LinearLayout) findViewById(R.id.delivery_detail);
        detDeliveryPrice = (TextView) findViewById(R.id.delivery_price);

        aDepositGroup = (RadioGroup) findViewById(R.id.deposit_group);
        rbDepositYes = (RadioButton) findViewById(R.id.r_deposit);
        rbDepositNo = (RadioButton) findViewById(R.id.r_no_deposit);
        rDepositValue = (LinearLayout) findViewById(R.id.row_deposit_value);
        aDepositValue = (EditText) findViewById(R.id.as_nominal_deposit);

        aDeliveryMethodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.r_deliver) {
                    rDeliveryPrice.setVisibility(View.VISIBLE);
                } else {
                    rDeliveryPrice.setVisibility(View.GONE);
                }
            }
        });

        aDepositGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.r_deposit) {
                    aDepositValue.addTextChangedListener(new NumberTextWatcherForThousand(aDepositValue));
                    rDepositValue.setVisibility(View.VISIBLE);
                } else {
                    rDepositValue.setVisibility(View.GONE);
                }
            }
        });

        detDeliveryPrice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent iDeliveryPrce = new Intent(currentActivity, ListDeliveryPriceActivity.class);
                iDeliveryPrce.putExtra("id_asset_category", String.valueOf(cat_num));
                startActivityForResult(iDeliveryPrce, SET_DELIVERY_PRICE);
            }
        });


        //PRICING
        aBasicPriceDisp = (TextView) findViewById(R.id.as_price_basic_disp);
        aBasicPrice = (EditText) findViewById(R.id.as_price_basic);
        btnAdvancePrice = (TextView) findViewById(R.id.btn_price_advance);
        conAdvancePrice = (LinearLayout) findViewById(R.id.con_advance_price);
        conAdvancePrice2 = (LinearLayout) findViewById(R.id.con_advance_price2);
        conAdvancePrice3 = (LinearLayout) findViewById(R.id.con_advance_price3);
        conAdvancePrice4 = (LinearLayout) findViewById(R.id.con_advance_price4);
        aAdvancePrice = (EditText) findViewById(R.id.as_price_advance);
        aAdvancePriceDisp = (TextView) findViewById(R.id.as_price_advance_disp);
        aStartDate = (TextView) findViewById(R.id.as_start_date);
        aEndDate = (TextView) findViewById(R.id.as_end_date);
        aAdvancePrice2 = (EditText) findViewById(R.id.as_price_advance2);
        aAdvancePriceDisp2 = (TextView) findViewById(R.id.as_price_advance_disp2);
        aStartDate2 = (TextView) findViewById(R.id.as_start_date2);
        aEndDate2 = (TextView) findViewById(R.id.as_end_date2);
        aAdvancePrice3 = (EditText) findViewById(R.id.as_price_advance3);
        aAdvancePriceDisp3 = (TextView) findViewById(R.id.as_price_advance_disp3);
        aStartDate3 = (TextView) findViewById(R.id.as_start_date3);
        aEndDate3 = (TextView) findViewById(R.id.as_end_date3);
        aAdvancePrice4 = (EditText) findViewById(R.id.as_price_advance4);
        aAdvancePriceDisp4 = (TextView) findViewById(R.id.as_price_advance_disp4);
        aStartDate4 = (TextView) findViewById(R.id.as_start_date4);
        aEndDate4 = (TextView) findViewById(R.id.as_end_date4);
        aRangName = (Spinner) findViewById(R.id.as_range_name);
        aRangName2 = (Spinner) findViewById(R.id.as_range_name2);
        aRangName3 = (Spinner) findViewById(R.id.as_range_name3);
        aRangName4 = (Spinner) findViewById(R.id.as_range_name4);
        aDispText = (TextView) findViewById(R.id.disptext);
        aDispText2 = (TextView) findViewById(R.id.disptext2);
        aDispText3 = (TextView) findViewById(R.id.disptext3);
        aDispText4 = (TextView) findViewById(R.id.disptext4);
        aDispText5 = (TextView) findViewById(R.id.disptext5);

        fee = Integer.parseInt(sm.getPreferences(fee_cat));

        aFeeMsg = "Harga yang akan anda terima (-" + fee + "%):";
        aDispText.setText(aFeeMsg);
        aDispText2.setText(aFeeMsg);
        aDispText3.setText(aFeeMsg);
        aDispText4.setText(aFeeMsg);
        aDispText5.setText(aFeeMsg);

        btnAdvancePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (ap) {
                    case 0:
                        conAdvancePrice.setVisibility(View.VISIBLE);
                        ap++;
                        break;
                    case 1:
                        conAdvancePrice2.setVisibility(View.VISIBLE);
                        ap++;
                        break;
                    case 2:
                        conAdvancePrice3.setVisibility(View.VISIBLE);
                        ap++;
                        break;
                    case 3:
                        conAdvancePrice4.setVisibility(View.VISIBLE);
                        btnAdvancePrice.setVisibility(View.GONE);
                        ap++;
                        break;
                }
                Log.e(TAG, "AP Positiom: " + ap);
            }
        });

        aBasicPrice.addTextChangedListener(new NumberTextWatcherForThousand(aBasicPrice));
        aBasicPrice.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable arg0) {
                if(!aBasicPrice.getText().toString().isEmpty()){
                    Integer price = Integer.parseInt(NumberTextWatcherForThousand.trimCommaOfString(aBasicPrice.getText().toString()));
                    aBasicPriceDisp.setText(PricingTools.PriceFormat(PricingTools.PriceMinFee(price, fee)));
                } else {
                    aBasicPriceDisp.setText("0 IDR");
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        aAdvancePrice.addTextChangedListener(new NumberTextWatcherForThousand(aAdvancePrice));
        aAdvancePrice.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable arg0) {
                if(!aAdvancePrice.getText().toString().isEmpty()){
                    Integer price = Integer.parseInt(NumberTextWatcherForThousand.trimCommaOfString(aAdvancePrice.getText().toString()));
                    aAdvancePriceDisp.setText(PricingTools.PriceFormat(PricingTools.PriceMinFee(price, fee)));
                } else {
                    aAdvancePriceDisp.setText("0 IDR");
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        aAdvancePrice2.addTextChangedListener(new NumberTextWatcherForThousand(aAdvancePrice2));
        aAdvancePrice2.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable arg0) {
                if(!aAdvancePrice2.getText().toString().isEmpty()){
                    Integer price = Integer.parseInt(NumberTextWatcherForThousand.trimCommaOfString(aAdvancePrice2.getText().toString()));
                    aAdvancePriceDisp2.setText(PricingTools.PriceFormat(PricingTools.PriceMinFee(price, fee)));
                } else {
                    aAdvancePriceDisp2.setText("0 IDR");
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        aAdvancePrice3.addTextChangedListener(new NumberTextWatcherForThousand(aAdvancePrice3));
        aAdvancePrice3.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable arg0) {
                if(!aAdvancePrice3.getText().toString().isEmpty()){
                    Integer price = Integer.parseInt(NumberTextWatcherForThousand.trimCommaOfString(aAdvancePrice3.getText().toString()));
                    aAdvancePriceDisp3.setText(PricingTools.PriceFormat(PricingTools.PriceMinFee(price, fee)));
                } else {
                    aAdvancePriceDisp3.setText("0 IDR");
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        aAdvancePrice4.addTextChangedListener(new NumberTextWatcherForThousand(aAdvancePrice4));
        aAdvancePrice4.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable arg0) {
                if(!aAdvancePrice4.getText().toString().isEmpty()){
                    Integer price = Integer.parseInt(NumberTextWatcherForThousand.trimCommaOfString(aAdvancePrice4.getText().toString()));
                    aAdvancePriceDisp4.setText(PricingTools.PriceFormat(PricingTools.PriceMinFee(price, fee)));
                } else {
                    aAdvancePriceDisp4.setText("0 IDR");
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST);
            }
        });

        aStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST);
            }
        });

        aEndDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST2);
            }
        });

        aStartDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST2);
            }
        });

        aEndDate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST3);
            }
        });

        aStartDate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST3);
            }
        });

        aEndDate4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST4);
            }
        });

        aStartDate4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST4);
            }
        });

        //set value
        if(iFormAsset.getStringExtra("action").equals("update")){
            getDataUpdate();
        }

        bSaveButton = (Button) findViewById(R.id.btn_save);
        bSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.setMessage("loading ...");
                showProgress(true);

                int transmissionId = aTransmisionGroup.getCheckedRadioButtonId();
                aTransmisionButton = (RadioButton) findViewById(transmissionId);
                tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
                category = iFormAsset.getStringExtra("cat");
                idAsset = iFormAsset.getIntExtra("id_asset",0);

                int rentReqId = aRentReqGroup.getCheckedRadioButtonId();
                if (rentReqId == aBasic.getId()) aRentReq = getResources().getString(R.string.member_badge_basic);
                else if (rentReqId == aVerified.getId()) aRentReq = getResources().getString(R.string.member_badge_verified);
                else aRentReq = getResources().getString(R.string.member_badge_smart_con);

                Boolean delivMethodValid = true;
                int deliveryMethodId = aDeliveryMethodGroup.getCheckedRadioButtonId();
                if (deliveryMethodId == rPickup.getId()) aDeliveryMethod = getResources().getString(R.string.delivery_pickup);
                else if (deliveryMethodId == rDelivery.getId()) {
                    aDeliveryMethod = getResources().getString(R.string.delivery_deliver);
                    if(aIdDelivery.isEmpty()){
                        delivMethodValid = false;
                    }
                }

                Boolean depositValid = true;
                int depositId = aDepositGroup.getCheckedRadioButtonId();
                if (depositId == rbDepositNo.getId()) {
                    aDepositStatus = "false";
                } else {
                    aDepositStatus = "true";
                    if(aDepositValue.getText().toString().isEmpty()){
                        depositValid = false;
                    }
                }

                if (aBasicPrice.getText().toString().isEmpty() ||
                        aType.toString().isEmpty() ||
                        aPlat.toString().isEmpty() || aPlatStart.toString().isEmpty() || aPlatEnd.toString().isEmpty() ||
                        aName.getText().toString().isEmpty() ||
                        aType.getText().toString().isEmpty() ||
                        aMinDayRent.getText().toString().isEmpty() ||
                        aNoRangka.getText().toString().isEmpty() ||
                        aNoMesin.getText().toString().isEmpty() ||
                        delivMethodValid.equals(false) ||
                        depositValid.equals(false)){
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), R.string.error_field_not_complete,Toast.LENGTH_LONG).show();
                } else{
                    pricingArray.clear();
                    getPrice();
                    postPriceCheck(pricingArray.toString());

                }

                String numplatPattern = "\\d";
                View focusView;
                Pattern r = Pattern.compile(numplatPattern);
                Matcher m = r.matcher(aPlatStart.getText().toString());
                Matcher n = r.matcher(aPlatEnd.getText().toString());

                Boolean startPlat = !m.find();
                Boolean endPlat = !n.find();

                if (aBasicPrice.getText().toString().isEmpty() ||
                        aBasicPrice.getText().toString().isEmpty() ||
                        aType.getText().toString().isEmpty() ||
                        aPlat.getText().toString().isEmpty() || aPlatStart.getText().toString().isEmpty() ||
                        aName.getText().toString().isEmpty() ||
                        aType.getText().toString().isEmpty() ||
                        aMinDayRent.getText().toString().isEmpty() ||
                        aNoRangka.getText().toString().isEmpty() ||
                        aNoMesin.getText().toString().isEmpty() ||
                        delivMethodValid.equals(false) ||
                        depositValid.equals(false)) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), R.string.error_field_not_complete, Toast.LENGTH_LONG).show();
                } else {
                    if (startPlat) {
                        if (endPlat) {
                            pricingArray.clear();
                            getPrice();
                            postPriceCheck(pricingArray.toString());
                        } else {
                            showProgress(false);
                            aPlatEnd.setError("Tidak boleh memasukan angka untuk Kode Plat Nomer");
                            focusView = aPlatEnd;
                            focusView.requestFocus();
                        }
                    } else {
                        showProgress(false);
                        aPlatStart.setError("Tidak boleh memasukan angka untuk Kode Plat Nomer");
                        focusView = aPlatStart;
                        focusView.requestFocus();
                    }
                }
            }

        });
    }

    private void getDataUpdate(){
        btnCamSTNK.setVisibility(View.GONE);
        btnFileSTNK.setVisibility(View.GONE);

        aName.setText(iFormAsset.getStringExtra("name"));
        aType.setText(iFormAsset.getStringExtra("type"));
        aMinDayRent.setText(iFormAsset.getStringExtra("min_rent_day"));
        aAddress.setText(iFormAsset.getStringExtra("address"));
        aLatitude = iFormAsset.getStringExtra("latitude");
        aLongitude = iFormAsset.getStringExtra("longitude");
        aNoRangka.setText(iFormAsset.getStringExtra("no_rangka"));
        aNoMesin.setText(iFormAsset.getStringExtra("no_mesin"));

        String plat = iFormAsset.getStringExtra("plat");
        String a,b,c;
        a = plat.substring(0, plat.indexOf(" ", 1));
        if (a.length()>1){
            b = plat.substring(plat.indexOf(" ")+1, plat.indexOf(" ", 3));
            c = plat.substring(plat.indexOf(" ", 3));
        }else{
            b = plat.substring(plat.indexOf(" "), plat.indexOf(" ", 2));
            c = plat.substring(plat.indexOf(" ", 2));
        }

        aPlatStart.setText(a.trim());
        aPlat.setText(b.trim());
        aPlatEnd.setText(c.trim());
        Log.e(TAG, "Awal :" + a +" tengah "+ b +" akhir "+ c);

        //spinner
        Tools.setSpinnerValue(iFormAsset.getStringExtra("subcat"), subcategory, R.array.motorcycle_subcategory_entries, getApplicationContext());
        Tools.setSpinnerValue(iFormAsset.getStringExtra("merk"), aMerk, R.array.motor_brand_entries, getApplicationContext());
        Tools.setSpinnerValue(iFormAsset.getStringExtra("color"), aColor, R.array.color_entries, getApplicationContext());
        Tools.setSpinnerValue(iFormAsset.getStringExtra("fuel"), aFuel, R.array.fuel_entries, getApplicationContext());
        Tools.setSpinnerValue(iFormAsset.getStringExtra("year"), aYear, R.array.year_entries, getApplicationContext());
        Tools.setSpinnerValue(iFormAsset.getStringExtra("engine_cap")+"cc", aEngCap, R.array.engine_cap_motor_entries, getApplicationContext());

        if(iFormAsset.getStringExtra("transmission").equals("manual")) {
            ((RadioButton)aTransmisionGroup.getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton)aTransmisionGroup.getChildAt(1)).setChecked(true);
        }

        //Image
        if(!iFormAsset.getStringExtra("main_image").isEmpty()) {
            String imageUrl = AppConfig.URL_IMAGE_ASSETS + iFormAsset.getStringExtra("main_image");
            Picasso.with(getApplicationContext()).load(imageUrl).into(aMainImage);
            conSecondImage.setVisibility(View.VISIBLE);
        }
        String secondImg = iFormAsset.getStringExtra("second_image");
        String thirdImg = iFormAsset.getStringExtra("third_image");
        String fourthImg = iFormAsset.getStringExtra("fourth_image");
        String fifthImg = iFormAsset.getStringExtra("fifth_image");
        if (!secondImg.isEmpty() && !secondImg.equals("default.png")) {
            conThirdImage.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext()).load(AppConfig.URL_IMAGE_ASSETS + secondImg).into(aSecondImage);
            delSecondImage.setVisibility(View.VISIBLE);
        }
        if (!thirdImg.isEmpty() && !thirdImg.equals("default.png")) {
            conThirdImage.setVisibility(View.VISIBLE);
            conFourthImage.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext()).load(AppConfig.URL_IMAGE_ASSETS + thirdImg).into(aThirdImage);
            delThirdImage.setVisibility(View.VISIBLE);
        }
        if (!fourthImg.isEmpty() && !fourthImg.equals("default.png")) {
            conFourthImage.setVisibility(View.VISIBLE);
            conFifthImage.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext()).load(AppConfig.URL_IMAGE_ASSETS + fourthImg).into(aFourthImage);
            delFourthImage.setVisibility(View.VISIBLE);
        }
        if (!fifthImg.isEmpty() && !fifthImg.equals("default.png")) {
            conFifthImage.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext()).load(AppConfig.URL_IMAGE_ASSETS + fifthImg).into(aFifthImage);
            delFifthImage.setVisibility(View.VISIBLE);
        }
        if(!iFormAsset.getStringExtra("no_stnk").isEmpty()) {
            String imageSTNKUrl = AppConfig.URL_IMAGE_DOCUMENTS + iFormAsset.getStringExtra("no_stnk");
            Picasso.with(getApplicationContext()).load(imageSTNKUrl).into(aImgSTNK);
        }

        if (iFormAsset.getStringExtra("assurance").equals("false")){aAssurace.setChecked(false);}

        if (iFormAsset.getStringExtra("delivery_method").equals(getResources().getString(R.string.delivery_pickup))) {
            ((RadioButton) aDeliveryMethodGroup.getChildAt(0)).setChecked(true);
        } else if (iFormAsset.getStringExtra("delivery_method").equals(getResources().getString(R.string.delivery_deliver))) {
            ((RadioButton) aDeliveryMethodGroup.getChildAt(1)).setChecked(true);
            rDeliveryPrice.setVisibility(View.VISIBLE);
            detDeliveryPrice.setText(iFormAsset.getStringExtra("delivery_detail"));
            aIdDelivery = iFormAsset.getStringExtra("id_delivery");
        }

        if (iFormAsset.getStringExtra("deposit").equals("false")) {
            ((RadioButton) aDepositGroup.getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton) aDepositGroup.getChildAt(1)).setChecked(true);
            rDepositValue.setVisibility(View.VISIBLE);
            aDepositValue.setText(iFormAsset.getStringExtra("nominal_deposit"));
        }

        if (iFormAsset.getStringExtra("member_badge").equals(getResources().getString(R.string.member_badge_basic))) {
            ((RadioButton) aRentReqGroup.getChildAt(0)).setChecked(true);
        } else if (iFormAsset.getStringExtra("member_badge").equals(getResources().getString(R.string.member_badge_verified))) {
            ((RadioButton) aRentReqGroup.getChildAt(1)).setChecked(true);
        }else  ((RadioButton) aRentReqGroup.getChildAt(2)).setChecked(true);

        // Parsing data price
        JSONArray priceArray = new JSONArray(PricingTools.PriceStringToArray(iFormAsset.getStringExtra("price")));
        Log.e(TAG, "PRICE ARRAY :" + iFormAsset.getStringExtra("price"));
        if (priceArray.length() > 0){
            try {
                for (int i = 0; i < priceArray.length(); i++) {
                    JSONObject priceObject = priceArray.getJSONObject(i);
                    if(priceObject.getString("range_name").equals("BASECOST")){
                        aBasicPrice.setText(priceObject.getString("price"));
                        priceArray.remove(i);
                    }
                }
            } catch (JSONException e) {e.printStackTrace();}

            if(priceArray.length() > 0){
                try {
                    conAdvancePrice.setVisibility(View.VISIBLE);
                    JSONObject priceObject = priceArray.getJSONObject(0);
                    Tools.setSpinnerValue(priceObject.getString("range_name"), aRangName, R.array.range_name_entries, getApplicationContext());
                    aAdvancePrice.setText(priceObject.getString("price"));
                    aStartDate.setText(Tools.datePriceAdvanceFormat(priceObject.getString("start_date")));
                    aEndDate.setText(Tools.datePriceAdvanceFormat(priceObject.getString("end_date")));
                    if (priceArray.length() > 1){
                        conAdvancePrice2.setVisibility(View.VISIBLE);
                        JSONObject priceObject2 = priceArray.getJSONObject(1);
                        Tools.setSpinnerValue(priceObject2.getString("range_name"), aRangName2, R.array.range_name_entries, getApplicationContext());
                        aAdvancePrice2.setText(priceObject2.getString("price"));
                        aStartDate2.setText(Tools.datePriceAdvanceFormat(priceObject2.getString("start_date")));
                        aEndDate2.setText(Tools.datePriceAdvanceFormat(priceObject2.getString("end_date")));
                        if (priceArray.length() > 2){
                            conAdvancePrice3.setVisibility(View.VISIBLE);
                            JSONObject priceObject3 = priceArray.getJSONObject(2);
                            Tools.setSpinnerValue(priceObject3.getString("range_name"), aRangName3, R.array.range_name_entries, getApplicationContext());
                            aAdvancePrice3.setText(priceObject3.getString("price"));
                            aStartDate3.setText(Tools.datePriceAdvanceFormat(priceObject3.getString("start_date")));
                            aEndDate3.setText(Tools.datePriceAdvanceFormat(priceObject3.getString("end_date")));
                            if (priceArray.length() > 3) {
                                conAdvancePrice4.setVisibility(View.VISIBLE);
                                JSONObject priceObject4 = priceArray.getJSONObject(3);
                                Tools.setSpinnerValue(priceObject4.getString("range_name"), aRangName4, R.array.range_name_entries, getApplicationContext());
                                aAdvancePrice4.setText(priceObject4.getString("price"));
                                aStartDate4.setText(Tools.datePriceAdvanceFormat(priceObject4.getString("start_date")));
                                aEndDate4.setText(Tools.datePriceAdvanceFormat(priceObject4.getString("end_date")));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void showFileChooser(String action) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        int request = 0;
        switch (action){
            case "main" : request = PICK_IMAGE_REQUEST; break;
            case "second" : request = PICK_IMAGE_SECOND_REQUEST; break;
            case "third" : request = PICK_IMAGE_THIRD_REQUEST; break;
            case "fourth" : request = PICK_IMAGE_FOURTH_REQUEST; break;
            case "fifth" : request = PICK_IMAGE_FIFTH_REQUEST; break;
            case "STNK" : request = PICK_IMAGE_STNK_REQUEST; break;
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap;
        String ext;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting the Bitmap to ImageView
//                aMainImage.setImageBitmap(bitmap);

                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);

                isiimage = Tools.getStringImageView(bitmap, aMainImage);
                imgStringMain = ext +"," + isiimage;
                conSecondImage.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMAGE_SECOND_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                aSecondImage.setImageBitmap(bitmap);
                Log.e(TAG, filePath.toString());

                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);

                isiimage = Tools.getStringImageView(bitmap, aSecondImage);
                imgStringSecond = ext +"," + isiimage;
                delSecondImage.setVisibility(View.VISIBLE);
                conThirdImage.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMAGE_THIRD_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                aThirdImage.setImageBitmap(bitmap);

                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);

                isiimage = Tools.getStringImageView(bitmap, aThirdImage);
                imgStringThird = ext +"," + isiimage;
                delThirdImage.setVisibility(View.VISIBLE);
                conFourthImage.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMAGE_FOURTH_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                aFourthImage.setImageBitmap(bitmap);

                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);

                isiimage = Tools.getStringImageView(bitmap, aFourthImage);
                imgStringFourth = ext +"," + isiimage;
                delFourthImage.setVisibility(View.VISIBLE);
                conFifthImage.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMAGE_FIFTH_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                aFifthImage.setImageBitmap(bitmap);

                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);

                isiimage = Tools.getStringImageView(bitmap, aFifthImage);
                imgStringFifth = ext +"," + isiimage;
                delFifthImage.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMAGE_STNK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                aImgSTNK.setImageBitmap(bitmap);
                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);

                isiimage = Tools.getStringImageView(bitmap, aImgSTNK);
                imgStringSTNK = ext +"," + isiimage;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String str64b = Tools.getStringImage(photo);
            imgStringSTNK = "image/jpeg," + str64b;
            aImgSTNK.setImageBitmap(photo);
        }

        if (requestCode == PICK_LOCATION_REQUEST){
            showProgress(false);
            aAddress.setClickable(true);
            if(resultCode == RESULT_OK){
                Place location = PlacePicker.getPlace(data,this);
                String address = String.valueOf(location.getAddress());
                aAddress.setText(address);

                String LatLong = String.valueOf(location.getLatLng());
                aLatitude = Tools.getLatitude(LatLong);
                aLongitude = Tools.getLongitude(LatLong);
            }
        }

        if (requestCode == SET_DELIVERY_PRICE && resultCode == Activity.RESULT_OK) {
            aIdDelivery = data.getStringExtra("id_delivery");
            detDeliveryPrice.setText(data.getStringExtra("delivery_detail"));
        }

        if (requestCode == PICK_DATE_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String resultStart = data.getStringExtra("startDate");
                String resultEnd = data.getStringExtra("endDate");
                aStartDate.setText(resultStart);
                aEndDate.setText(resultEnd);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == PICK_DATE_REQUEST2) {
            if(resultCode == Activity.RESULT_OK){
                String resultStart = data.getStringExtra("startDate");
                String resultEnd = data.getStringExtra("endDate");
                aStartDate2.setText(resultStart);
                aEndDate2.setText(resultEnd);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == PICK_DATE_REQUEST3) {
            if(resultCode == Activity.RESULT_OK){
                String resultStart = data.getStringExtra("startDate");
                String resultEnd = data.getStringExtra("endDate");
                aStartDate3.setText(resultStart);
                aEndDate3.setText(resultEnd);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == PICK_DATE_REQUEST4) {
            if(resultCode == Activity.RESULT_OK){
                String resultStart = data.getStringExtra("startDate");
                String resultEnd = data.getStringExtra("endDate");
                aStartDate4.setText(resultStart);
                aEndDate4.setText(resultEnd);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_save_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            int transmissionId = aTransmisionGroup.getCheckedRadioButtonId();
            aTransmisionButton = (RadioButton) findViewById(transmissionId);
            tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
            category = iFormAsset.getStringExtra("cat");
            idAsset = iFormAsset.getIntExtra("id_asset",0);

            int rentReqId = aRentReqGroup.getCheckedRadioButtonId();
            if (rentReqId == aBasic.getId()) aRentReq = getResources().getString(R.string.member_badge_basic);
            else if (rentReqId == aVerified.getId()) aRentReq = getResources().getString(R.string.member_badge_verified);
            else aRentReq = getResources().getString(R.string.member_badge_smart_con);

            if(aDelivery.isChecked() && aPickup.isChecked()){aDeliveryMethod = "both";}
            else if (aDelivery.isChecked()){ aDeliveryMethod = "deliver";}
            else if (aPickup.isChecked()){ aDeliveryMethod = "pickup";}
            else { aDeliveryMethod = "nodefine";}

            if (aBasicPrice.getText().toString().isEmpty() || aDeliveryMethod.equals("nodefine") ||
                    aType.toString().isEmpty() || aPlat.toString().isEmpty() || aPlatStart.toString().isEmpty() || aPlatEnd.toString().isEmpty() ||
                    aName.getText().toString().isEmpty() || aType.getText().toString().isEmpty() || aMinDayRent.getText().toString().isEmpty() ||
                    aNoRangka.getText().toString().isEmpty() || aNoMesin.getText().toString().isEmpty() || aAddress.getText().toString().isEmpty()){

                Toast.makeText(getApplicationContext(), R.string.error_field_not_complete,Toast.LENGTH_LONG).show();
            } else{
                pricingArray.clear();
                getPrice();
                postPriceCheck(pricingArray.toString());

            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPrice(){
        JSONObject priceBasicObject = new JSONObject();
        try {
            priceBasicObject.put("price", NumberTextWatcherForThousand.trimCommaOfString(aBasicPrice.getText().toString()));
            priceBasicObject.put("range_name","BASECOST");
            priceBasicObject.put("start_date","1970-01-01");
            priceBasicObject.put("end_date","1970-01-01");

            pricingArray.add(priceBasicObject.toString().replace("=",":"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(!aAdvancePrice.getText().toString().isEmpty()) {
            Map<String, String> pricingObject = new HashMap<String, String>();
            pricingObject.put("\"range_name\"", "\"" + aRangName.getSelectedItem().toString() + "\"");
            pricingObject.put("\"start_date\"", "\"" + aStartDate.getText().toString() + "\"");
            pricingObject.put("\"end_date\"", "\"" + aEndDate.getText().toString() + "\"");
            pricingObject.put("\"price\"", "\"" + NumberTextWatcherForThousand.trimCommaOfString(aAdvancePrice.getText().toString()) + "\"");

            pricingArray.add(pricingObject.toString().replace("=", ":"));
        }

        if(!aAdvancePrice2.getText().toString().isEmpty()) {
            Map<String, String> pricingObject = new HashMap<String, String>();
            pricingObject.put("\"range_name\"", "\"" + aRangName2.getSelectedItem().toString() + "\"");
            pricingObject.put("\"start_date\"", "\"" + aStartDate2.getText().toString() + "\"");
            pricingObject.put("\"end_date\"", "\"" + aEndDate2.getText().toString() + "\"");
            pricingObject.put("\"price\"", "\"" + NumberTextWatcherForThousand.trimCommaOfString(aAdvancePrice2.getText().toString()) + "\"");

            pricingArray.add(pricingObject.toString().replace("=", ":"));
        }

        if(!aAdvancePrice3.getText().toString().isEmpty()) {
            Map<String, String> pricingObject = new HashMap<String, String>();
            pricingObject.put("\"range_name\"", "\"" + aRangName3.getSelectedItem().toString() + "\"");
            pricingObject.put("\"start_date\"", "\"" + aStartDate3.getText().toString() + "\"");
            pricingObject.put("\"end_date\"", "\"" + aEndDate3.getText().toString() + "\"");
            pricingObject.put("\"price\"", "\"" + NumberTextWatcherForThousand.trimCommaOfString(aAdvancePrice3.getText().toString()) + "\"");

            pricingArray.add(pricingObject.toString().replace("=", ":"));
        }

        if(!aAdvancePrice4.getText().toString().isEmpty()) {
            Map<String, String> pricingObject = new HashMap<String, String>();
            pricingObject.put("\"range_name\"", "\"" + aRangName4.getSelectedItem().toString() + "\"");
            pricingObject.put("\"start_date\"", "\"" + aStartDate4.getText().toString() + "\"");
            pricingObject.put("\"end_date\"", "\"" + aEndDate4.getText().toString() + "\"");
            pricingObject.put("\"price\"", "\"" + NumberTextWatcherForThousand.trimCommaOfString(aAdvancePrice4.getText().toString()) + "\"");

            pricingArray.add(pricingObject.toString().replace("=", ":"));
        }
    }

    public void postPriceCheck(final String price) {
        final String URL_PRICE_CHECK = AppConfig.URL_PRICE_CHECK ;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_PRICE_CHECK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Price Check Response : ", response);

                try {
                    JSONObject responseObj = new JSONObject(response);

                    priceStatus = responseObj.getString("status");
                    if(priceStatus.equals("OVERLAP")){
                        showProgress(false);
                        Toast.makeText(getApplicationContext(), responseObj.getString("message"), Toast.LENGTH_LONG).show();
                    }else{
                        if(iFormAsset.getStringExtra("action").equals("update")){
                            updateDataAset(category);
                        }else {
                            if (imgStringSTNK.equals("")) {
                                showProgress(false);
                                Toast.makeText(getApplicationContext(), "Harap Lengkapi Foto STNK", Toast.LENGTH_LONG).show();
                            } else {
                                if (!imgStringMain.isEmpty()) addDataAset(tenant);
                                else {
                                    showProgress(false);
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.error_main_image_not_complete), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Price Check Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("price", price);

                Log.e(TAG, URL_PRICE_CHECK + " With Key Body : " + keys.toString());
                return keys;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };

        queue.add(strReq);
    }

    private void addDataAset(String tenant) {
        aRentPackage = "-";

        new addAsetTask(tenant).execute();
    }

    private class addAsetTask extends AsyncTask<String, String, String> {

        private final String mTenant;
        private String errorMsg, responseAsset;

        private addAsetTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response != null){
                        Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
                        finish();
                    }                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Asset Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("name", aName.getText().toString());
                    keys.put("slug", aName.getText().toString().replace(" ","-"));
                    keys.put("brand", aMerk.getSelectedItem().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
                    keys.put("year", aYear.getSelectedItem().toString());
                    keys.put("colour", aColor.getSelectedItem().toString());
                    keys.put("engine_capacity", aEngCap.getSelectedItem().toString().replace("cc",""));
                    keys.put("license_plat", aPlatStart.getText().toString()+" "+aPlat.getText().toString()+" "+aPlatEnd.getText().toString());
                    keys.put("transmission", aTransmisionButton.getText().toString());
                    keys.put("fuel", aFuel.getSelectedItem().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("min_rent_day", aMinDayRent.getText().toString());
                    keys.put("delivery_method", aDeliveryMethod);
                    keys.put("address", aAddress.getText().toString());
                    keys.put("rent_package", aRentPackage);
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
                    keys.put("member_badge", aRentReq);
                    keys.put("price", pricingArray.toString());
                    if(!imgStringSTNK.isEmpty()) { keys.put("stnk", imgStringSTNK);}
                    if(!imgStringMain.isEmpty()){ keys.put("file", imgStringMain);}
                    if(!imgStringSecond.isEmpty()){keys.put("file1", imgStringSecond);}
                    if(!imgStringThird.isEmpty()){keys.put("file2", imgStringThird);}
                    if(!imgStringFourth.isEmpty()){keys.put("file3", imgStringFourth);}
                    if(!imgStringFifth.isEmpty()){keys.put("file4", imgStringFifth);}
                    keys.put("no_rangka", aNoRangka.getText().toString());
                    keys.put("no_mesin", aNoMesin.getText().toString());
                    Log.e(TAG, "Post Data : " + keys.toString());

                    if(aDeliveryMethod.equals("deliver")) keys.put("id_delivery", aIdDelivery);
                    keys.put("deposit", aDepositStatus);
                    if(aDepositStatus.equals("true")) keys.put("nominal_deposit", NumberTextWatcherForThousand.trimCommaOfString(aDepositValue.getText().toString()));

                    return keys;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", TOKEN);
                    return keys;
                }
            };

            try {
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return responseAsset;
        }

        @Override
        protected void onCancelled() {
            mAddAssetTask = null;
            showProgress(false);
        }
    }

    private void updateDataAset(String id) {
        aRentPackage = "-";

        new updateAsetTask(id).execute();
    }

    private class updateAsetTask extends AsyncTask<String, String, String> {
        private final String mIdAset;
        private String errorMsg, responseAsset;

        private updateAsetTask(String id) {
            mIdAset = id;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response != null){
                        Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
                        finish();
                    }                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Asset Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_asset", String.valueOf(idAsset));
                    keys.put("name", aName.getText().toString());
                    keys.put("slug", aName.getText().toString().replace(" ","-"));
                    keys.put("brand", aMerk.getSelectedItem().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
                    keys.put("year", aYear.getSelectedItem().toString());
                    keys.put("colour", aColor.getSelectedItem().toString());
                    keys.put("engine_capacity", aEngCap.getSelectedItem().toString().replace("cc",""));
                    keys.put("license_plat", aPlatStart.getText().toString()+" "+aPlat.getText().toString()+" "+aPlatEnd.getText().toString());
                    keys.put("transmission", aTransmisionButton.getText().toString());
                    keys.put("fuel", aFuel.getSelectedItem().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("min_rent_day", aMinDayRent.getText().toString());
                    keys.put("delivery_method", aDeliveryMethod);
                    keys.put("address", aAddress.getText().toString());
                    keys.put("rent_package", aRentPackage);
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
                    keys.put("member_badge", aRentReq);
                    keys.put("price", pricingArray.toString());
                    if(!imgStringMain.isEmpty()){ keys.put("file", imgStringMain);}
                    if(!imgStringSecond.isEmpty()){keys.put("file1", imgStringSecond);}
                    if(!imgStringThird.isEmpty()){keys.put("file2", imgStringThird);}
                    if(!imgStringFourth.isEmpty()){keys.put("file3", imgStringFourth);}
                    if(!imgStringFifth.isEmpty()){keys.put("file4", imgStringFifth);}
                    keys.put("no_rangka", aNoRangka.getText().toString());
                    keys.put("no_mesin", aNoMesin.getText().toString());

                    if(aDeliveryMethod.equals("deliver")) keys.put("id_delivery", aIdDelivery);
                    keys.put("deposit", aDepositStatus);
                    if(aDepositStatus.equals("true")) keys.put("nominal_deposit", NumberTextWatcherForThousand.trimCommaOfString(aDepositValue.getText().toString()));

                    Log.e(TAG, "Post Data : " + keys.toString());

                    return keys;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", TOKEN);
                    return keys;
                }
            };

            try {
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return responseAsset;
        }


        @Override
        protected void onCancelled() {
            mAddAssetTask = null;
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if(show){
            if (!pDialog.isShowing()){
                pDialog.show();
            }
        }else{
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }

}
