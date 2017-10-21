package id.rentist.mitrarentist.tools;

import android.content.Context;

/**
 * Created by mdhif on 21/10/2017.
 */

public class FormValidation {
    private Context context;

    public FormValidation(Context context){
        this.context = context;
    }

    public boolean isEmailValid(String email) {
        boolean result = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email.matches(emailPattern) && email.length() > 0){
            result = true;
        }

        return result;
    }

    public boolean isPhoneValid(String phone) {
        boolean result = false;

        if(phone.length() > 9 && phone.length() < 14){
            result = true;
        }

        return result;
    }

    public boolean isPasswordValid(String password) {
        boolean result = false;

        if(password.length() > 4){
            result = true;
        }

        return result;
    }

    public boolean isConfirmPasswordValid(String pass, String confirm) {
        boolean result = false;

        if(pass.equals(confirm)){
            result = true;
        }

        return result;
    }
}
