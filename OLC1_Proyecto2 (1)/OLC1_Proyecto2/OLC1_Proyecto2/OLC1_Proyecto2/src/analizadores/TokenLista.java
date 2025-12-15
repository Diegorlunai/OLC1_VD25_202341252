package analizadores;

import java.util.LinkedList;

public class TokenLista {
    
    // Clase interna para guardar la info de cada token
    public static class TokenDato {
        public Tokens tipo;
        public String lexema;
        public int linea;
        public int columna;

        public TokenDato(Tokens tipo, String lexema, int linea, int columna) {
            this.tipo = tipo;
            this.lexema = lexema;
            this.linea = linea;
            this.columna = columna;
        }
    }

    // Lista estática para guardar los tokens encontrados
    public static LinkedList<TokenDato> lista = new LinkedList<>();

    // Método para agregar un token a la lista
    public static void guardar(Tokens tipo, String lexema, int linea, int col) {
        lista.add(new TokenDato(tipo, lexema, linea, col));
    }

    // Método para limpiar la lista antes de una nueva compilación
    public static void limpiar() {
        lista.clear();
    }
}