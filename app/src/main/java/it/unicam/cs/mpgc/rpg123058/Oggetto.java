package it.unicam.cs.mpgc.rpg123058;

import java.util.Objects;

/**
 * Definisce le caratteristiche comuni degli oggetti del gioco.
 * Due oggetti sono considerati uguali quando hanno lo stesso nome.
 */
public abstract class Oggetto {

    private final String nome;

    /**
     * Inizializza un oggetto con il nome specificato.
     *
     * @param nome il nome dell'oggetto, non nullo e non vuoto
     * @throws NullPointerException se il nome è nullo
     * @throws IllegalArgumentException se il nome è vuoto o composto da soli spazi
     */
    protected Oggetto(String nome){
        Objects.requireNonNull(nome, "Il nome non può essere null");

        if (nome.isBlank())
            throw new IllegalArgumentException("Nome vuoto");

        this.nome = nome;
    }

    /**
     * Restituisce il nome dell'oggetto.
     *
     * @return il nome dell'oggetto
     */
    public String getNome() {
        return nome;
    }

    /**
     * Confronta questo oggetto con un altro in base al nome.
     *
     * @param obj l'oggetto da confrontare
     * @return {@code true} se l'argomento è un {@code Oggetto} con lo stesso nome
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if((!(obj instanceof Oggetto oggetto))) return false;

        return this.nome.equals(oggetto.getNome());
    }

    /**
     * Restituisce il codice hash derivato dal nome dell'oggetto.
     *
     * @return il codice hash del nome
     */
    @Override
    public int hashCode() {
        return this.nome.hashCode();
    }
}
