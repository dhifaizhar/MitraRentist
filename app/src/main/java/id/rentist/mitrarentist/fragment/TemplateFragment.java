package id.rentist.mitrarentist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mdhif on 18/06/2017.
 */

public class TemplateFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    public TemplateFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view =  inflater.inflate(R.layout.fragment_transaksi, container, false);
//
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.tr_recyclerView);
//        mRecyclerView.setHasFixedSize(true);
//
//        mLayoutManager = new GridLayoutManager(getActivity(),1);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        mAdapter = new TransaksiAdapter();
//        mRecyclerView.setAdapter(mAdapter);

//        return view;
        return null;
    }
}
