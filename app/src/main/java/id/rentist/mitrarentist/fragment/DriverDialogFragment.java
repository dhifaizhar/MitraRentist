package id.rentist.mitrarentist.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import id.rentist.mitrarentist.R;

/**
 * Created by Nugroho Tri Pambud on 8/30/2017.
 */

public class DriverDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pilih Driver")
                    .setItems(R.array.driver_entries, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        }
                    });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

