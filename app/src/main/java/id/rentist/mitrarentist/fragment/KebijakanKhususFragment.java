package id.rentist.mitrarentist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.FormAddKebijakanActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.adapter.KebijakanKhususAdapter;
import id.rentist.mitrarentist.modul.KebijakanModul;

public class KebijakanKhususFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FloatingActionButton mFab;


    public KebijakanKhususFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<KebijakanModul> mKebijakan = new ArrayList<KebijakanModul>();
        View view = inflater.inflate(R.layout.fragment_kebijakan_khusus, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.kbjk_recyclerViewFrag);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new KebijakanKhususAdapter(mKebijakan);
        mRecyclerView.setAdapter(mAdapter);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent iAddKebijakan = new Intent(v.getContext(), FormAddKebijakanActivity.class);
                v.getContext().startActivity(iAddKebijakan);
            }
        });

        return view;
    }



}
