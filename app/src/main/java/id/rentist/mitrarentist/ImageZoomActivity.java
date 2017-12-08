package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageZoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        Intent imageZoom = getIntent();

        setTitle(imageZoom.getStringExtra("title"));
        Picasso.with(getApplicationContext()).load(imageZoom.getStringExtra("image")).into(imageView);
    }
}
