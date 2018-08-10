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

/*public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFireBaseDatabase();
    private FirebaseAuth autenticacao = ConfiguraçãoFirebase.getFirebaseAuth();
    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);

        //Preenche o campo data com a date atual
        campoData.setText( DateCustom.dataAtual() );
        recuperarDespesaTotal();

    }

    public void salvarDespesa(View view){

        if ( validarCamposDespesa() ){

            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao.setValor( valorRecuperado );
            movimentacao.setCategoria( campoCategoria.getText().toString() );
            movimentacao.setDescricao( campoDescricao.getText().toString() );
            movimentacao.setData( data );
            movimentacao.setTipo( "d" );

            Double despesaAtualizada = despesaTotal + valorRecuperado;
            atualizarDespesa( despesaAtualizada );

            movimentacao.salvar( data );

        }


    }

    public Boolean validarCamposDespesa(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if ( !textoValor.isEmpty() ){
            if ( !textoData.isEmpty() ){
                if ( !textoCategoria.isEmpty() ){
                    if ( !textoDescricao.isEmpty() ){
                        return true;
                    }else {
                        Toast.makeText(DespesasActivity.this,
                                "Descrição não foi preenchida!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else {
                    Toast.makeText(DespesasActivity.this,
                            "Categoria não foi preenchida!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(DespesasActivity.this,
                        "Data não foi preenchida!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(DespesasActivity.this,
                    "Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.CodificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void atualizarDespesa(Double despesa){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.CodificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.child("despesaTotal").setValue(despesa);

    }

}
*/

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

        valor = findViewById(R.id.editValor);
        data = findViewById(R.id.editData);
        categoria = findViewById(R.id.editCategoria);
        descricao = findViewById(R.id.editDescricao);

        data.setText(DateCustom.dataAtual());
        recuperarDespesa();
    }
    public void confirmDespesa(View view){
        if(validarCamposDespesa()) {
            movimentacao = new Movimentacao(data.getText().toString(), categoria.getText().toString(), descricao.getText().toString(), "despesa", Double.parseDouble(valor.getText().toString()));

            despesaGerada = Double.parseDouble(valor.getText().toString());
            Double despesa = despesaTotal + despesaGerada;
            atualizarDespesa(despesa);

            movimentacao.salvar(data.getText().toString());


        }
    }

    public Boolean validarCamposDespesa(){
        if(!valor.getText().toString().isEmpty() && !data.getText().toString().isEmpty() && !categoria.getText().toString().isEmpty() && !descricao.getText().toString().isEmpty()){
            return true;
        }else {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT);
            return false;
        }
    }

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

    public void atualizarDespesa(Double despesa){
        reference.child("usuarios").child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail())).child("despesaTotal").setValue(despesa);
    }
}
