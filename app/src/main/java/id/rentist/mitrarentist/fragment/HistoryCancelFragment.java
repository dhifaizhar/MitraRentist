package id.rentist.mitrarentist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.adapter.HistoryCancelAdapter;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;

/**
 * Created by mdhif on 07/07/2017.
 */

public class HistoryCancelFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    public HistoryCancelFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<ItemTransaksiModul> mTrans = new ArrayList<ItemTransaksiModul>();

        View view = inflater.inflate(R.layout.fragment_history_cancel, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.hcancel_recyclerViewFrag);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HistoryCancelAdapter(getActivity(), mTrans);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
