package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    public Button mEmailSignInButton;
    public TextView forgotPassword;

    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private SessionManager sm;
    private ProgressDialog pDialog;
    private JSONObject userObject, responseMessage;
    private String user;

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_READ_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mLoginFormView = findViewById(R.id.login_form);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);

        //Perform SIGN IN authentication
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        //Perform FORGOT PASSWORD
        forgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Ingin keluar dari aplikasi ?");

        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        AlertDialog alertDialog = showAlert.create();
        alertDialog.show();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        View focusView;

        // Check for a valid email & password, if the user entered one.
        if (isEmailValid(email) && isPasswordValid(password)) {
            pDialog.setMessage("authentication request ...");
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
        } else if(TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            if(TextUtils.isEmpty(password)){
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                focusView.requestFocus();
            } else if(TextUtils.isEmpty(email)){
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                focusView.requestFocus();
            }
            Toast.makeText(getApplicationContext(),"Tidak dapat memproses email atau password yang kosong", Toast.LENGTH_LONG).show();
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            focusView.requestFocus();
            Toast.makeText(getApplicationContext(),"Masukan keliru pada email", Toast.LENGTH_LONG).show();
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            focusView.requestFocus();
            Toast.makeText(getApplicationContext(),"Masukan keliru pada password", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic

        boolean result = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email.matches(emailPattern) && email.length() > 0){
            result = true;
        }

        return result;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic

        boolean result = false;

        if(password.length() > 4){
            result = true;
        }

        return result;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if(show){
            mLoginFormView.setVisibility(View.GONE);
            if (!pDialog.isShowing()){
                pDialog.show();
            }
        }else{
            mLoginFormView.setVisibility(View.VISIBLE);
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    }

    // start auto complete action : not use
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,

                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
    //end auto complete action

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private class UserLoginTask extends AsyncTask<String, String, String> {

        private final String mEmail;
        private final String mPassword;
        String sEmail, sNamaRent, sNamaPem, sTelp, sAlamat, sStat;
        Integer sId, sIdTenant, sImg;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    String errorMsg;

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int errorCode = jsonObject.getInt("code");
                        String data = jsonObject.getString("data");
                        if(errorCode == 200 && !Objects.equals(data, "false")){
                            Log.e(TAG, "Login Status : Sukses");
                            errorMsg = "";
                            user = String.valueOf(jsonObject.getJSONObject("data"));
                        }else {
                            showProgress(false);
                            user = null;
                            responseMessage = jsonObject.getJSONObject("reponse");
                            errorMsg = responseMessage.getString("message");
                            mPasswordView.setError("Masukan kembali kata kunci");
                            mPasswordView.requestFocus();
                            Toast.makeText(getApplicationContext(),R.string.error_incorrect_password, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Login Error : " + errorMsg);
                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        errorMsg = "";
                        showProgress(false);
                        user = null;
                        mEmailView.setError("Masukan kembali data anda!");
                        mEmailView.requestFocus();
                        Toast.makeText(getApplicationContext(),"Alamat email dan kata kunci tidak cocok", Toast.LENGTH_LONG).show();
                    }

                    Log.d(TAG, "Login Error Response : " + errorMsg);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error : " + error.toString());
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    // Posting parameters to login url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("email", mEmail);
                    keys.put("password", mPassword);
                    return keys;
                }
            };

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return user;
        }

        @Override
        protected void onPostExecute(String user) {
            mAuthTask = null;
            showProgress(false);

            if (user != null) {
                try {
                    userObject = new JSONObject(user);
                    Log.d(TAG, String.valueOf(userObject));

                    sId = userObject.getInt("id");
                    sIdTenant = userObject.getInt("id_tenant");
                    sEmail = userObject.getString("email");
                    sNamaRent = userObject.getString("name");
                    sNamaPem = "ABDI NEGARA";
                    sAlamat = "Gg. Pusaka Kuta, Kabupaten Badung, Bali 80361";
                    sTelp = "081 1232 3144";
                    sImg = R.drawable.user_ava_man;
                    sStat = "1";

                    sm.setIntPreferences("id", sId);
                    sm.setIntPreferences("id_tenant", sIdTenant);
                    sm.setPreferences("email", sEmail);
                    sm.setPreferences("nama_rental", sNamaRent);
                    sm.setPreferences("nama_pemilik", sNamaPem);
                    sm.setPreferences("alamat", sAlamat);
                    sm.setPreferences("telepon", sTelp);
                    sm.setIntPreferences("foto_profil", sImg);
                    sm.setPreferences("status",sStat);

                    if(sStat.equals("1")){
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(LoginActivity.this, AktivasiActivity.class));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSON Error : " + e);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
}

