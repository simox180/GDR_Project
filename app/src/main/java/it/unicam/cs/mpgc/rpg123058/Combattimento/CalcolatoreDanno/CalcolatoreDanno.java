package it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno;

import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;

public interface CalcolatoreDanno {

    /**
     * Calcola il danno di un attacco.
     * @param attaccante personaggio che attacca
     * @param bersaglio personaggio che riceve il colpo
     * @return danno calcolato
     */
    public int calcolaDanno(Personaggio attaccante, Personaggio bersaglio);
}
