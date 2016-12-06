package hao.sakahao.com.hao;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Noel on 7/17/2016.
 */
public class UserDetails {

    private String uName;
    private String uID;


   public void setUpDetails(){
       FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
           @Override
           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               FirebaseUser User = firebaseAuth.getCurrentUser();
               if(User != null){
                   uID = User.getUid();
                   uName = User.getEmail();
               }

           }
       };

    }
    public String getuName(){
        return uName;
    }
    public String getuID(){
        return uID;
    }
}
