package mx.ipn.escom.compiladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.util.ElementScanner6;
import javax.xml.transform.Source;

public class Scanner 
{

    private final String source;
    private int Estado = 0;

    private final List<Token> tokens = new ArrayList<>();

    private int linea = 1;

    private static final Map<String, TipoToken> palabrasReservadas;
    static 
    {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("y", TipoToken.Y);
        palabrasReservadas.put("clase", TipoToken.CLASE);
        palabrasReservadas.put("ademas", TipoToken.ADEMAS);
        palabrasReservadas.put("falso", TipoToken.FALSO);
        palabrasReservadas.put("para", TipoToken.PARA);
        palabrasReservadas.put("fun", TipoToken.FUN); //definir funciones
        palabrasReservadas.put("si", TipoToken.SI);
        palabrasReservadas.put("nulo", TipoToken.NULO);
        palabrasReservadas.put("o", TipoToken.O);
        palabrasReservadas.put("imprimir", TipoToken.IMPRIMIR);
        palabrasReservadas.put("retornar", TipoToken.RETORNAR);
        palabrasReservadas.put("super", TipoToken.SUPER);
        palabrasReservadas.put("este", TipoToken.ESTE);
        palabrasReservadas.put("verdadero", TipoToken.VERDADERO);
        palabrasReservadas.put("var", TipoToken.VAR); //definir variables
        palabrasReservadas.put("mientras", TipoToken.MIENTRAS);
    }


    Scanner(String source)
    {
        this.source = source + " ";
    }

