package it.unicam.cs.mpgc.rpg123058;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * Rappresenta una voce dell'inventario, composta da un oggetto e dalla sua quantità.
 */
public class VoceInventario {
    private final Oggetto oggetto;
    private int quantita;

    /**
     * Crea una voce contenente una singola copia dell'oggetto specificato usando il secondo costruttore che prende
     * come parametro anche la quantità
     * @param oggetto l'oggetto associato alla voce, non nullo
     * @throws NullPointerException se l'oggetto è nullo
     */
    public VoceInventario(@NonNull Oggetto oggetto){
        this(oggetto,1);
    }

    public VoceInventario(@NonNull Oggetto oggetto, int quantita){
        this.oggetto = Objects.requireNonNull(
                oggetto, "L'oggetto non può essere null"
        );

        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }

        this.quantita = quantita;
    }

    /**
     * Incrementa di una unità la quantità dell'oggetto nella voce.
     */
    public void aumentaQuantita(){
        if(this.quantita < Integer.MAX_VALUE) this.quantita++;
    }

    /**
     * Decrementa di una unità la quantità se contiene più di una copia.
     *
     * @return {@code true} se la quantità è stata diminuita, {@code false} se era già pari a uno
     */
    public boolean diminuisciQuantita(){
        if(this.quantita == 1) return false;
        this.quantita--;
        return true;
    }

    /**
     * Restituisce l'oggetto associato alla voce.
     *
     * @return l'oggetto della voce
     */
    public Oggetto getOggetto(){ return oggetto;}

    /**
     * Restituisce il numero di copie dell'oggetto nella voce.
     *
     * @return la quantità corrente
     */
    public int getQuantita() { return quantita; }
}
