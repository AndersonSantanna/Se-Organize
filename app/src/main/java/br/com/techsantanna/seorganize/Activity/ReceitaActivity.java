package br.com.techsantanna.seorganize.Activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.techsantanna.seorganize.R;
import br.com.techsantanna.seorganize.config.ConfiguraçãoFirebase;
import br.com.techsantanna.seorganize.helper.Base64Custom;
import br.com.techsantanna.seorganize.helper.DateCustom;
import br.com.techsantanna.seorganize.model.Movimentacao;
import br.com.techsantanna.seorganize.model.Usuario;

public class ReceitaActivity extends AppCompatActivity {
    private EditText valor;
    private TextInputEditText data, categoria, descricao;
    private Movimentacao movimentacao;
    private DatabaseReference reference = ConfiguraçãoFirebase.getFireBaseDatabase();
    private FirebaseAuth auth = ConfiguraçãoFirebase.getFirebaseAuth();
    private Double receitaTotal, receitaGerada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);

        valor = findViewById(R.id.EditValorReceita);
        data = findViewById(R.id.EditDataReceita);
        categoria = findViewById(R.id.EditCategoriaReceita);
        descricao = findViewById(R.id.EditDescricaoReceita);

        data.setText(DateCustom.dataAtual());
        recuperarReceita();
    }

    public void salvarReceita(View view){
        if (validarCamposReceita()) {
            movimentacao = new Movimentacao(DateCustom.mesAno(data.getText().toString()), categoria.getText().toString(), descricao.getText().toString(), "receita", Double.parseDouble(valor.getText().toString()), data.getText().toString());

            receitaGerada = Double.parseDouble(valor.getText().toString());
            Double result = receitaTotal + receitaGerada;
            atualizarReceita(result);

            movimentacao.salvar(data.getText().toString());
            finish();
        }
    }
    public Boolean validarCamposReceita(){
        if(!valor.getText().toString().isEmpty() && !data.getText().toString().isEmpty() && !categoria.getText().toString().isEmpty() && !descricao.getText().toString().isEmpty()){
            return true;
        }else {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT);
            return false;
        }
    }

    public void recuperarReceita(){
        final DatabaseReference usarioRef = reference.child("usuarios").child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail()));
        usarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void atualizarReceita(Double despesa){
        reference.child("usuarios").child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail())).child("receitaTotal").setValue(despesa);
    }
}
