package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.TestimonyModul;

/**
 * Created by mdhif on 21/06/2017.
 */

public class TestimonyAdapter extends RecyclerView.Adapter<TestimonyAdapter.ViewHolder> {

    private List<TestimonyModul> mTesti;
    private Context context;
    private static final String TAG = "TestimonyAdapter";

    public TestimonyAdapter(final Context context, final List<TestimonyModul> mTesti){
        super();
        this.mTesti = mTesti;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.testimony_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mTesti.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tNameMember, tPhone, tEmail, tContent, tDate;
        private SimpleRatingBar tRating;
        private CardView cardDetAset;

        public ViewHolder(View itemView){
            super(itemView);
            tNameMember = (TextView) itemView.findViewById(R.id.tm_member_name);
            tContent = (TextView) itemView.findViewById(R.id.tm_member_content);
            tDate = (TextView) itemView.findViewById(R.id.tm_member_date);
            tRating = (SimpleRatingBar) itemView.findViewById(R.id.tm_aset_rating);
            //tPhone = (TextView) itemView.findViewById(R.id.tm_mem);
            //tEmail = (TextView) itemView.findViewById(R.id.as_seat_det);
            cardDetAset = (CardView) itemView.findViewById(R.id.card_testimoni_view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        TestimonyModul tm = mTesti.get(i);

//        simpan value dalam object
        viewHolder.tNameMember.setText(tm.getNameMember());
        viewHolder.tContent.setText(tm.getContent());
        viewHolder.tDate.setText(tm.getDate());
        viewHolder.tRating.setRating(tm.getRating());
    }
}
