package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class FormMaternityAsetActivity extends AppCompatActivity {
    private Activity currentActivity = FormMaternityAsetActivity.this;
    private int subCategotyArray = R.array.maternity_subcategory_entries;
    private int layout = R.layout.activity_form_maternity_aset;
    private int cat_num = 8;
    private String URL = AppConfig.URL_MATERNITY;
    private String fee_cat = "fee_maternity";

    String[] asset_category;
    private static final String TAG = "FormAssetActivity";
    private static final String TOKEN = "secretissecret";

    private AsyncTask mAddAssetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private int PICK_LOCATION_REQUEST = 10;

    Intent iFormAsset;
    TextView aName, aMerk, aType, aMinDayRent, aDesc, aAddress, aWeight, aDimensionP, aDimensionL, aDimensionT;
    Integer idAsset;
    String aLatitude, aLongitude, aRentPackage, tenant, category, encodedImage, aDeliveryMethod,
            aDimension,  aRentReq;
    CheckBox aAssurace;
    Spinner subcategory;
    RadioGroup aRentReqGroup;
    RadioButton aBasic, aVerified, aSmartCon;
    Button bSaveButton;
    EditText aAssetValue;

    //Delivery & Deposit
    String aIdDelivery = "", aDepositStatus;
    RadioGroup aDeliveryMethodGroup, aDepositGroup;
    RadioButton rPickup, rDelivery, aLogistic, rbDepositYes, rbDepositNo;
    LinearLayout rDeliveryPrice, rDeposit, rDepositValue;
    TextView detDeliveryPrice;
    EditText aDepositValue;
    private int SET_DELIVERY_PRICE = 11;

    //Image Initial
    String[] imagesArray = {AppConfig.URL_IMAGE_ASSETS + "default.png"};
    ImageView aMainImage, aSecondImage, aThirdImage, aFourthImage, aFifthImage;
    ImageButton delSecondImage, delThirdImage, delFourthImage, delFifthImage;
    RelativeLayout conSecondImage, conThirdImage, conFourthImage, conFifthImage;
    String isiimage, imgStringMain = "", imgStringSecond = "", imgStringThird = "", imgStringFourth = "", imgStringFifth = "";
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_IMAGE_SECOND_REQUEST = 2;
    private int PICK_IMAGE_THIRD_REQUEST = 3;
    private int PICK_IMAGE_FOURTH_REQUEST = 4;
    private int PICK_IMAGE_FIFTH_REQUEST = 5;

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
    private int PICK_DATE_REQUEST = 6;
    private int PICK_DATE_REQUEST2 = 7;
    private int PICK_DATE_REQUEST3 = 8;
    private int PICK_DATE_REQUEST4 = 9;

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
        bSaveButton = (Button) findViewById(R.id.btn_save);

        subcategory = (Spinner) findViewById(R.id.as_subcat_spinner);
        aName = (TextView) findViewById(R.id.as_name);
        aMerk = (TextView) findViewById(R.id.as_merk);
        aType = (TextView) findViewById(R.id.as_type);
        aAssurace = (CheckBox) findViewById(R.id.as_ck_assurance);
        aDesc = (TextView) findViewById(R.id.as_desc);
        aMinDayRent = (TextView) findViewById(R.id.as_min_day_rent);
        aAddress = (TextView) findViewById(R.id.as_address);
        aAssetValue = (EditText) findViewById(R.id.as_value);
        aWeight = (TextView) findViewById(R.id.as_weight);
        aDimensionP = (TextView) findViewById(R.id.as_dim_p);
        aDimensionL = (TextView) findViewById(R.id.as_dim_l);
        aDimensionT = (TextView) findViewById(R.id.as_dim_t);
        aRentReqGroup = (RadioGroup) findViewById(R.id.rent_req_group);
        aBasic = (RadioButton) findViewById(R.id.r_basic);
        aVerified = (RadioButton) findViewById(R.id.r_verified);
        aSmartCon = (RadioButton) findViewById(R.id.r_smart_con);
        aAssetValue.addTextChangedListener(new NumberTextWatcherForThousand(aAssetValue));

        //Image
        conSecondImage = (RelativeLayout) findViewById(R.id.con_second_image);
        conThirdImage = (RelativeLayout) findViewById(R.id.con_third_image);
        conFourthImage = (RelativeLayout) findViewById(R.id.con_fourth_image);
        conFifthImage = (RelativeLayout) findViewById(R.id.con_fifth_image);
        aMainImage = (ImageView) findViewById(R.id.main_image);
        aSecondImage = (ImageView) findViewById(R.id.second_image);
        aThirdImage = (ImageView) findViewById(R.id.third_image);
        aFourthImage = (ImageView) findViewById(R.id.fourth_image);
        aFifthImage = (ImageView) findViewById(R.id.fifth_image);
        delSecondImage = (ImageButton) findViewById(R.id.delete_second_image);
        delThirdImage = (ImageButton) findViewById(R.id.delete_third_image);
        delFourthImage = (ImageButton) findViewById(R.id.delete_fourth_image);
        delFifthImage = (ImageButton) findViewById(R.id.delete_fifth_image);

        Tools.setSpinnerValue("", subcategory, subCategotyArray, getApplicationContext());
        aAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                aAddress.setClickable(false);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(currentActivity);
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
                PickSetup setup = Tools.imagePickerSetup();
                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        r.getBitmap();
                        r.getError();
                        r.getUri();
                        imgStringMain = Tools.getStringImageView(r.getBitmap(), aMainImage);
                        Log.e("onPickResult: ",imgStringMain);
                        conSecondImage.setVisibility(View.VISIBLE);
                    }
                }).show((FragmentActivity) currentActivity);
            }
        });
        aSecondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickSetup setup = Tools.imagePickerSetup();
                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        r.getBitmap();
                        r.getError();
                        r.getUri();
                        imgStringSecond = Tools.getStringImageView(r.getBitmap(), aSecondImage);
                        Log.e("onPickResult: ",imgStringSecond);
                        delSecondImage.setVisibility(View.VISIBLE);
                        conThirdImage.setVisibility(View.VISIBLE);
                    }
                }).show((FragmentActivity) currentActivity);
            }
        });
        aThirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickSetup setup = Tools.imagePickerSetup();
                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        r.getBitmap();
                        r.getError();
                        r.getUri();
                        imgStringThird = Tools.getStringImageView(r.getBitmap(), aThirdImage);
                        Log.e("onPickResult: ",imgStringThird);
                        delThirdImage.setVisibility(View.VISIBLE);
                        conFourthImage.setVisibility(View.VISIBLE);
                    }
                }).show((FragmentActivity) currentActivity);
            }
        });
        aFourthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickSetup setup = Tools.imagePickerSetup();
                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        r.getBitmap();
                        r.getError();
                        r.getUri();
                        imgStringFourth = Tools.getStringImageView(r.getBitmap(), aFourthImage);
                        Log.e("onPickResult: ",imgStringFourth);
                        delFourthImage.setVisibility(View.VISIBLE);
                        conFifthImage.setVisibility(View.VISIBLE);
                    }
                }).show((FragmentActivity) currentActivity);
            }
        });
        aFifthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickSetup setup = Tools.imagePickerSetup();
                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        r.getBitmap();
                        r.getError();
                        r.getUri();
                        imgStringFifth = Tools.getStringImageView(r.getBitmap(), aFifthImage);
                        Log.e("onPickResult: ",imgStringFifth);
                        delFifthImage.setVisibility(View.VISIBLE);
                    }
                }).show((FragmentActivity) currentActivity);
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

        if(iFormAsset.getStringExtra("action").equals("update")){
            getDataUpdate();
        }

        bSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.setMessage("loading ...");
                showProgress(true);

                tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
                category = iFormAsset.getStringExtra("cat");
                idAsset = iFormAsset.getIntExtra("id_asset",0);
                aDimension = String.valueOf(aDimensionP.getText() + "x" + aDimensionL.getText() + "x" + aDimensionT.getText());

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
                        aName.getText().toString().isEmpty() ||
                        aMerk.getText().toString().isEmpty() ||
                        aType.getText().toString().isEmpty() ||
                        aAddress.getText().toString().isEmpty() ||
                        aMinDayRent.getText().toString().isEmpty() ||
                        aDesc.getText().toString().isEmpty() ||
                        aAssetValue.getText().toString().isEmpty() ||
                        aWeight.getText().toString().isEmpty() ||
                        aDimension.isEmpty() ||
                        delivMethodValid.equals(false) ||
                        depositValid.equals(false)
                        ){
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.error_field_not_complete),Toast.LENGTH_LONG).show();
                } else{
                    pricingArray.clear();
                    getPrice();
                    postPriceCheck(pricingArray.toString());
                }
            }
        });
    }

    private void getDataUpdate(){
        aName.setText(iFormAsset.getStringExtra("name"));
        aMerk.setText(iFormAsset.getStringExtra("merk"));
        aType.setText(iFormAsset.getStringExtra("type"));
        aMinDayRent.setText(iFormAsset.getStringExtra("min_rent_day"));
        aDesc.setText(iFormAsset.getStringExtra("description"));
        aAddress.setText(iFormAsset.getStringExtra("address"));
        aLatitude = iFormAsset.getStringExtra("latitude");
        aLongitude = iFormAsset.getStringExtra("longitude");
        aAssetValue.setText(iFormAsset.getStringExtra("asset_value"));
        aWeight.setText(iFormAsset.getStringExtra("weight"));

        String pattern = "([^a-zA-z]*)[a-zA-z]([^a-zA-z]*)[a-zA-z]([^a-zA-z]*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(iFormAsset.getStringExtra("dimension"));
        while (m.find()) {
            aDimensionP.setText(m.group(1));
            aDimensionL.setText(m.group(2));
            aDimensionT.setText(m.group(3));
        }

        if (iFormAsset.getStringExtra("assurance").equals("true")){aAssurace.setChecked(true);}

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

        //spinner
        Tools.setSpinnerValue(iFormAsset.getStringExtra("subcat"), subcategory, subCategotyArray, getApplicationContext());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                        if (iFormAsset.getStringExtra("action").equals("update")) {
                            updateDataAset(category);
                        } else {
                            if(!imgStringMain.isEmpty()) addDataAset(tenant);
                            else {
                                showProgress(false);
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.error_main_image_not_complete), Toast.LENGTH_LONG).show();
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
                    Log.e(TAG, "resnpose balik" + response);
                    if(response != null){
                        try {
                            JSONObject assetObject = new JSONObject(response);
                            putImageAsset(assetObject.getString("id"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
//                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
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
                    keys.put("description", aDesc.getText().toString());
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
                    keys.put("brand", aMerk.getText().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("min_rent_day", aMinDayRent.getText().toString());
                    keys.put("delivery_method", aDeliveryMethod);
                    keys.put("address", aAddress.getText().toString());
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
                    keys.put("price", pricingArray.toString());
                    keys.put("asset_value", NumberTextWatcherForThousand.trimCommaOfString(aAssetValue.getText().toString()));
                    keys.put("weight", aWeight.getText().toString());
                    keys.put("dimension", aDimension);
                    keys.put("member_badge", aRentReq);

                    if(aDeliveryMethod.equals("deliver")) keys.put("id_delivery", aIdDelivery);
                    keys.put("deposit", aDepositStatus);
                    if(aDepositStatus.equals("true")) keys.put("nominal_deposit", NumberTextWatcherForThousand.trimCommaOfString(aDepositValue.getText().toString()));

//                    if(!imgStringMain.isEmpty()){ keys.put("file", imgStringMain);}
//                    if(!imgStringSecond.isEmpty()){keys.put("file1", imgStringSecond);}
//                    if(!imgStringThird.isEmpty()){keys.put("file2", imgStringThird);}
//                    if(!imgStringFourth.isEmpty()){keys.put("file3", imgStringFourth);}
//                    if(!imgStringFifth.isEmpty()){keys.put("file4", imgStringFifth);}
                    Log.e(TAG, "Value Object : " + keys.toString());
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

    private void putImageAsset (final String id_asset){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "resnpose balik image :" + response);
                if(response != null){
                    Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.e(TAG, "Form Asset Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_asset", id_asset);
                keys.put("price", pricingArray.toString());
                if(!imgStringMain.isEmpty()){ keys.put("file", imgStringMain);}
                if(!imgStringSecond.isEmpty()){keys.put("file1", imgStringSecond);}
                if(!imgStringThird.isEmpty()){keys.put("file2", imgStringThird);}
                if(!imgStringFourth.isEmpty()){keys.put("file3", imgStringFourth);}
                if(!imgStringFifth.isEmpty()){keys.put("file4", imgStringFifth);}

                Log.e(TAG, "Image Keys: " + String.valueOf(keys));
                return keys;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("token", TOKEN);
                return keys;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updateDataAset(String category) {
        aRentPackage = "-";

        new updateAsetTask(category).execute();
    }

    private class updateAsetTask extends AsyncTask<String, String, String>  {
        private final String mCategory;
        private String errorMsg, responseAsset;

        private updateAsetTask(String category) {
            mCategory = category;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "resnpose balik" + response);
                    if(response != null){
                        Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
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
                    keys.put("description", aDesc.getText().toString());
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
                    keys.put("brand", aMerk.getText().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("min_rent_day", aMinDayRent.getText().toString());
                    keys.put("delivery_method", aDeliveryMethod);
                    keys.put("address", aAddress.getText().toString());
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
                    keys.put("price", pricingArray.toString());
                    keys.put("asset_value", NumberTextWatcherForThousand.trimCommaOfString(aAssetValue.getText().toString()));
                    keys.put("weight", aWeight.getText().toString());
                    keys.put("dimension", aDimension);
                    keys.put("member_badge", aRentReq);
                    if(!imgStringMain.isEmpty()){ keys.put("file", imgStringMain);}
                    if(!imgStringSecond.isEmpty()){keys.put("file1", imgStringSecond);}
                    if(!imgStringThird.isEmpty()){keys.put("file2", imgStringThird);}
                    if(!imgStringFourth.isEmpty()){keys.put("file3", imgStringFourth);}
                    if(!imgStringFifth.isEmpty()){keys.put("file4", imgStringFifth);}
                    if(aDeliveryMethod.equals("deliver")) keys.put("id_delivery", aIdDelivery);
                    keys.put("deposit", aDepositStatus);
                    if(aDepositStatus.equals("true")) keys.put("nominal_deposit", NumberTextWatcherForThousand.trimCommaOfString(aDepositValue.getText().toString()));

                    Log.e(TAG, "Asset Keys: " + String.valueOf(keys));
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
        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Data aset belum tersimpan, anda yakin akan keluar ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                onBackPressed();
                currentActivity.finish();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = showAlert.create();
        alertDialog.show();

        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Data aset belum tersimpan, anda yakin akan keluar ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                onBackPressed();
                currentActivity.finish();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = showAlert.create();
        alertDialog.show();
    }
}