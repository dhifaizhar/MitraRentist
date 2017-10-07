package id.rentist.mitrarentist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import id.rentist.mitrarentist.R;

public class PricingBasicFragment extends Fragment {
    private View view;
    private EditText aBasicPrice;
    String basicPrice;


    public PricingBasicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pricing_basic, container, false);
        aBasicPrice = (EditText) view.findViewById(R.id.as_price_basic);

//        TextView aBasicPrice = (TextView) this.getView().findViewById(R.id.as_price_basic);


        return view;
    }

    public JSONObject getBasicPrice(){

        JSONObject priceBasicObject = new JSONObject();
        String basicPrice = aBasicPrice.getText().toString();

        try {
            priceBasicObject.put("range_name","BASECOST");
            priceBasicObject.put("start_date","0");
            priceBasicObject.put("end_date","0");
            priceBasicObject.put("price", basicPrice);
//            priceBasicObject.put("\"price\"",aBasicPrice.getText().toString());


            Log.e("PRICEBASIC","BasicPrice : " + basicPrice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return priceBasicObject;
    }

}
