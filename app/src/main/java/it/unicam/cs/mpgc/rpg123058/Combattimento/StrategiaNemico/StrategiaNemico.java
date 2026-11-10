package it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico;

import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;

public interface StrategiaNemico {

    /**
     * Sceglie l’azione del nemico indicato.
     * @param nemico nemico di turno
     * @return azione scelta
     */
    AzioneNemico scegliAzione(Nemico nemico);
}
