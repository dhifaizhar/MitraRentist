package id.rentist.mitrarentist.adapter;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.KebijakanModul;

/**
 * Created by Nugroho Tri Pambud on 7/12/2017.
 */

public class AddKebijakanAdapter extends RecyclerView.Adapter<AddKebijakanAdapter.ViewHolder> {

    private List<KebijakanModul> mKebijakan;
    private int j;

    public AddKebijakanAdapter(){
        super();
        this.mKebijakan = new ArrayList<KebijakanModul>();
        KebijakanModul kbj;

        kbj = new KebijakanModul();
        kbj.setTitle("");
        kbj.setDesc("");

        mKebijakan.add(kbj);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_form_add_kebijakan, viewGroup, false);
        AddKebijakanAdapter.ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        KebijakanModul kbj = mKebijakan.get(i);

//        simpan value dalam object
        viewHolder.title.setText(kbj.getTitle());
        viewHolder.desc.setText(kbj.getDesc());

    }

    @Override
    public int getItemCount() {
        return mKebijakan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc;
        public Button add;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.kbj_title);
            desc = (TextView) itemView.findViewById(R.id.kbj_desc);
            add = (Button) itemView.findViewById(R.id.btn_add);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Kebijakan ditambahkan", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        }
    }
}
