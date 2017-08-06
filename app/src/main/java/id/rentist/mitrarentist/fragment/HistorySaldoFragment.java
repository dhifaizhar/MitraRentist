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
import id.rentist.mitrarentist.adapter.HistorySaldoAdapter;
import id.rentist.mitrarentist.modul.DompetModul;

/**
 * Created by mdhif on 07/07/2017.
 */

public class HistorySaldoFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    public HistorySaldoFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<DompetModul> mDompet = new ArrayList<DompetModul>();

        View view = inflater.inflate(R.layout.fragment_history_saldo, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.hsaldo_recyclerViewFrag);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HistorySaldoAdapter(mDompet);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
