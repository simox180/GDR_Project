package it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno;

import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;

public class CalcolatoreDannoBase implements CalcolatoreDanno {
    /**
     * Calcola il danno effettivo tenendo conto dell'attacco e della difesa dei personaggi
     * (Questo metodo non è statico perchè non funzionerebbe l'overriding ma sarebbe legato
     * a una singola implementazione)
     *
     * @param attaccante il personaggio che procura il danno
     * @param bersaglio il personaggio che subisce il danno
     * @return il danno effettivo
     */
    @Override
    public int calcolaDanno(Personaggio attaccante, Personaggio bersaglio) {
        if( attaccante == null || bersaglio == null) throw new NullPointerException("Uno dei personaggi è null");

        return Math.max(0, attaccante.getAttacco() - bersaglio.getDifesa());
    }
}
