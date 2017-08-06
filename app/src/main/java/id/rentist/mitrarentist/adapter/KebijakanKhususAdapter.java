package id.rentist.mitrarentist.adapter;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.KebijakanModul;


public class KebijakanKhususAdapter extends RecyclerView.Adapter<KebijakanKhususAdapter.ViewHolder> {

    private final List<KebijakanModul> mKhusus;
    private int j;

    public KebijakanKhususAdapter(List<KebijakanModul> mKhusus) {
        super();
        this.mKhusus = mKhusus;
        KebijakanModul kbj;

        for (j = 1; j < 3;j++){
            kbj = new KebijakanModul();
            kbj.setTitle("Pengembalian Saat Peminjaman");
            kbj.setDesc("Waktu Menerima order adalah 5 menit, jika lebih dari itu maka otomatis tertolak");

            this.mKhusus.add(kbj);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kebijakan_khusus_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        KebijakanModul kbjm = mKhusus.get(i);

        viewHolder.title.setText(kbjm.getTitle());
        viewHolder.desc.setText(kbjm.getDesc());

    }

    @Override
    public int getItemCount() {
        return mKhusus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc;
        public ImageView btn_expand;
        public RelativeLayout mItemExpand;
        public RelativeLayout mItemDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.kbj_title);
            desc = (TextView) itemView.findViewById(R.id.kbj_desc);
            btn_expand = (ImageView) itemView.findViewById(R.id.expandable_toggle_button);
            mItemExpand = (RelativeLayout) itemView.findViewById(R.id.expand);
            mItemDescription = (RelativeLayout) itemView.findViewById(R.id.expandable);

            mItemExpand.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (mItemDescription.getVisibility() == View.GONE) {
                        // it's collapsed - expand it
                        mItemDescription.setVisibility(View.VISIBLE);
                        btn_expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_18dp);
                    } else {
                        // it's expanded - collapse it
                        mItemDescription.setVisibility(View.GONE);
                        btn_expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_18dp);
                    }

                    ObjectAnimator animation = ObjectAnimator.ofInt(mItemDescription, "layout_height", mItemDescription.getHeight());
                    animation.setDuration(200).start();
                }
            });
        }
    }
}
