package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

public class ImageZoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        PhotoView imageView = (PhotoView) findViewById(R.id.image);
        Intent imageZoom = getIntent();
        PhotoView photoView = new PhotoView(getApplicationContext());
        LinearLayout layout = (LinearLayout) findViewById(R.id.image);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL;

        setTitle(imageZoom.getStringExtra("title"));
        Picasso.with(getApplicationContext()).load(imageZoom.getStringExtra("image")).into(photoView);
        photoView.setLayoutParams(params);
        layout.addView(photoView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }
}
