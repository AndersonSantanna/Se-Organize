package br.com.techsantanna.seorganize.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import br.com.techsantanna.seorganize.R;
import br.com.techsantanna.seorganize.config.ConfiguraçãoFirebase;
import br.com.techsantanna.seorganize.model.Usuario;

public class LoginActivity extends AppCompatActivity {
    private EditText email, senha;
    private Button login;
    private Usuario usuario;
    private FirebaseAuth auth;
    private String string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editTextInEmail);
        senha = findViewById(R.id.editTextInPassword);
        login = findViewById(R.id.buttonLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!email.getText().toString().isEmpty()){
                    if (!senha.getText().toString().isEmpty()){
                        usuario = new Usuario();
                        usuario.setEmail(email.getText().toString());
                        usuario.setSenha(senha.getText().toString());
                        validarLogin();
                    }else {
                        Toast.makeText(getApplicationContext(), "Preencha o campo senha", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Preencha o campo senha", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void validarLogin(){
        auth = ConfiguraçãoFirebase.getFirebaseAuth();
        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        string = "E-mail e senha não correspodem!";
                    }catch (FirebaseAuthInvalidUserException e){
                        string = "Usuário não está cadastrado";
                    }catch (Exception e ){
                        string = "Erro ao fazer login";
                    }
                    Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
