package br.com.techsantanna.seorganize.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.techsantanna.seorganize.Adapter.AdapterMovimentacao;
import br.com.techsantanna.seorganize.R;
import br.com.techsantanna.seorganize.config.ConfiguraçãoFirebase;
import br.com.techsantanna.seorganize.helper.Base64Custom;
import br.com.techsantanna.seorganize.model.Movimentacao;
import br.com.techsantanna.seorganize.model.Usuario;

public class PrincipalActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private TextView saudacao, saldo;
    private FirebaseAuth auth = ConfiguraçãoFirebase.getFirebaseAuth();
    private DatabaseReference reference = ConfiguraçãoFirebase.getFireBaseDatabase();
    private Double saldoUsuario = 0.0;
    private Double receitaTotal = 0.0;
    private Double despesaTotal = 0.0;
    private LinearLayout linearLayout;
    private DatabaseReference usuarioRef;
    private RecyclerView recyclerView;
    private AdapterMovimentacao adaptermovimentacao;
    private List<Movimentacao> list = new ArrayList<>();
    private DatabaseReference movimentacaoRef;
    private Movimentacao movimentacao;
    private String mesAnoSelecionado;
    private ValueEventListener eventListener;
    private ValueEventListener eventListenerMovimentacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Se Organize");
        setSupportActionBar(toolbar);

        //Recuperando id's
        saldo = findViewById(R.id.textViewSaldo);
        saudacao = findViewById(R.id.textViewSaudacao);
        calendarView = findViewById(R.id.calendarView);
        linearLayout = findViewById(R.id.LinearResultado);
        recyclerView = findViewById(R.id.RecyclerView);

        //configurar adapter
        adaptermovimentacao = new AdapterMovimentacao(list, this);
        //configurar recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adaptermovimentacao);

        //Configuração do calendario
        CalendarDay data = calendarView.getCurrentDate();

        //formataçoes dos valores
        String mesSelecionado = String.format("%02d", (data.getMonth() + 1));
        mesAnoSelecionado = String.valueOf( mesSelecionado + "" + data.getYear());

        //recuperando mes em que o usuario esta
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1));
                mesAnoSelecionado = String.valueOf( mesSelecionado + "" + date.getYear());
                movimentacaoRef.removeEventListener(eventListenerMovimentacao);
                recuperarMovimentacoes();
            }
        });
        swipe();

    }
    /**Swipe metodo de deslizar item na tela, no caso do recycler view*/
    public void swipe(){

        ItemTouchHelper.Callback item = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                return makeMovementFlags(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder);
            }
        };
        //Instancia o item touch e referencia o recycler view
        new ItemTouchHelper(item).attachToRecyclerView(recyclerView);
    }
    /**Metodo de excluir item que chama um alert Dialog */
    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder){
         new AlertDialog.Builder(this)
                .setTitle("Excluir Movimentação da Conta")
                .setMessage("Você tem certeza que deseja realmente excluir essa movimentação ?")
                .setCancelable(false)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**Ao deslizar para excluir ele verefica o item no banco*/
                        movimentacaoRef = reference.child("movimentação")
                                .child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail()))
                                .child(mesAnoSelecionado);
                        /**recupera a movimentação*/
                        movimentacao = list.get(viewHolder.getAdapterPosition());

                        /**Com base na key do usuario exclui o item do banco de dados*/
                        movimentacaoRef.child(movimentacao.getKey()).removeValue();
                        /**notifica o Adapter (Tela do recycler View)*/
                        adaptermovimentacao.notifyItemRemoved(viewHolder.getAdapterPosition());
                        usuarioRef = reference.child("usuarios").child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail()));

                        if (movimentacao.getTipo().equals("receita")){
                            receitaTotal = receitaTotal -  movimentacao.getValor();
                            usuarioRef.child("receitaTotal").setValue(receitaTotal);
                        }else if (movimentacao.getTipo().equals("despesa")){
                            despesaTotal = despesaTotal -  movimentacao.getValor();
                            usuarioRef.child("despesaTotal").setValue(despesaTotal);

                        }
                        Toast.makeText(getApplicationContext(), "Item excluido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**Cancela a operação trazendo o item de volta pra tela*/
                        Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                        adaptermovimentacao.notifyDataSetChanged();
                    }
                }).create().show();

    }
    /**Botão receita*/
    public void addReceita(View view){
        startActivity(new Intent(PrincipalActivity.this, ReceitaActivity.class));
    }
    /**Botão despesa*/
    public void addDespesa(View view){
        startActivity(new Intent(PrincipalActivity.this, DespesasActivity.class));
    }
    /**Botão Relatorio*/
    public void addRelatorio(View view){
        Toast.makeText(getApplicationContext(), "Em desenvolvimento...", Toast.LENGTH_SHORT).show();
    }

    /**Tratando as opções do menu, no caso do menur sair*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sair){
            auth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    /**Cria um menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**Recupera as Movimentações do usario do BD do firebase */
    public void recuperarMovimentacoes(){
        movimentacaoRef = reference.child("movimentação").child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail())).child(mesAnoSelecionado);
        eventListenerMovimentacao = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Movimentacao mov = dados.getValue(Movimentacao.class);
                    mov.setKey(dados.getKey());
                    //Adiciona lista ao recycler view
                    list.add(mov);
                }
                //Atualiza tela
                adaptermovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    /**Recupera o Saldo do usuario do BD do firebase*/
    public void recuperarSaldo(){
        usuarioRef = reference.child("usuarios").child(Base64Custom.CodificarBase64(auth.getCurrentUser().getEmail()));
        eventListener = usuarioRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                /*Calculando saldo*/
                receitaTotal = usuario.getReceitaTotal();
                despesaTotal=  usuario.getDespesaTotal();
                saldoUsuario = receitaTotal - despesaTotal;
                //Se for negativo cor de fundo fica vermelho
                if (saldoUsuario < 0 ){
                    linearLayout.setBackgroundColor(Color.parseColor("#E75043"));
                }
                //Formataçao na tela principal
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String string = decimalFormat.format(saldoUsuario).toString();
                saudacao.setText("Olá, ".concat(usuario.getNome()));
                saldo.setText("R$ " + string);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /**Ao iniciar recupera o saldo e a movimentações*/
    @Override
    protected void onStart() {
        super.onStart();
        recuperarSaldo();
        recuperarMovimentacoes();
    }
    /**Ao parar o app retira os Event listenner*/
    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(eventListener);
        movimentacaoRef.removeEventListener(eventListenerMovimentacao);
    }
}
