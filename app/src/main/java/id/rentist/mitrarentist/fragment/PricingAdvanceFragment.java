package id.rentist.mitrarentist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.rentist.mitrarentist.R;

public class PricingAdvanceFragment extends Fragment {
    private View view;

    public PricingAdvanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_pricing_advance, container, false);
        return view;
    }



}
