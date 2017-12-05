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
import id.rentist.mitrarentist.tools.Tools;

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
        private TextView tContent, tDate ,tNameMember;
        private SimpleRatingBar clean, neat, honest, com;
        private CardView cardDetAset;

        public ViewHolder(View itemView){
            super(itemView);
            tNameMember = (TextView) itemView.findViewById(R.id.tm_member_name);
            tContent = (TextView) itemView.findViewById(R.id.tm_member_content);
            tDate = (TextView) itemView.findViewById(R.id.tm_member_date);
            clean = (SimpleRatingBar) itemView.findViewById(R.id.tm_cleanliness);
            neat = (SimpleRatingBar) itemView.findViewById(R.id.tm_neatness);
            honest = (SimpleRatingBar) itemView.findViewById(R.id.tm_honesty);
            com = (SimpleRatingBar) itemView.findViewById(R.id.tm_comunication);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        TestimonyModul tm = mTesti.get(i);

//        simpan value dalam object
        viewHolder.tNameMember.setText(tm.getMember());
        viewHolder.tContent.setText(tm.getContent());
        viewHolder.tDate.setText(Tools.dateHourFormat(tm.getDate().substring(0,19)));
        viewHolder.clean.setRating(tm.getCleanliness());
        viewHolder.neat.setRating(tm.getNeatness());
        viewHolder.honest.setRating(tm.getHonesty());
        viewHolder.com.setRating(tm.getComunication());
    }
}
