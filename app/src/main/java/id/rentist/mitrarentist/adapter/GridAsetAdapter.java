package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
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

/**
 * Created by mdhif on 17/09/2017.
 */

public class GridAsetAdapter extends RecyclerView.Adapter<GridAsetAdapter.ViewHolder>  {
    private List<ItemCategoryModul> itemCategory;
    private Context context;
    private static final String TAG = "CategoryAdapter";

    public GridAsetAdapter(Context context){
        super();
        this.context = context;
        itemCategory = new ArrayList<ItemCategoryModul>();
        ItemCategoryModul category;

        category = new ItemCategoryModul();
        category.setId(1);
        category.setTitle("Mobil");
        category.setThumbnail(R.drawable.suv);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(2);
        category.setTitle("Motor");
        category.setThumbnail(R.drawable.motorbiking);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(3);
        category.setTitle("Yacht");
        category.setThumbnail(R.drawable.leisure_1);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(4);
        category.setTitle("Medical");
        category.setThumbnail(R.drawable.hospital);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(5);
        category.setTitle("Fotografi");
        category.setThumbnail(R.drawable.photo_camera);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(6);
        category.setTitle("Mainan Anak");
        category.setThumbnail(R.drawable.rocking_horse);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(7);
        category.setTitle("Water Sport");
        category.setThumbnail(R.drawable.surf);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(8);
        category.setTitle("Maternity");
        category.setThumbnail(R.drawable.breast_pump);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(9);
        category.setTitle("Elektronik");
        category.setThumbnail(R.drawable.printer);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(10);
        category.setTitle("Sepeda");
        category.setThumbnail(R.drawable.cycle);
        itemCategory.add(category);

        category = new ItemCategoryModul();
        category.setId(11);
        category.setTitle("Kantor");
        category.setThumbnail(R.drawable.office_material);
        itemCategory.add(category);

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
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ItemCategoryModul ac = itemCategory.get(i);
        final String idc = String.valueOf(ac.getId());

//        simpan value dalam object
        viewHolder.title.setText(ac.getTitle());
        viewHolder.imgThumbnail.setImageResource(ac.getThumbnail());
        viewHolder.cardDetAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAset = new Intent(context, AsetListActivity.class);
                iAset.putExtra("id_category", idc);
                iAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iAset);
            }
        });
    }
}
