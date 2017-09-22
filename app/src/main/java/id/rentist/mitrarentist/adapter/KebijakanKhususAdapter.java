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

import id.rentist.mitrarentist.FormAddKebijakanActivity;
import id.rentist.mitrarentist.FormVoucherActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.KebijakanModul;


public class KebijakanKhususAdapter extends RecyclerView.Adapter<KebijakanKhususAdapter.ViewHolder> {

    private final List<KebijakanModul> mKhusus;
    private Context context;
    private static final String TAG = "KebijakanAdapter";

    public KebijakanKhususAdapter(final Context context, final List<KebijakanModul> mKhusus) {
        super();
        this.mKhusus = mKhusus;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kebijakan_khusus_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final KebijakanModul kbjm = mKhusus.get(i);
        viewHolder.title.setText(kbjm.getTitle());
        viewHolder.desc.setText(kbjm.getDesc());

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FormAddKebijakanActivity.class);
                i.putExtra("action","update");
                i.putExtra("id", kbjm.getId());
                i.putExtra("title", kbjm.getTitle());
                i.putExtra("desc", kbjm.getDesc());

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKhusus.size();
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
