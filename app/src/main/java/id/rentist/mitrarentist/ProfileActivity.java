package id.rentist.mitrarentist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import id.rentist.mitrarentist.tools.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private SessionManager sm;
    Intent iEditRent;
    TextView rName, rOwner, rAddress, rEmail, rPhone;
    ImageView profilePhoto;
    Button btnEdit;
    ImageButton vAll;
    String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_profile);
        setTitle(sm.getPreferences("nama_rental"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // conten call controll
        controlContent();
    }

    private void controlContent() {
        //initialize view
//        rName = (TextView) findViewById(R.id.pr_rental_name);
        rOwner = (TextView) findViewById(R.id.pr_owner_name) ;
        rAddress = (TextView) findViewById(R.id.pr_address_name);
        rPhone = (TextView) findViewById(R.id.pr_phone_number);
        rEmail = (TextView) findViewById(R.id.pr_email);
        profilePhoto = (ImageView) findViewById(R.id.pr_thumb);
        vAll = (ImageButton) findViewById(R.id.view_testi);

        // set content control value
//        rName.setText(sm.getPreferences("nama_rental"));
        rOwner.setText(sm.getPreferences("nama_pemilik"));
        rAddress.setText(sm.getPreferences("alamat"));
        rPhone.setText(sm.getPreferences("telepon"));
        rEmail.setText(sm.getPreferences("email_rental"));
//        profilePhoto.setImageResource(sm.getPreferences("foto_profil"));
        encodedImage = sm.getPreferences("foto_profil");
        if (encodedImage.equals("null")){
            profilePhoto.setImageResource(R.drawable.user_ava_man);
        } else {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profilePhoto.setImageBitmap(decodedByte);
    }

        vAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iViewTesti = new Intent(ProfileActivity.this, TestimonyActivity.class);
                startActivity(iViewTesti);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            if(sm.getPreferences("role").equals("SuperAdmin")){
                iEditRent = new Intent(ProfileActivity.this, FormEditProfilActivity.class);
                startActivity(iEditRent);
            }else{
                Toast.makeText(getApplicationContext(), "Hanya untuk Administrator",
                        Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
