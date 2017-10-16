package id.rentist.mitrarentist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import id.rentist.mitrarentist.tools.SessionManager;

public class SetupCategoryActivity extends AppCompatActivity {
    private SessionManager sm;
    Switch car, motorcycle, yacht, medic, photo, toys, adventure, maternity, electronic, bicycle, office;

    private static final String TAG = "SetupAssetActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Pengaturan Aset");
        sm = new SessionManager(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        car = (Switch) findViewById(R.id.sw_car);
        motorcycle = (Switch) findViewById(R.id.sw_motorcycle);
        yacht = (Switch) findViewById(R.id.sw_yacht);
        medic = (Switch) findViewById(R.id.sw_medic);
        photo = (Switch) findViewById(R.id.sw_photo);
        toys = (Switch) findViewById(R.id.sw_toys);
        adventure = (Switch) findViewById(R.id.sw_adventure);
        maternity = (Switch) findViewById(R.id.sw_maternity);
        electronic = (Switch) findViewById(R.id.sw_electronic);
        bicycle = (Switch) findViewById(R.id.sw_bicycle);
        office  = (Switch) findViewById(R.id.sw_office);

        if (sm.getPreferences("car").equals("true")){car.setChecked(true);}
        if (sm.getPreferences("motorcycle").equals("true")){motorcycle.setChecked(true);}
        if (sm.getPreferences("yacht").equals("true")){yacht.setChecked(true);}
        if (sm.getPreferences("medical_equipment").equals("true")){medic.setChecked(true);}
        if (sm.getPreferences("photography").equals("true")){photo.setChecked(true);}
        if (sm.getPreferences("toys").equals("true")){ toys.setChecked(true);}
        if (sm.getPreferences("adventure").equals("true")){adventure.setChecked(true);}
        if (sm.getPreferences("maternity").equals("true")){maternity.setChecked(true);}
        if (sm.getPreferences("electronic").equals("true")){electronic.setChecked(true);}
        if (sm.getPreferences("bicycle").equals("true")){bicycle.setChecked(true);}
        if (sm.getPreferences("office").equals("true")){office.setChecked(true);}

        car.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("car","true");
                    Toast.makeText(getApplicationContext(), "Kategori Mobil Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("car","false");
                    Toast.makeText(getApplicationContext(), "Kategori Mobil Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        motorcycle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("motorcycle","true");
                    Toast.makeText(getApplicationContext(), "Kategori Motor Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("motorcycle","false");
                    Toast.makeText(getApplicationContext(), "Kategori Motor Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        yacht.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("yacht","true");
                    Toast.makeText(getApplicationContext(), "Kategori Yacht Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("yacht","false");
                    Toast.makeText(getApplicationContext(), "Kategori Yacht Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        medic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("medical_equipment","true");
                    Toast.makeText(getApplicationContext(), "Kategori Peralatan Medis Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("medical_equipment","false");
                    Toast.makeText(getApplicationContext(), "Kategori Peralatan Medis Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        photo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("photography","true");
                    Toast.makeText(getApplicationContext(), "Kategori Fotografi Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("photography","false");
                    Toast.makeText(getApplicationContext(), "Kategori Fotografi Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        toys.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("toys","true");
                    Toast.makeText(getApplicationContext(), "Kategori Mainan Anak Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("toys","false");
                    Toast.makeText(getApplicationContext(), "Kategori Mainan Anak Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        adventure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("adventure","true");
                    Toast.makeText(getApplicationContext(), "Kategori Adventure Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("adventure","false");
                    Toast.makeText(getApplicationContext(), "Kategori Adventure Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        maternity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("maternity","true");
                    Toast.makeText(getApplicationContext(), "Kategori Maternity Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("maternity","false");
                    Toast.makeText(getApplicationContext(), "Kategori Maternity Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        electronic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("electronic","true");
                    Toast.makeText(getApplicationContext(), "Kategori Elektronik Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("electronic","false");
                    Toast.makeText(getApplicationContext(), "Kategori Elektronik Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        bicycle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("bicycle","true");
                    Toast.makeText(getApplicationContext(), "Kategori Sepeda Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("bicycle","false");
                    Toast.makeText(getApplicationContext(), "Kategori Sepeda Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });

        office.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sm.setPreferences("office","true");
                    Toast.makeText(getApplicationContext(), "Kategori Peralatan Kantor Ditampilkan", Toast.LENGTH_LONG).show();}
                else {
                    sm.setPreferences("office","false");
                    Toast.makeText(getApplicationContext(), "Kategori Peralatan Kantor Disembunyikan", Toast.LENGTH_LONG).show();}
            }
        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
