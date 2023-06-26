package mx.ipn.escom.compiladores;

import java.beans.Expression;
import java.net.CookieHandler;
import java.util.List;

public class Parser 
{

    private final List<Token> tokens;

    private final Token identificador = new Token(TipoToken.IDENTIFICADOR, "");
    private final Token coma = new Token(TipoToken.COMA, ",");
    private final Token punto = new Token(TipoToken.PUNTO, ".");
    private final Token asterisco = new Token(TipoToken.ASTERISCO, "*");
    private final Token finCadena = new Token(TipoToken.EOF, "");
    private final Token clase = new Token(TipoToken.CLASE,"clase");
    private final Token y = new Token(TipoToken.Y, "y");
    private final Token fun = new Token(TipoToken.FUN, "fun");
    private final Token var = new Token(TipoToken.VAR, "var");
    private final Token ademas = new Token(TipoToken.ADEMAS, "else");
    private final Token falso = new Token(TipoToken.FALSO, "falso");
    private final Token para = new Token(TipoToken.PARA, "para");
    private final Token si = new Token(TipoToken.SI, "if");
    private final Token nulo = new Token(TipoToken.NULO, "nulo");
    private final Token o = new Token(TipoToken.O, "o");
    private final Token imprimir = new Token(TipoToken.IMPRIMIR, "print");
    private final Token retornar = new Token(TipoToken.RETORNAR, "retornar");
    private final Token superr = new Token(TipoToken.SUPER, "superr");
    private final Token este = new Token(TipoToken.ESTE, "este");
    private final Token verdadero = new Token(TipoToken.VERDADERO, "verdadero");
    private final Token mientras = new Token(TipoToken.MIENTRAS, "mientras");
    private final Token cadena = new Token(TipoToken.CADENA, "");
    private final Token numero = new Token(TipoToken.NUMERO, "");
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

    public boolean parse()
    {
        i = 0;
        preanalisis = tokens.get(i);
        DECLARATION();

        if(!hayErrores && !preanalisis.equals(finCadena))
        {
            //System.out.println("Error en la posición " + preanalisis.posicion + ". No se esperaba el token " + preanalisis.tipo);
            System.out.println("Error. No se esperaba el token " + preanalisis.tipo);

            return false;
        }
        else if(!hayErrores && preanalisis.equals(finCadena))
        {
            //System.out.println("Codigo Correcto");

            return true;
        }
        return hayErrores;
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

        coincidir(clase);
        coincidir(identificador);
        CLASS_INHER();
        coincidir(abrir_llave);  
        FUNCTIONS(); 
        coincidir(cerrar_llave);
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

        coincidir(fun);
        FUNCTION();
    }

    void VAR_DECL()
    {
        if(hayErrores) return;

        coincidir(var);
        coincidir(identificador);
        VAR_INIT();
        coincidir(punto_y_coma);
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

    void STATEMENT()
    {
        if(hayErrores) return;

        if(preanalisis.equals(diferente) || preanalisis.equals(menos) || preanalisis.equals(verdadero) || preanalisis.equals(falso) || 
        preanalisis.equals(nulo) || preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || 
        preanalisis.equals(identificador) || preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr))
        {
            EXPR_STMT();
        }
        else if(preanalisis.equals(para))
        {
            FOR_STMT();
        }
        else if(preanalisis.equals(si))
        {
            IF_STMT();
        }
        else if(preanalisis.equals(imprimir))
        {
            PRINT_STMT();
        }
        else if(preanalisis.equals(retornar))
        {
            RETURN_STMT();
        }
        else if(preanalisis.equals(mientras))
        {
            WHILE_STMT();
        }
        else if(preanalisis.equals(abrir_llave))
        {
            BLOCK();
        }
        else
        {
            hayErrores = true;
            //System.out.println("Error en la posición " + preanalisis.posicion);
            System.out.println("Error semantico");
            System.exit(1);
        }
    }

