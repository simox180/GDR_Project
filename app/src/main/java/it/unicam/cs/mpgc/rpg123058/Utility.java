package it.unicam.cs.mpgc.rpg123058;

import java.util.List;
import java.util.Objects;

public class Utility {

    /**
     * Metodo di utilità per trovare una lista duplicata
     * (creato per virtuosismo ma non so se verrà utilizzato per più di una volta)
     *
     * @param lista la lista di tipi generici da validare
     * @return {@code true} se viene trovato un duplicato, {@code false} se non ci sono duplicati
     * @param <E> generics usato per accettare qualsiasi tipo passato
     */
    public static <E> boolean trovaDuplicati(List<E> lista){
        boolean duplicato = false;

        for (int i = 0; i < lista.size() && !duplicato; i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                if (lista.get(i) != null && lista.get(i) == lista.get(j)) {
                    duplicato = true;
                    break;
                }
            }
        }

        return duplicato;
    }
}
