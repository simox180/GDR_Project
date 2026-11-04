package it.unicam.cs.mpgc.rpg123058;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class VoceInventario {
    private final Oggetto oggetto;
    private int quantita = 1;

    public VoceInventario(@NonNull Oggetto oggetto){
        Objects.requireNonNull(oggetto, "L'oggetto non può essere null");

        this.oggetto = oggetto;
    }

    public void aumentaQuantita(){ this.quantita++; }
    public boolean diminuisciQuantita(){
        if(this.quantita == 1) return false;
        this.quantita--;
        return true;
    }

    public Oggetto getOggetto(){ return oggetto;}

    public int getQuantita() { return quantita; }

    @Override
    public boolean equals(Object obj) {
        return oggetto.equals(obj);
    }

    @Override
    public int hashCode() {
        return oggetto.hashCode();
    }
}
