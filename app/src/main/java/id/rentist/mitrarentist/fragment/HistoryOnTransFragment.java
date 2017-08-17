package id.rentist.mitrarentist.fragment;

import android.os.AsyncTask;
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
import id.rentist.mitrarentist.adapter.HistoryOnTransAdapter;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;

/**
 * Created by mdhif on 13/07/2017.
 */

public class HistoryOnTransFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private AsyncTask mOngoingTask = null;

    private static final String TAG = "HistOnTransActivity";
    private static final String TOKEN = "secretissecret";
    String tenant;

    public HistoryOnTransFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<ItemTransaksiModul> mTrans = new ArrayList<ItemTransaksiModul>();
        View view = inflater.inflate(R.layout.fragment_history_on_transaksi, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.htranson_recyclerViewFrag);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HistoryOnTransAdapter(getActivity(),mTrans);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
