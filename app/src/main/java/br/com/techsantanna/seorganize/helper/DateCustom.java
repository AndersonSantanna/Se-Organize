package br.com.techsantanna.seorganize.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public static String mesAno(String data){
        String retorno[] = data.split("/");
        String mes = retorno[1];
        String ano = retorno[2];
        return mes + ano;
    }
}
