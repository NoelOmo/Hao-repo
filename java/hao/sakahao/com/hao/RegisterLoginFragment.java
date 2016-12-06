package hao.sakahao.com.hao;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

public class RegisterLoginFragment extends Fragment {
    Button btnReg;
    Button btnSignIn;
    Button btnTest;
    String email;
    EditText loginEmail;
    EditText loginPassword;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener;
    String password;
    ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_login, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Just a minute :D");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mAuth = FirebaseAuth.getInstance();
        loginEmail = (EditText) view.findViewById(R.id.loginEmail);
        loginPassword = (EditText) view.findViewById(R.id.loginPassword);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };


        btnReg = (Button) view.findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        });
        this.btnSignIn = (Button) view.findViewById(R.id.btnLogin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(TextUtils.isEmpty(loginEmail.getText().toString()) || TextUtils.isEmpty(loginPassword.getText().toString())){
                        Toast.makeText(getContext(), "Password and email are both required", Toast.LENGTH_LONG).show();
                    }else{
                        progressDialog.show();
                        loginMethod(loginEmail.getText().toString().trim(), loginPassword.getText().toString().trim());
                    }

                }

        });
        return view;
    }

    public void openSignUpActivity() {
        getFragmentManager().beginTransaction().replace(R.id.fragmentRegisterLogin, new Register()).commit();
    }

    public void loginMethod(String email, String password) {
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()){

                            if(task.isSuccessful()){
                                Intent i = new Intent(getActivity(), MapsActivity.class);
                                startActivity(i);
                            }else{
                                //Toast.makeText(getActivity(), "User does not exist please try again",Toast.LENGTH_LONG).show();
                                Toast.makeText(getContext(), "Authentication failed:" +
                                        task.getException(), Toast.LENGTH_LONG).show();
                                Log.d("Network", String.valueOf(task.getException()));

                            }

                        }else{
                            Toast.makeText(getActivity(), "We encountered a problem while trying to log you in, check your details and try again",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.hide();
                    }
                });
    }

    public void onStart() {
        super.onStart();
        this.mAuth.addAuthStateListener(this.mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (this.mAuthListener != null) {
            this.mAuth.removeAuthStateListener(this.mAuthListener);
        }
    }
}
