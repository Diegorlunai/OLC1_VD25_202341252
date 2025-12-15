package analizadores;

public enum Tokens {
    // Tipos de dato
    INT, DOUBLE, BOOL, CHAR, STRING,
    
    // Declaración
    VAR,
    
    // Sentencias de control
    IF, ELSE, SWITCH, CASE, DEFAULT,
    
    // Ciclos
    WHILE, DO, FOR,
    
    // Transferencia
    BREAK, CONTINUE, RETURN,
    
    // Valores booleanos
    TRUE, FALSE,
    
    // Función nativa
    PRINT, START, 
    
    // Operadores aritméticos
    SUMA,       // +
    RESTA,      // -
    MULT,       // *
    DIV,        // /
    POT,        // **
    MOD,        // %
    
    // Operadores relacionales
    IGUAL,      // ==
    DIF,        // !=
    MENOR,      // <
    MENORIGUAL, // <=
    MAYOR,      // >
    MAYORIGUAL, // >=
    
    // Operadores lógicos
    AND,        // &&
    OR,         // ||
    NOT,        // !
    XOR,        // ^
    
    // Asignación e incremento
    ASIGN,      // =
    INCREMENTO, // ++
    DECREMENTO, // --
    
    // Símbolos
    PAR_A,      // (
    PAR_C,      // )
    LLAVE_A,    // {
    LLAVE_C,    // }
    COR_IZQ,    // [
    COR_DER,    // ]
    PUNTO_COMA, // ;
    DOS_PUNTOS, // :
    COMA,       // ,
    PUNTO,      // .
    
    // Literales
    ID,
    ENTERO,
    DECIMAL,
    LIT_CHAR,   // 'a'
    LIT_STRING, // "hola"
    
    // Vectores y Listas (Fase 2)
    NEW, LIST, APPEND,
    
    // Otros
    ERROR
}