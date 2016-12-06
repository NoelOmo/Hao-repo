package hao.sakahao.com.hao;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddHaoDialog extends DialogFragment {
    String mHaoName, mHaoDescription, mHaoLocation;
    double mHaoLat, mHaoLong;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    EditText addHaoTitle, addHaoDescription;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_hao_dialog, null);
        addHaoTitle = (EditText) view.findViewById(R.id.add_hao_title);
        addHaoDescription = (EditText) view.findViewById(R.id.add_hao_description);

        builder.setView(view)
                .setPositiveButton("Add new Hao", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mHaoDescription = addHaoDescription.getText().toString();
                        mHaoName = addHaoTitle.getText().toString();
                        String key = mDatabase.child("HaoDetails").push().getKey();
                        Hao hao = new Hao(mHaoName, mHaoLocation, mHaoDescription, mHaoLat, mHaoLong);
                        Map<String, Object> postValues = hao.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put( key, postValues);
                        mDatabase.updateChildren(childUpdates);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        //mHaoName = args.getString("key1");
        mHaoLocation = args.getString("key2");
        mHaoDescription = args.getString("key3");
        mHaoLat = args.getDouble("key4");
        mHaoLong = args.getDouble("key5");
    }






}
