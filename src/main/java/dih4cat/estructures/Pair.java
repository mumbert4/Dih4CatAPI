package dih4cat.estructures;

public class Pair<F,S> {
    private F first;
    private S second;

    /**
     * Constructora de la classe Pair
     * @param first Valor que volem que tingui el primer paràmetre
     * @param second Valor que volem que tingui el segon paràmetre
     * Complexitat O (1)
     */
    public Pair(F first, S second) {
        this.first  = first;
        this.second = second;
    }

    /**
     * Actualitzar el valor del primer paràmetre del Pair
     * @param first Nou valor que tendra el paràmetre
     * Complexitat O(1)
     */
    public void setFirst(F first) {
        this.first = first;
    }

    /**
     * Actualitzar el valor del segon paràmetre del Pair
     * @param second Nou valor que tendra el paràmetre
     * Complexitat O(1)
     */
    public void setSecond(S second) {
        this.second = second;
    }

    /**
     * Obtenir el valor del primer paràmetre del Pair
     * @return Valor del primer paràmetre
     * Complexitat O(1)
     */
    public F getFirst() {
        return first;
    }

    /**
     * Obtenir el valor del segon paràmetre del Pair
     * @return Valor del primer paràmetre
     * Complexitat O(1)
     */
    public S getSecond() {
        return second;
    }
}

