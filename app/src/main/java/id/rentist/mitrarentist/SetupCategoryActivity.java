package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import id.rentist.mitrarentist.tools.SessionManager;

public class SetupCategoryActivity extends AppCompatActivity {
    private SessionManager sm;
    Switch car, motorcycle, yacht, medic, photo, toys, adventure, maternity, electronic,
            bicycle, office, fashion;
    Button btnApply;

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

        TextView tCar = (TextView) findViewById(R.id.title_car);
        TextView tMotor = (TextView) findViewById(R.id.title_motor);
        TextView tYacht = (TextView) findViewById(R.id.title_yacht);
        TextView tMedic = (TextView) findViewById(R.id.title_medic);
        TextView tPhoto = (TextView) findViewById(R.id.title_photo);
        TextView tToys = (TextView) findViewById(R.id.title_toys);
        TextView tAdventure = (TextView) findViewById(R.id.title_adventure);
        TextView tMaternity = (TextView) findViewById(R.id.title_maternity);
        TextView tElectronic = (TextView) findViewById(R.id.title_electronic);
        TextView tOffice = (TextView) findViewById(R.id.title_office);
        TextView tBicycle = (TextView) findViewById(R.id.title_bicycle);
        TextView tFashion = (TextView) findViewById(R.id.title_fashion);

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
        fashion  = (Switch) findViewById(R.id.sw_fashion);
        btnApply= (Button) findViewById(R.id.btn_apply);

        String[] asset_category = getResources().getStringArray(R.array.asset_category_entries);
        tCar.setText(asset_category[0] + " (" + sm.getIntPreferences("sum_car") + ")");
        tMotor.setText(asset_category[1] + " (" + sm.getIntPreferences("sum_motor") + ")");
        tYacht.setText(asset_category[2] + " (" + sm.getIntPreferences("sum_yacht") + ")");
        tMedic.setText(asset_category[3] + " (" + sm.getIntPreferences("sum_medic") + ")");
        tPhoto.setText(asset_category[4] + " (" + sm.getIntPreferences("sum_photography") + ")");
        tToys.setText(asset_category[5] + " (" + sm.getIntPreferences("sum_toys") + ")");
        tAdventure.setText(asset_category[6] + " (" + sm.getIntPreferences("sum_adventure") + ")");
        tMaternity.setText(asset_category[7] + " (" + sm.getIntPreferences("sum_maternity") + ")");
        tElectronic.setText(asset_category[8] + " (" + sm.getIntPreferences("sum_electronic") + ")");
        tBicycle.setText(asset_category[9] + " (" + sm.getIntPreferences("sum_bicycle") + ")");
        tOffice.setText(asset_category[10] + " (" + sm.getIntPreferences("sum_office") + ")");
        tFashion.setText(asset_category[11] + " (" + sm.getIntPreferences("sum_fashion") + ")");

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
        if (sm.getPreferences("fashion").equals("true")){fashion.setChecked(true);}

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (car.isChecked())sm.setPreferences("car","true");else sm.setPreferences("car","false");
                if (motorcycle.isChecked())sm.setPreferences("motorcycle","true");else sm.setPreferences("motorcycle","false");
                if (yacht.isChecked())sm.setPreferences("yacht","true");else sm.setPreferences("yacht","false");
                if (medic.isChecked())sm.setPreferences("medical_equipment","true");else sm.setPreferences("medical_equipment","false");
                if (photo.isChecked())sm.setPreferences("photography","true");else sm.setPreferences("photography","false");
                if (toys.isChecked())sm.setPreferences("toys","true");else sm.setPreferences("toys","false");
                if (adventure.isChecked())sm.setPreferences("adventure","true");else sm.setPreferences("adventure","false");
                if (maternity.isChecked())sm.setPreferences("maternity","true");else sm.setPreferences("maternity","false");
                if (electronic.isChecked())sm.setPreferences("electronic","true");else sm.setPreferences("electronic","false");
                if (bicycle.isChecked())sm.setPreferences("bicycle","true");else sm.setPreferences("bicycle","false");
                if (office.isChecked())sm.setPreferences("office","true");else sm.setPreferences("office","false");
                if (fashion.isChecked())sm.setPreferences("fashion","true");else sm.setPreferences("fashion","false");

                int sum_cat = 0;
                if (sm.getPreferences("car").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("motorcycle").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("yacht").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("medical_equipment").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("photography").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("toys").equals("true")){ sum_cat += 1;}
                if (sm.getPreferences("adventure").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("maternity").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("electronic").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("bicycle").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("office").equals("true")){sum_cat += 1;}
                if (sm.getPreferences("fashion").equals("true")){sum_cat += 1;}

                sm.setIntPreferences("sum_cat", sum_cat);
                Intent intent = new Intent(SetupCategoryActivity.this,AsetActivity.class);
                setResult(RESULT_OK, intent);
                finish();
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
