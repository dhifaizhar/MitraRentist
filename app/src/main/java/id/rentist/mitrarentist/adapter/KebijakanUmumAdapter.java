package id.rentist.mitrarentist.adapter;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.FormKebijakanActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.KebijakanModul;


public class KebijakanUmumAdapter extends RecyclerView.Adapter<KebijakanUmumAdapter.ViewHolder> {

    private final List<KebijakanModul> mUmum;
    private Context context;
    private int j;

    public KebijakanUmumAdapter(final Context context, List<KebijakanModul> mUmum) {
        super();
        this.mUmum = mUmum;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kebijakan_khusus_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final KebijakanModul kbjm = mUmum.get(i);
        viewHolder.title.setText(kbjm.getTitle());
        viewHolder.desc.setText(kbjm.getDesc());

        viewHolder.btnEdit.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mUmum.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, desc, btnEdit;
        private ImageView btn_expand;
        private RelativeLayout mItemExpand, mItemDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.kbj_title);
            desc = (TextView) itemView.findViewById(R.id.kbj_desc);
            btn_expand = (ImageView) itemView.findViewById(R.id.expandable_toggle_button);
            mItemExpand = (RelativeLayout) itemView.findViewById(R.id.expand);
            mItemDescription = (RelativeLayout) itemView.findViewById(R.id.expandable);
            btnEdit = (TextView) itemView.findViewById(R.id.kbj_edit);

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

                    @SuppressLint("ObjectAnimatorBinding")
                    ObjectAnimator animation = ObjectAnimator.ofInt(mItemDescription, "layout_height", mItemDescription.getHeight());
                    animation.setDuration(200).start();
                }
            });
        }
    }
}