    void EXPR_STMT()
    {
        if(hayErrores) return;

        EXPRESSION();
        coincidir(punto_y_coma);
    }

    void FOR_STMT()
    {
        if(hayErrores) return;

        coincidir(para);
        coincidir(abrir_parentesis);
        FOR_STMT_1();
        FOR_STMT_2();
        FOR_STMT_3();
        coincidir(cerrar_parentesis);
        STATEMENT();
    }

    void FOR_STMT_1()
    {
        if(hayErrores) return;

        if(preanalisis.equals(var))
        {
            VAR_DECL();
        }
        else if(preanalisis.equals(diferente) || preanalisis.equals(menos) || preanalisis.equals(verdadero) || preanalisis.equals(falso) || 
        preanalisis.equals(nulo) || preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || 
        preanalisis.equals(identificador) || preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr))
        {
            EXPR_STMT();
        }
        else if(preanalisis.equals(punto_y_coma))
        {
            coincidir(punto_y_coma);
        }
        else
        {
            hayErrores = true;
            //System.out.println("Error en la posición " + preanalisis.posicion);
            System.out.println("Error semantico");
            System.exit(1);
        }
    }

    void FOR_STMT_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(diferente) || preanalisis.equals(menos) || preanalisis.equals(verdadero) || preanalisis.equals(falso) || 
        preanalisis.equals(nulo) || preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || 
        preanalisis.equals(identificador) || preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr))
        {
            EXPRESSION();
            coincidir(punto_y_coma);
        }
        else if(preanalisis.equals(punto_y_coma))
        {
            coincidir(punto_y_coma);
        }
        else
        {
            hayErrores = true;
            //System.out.println("Error en la posición " + preanalisis.posicion);
            System.out.println("Error semantico");
            System.exit(1);
        }
    }

    void FOR_STMT_3()
    {
        if(hayErrores) return;

        if(preanalisis.equals(diferente) || preanalisis.equals(menos) || preanalisis.equals(verdadero) || preanalisis.equals(falso) || 
        preanalisis.equals(nulo) || preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || 
        preanalisis.equals(identificador) || preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr))
        {
            EXPRESSION();
        }
    }

    void IF_STMT()
    {
        if(hayErrores) return;

        coincidir(si); 
        coincidir(abrir_parentesis);
        EXPRESSION();
        coincidir(cerrar_parentesis); 
        STATEMENT(); 
        ELSE_STATEMENT();
    }

    void ELSE_STATEMENT()
    {
        if(hayErrores) return;

        if(preanalisis.equals(ademas))
        {
            coincidir(ademas);
            STATEMENT(); 
        }
    }

    void PRINT_STMT()
    {
        if(hayErrores) return;

        coincidir(imprimir);
        EXPRESSION();
        coincidir(punto_y_coma);
    }

    void RETURN_STMT()
    {
        if(hayErrores) return;

        coincidir(retornar);
        RETURN_EXP_OPC(); 
        coincidir(punto_y_coma);
    }

    void RETURN_EXP_OPC()
    {
        if(hayErrores) return;

        if(preanalisis.equals(diferente) || preanalisis.equals(menos) || preanalisis.equals(verdadero) || preanalisis.equals(falso) || 
        preanalisis.equals(nulo) || preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || 
        preanalisis.equals(identificador) || preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr))
        {
            EXPRESSION();
        }
    }

    void WHILE_STMT()
    {
        if(hayErrores) return;

        coincidir(mientras); 
        coincidir(abrir_parentesis); 
        EXPRESSION(); 
        coincidir(cerrar_parentesis);
        STATEMENT();
    }

    void BLOCK()
    {
        if(hayErrores) return;

        coincidir(abrir_llave); 
        BLOCK_DECL();
        coincidir(cerrar_llave);
    }

    void BLOCK_DECL()
    {
        if(hayErrores) return;

        if(preanalisis.equals(clase) || preanalisis.equals(fun) || preanalisis.equals(var) || preanalisis.equals(diferente) || preanalisis.equals(menos) || preanalisis.equals(verdadero) ||preanalisis.equals(falso) || preanalisis.equals(nulo) ||
        preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || preanalisis.equals(identificador) ||
        preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr) || preanalisis.equals(para) || preanalisis.equals(si) || preanalisis.equals(imprimir) ||
        preanalisis.equals(retornar) || preanalisis.equals(mientras) || preanalisis.equals(abrir_llave))
        {
            DECLARATION(); 
            BLOCK_DECL();
        }
    }

    void EXPRESSION()
    {
        if(hayErrores) return;

        ASSIGNMENT();
    }

    void ASSIGNMENT()
    {
        if(hayErrores) return;

        LOGIC_OR(); 
        ASSIGNMENT_OPC();
    }

    void ASSIGNMENT_OPC()
    {
        if(hayErrores) return;

        if(preanalisis.equals(asignacion))
        {
            coincidir(asignacion);
            EXPRESSION();
        }
    }

    void LOGIC_OR()
    {
        if(hayErrores) return;

        LOGIC_AND();
        LOGIC_OR_2();
    }

    void LOGIC_OR_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(o))
        {
            coincidir(o);
            LOGIC_AND();
            LOGIC_OR_2();
        }
    }

    void LOGIC_AND()
    {
        if(hayErrores) return;

        EQUALITY(); 
        LOGIC_AND_2();
    }

    void LOGIC_AND_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(y))
        {
            coincidir(y);
            EQUALITY(); 
            LOGIC_AND_2();
        }
    }

    void EQUALITY()
    {
        if(hayErrores) return;

        COMPARISON(); 
        EQUALITY_2();
    }

    void EQUALITY_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(diferente_de))
        {
            coincidir(diferente_de);
            COMPARISON(); 
            EQUALITY_2();
        }
        else if(preanalisis.equals(igual))
        {
            coincidir(igual);
            COMPARISON(); 
            EQUALITY_2();
        }
    }

    void COMPARISON()
    {
        if(hayErrores) return;

        TERM();
        COMPARISON_2();
    }

    void COMPARISON_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(mayor_que))
        {
            coincidir(mayor_que);
            TERM();
            COMPARISON_2();
        }
        else if(preanalisis.equals(mayor_igual_que))
        {
            coincidir(mayor_igual_que);
            TERM();
            COMPARISON_2();
        }
        else if(preanalisis.equals(menor_que))
        {
            coincidir(menor_que);
            TERM();
            COMPARISON_2();
        }
        else if(preanalisis.equals(menor_igual_que))
        {
            coincidir(menor_igual_que);
            TERM();
            COMPARISON_2();
        }
    }

    void TERM()
    {
        if(hayErrores) return;

        FACTOR();
        TERM_2();
    }

    void TERM_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(menos))
        {
            coincidir(menos);
            FACTOR();
            TERM_2();
        }
        else if(preanalisis.equals(suma))
        {
            coincidir(suma);
            FACTOR();
            TERM_2();
        }
    }

    void FACTOR()
    {
        if(hayErrores) return;

        UNARY();
        FACTOR_2();
    }

    void FACTOR_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(diagonal))
        {
            coincidir(diagonal);
            UNARY();
            FACTOR_2();
        }

        else if(preanalisis.equals(asterisco))
        {
            coincidir(asterisco);
            UNARY();
            FACTOR_2();
        }
    }

    void UNARY()
    {
        if(hayErrores) return;

        if(preanalisis.equals(diferente))
        {
            coincidir(diferente);
            UNARY();
        }

        else if(preanalisis.equals(menos))
        {
            coincidir(menos);
            UNARY();
        }

        else if(preanalisis.equals(verdadero) || preanalisis.equals(falso) || preanalisis.equals(nulo) ||
        preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || preanalisis.equals(identificador) ||
        preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr))
        {
            CALL();
        }
        else
        {
            hayErrores = true;
            //System.out.println("Error en la posición " + preanalisis.posicion);
            System.out.println("Error semantico");
            System.exit(1);
        }
    }

    void CALL()
    {
        if(hayErrores) return;

        PRIMARY();
        CALL_2();
    }

    void CALL_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(abrir_parentesis))
        {
            coincidir(abrir_parentesis);
            ARGUMENTS_OPC(); 
            coincidir(cerrar_parentesis); 
            CALL_2();
        }
        else if(preanalisis.equals(punto))
        {
            coincidir(punto);
            coincidir(identificador);
            CALL_2();
        }
    }

    void CALL_OPC()
    {
        if(hayErrores) return;

        if(preanalisis.equals(verdadero) || preanalisis.equals(falso) || preanalisis.equals(nulo) ||
        preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || preanalisis.equals(identificador) ||
        preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr))
        {
            CALL();
            coincidir(punto);
        }
    }

    void PRIMARY()
    {
        if(hayErrores) return;

        if(preanalisis.equals(verdadero))
        {
            coincidir(verdadero);
        }
        else if(preanalisis.equals(falso))
        {
            coincidir(falso);
        }
        else if(preanalisis.equals(nulo))
        {
            coincidir(nulo);
        }
        else if(preanalisis.equals(este))
        {
            coincidir(este);
        }
        else if(preanalisis.equals(numero))
        {
            coincidir(numero);
        }
        else if(preanalisis.equals(cadena))
        {
            coincidir(cadena);
        }
        else if(preanalisis.equals(identificador))
        {
            coincidir(identificador);
        }
        else if(preanalisis.equals(abrir_parentesis))
        {
            coincidir(abrir_parentesis);
            EXPRESSION();
            coincidir(cerrar_parentesis);
        }
        else if(preanalisis.equals(superr))
        {
            coincidir(superr);
            coincidir(punto);
            coincidir(identificador);
        }
        else
        {
            hayErrores = true;
            //System.out.println("Error en la posición " + preanalisis.posicion);
            System.out.println("Error semantico");
            System.exit(1);
        }
    }

    void FUNCTION()
    {
        if(hayErrores) return;

        coincidir(identificador);
        coincidir(abrir_parentesis);
        PARAMETERS_OPC(); 
        coincidir(cerrar_parentesis); 
        BLOCK();
    }

    void FUNCTIONS()
    {
        if (hayErrores) return;

        if(preanalisis.equals(identificador))
        {
            FUNCTION();
            FUNCTIONS();
        }
    }

    void PARAMETERS_OPC()
    {
        if(hayErrores) return;
        
        if(preanalisis.equals(identificador))
        {
            PARAMETERS();
        }
    }

    void PARAMETERS()
    {
        if(hayErrores) return;

        coincidir(identificador);
        PARAMETERS_2();
    }

    void PARAMETERS_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(coma))
        {
            coincidir(coma);
            coincidir(identificador);
            PARAMETERS_2();
        }
    }

    void ARGUMENTS_OPC()
    {
        if(hayErrores) return;

        if(preanalisis.equals(diferente) || preanalisis.equals(menos) || preanalisis.equals(verdadero) || preanalisis.equals(falso) || preanalisis.equals(nulo) ||
        preanalisis.equals(este) || preanalisis.equals(numero) || preanalisis.equals(cadena) || preanalisis.equals(identificador) ||
        preanalisis.equals(abrir_parentesis) || preanalisis.equals(superr))
        {
            ARGUMENTS();
        }
    }

    void ARGUMENTS()
    {
        if(hayErrores) return;

        EXPRESSION();
        ARGUMENTS_2();
    }

    void ARGUMENTS_2()
    {
        if(hayErrores) return;

        if(preanalisis.equals(coma))
        {
            coincidir(coma);
            EXPRESSION();
            ARGUMENTS_2();
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
            //System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba un  " + t.tipo);
            System.out.println("Error semantico");
            System.exit(1);
        }
    }

}
