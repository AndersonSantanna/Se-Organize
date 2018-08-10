package br.com.techsantanna.seorganize.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguraçãoFirebase {
    private static FirebaseAuth auth;
    private static DatabaseReference reference;

    public static DatabaseReference getFireBaseDatabase(){
        if (reference == null){
            reference = FirebaseDatabase.getInstance().getReference();
        }
        return reference;
    }
    public static FirebaseAuth getFirebaseAuth(){
        if(auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

}
