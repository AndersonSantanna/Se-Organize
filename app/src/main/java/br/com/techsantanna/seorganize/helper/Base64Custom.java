package br.com.techsantanna.seorganize.helper;

import android.util.Base64;

public class Base64Custom {
    public static String CodificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }
    public static String DescodificarBase64(String textoCodificado){
        return String.valueOf(Base64.decode(textoCodificado, Base64.DEFAULT));
    }
}
