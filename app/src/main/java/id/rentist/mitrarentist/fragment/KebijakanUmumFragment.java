package id.rentist.mitrarentist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.adapter.KebijakanUmumAdapter;
import id.rentist.mitrarentist.modul.KebijakanModul;

public class KebijakanUmumFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;


    public KebijakanUmumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<KebijakanModul> mKebijakan = new ArrayList<KebijakanModul>();
        View view = inflater.inflate(R.layout.fragment_kebijakan_umum, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.kbju_recyclerViewFrag);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new KebijakanUmumAdapter(mKebijakan);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
