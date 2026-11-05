package it.unicam.cs.mpgc.rpg123058.Dominio;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Rappresenta un oggetto equipaggiabile che incrementa l'attacco del personaggio.
 */
public class Arma extends Oggetto {

    private final int attaccoBonus;

    /**
     * Crea un'arma con il nome e il bonus di attacco indicati.
     *
     * @param nome il nome dell'arma, non nullo
     * @param attaccoBonus il bonus di attacco, che deve essere positivo
     * @throws IllegalArgumentException se il bonus non è positivo
     */
    public Arma(@NonNull String nome, int attaccoBonus){
        super(nome);
        if(attaccoBonus <= 0) throw new IllegalArgumentException("Attacco bonus non può essere negativo");

        this.attaccoBonus = attaccoBonus;
    }

    /**
     * Restituisce il bonus di attacco fornito dall'arma.
     *
     * @return il bonus di attacco
     */
    public int getAttaccoBonus() {
        return attaccoBonus;
    }
}
