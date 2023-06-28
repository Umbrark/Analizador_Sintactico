package mx.ipn.escom.compiladores;

import com.sun.deploy.security.SelectableSecurityManager;

import java.sql.SQLOutput;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.util.ElementScanner6;

public class Arbol {
    private Nodo raiz;

    public Arbol(Nodo raiz)
    {
        this.raiz = raiz;
    }

    public void recorrer()
    {
        for(Nodo n : raiz.getHijos()){
            Token t = n.getValue();
            switch (t.tipo)
            {
                // Operadores aritméticos
                case SUMA:
                case RESTA:
                case ASTERISCO:
                case DIAGONAL:
                case MENOR_QUE:
                case MENOR_IGUAL_QUE:
                case MAYOR_QUE:
                case MAYOR_IGUAL_QUE:
                case IGUAL:
                case DIFERENTE_DE:
                case ASIGNACION:
                    SolverAritmetico solver = new SolverAritmetico(n);
                    Object res = solver.resolver();
                    //System.out.println(res);
                break;


                case VAR:
                    // Crear una variable. Usar tabla de simbolos

                    Nodo VerHijos = n.getHijos().get(0);

                    if(VerHijos.getHijos() == null)
                    {
                        if(TablaSimbolos.existeIdentificador(n.getHijos().get(0).getValue().lexema) == false)
                        {
                            TablaSimbolos.asignar(n.getHijos().get(0).getValue().lexema,null);
                        }
                        else
                        {
                            System.err.println("Ya existe el identificador " + n.getHijos().get(0).getValue().lexema);
                            System.exit(1);
                        }

                    }

                    else if(TablaSimbolos.existeIdentificador(VerHijos.getHijos().get(0).getValue().lexema)==false)
                    {

                        if(VerHijos.getHijos().size() == 2)
                        {
                            Nodo NodoOperaciones = VerHijos.getHijos().get(1);
                            SolverAritmetico SolverVar = new SolverAritmetico(NodoOperaciones);
                            Object ResOpeVar = SolverVar.resolver();

                            TablaSimbolos.asignar(VerHijos.getHijos().get(0).getValue().lexema, ResOpeVar);
                        }
                    }
                    else
                    {
                        System.err.println("Ya existe el identificador " + VerHijos.getHijos().get(0).getValue().lexema);
                        System.exit(1);
                    }
                    
                break;
                case SI:
                    Nodo VerHijos1 = n.getHijos().get(0);
                    int ELSE = n.getHijos().size()-1;

                    if(VerHijos1.getHijos().size() == 1)
                    {
                        System.out.println("Faltan elementos dentro del if");
                        System.exit(1);
                    }

                    SolverAritmetico SlvCon = new SolverAritmetico(VerHijos1);
                    boolean ConCumIf = (boolean) SlvCon.resolver();

                    if (ConCumIf)
                    {
                        for (int a = 0; a <= n.getHijos().size() - 1; a++)
                        {

                            if(n.getHijos().get(a).getValue().tipo == TipoToken.ADEMAS)
                            {
                                a++;
                            }
                            else
                            {
                                Nodo Aux = new Nodo(null);
                                Nodo SigIns = n.getHijos().get(a);
                                Aux.insertarHijo(SigIns);
                                Arbol NuevoArbol = new Arbol(Aux);
                                NuevoArbol.recorrer();
                            }
                        }
                    }
                    else if (n.getHijos().get(ELSE).getValue().tipo == TipoToken.ADEMAS)
                    {
                        Nodo Aux = new Nodo(null);
                        Nodo ademas = n.getHijos().get(ELSE);
                        Aux.insertarHijo(ademas);
                        Arbol NuevoArbol = new Arbol(Aux);
                        NuevoArbol.recorrer();
                    }

                break;
                case ADEMAS:
                    if (n.getHijos() == null)
                    {
                        System.out.println("Faltan elementos dentro del else");
                        System.exit(1);
                    }

                    for (int b = 0; b < n.getHijos().size(); b++)
                    {
                        Nodo Aux = new Nodo(null);
                        Nodo SigIns = n.getHijos().get(b);
                        Aux.insertarHijo(SigIns);
                        Arbol NuevoArbol = new Arbol(Aux);
                        NuevoArbol.recorrer();
                    }
                    break;
                case IMPRIMIR:

                    Nodo print = n.getHijos().get(0);
                    SolverAritmetico Slv = new SolverAritmetico(print);
                    Object resultado = Slv.resolver();
                    System.out.println(resultado);
                    
                break;
                case MIENTRAS:
                break;
                case PARA:
                break;

            }
        }
    }

}

