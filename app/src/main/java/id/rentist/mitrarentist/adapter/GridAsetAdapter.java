package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import id.rentist.mitrarentist.AsetListActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.SetupCategoryActivity;
import id.rentist.mitrarentist.modul.ItemCategoryModul;
import id.rentist.mitrarentist.tools.SessionManager;

/**
 * Created by mdhif on 17/09/2017.
 */

public class GridAsetAdapter extends RecyclerView.Adapter<GridAsetAdapter.ViewHolder>  {
    private List<ItemCategoryModul> itemCategory;
    private SessionManager sm;
    private Context context;
    private static final String TAG = "CategoryAdapter";

    public GridAsetAdapter(Context context){
        super();
        this.context = context;
        itemCategory = new ArrayList<ItemCategoryModul>();
        ItemCategoryModul category;
        sm = new SessionManager(context);
        String[] asset_category = context.getResources().getStringArray(R.array.asset_category_entries);

        if (sm.getPreferences("car").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(1);
            category.setTitle(asset_category[0]);
            category.setThumbnail(R.drawable.cat_mobil);
            category.setSum(sm.getIntPreferences("sum_car"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("motorcycle").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(2);
            category.setTitle(asset_category[1]);
            category.setThumbnail(R.drawable.cat_motor);
            category.setSum(sm.getIntPreferences("sum_motor"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("yacht").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(3);
            category.setTitle(asset_category[2]);
            category.setThumbnail(R.drawable.cat_yatch);
            category.setSum(sm.getIntPreferences("sum_yacht"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("medical_equipment").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(4);
            category.setTitle(asset_category[3]);
            category.setThumbnail(R.drawable.cat_medicalequipment);
            category.setSum(sm.getIntPreferences("sum_medic"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("photography").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(5);
            category.setTitle(asset_category[4]);
            category.setThumbnail(R.drawable.cat_cameras);
            category.setSum(sm.getIntPreferences("sum_photography"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("toys").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(6);
            category.setTitle(asset_category[5]);
            category.setThumbnail(R.drawable.cat_mainananak);
            category.setSum(sm.getIntPreferences("sum_toys"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("adventure").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(7);
            category.setTitle(asset_category[6]);
            category.setThumbnail(R.drawable.cat_adventure);
            category.setSum(sm.getIntPreferences("sum_adventure"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("maternity").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(8);
            category.setTitle(asset_category[7]);
            category.setThumbnail(R.drawable.cat_maternity);
            category.setSum(sm.getIntPreferences("sum_maternity"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("electronic").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(9);
            category.setTitle(asset_category[8]);
            category.setThumbnail(R.drawable.cat_elektronik);
            category.setSum(sm.getIntPreferences("sum_electronic"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("bicycle").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(10);
            category.setTitle(asset_category[9]);
            category.setThumbnail(R.drawable.cat_sepeda);
            category.setSum(sm.getIntPreferences("sum_bicycle"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("office").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(11);
            category.setTitle(asset_category[10]);
            category.setThumbnail(R.drawable.cat_officeequipment);
            category.setSum(sm.getIntPreferences("sum_office"));
            itemCategory.add(category);
        }

        if (sm.getPreferences("fashion").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(12);
            category.setTitle(asset_category[11]);
            category.setThumbnail(R.drawable.cat_fashion);
            category.setSum(sm.getIntPreferences("sum_fashion"));
            itemCategory.add(category);
        }


        if (!sm.getPreferences("role").equals(context.getString(R.string.role_finance))) {
           if ( !sm.getPreferences("role").equals(context.getString(R.string.role_delivery))) {
               if( sm.getIntPreferences("sum_cat") != 12) {
                   category = new ItemCategoryModul();
                   category.setId(13);
                   category.setTitle("Tambah Aset");
                   category.setThumbnail(R.drawable.ic_add_black_48dp);
                   itemCategory.add(category);
               }
           }
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_aset_category, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return itemCategory.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title, sum;
        private ImageView imgThumbnail;
        private CardView cardDetAset;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_c_title);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.item_c_img);
            sum = (TextView) itemView.findViewById(R.id.item_c_sum);
            cardDetAset = (CardView) itemView.findViewById(R.id.card_gridview_aset);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i ){
        ItemCategoryModul ac = itemCategory.get(i);
        final String idc = String.valueOf(ac.getId()), cname = ac.getTitle();

//        simpan value dalam object
        viewHolder.title.setText(ac.getTitle());
        viewHolder.imgThumbnail.setImageResource(ac.getThumbnail());

        if(ac.getSum() > 0){viewHolder.sum.setText(String.valueOf(ac.getSum()));}
        else{viewHolder.sum.setVisibility(View.GONE);}

        viewHolder.cardDetAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idc.matches("1|2|3|4|5|6|7|8|9|10|11|12")) {
                    Intent iAset = new Intent(context, AsetListActivity.class);
                    iAset.putExtra("id_category", idc);
                    iAset.putExtra("name_category", cname);
                    iAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iAset);
                } else if (Objects.equals(idc, "13")) {
//                    Intent intent = new Intent("add-asset");
//                    intent.putExtra("action","add");
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    Intent iSetup = new Intent(context, SetupCategoryActivity.class);
                    iSetup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iSetup);
                } else {
                    Snackbar snackbar = Snackbar.make(v, "Oops, Sorry ! This Feature Will Online Soon", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}
