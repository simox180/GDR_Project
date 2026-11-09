package it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico;

import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;

public interface StrategiaNemico {

    AzioneNemico scegliAzione(Nemico nemico);
}
