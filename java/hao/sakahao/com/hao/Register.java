package hao.sakahao.com.hao;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 *
 */

public class Register extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText suName;
    EditText suEmail;
    EditText suPassword;
    ProgressDialog progressDialog;

    public Register() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        suName = (EditText) view.findViewById(R.id.suName);
        suEmail = (EditText) view.findViewById(R.id.suEmail);
        suPassword = (EditText) view.findViewById(R.id.suPassword);
        Button btnDone = (Button)view.findViewById(R.id.btnDone);
        progressDialog  = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Any moment now :)");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(suEmail.getText().toString()) || TextUtils.isEmpty(suPassword.getText().toString())){
                    Toast.makeText(getContext(),"Email and Password fields are required",Toast.LENGTH_LONG).show();

                }else{
                    signUpMethod(suEmail.getText().toString().trim(), suPassword.getText().toString().trim());
                    progressDialog.show();
                }

            }
        });





        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser fuUser = firebaseAuth.getCurrentUser();

                if (fuUser != null){

                }else{

                }
            }
        };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void signUpMethod(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Authentication failed. Please try again",Toast.LENGTH_LONG).show();
                            suName.setText("");
                            suEmail.setText("");
                            suPassword.setText("");
                        }else{
                            Intent i = new Intent(getActivity(), MapsActivity.class);
                            startActivity(i);
                        }
                        progressDialog.hide();
                    }
                });
    }
}
