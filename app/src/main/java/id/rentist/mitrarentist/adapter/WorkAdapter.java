package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.icu.util.Calendar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ProfileContentModul;

/**
 * Created by mdhif on 16/07/2017.
 */

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {

    private Context context;
    private Calendar calendar;
    private List<ProfileContentModul> mProfile;
    private int jam, menit;
    private int j;

    public WorkAdapter(Context context){
        super();
        this.mProfile = new ArrayList<ProfileContentModul>();
        this.context = context;
        ProfileContentModul pr;

        pr = new ProfileContentModul();
        mProfile.add(pr);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_work_date, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mProfile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView startDate;
        private ImageView profilePhoto;
        private Button btnstartDate, btnendDate;

        public ViewHolder(View itemView){
            super(itemView);
//            rental = (TextView) itemView.findViewById(R.id.pr_rental_name);
            startDate = (TextView) itemView.findViewById(R.id.startDate);
            btnstartDate = (Button) itemView.findViewById(R.id.btn_start_date);
            btnendDate = (Button) itemView.findViewById(R.id.btn_end_date);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i ){
        ProfileContentModul as = mProfile.get(i);

//        simpan value dalam object
//        viewHolder.rental.setText(as.getRentName());
    }
}
