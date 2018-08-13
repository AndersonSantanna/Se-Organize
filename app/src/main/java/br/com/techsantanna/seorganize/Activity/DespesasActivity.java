package br.com.techsantanna.seorganize.Activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText data, categoria, descricao;
    private EditText valor;
    private Movimentacao movimentacao;
    private DatabaseReference reference = ConfiguraçãoFirebase.getFireBaseDatabase();
    private FirebaseAuth auth = ConfiguraçãoFirebase.getFirebaseAuth();
    private Double despesaTotal, despesaGerada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        //recupera id's
        valor = findViewById(R.id.editValor);
        data = findViewById(R.id.editData);
        categoria = findViewById(R.id.editCategoria);
        descricao = findViewById(R.id.editDescricao);

        //Recupera o dia atual e recupera a despesa total para somar com a atual
        data.setText(DateCustom.dataAtual());
        recuperarDespesa();
    }

    /**Botão de salvar as despesas registradas*/
    public void confirmDespesa(View view){
        if(validarCamposDespesa()) {
            movimentacao = new Movimentacao(data.getText().toString(), categoria.getText().toString(), descricao.getText().toString(), "despesa", Double.parseDouble(valor.getText().toString()), data.getText().toString());
            //Somando despesas do usuario
            despesaGerada = Double.parseDouble(valor.getText().toString());
            Double despesa = despesaTotal + despesaGerada;
            atualizarDespesa(despesa);
            //salvando alterações e voltando para tela principal
            movimentacao.salvar(data.getText().toString());
            finish();

        }
    }
    /**Verificador de campos estão preenchidos*/
    public Boolean validarCamposDespesa(){
        if(!valor.getText().toString().isEmpty() && !data.getText().toString().isEmpty() && !categoria.getText().toString().isEmpty() && !descricao.getText().toString().isEmpty()){
            return true;
        }else {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT);
            return false;
        }
    }

    /**Recuper o total de despesas do usuario*/
    public void recuperarDespesa(){
        final DatabaseReference usarioRef = reference.child("usuarios").child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail()));
        usarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /**Atualiza valor da despesa total*/
    public void atualizarDespesa(Double despesa){
        reference.child("usuarios").child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail())).child("despesaTotal").setValue(despesa);
    }
}
