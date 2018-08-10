package br.com.techsantanna.seorganize.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import br.com.techsantanna.seorganize.Activity.CadastrarActivity;
import br.com.techsantanna.seorganize.Activity.LoginActivity;
import br.com.techsantanna.seorganize.R;
import br.com.techsantanna.seorganize.config.ConfiguraçãoFirebase;

public class MainActivity extends IntroActivity {
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        verificadorLogin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(false);
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_1)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_2)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_3)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_4)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build());

    }
    public void entrar(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void cadastrar(View view){
        startActivity(new Intent(this, CadastrarActivity.class));
    }

    public void verificadorLogin(){
        auth = ConfiguraçãoFirebase.getFirebaseAuth();
   //     auth.signOut();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, PrincipalActivity.class));
            finish();
        }
    }


}
