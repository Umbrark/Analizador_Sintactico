package mx.ipn.escom.compiladores;

public class SolverAritmetico {

    private final Nodo nodo;

    public SolverAritmetico(Nodo nodo) {
        this.nodo = nodo;
    }

    public Object resolver(){
        return resolver(nodo);
    }
    private Object resolver(Nodo n)
    {
        // No tiene hijos, es un operando
        if(n.getHijos() == null)
        {
            if (n.getValue().tipo == TipoToken.NUMERO || n.getValue().tipo == TipoToken.CADENA)
            {
                return n.getValue().literal;
            }
            else if (n.getValue().tipo == TipoToken.IDENTIFICADOR)
            {
                // Ver la tabla de símbolos
                if (TablaSimbolos.existeIdentificador(n.getValue().lexema) == true)
                {
                    return TablaSimbolos.obtener(n.getValue().lexema);
                }
                else
                {
                    System.out.println("La variable " +  n.getValue().lexema + " no existe");
                    System.exit(1);
                }
            }
            else if (n.getValue().tipo == TipoToken.FALSO)
            {
                return false;
            }
            else if (n.getValue().tipo == TipoToken.VERDADERO)
            {
                return true;
            }
        }
        // Por simplicidad se asume que la lista de hijos del nodo tiene dos elementos
        Nodo izq = n.getHijos().get(0);
        Nodo der = n.getHijos().get(1);

        Object resultadoIzquierdo = resolver(izq);
        Object resultadoDerecho = resolver(der);

        if(resultadoIzquierdo instanceof Double && resultadoDerecho instanceof Double){
            switch (n.getValue().tipo){
                case SUMA:
                    return ((Double)resultadoIzquierdo + (Double) resultadoDerecho);
                case RESTA:
                    return ((Double)resultadoIzquierdo - (Double) resultadoDerecho);
                case ASTERISCO:
                    return ((Double)resultadoIzquierdo * (Double) resultadoDerecho);
                case DIAGONAL:
                    return ((Double)resultadoIzquierdo / (Double) resultadoDerecho);
                case MENOR_QUE:
                    return ((Double)resultadoIzquierdo < (Double) resultadoDerecho);
                case MENOR_IGUAL_QUE:
                    return ((Double)resultadoIzquierdo <= (Double) resultadoDerecho);
                case MAYOR_QUE:
                    return ((Double)resultadoIzquierdo > (Double) resultadoDerecho);
                case MAYOR_IGUAL_QUE:
                    return ((Double)resultadoIzquierdo >= (Double) resultadoDerecho);
                case IGUAL:
                    return (((Double) resultadoIzquierdo).equals((Double) resultadoDerecho));
                case DIFERENTE_DE:
                    return (!((Double) resultadoIzquierdo).equals((Double) resultadoDerecho));
                case ASIGNACION:
                    if (izq.getValue().tipo == TipoToken.IDENTIFICADOR)
                    {
                        TablaSimbolos.asignar(izq.getValue().lexema, resultadoDerecho);
                    }
                    break;

            }
        }
        else if(resultadoIzquierdo instanceof String && resultadoDerecho instanceof String)
        {
            if (n.getValue().tipo == TipoToken.SUMA)
            {
                // Ejecutar la concatenación

                return (String) resultadoIzquierdo + resultadoDerecho;
            }
            else
            {
                System.out.println("Error. Solo se permite suma para las cadenas");
                System.exit(1);
            }

        }
        else if (resultadoIzquierdo instanceof Boolean && resultadoDerecho instanceof Boolean)
        {
            switch (n.getValue().tipo)
            {
                case Y:
                    return ((Boolean) resultadoIzquierdo && (Boolean) resultadoDerecho);
                case O:
                    return ((Boolean) resultadoIzquierdo || (Boolean) resultadoDerecho);
            }
        }
        else
        {
            // Error por diferencia de tipos
            System.out.println("Error, no se puede realizar operaciones con variables de diferente tipo");
            System.exit(1);
        }

        return null;
    }
}
