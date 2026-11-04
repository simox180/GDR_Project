package it.unicam.cs.mpgc.rpg123058;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Arma extends Oggetto {

    private final int attaccoBonus;

    public Arma(@NonNull String nome, int attaccoBonus){
        super(nome);
        if(attaccoBonus <= 0) throw new IllegalArgumentException("Attacco bonus non può essere negativo");

        this.attaccoBonus = attaccoBonus;
    }

    public int getAttaccoBonus() {
        return attaccoBonus;
    }
}
