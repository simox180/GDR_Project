package it.unicam.cs.mpgc.rpg123058.Combattimento;

import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;

public class CalcolatoreDannoBase implements CalcolatoreDanno{

    @Override
    public int calcolaDanno(Personaggio attaccante, Personaggio bersaglio) {
        if( attaccante == null || bersaglio == null) throw new NullPointerException("Uno dei personaggi è null");

        return Math.max(0, attaccante.getAttacco() - bersaglio.getDifesa());
    }
}