    List<Token> scanTokens()
    {
        int Estado, Posicion;
        char Caracter;
        String lexema = "";
        int inicioLexema = 0;

        Estado = 0;
        

        for (Posicion=0; Posicion<source.length(); Posicion++)
        {
            Caracter = source.charAt(Posicion);
            
            switch(Estado)
            {
                case 0:

                    if (Caracter=='(')
                    {
                        tokens.add(new Token(TipoToken.ABRIR_PARENTESIS, "("));
                    }
                    else if(Caracter==')')
                    {
                        tokens.add(new Token(TipoToken.CERRAR_PARENTESIS, ")"));
                    }
                    else if(Caracter=='{')
                    {
                        tokens.add(new Token(TipoToken.ABRIR_LLAVE, "{"));
                    }
                    else if(Caracter=='}')
                    {
                        tokens.add(new Token(TipoToken.CERRAR_LLAVE, "}"));
                    }
                    else if(Caracter==',')
                    {
                        tokens.add(new Token(TipoToken.COMA, ","));
                    }
                    else if(Caracter=='.')
                    {
                        tokens.add(new Token(TipoToken.PUNTO, "."));
                    }
                    else if(Caracter==';')
                    {
                        tokens.add(new Token(TipoToken.PUNTO_Y_COMA, ";"));
                    }
                    else if(Caracter=='-')
                    {
                        tokens.add(new Token(TipoToken.RESTA, "-"));
                    }
                    else if(Caracter=='+')
                    {
                        tokens.add(new Token(TipoToken.SUMA, "+"));
                    }
                    else if(Caracter=='*')
                    {
                        tokens.add(new Token(TipoToken.ASTERISCO, "*"));
                    }
                    else if(Caracter=='/')
                    {
                        Estado = 1;
                    }
                    else if(Caracter=='!')
                    {
                        Estado = 2;
                    }
                    else if(Caracter=='=')
                    {
                        Estado = 3;
                    }
                    else if(Caracter=='<')
                    {
                        Estado = 4;
                    }
                    else if(Caracter=='>')
                    {
                        Estado = 5;
                    }
                    else if(Caracter=='"')
                    {
                       Estado = 9;
                    }
                    else if(Character.isDigit(Caracter))
                    {
                        Estado = 10;
                        lexema = lexema + Caracter;
                    }
                    else if(Character.isAlphabetic(Caracter))
                    {
                        Estado = 16;
                        lexema = lexema + Caracter;
                        inicioLexema = Posicion;
                    }
               break;
                
               case 1:

                    if(Caracter=='/')
                    {
                        Estado = 6;
                    }
                    else if(Caracter=='*')
                    {
                        Estado = 7;
                    }
                    else
                    {
                        Posicion--;
                        tokens.add(new Token(TipoToken.ENTRE, "/"));
                        Estado = 0;
                    }
                    
               break;
                case 2:

                    if (Caracter=='=')
                    {
                        tokens.add(new Token(TipoToken.DIFERENTE_DE, "!="));
                    }
                    else
                    {
                        Posicion--;
                        tokens.add(new Token(TipoToken.DIFERENTE, "!"));
                    }

                    Estado = 0;

                break;

                case 3:

                    if(Caracter=='=')
                    {
                        tokens.add(new Token(TipoToken.IGUAL, "=="));
                    }   
                    else
                    {
                        Posicion--;
                        tokens.add(new Token(TipoToken.ASIGNACION, "="));
                    } 

                    Estado = 0;

                break;

                case 4:

                    if (Caracter=='=')
                    {
                        tokens.add(new Token(TipoToken.MENOR_IGUAL_QUE, "<="));
                    }
                    else
                    {
                        Posicion--;
                        tokens.add(new Token(TipoToken.MENOR_QUE, "<"));
                    }

                    Estado = 0;

                break;

                case 5:

                    if (Caracter=='=')
                    {
                        tokens.add(new Token(TipoToken.MAYOR_IGUAL_QUE,">="));
                    }
                    else
                    {
                        Posicion--;
                        tokens.add(new Token(TipoToken.MAYOR_QUE, ">"));
                    }

                    Estado = 0;

                break;

                case 6:

                    if(Caracter=='\n')
                    {
                        Estado = 0;
                    }
                    else
                    {
                        Estado = 6;
                    }

                break;

                case 7:

                    if(Caracter=='*')
                    {
                        Estado = 8;
                    }
                    else
                    {
                        Estado = 7;
                    }

                break;

                case 8:

                    if(Caracter=='/')
                    {
                        Estado = 0;
                    }
                    else
                    {
                        Estado = 7;
                    }

                break;

                case 9:

                if(Caracter == '"')
                {
                    tokens.add(new Token(TipoToken.CADENA,lexema));

                    lexema ="";
                    Estado = 0;
                }
                else
                {
                    Estado = 9;
                    lexema = lexema + Caracter;
                }


            break;

            case 10:

                if(Character.isDigit(Caracter))
                {
                    Estado = 10;
                    lexema = lexema + Caracter;
                }                
                else if(Caracter == '.')
                {
                    Estado = 11;
                    lexema = lexema + Caracter;
                }
                else if(Caracter == 'E')
                {
                    Estado = 13;
                    lexema = lexema + Caracter;
                }
                else
                {
                    Posicion--;
                    tokens.add(new Token(TipoToken.NUMERO,lexema,Double.valueOf(lexema)));
                    lexema="";

                    Estado = 0;
                }

            break;
            
            case 11:
                if(Character.isDigit(Caracter))
                {
                    Estado = 12;
                    lexema = lexema + Caracter;
                }
            break;

            case 12:
                if(Character.isDigit(Caracter))
                {
                    Estado = 12;
                    lexema = lexema + Caracter;
                }
                else if(Caracter == 'E')
                {
                    Estado = 13;
                    lexema = lexema + Caracter;
                }
                else
                {
                    Posicion--;
                    tokens.add(new Token(TipoToken.NUMERO,lexema,Double.valueOf(lexema)));
                    lexema ="";
                    Estado = 0;
                }
            break;

            case 13:
                if(Caracter == '+' || Caracter == '-')
                {
                    Estado = 14;
                    lexema = lexema + Caracter;
                }
                else if(Character.isDigit(Caracter))
                {
                    Estado = 15;
                    lexema = lexema + Caracter;
                }
            break;

            case 14:
                if(Character.isDigit(Caracter))
                {
                    Estado = 15;
                    lexema = lexema + Caracter;
                }
            break;

            case 15:
                if(Character.isDigit(Caracter))
                {
                    Estado = 15;
                    lexema = lexema + Caracter;
                }
                else
                {
                    Posicion--;
                    tokens.add(new Token(TipoToken.NUMERO,lexema,Double.valueOf(lexema)));
                    lexema = "";

                    Estado = 0;
                }
            break;

            case 16:

                if(Character.isAlphabetic(Caracter) || Character.isDigit(Caracter) )
                {
                    lexema = lexema + Caracter;
                }
                else
                {
                    TipoToken tt = palabrasReservadas.get(lexema);
                    if(tt == null)
                    {
                        tokens.add(new Token(TipoToken.IDENTIFICADOR, lexema));
                    }
                    else
                    {
                        tokens.add(new Token(tt, lexema));
                    }

                    Estado = 0;
                    Posicion--;
                    lexema = "";
                    inicioLexema = 0;
                }
            break;

            }
        }
        //Aquí va el corazón del scanner.
        
            
        /*
        Analizar el texto de entrada para extraer todos los tokens
        y al final agregar el token de fin de archivo
         */
        tokens.add(new Token(TipoToken.EOF,""));
    
        return tokens;
    }
}