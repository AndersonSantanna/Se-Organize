package br.com.techsantanna.seorganize.model;

import com.google.firebase.database.DatabaseReference;

import br.com.techsantanna.seorganize.config.ConfiguraçãoFirebase;
import br.com.techsantanna.seorganize.helper.Base64Custom;
import br.com.techsantanna.seorganize.helper.DateCustom;

public class Movimentacao {
    private String data, categoria, descricao, tipo, key, dataBD;
    private double valor;

    public Movimentacao() {
    }

    public Movimentacao(String data, String categoria, String descricao, String tipo, double valor, String dataBD) {
        this.data = data;
        this.dataBD = dataBD;
        this.categoria = categoria;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
    }
    /**Salvando alterações no BD*/
    public void salvar(String dataEscolhida){
        DatabaseReference reference = ConfiguraçãoFirebase.getFireBaseDatabase();
        reference.child("movimentação").child(Base64Custom.CodificarBase64(ConfiguraçãoFirebase.getFirebaseAuth().getCurrentUser().getEmail())).child(DateCustom.mesAno(dataEscolhida)).push().setValue(this);
    }

    public String getDataBD() {
        return dataBD;
    }

    public void setDataBD(String dataBD) {
        this.dataBD = dataBD;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
