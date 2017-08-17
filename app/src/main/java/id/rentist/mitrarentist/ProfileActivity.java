package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import id.rentist.mitrarentist.tools.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private SessionManager sm;
    TextView rName, rOwner, rAddress, rEmail, rPhone, vAll;
    ImageView profilePhoto;
    Button btnEdit;

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
        btnEdit = (Button) findViewById(R.id.btn_edit_profil);
        vAll = (TextView) findViewById(R.id.view_testi);

        // set content control value
//        rName.setText(sm.getPreferences("nama_rental"));
        rOwner.setText(sm.getPreferences("nama"));
        rAddress.setText(sm.getPreferences("alamat"));
        rPhone.setText(sm.getPreferences("telepon"));
        rEmail.setText(sm.getPreferences("email"));
        profilePhoto.setImageResource(sm.getIntPreferences("foto_profil"));

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iEditRent = new Intent(ProfileActivity.this, FormEditProfilActivity.class);
                startActivity(iEditRent);
            }
        });

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
}
