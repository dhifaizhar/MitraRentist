package id.rentist.mitrarentist;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import id.rentist.mitrarentist.tools.SessionManager;

public class FormEditProfilActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private SessionManager sm;
    EditText rName, rOwner, rAddress, rEmail, rPhone;
    ImageView profilePhoto;
    Button btnUploadFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_form_edit_profil);
        setTitle("Edit Profil Rental");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // conten call controll
        controlContent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfileRent();
            }
        });
    }

    private void controlContent() {
        //initialize view
        rName = (EditText) findViewById(R.id.epr_rental_name);
        rOwner = (EditText) findViewById(R.id.epr_owner_name) ;
        rAddress = (EditText) findViewById(R.id.epr_address_name);
        rPhone = (EditText) findViewById(R.id.epr_telp);
        rEmail = (EditText) findViewById(R.id.epr_email);
        profilePhoto = (ImageView) findViewById(R.id.pr_thumb);
        btnUploadFoto = (Button) findViewById(R.id.btnUploadFoto);

        // set content control value
        rName.setText(sm.getPreferences("nama_rental"));
        rOwner.setText(sm.getPreferences("nama_pemilik"));
        rAddress.setText(sm.getPreferences("alamat"));
        rPhone.setText(sm.getPreferences("telepon"));
        rEmail.setText(sm.getPreferences("email"));
        profilePhoto.setImageResource(sm.getIntPreferences("foto_profil"));
        btnUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPic();
            }
        });
    }

    private void uploadPic() {
        //invoke image gallery
        Intent igetGallery = new Intent(Intent.ACTION_PICK);

        // where to find data
        File picDrectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String picDirPath = picDrectory.getPath();

        // get URI representation
        Uri data = Uri.parse(picDirPath);

        // get all image
        igetGallery.setDataAndType(data, "image/*");

        startActivityForResult(igetGallery, RESULT_LOAD_IMAGE);
    }

    private void updateProfileRent() {
        String erName, erOwner, erAddress, erEmail, erPhone;
        Resources eprofilePhoto;

        //initialize new value
        erName = rName.getText().toString();
        erOwner = rOwner.getText().toString();
        erAddress = rAddress.getText().toString();
        erPhone = rPhone.getText().toString();
        erEmail = rEmail.getText().toString();
        eprofilePhoto = profilePhoto.getResources();

        //some code for post value

        // set new preferences
        sm.setPreferences("nama_rental", erName);
        sm.setPreferences("nama_pemilik", erOwner);
        sm.setPreferences("alamat", erAddress);
        sm.setPreferences("telepon", erPhone);
        sm.setPreferences("email", erEmail);
//        sm.setIntPreferences("foto_profil", eprofilePhoto);

        Intent iEditRent = new Intent(FormEditProfilActivity.this, ProfileActivity.class);
        startActivity(iEditRent);
        Toast.makeText(getApplicationContext(),eprofilePhoto.toString(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            profilePhoto.setImageURI(selectedImage);
            Toast.makeText(getApplicationContext(),selectedImage.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
