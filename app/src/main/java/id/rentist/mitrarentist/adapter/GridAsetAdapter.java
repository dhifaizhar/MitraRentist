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

import id.rentist.mitrarentist.AsetListActivity;
import id.rentist.mitrarentist.R;
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

        if (sm.getPreferences("car").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(1);
            category.setTitle("Mobil");
            category.setThumbnail(R.drawable.mobil);
            itemCategory.add(category);
        }

        if (sm.getPreferences("motorcycle").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(2);
            category.setTitle("Motor");
            category.setThumbnail(R.drawable.motor);
            itemCategory.add(category);
        }

        if (sm.getPreferences("yacht").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(3);
            category.setTitle("Kapal Pesiar");
            category.setThumbnail(R.drawable.yatch);
            itemCategory.add(category);
        }

        if (sm.getPreferences("medical_equipment").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(4);
            category.setTitle("Peralatan Medis");
            category.setThumbnail(R.drawable.medical_equipment);
            itemCategory.add(category);
        }

        if (sm.getPreferences("photography").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(5);
            category.setTitle("Fotografi");
            category.setThumbnail(R.drawable.camera);
            itemCategory.add(category);
        }

        if (sm.getPreferences("toys").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(6);
            category.setTitle("Mainan Anak");
            category.setThumbnail(R.drawable.mianan_anak);
            itemCategory.add(category);
        }

        if (sm.getPreferences("adventure").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(7);
            category.setTitle("Olahraga & Petualangan");
            category.setThumbnail(R.drawable.adventure);
            itemCategory.add(category);
        }

        if (sm.getPreferences("maternity").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(8);
            category.setTitle("Ibu & Anak");
            category.setThumbnail(R.drawable.maternity);
            itemCategory.add(category);
        }

        if (sm.getPreferences("electronic").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(9);
            category.setTitle("Elektronik");
            category.setThumbnail(R.drawable.elektronik);
            itemCategory.add(category);
        }

        if (sm.getPreferences("bicycle").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(10);
            category.setTitle("Sepeda");
            category.setThumbnail(R.drawable.sepeda);
            itemCategory.add(category);
        }

        if (sm.getPreferences("office").equals("true")) {
            category = new ItemCategoryModul();
            category.setId(11);
            category.setTitle("Peralatan Kantor");
            category.setThumbnail(R.drawable.office_equipmen);
            itemCategory.add(category);
        }

//

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
        private TextView title;
        private ImageView imgThumbnail;
        private CardView cardDetAset;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_c_title);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.item_c_img);
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
        viewHolder.cardDetAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idc.matches("1|2|3|4|5|6|7|8|9|10|11")) {
                    Intent iAset = new Intent(context, AsetListActivity.class);
                    iAset.putExtra("id_category", idc);
                    iAset.putExtra("name_category", cname);
                    iAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iAset);
                } else{
                    Snackbar snackbar = Snackbar.make(v, "Oops, Sorry ! This Feature Will Online Soon", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}
