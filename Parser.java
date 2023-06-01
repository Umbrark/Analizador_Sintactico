package mx.ipn.escom.compiladores;

import java.beans.Expression;
import java.util.List;

public class Parser {

    private final List<Token> tokens;

    private final Token identificador = new Token(TipoToken.IDENTIFICADOR, "");
    private final Token coma = new Token(TipoToken.COMA, ",");
    private final Token punto = new Token(TipoToken.PUNTO, ".");
    private final Token asterisco = new Token(TipoToken.ASTERISCO, "*");
    private final Token finCadena = new Token(TipoToken.EOF, "");
    private final Token clase = new Token(TipoToken.CLASE,"class");
    private final Token fun = new Token(TipoToken.FUN, "fun");
    private final Token var = new Token(TipoToken.VAR, "var");
    private final Token ademas = new Token(TipoToken.ADEMAS, "ademas");
    private final Token falso = new Token(TipoToken,FALSO, "false");
    private final Token para = new Token(TipoToken.PARA, "for");
    private final Token si = new Token(TipoToken.SI, "if");
    private final Token nulo = new Token(TipoToken.NULO, "null");
    private final Token o = new Token(TipoToken.O, "or");
    private final Token imprimir = new Token(TipoToken.IMPRIMIR, "print");
    private final Token retornar = new Token(TipoToken.RETORNAR, "return");
    private final Token superr = new Token(TipoToken.SUPER, "super");
    private final Token este = new Token(TipoToken.ESTE, "this");
    private final Token verdadero = new Token(TipoToken.VERDADERO, "true");
    private final Token mientras = new Token(TipoToken.MIENTRAS, "while");
    private final Token cadena = new Token(TipoToken.CADENA, "string");
    private final Token numero = new Token(TipoToken.NUMERO, "number");
    private final Token abrir_parentesis = new Token(TipoToken.ABRIR_PARENTESIS, "(");
    private final Token cerrar_parentesis = new Token(TipoToken.CERRAR_PARENTESIS, ")");
    private final Token abrir_llave = new Token(TipoToken.ABRIR_LLAVE, "{");
    private final Token cerrar_llave = new Token(TipoToken.CERRAR_LLAVE, "}");
    private final Token punto_y_coma = new Token(TipoToken.PUNTO_Y_COMA, ";");
    private final Token suma = new Token(TipoToken.SUMA, "+");
    private final Token menos = new Token(TipoToken.RESTA, "-");
    private final Token diagonal = new Token(TipoToken.DIAGONAL, "/");
    private final Token diferente = new Token(TipoToken.DIFERENTE, "!");
    private final Token diferente_de = new Token(TipoToken.DIFERENTE_DE, "!=");
    private final Token asignacion = new Token(TipoToken.ASIGNACION, "=");
    private final Token igual = new Token(TipoToken.IGUAL, "==");
    private final Token menor_que = new Token(TipoToken.MENOR_QUE, "<");
    private final Token menor_igual_que = new Token(TipoToken.MENOR_IGUAL_QUE, "<=");
    private final Token mayor_que = new Token(TipoToken.MAYOR_QUE, ">");
    private final Token mayor_igual_que = new Token(TipoToken.MAYOR_IGUAL_QUE, ">=");

    private int i = 0;
    private boolean hayErrores = false;

    private Token preanalisis;

    public Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    public void parse()
    {
        i = 0;
        preanalisis = tokens.get(i);
        PROGRAM();

        if(!hayErrores && !preanalisis.equals(finCadena))
        {
            System.out.println("Error en la posición " + preanalisis.posicion + ". No se esperaba el token " + preanalisis.tipo);
        }
        else if(!hayErrores && preanalisis.equals(finCadena))
        {
            System.out.println("Consulta válida");
        }

        /*if(!preanalisis.equals(finCadena)){
            System.out.println("Error en la posición " + preanalisis.posicion + ". No se esperaba el token " + preanalisis.tipo);
        }else if(!hayErrores){
            System.out.println("Consulta válida");
        }*/
    }

    void PROGRAM(){
        if(preanalisis.equals(clase))
        {
            DECLARATION();
        }
        else
        {
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba la palabra reservada CLASE.");
        }
    }

    void DECLARATION()
    {
        if(hayErrores) return;

        if(preanalisis.equals(clase))
        {
            CLASS_DECL(); 
            DECLARATION();
        }
        else if(preanalisis.equals(fun))
        {
            FUN_DECL();
            DECLARATION();
        }
        else if(preanalisis.equals(var))
        {
            VAR_DECL();
            DECLARATION();
        }
        else if(preanalisis.equals(diferente) || preanalisis.equals(menos) || preanalisis.equals(verdadero) || preanalisis.equals(falso) ||
        preanalisis.equals(nulo) ||preanalisis.equals(este) ||preanalisis.equals(numero) ||preanalisis.equals(cadena) ||preanalisis.equals(identificador) ||
        preanalisis.equals(abrir_parentesis) ||preanalisis.equals(superr) ||preanalisis.equals(para) || preanalisis.equals(si) ||
        preanalisis.equals(imprimir) || preanalisis.equals(retornar) || preanalisis.equals(mientras) || preanalisis.equals(abrir_llave))
        {
            STATEMENT(); 
            DECLARATION();
        }
    }

    void CLASS_DECL()
    {
        if(hayErrores) return;

        if(preanalisis.equals(clase))
        {
            coincidir(clase);
            coincidir(identificador);
            CLASS_INHER();
            coincidir(abrir_llave);  
            FUNCTIONS(); 
            coincidir(cerrar_llave);
        }
        else
        {
            hayErrores = true;
            System.out.println("Error en la posición" + preanalisis.posicion);
        }
    }

    void CLASS_INHER()
    {
        if(hayErrores) return;

        if(preanalisis.equals(menor_que))
        {
            coincidir(menor_que);
            coincidir(identificador);
        }
    }

    void FUN_DECL()
    {
        if(hayErrores) return;

        if(preanalisis.equals(fun))
        {
            coincidir(fun);
            FUNCTION();
        }
    }

    void VAR_DECL()
    {
        if(hayErrores) return;

        if(preanalisis.equals(var))
        {
            coincidir(var);
            coincidir(identificador);
            VAR_INIT();
        }
        else
        {
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion);
        }
    }

    void VAR_INIT()
    {
        if(hayErrores) return;

        if(preanalisis.equals(asignacion))
        {
            coincidir(asignacion);
            EXPRESSION();
        }
    }

    void coincidir(Token t)
    {
        if(hayErrores) return;

        if(preanalisis.tipo == t.tipo)
        {
            i++;
            preanalisis = tokens.get(i);
        }
        else
        {
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba un  " + t.tipo);

        }
    }

}
