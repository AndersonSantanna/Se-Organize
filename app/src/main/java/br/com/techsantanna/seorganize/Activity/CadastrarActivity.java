package br.com.techsantanna.seorganize.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.techsantanna.seorganize.R;
import br.com.techsantanna.seorganize.config.ConfiguraçãoFirebase;
import br.com.techsantanna.seorganize.helper.Base64Custom;
import br.com.techsantanna.seorganize.model.Usuario;

public class CadastrarActivity extends AppCompatActivity {
    private EditText nome, email, senha;
    private Button cadastrar;
    private FirebaseAuth auth;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        nome = findViewById(R.id.editTextNome);
        email = findViewById(R.id.editTextEmail);
        senha = findViewById(R.id.editTextSenha);
        cadastrar = findViewById(R.id.buttonCadastrar);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nome.getText().toString().isEmpty() ){
                    if(!email.getText().toString().isEmpty()){
                        if (!senha.getText().toString().isEmpty()){
                            usuario = new Usuario(nome.getText().toString(), email.getText().toString(), senha.getText().toString());
                            auth= ConfiguraçãoFirebase.getFirebaseAuth();
                            auth.createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                usuario.setIdUsuario(Base64Custom.CodificarBase64(usuario.getEmail()));
                                                usuario.salvar();
                                                finish();
                                                Toast.makeText(getApplicationContext(), "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                            }else {
                                                try {
                                                    throw task.getException();
                                                }catch (FirebaseAuthWeakPasswordException e){
                                                    alerta(R.drawable.ic_warning_black_24dp, "Atenção", "Sua senha é muito fraca");
                                                }catch (FirebaseAuthEmailException e){
                                                    alerta(R.drawable.ic_warning_black_24dp, "Atenção","Esse email nao exite");
                                                }catch (FirebaseAuthUserCollisionException e){
                                                    alerta(R.drawable.ic_warning_black_24dp, "Atenção","Já existe uma conta com esse email");
                                                }catch (Exception e){
                                                    alerta(R.drawable.ic_warning_black_24dp, "Atenção","Erro ao cadastrar Usuario");
                                                }
                                            }

                                        }
                                    });
                        }else
                            Toast.makeText(getApplicationContext(), "Preencha o campo senha", Toast.LENGTH_SHORT).show();

                    }else
                        Toast.makeText(getApplicationContext(), "Preencha o campo email", Toast.LENGTH_SHORT).show();

                }else
                    Toast.makeText(getApplicationContext(), "Preencha o campo nome", Toast.LENGTH_SHORT).show();

            }
        });

    }
    public void alerta(int icone, String titulo, String mensagem){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setMessage(mensagem);
        alert.setIcon(icone);
        alert.setPositiveButton("Okay", null);
        alert.create();
        alert.show();
    }
}
