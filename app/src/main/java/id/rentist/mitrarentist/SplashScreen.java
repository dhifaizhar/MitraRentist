package id.rentist.mitrarentist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import id.rentist.mitrarentist.tools.SessionManager;

public class SplashScreen extends Activity {
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_splash_screen);
        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(3000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    if(sm.getPreferences("status").isEmpty()){
                        if(sm.getAppPreferences("APP_ONBOARDVIEW").isEmpty()){
                            startActivity(new Intent(SplashScreen.this, OnBoardActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(SplashScreen.this, DashboardActivity.class));
                            finish();
                        }
                    }else{
                        startActivity(new Intent(SplashScreen.this, DashboardActivity.class));
                        finish();
                    }
                }
            }
        };
    thread.start();
    }
}
