package it.unicam.cs.mpgc.rpg123058.Combattimento;

import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface GestoreTurni {

    @Nullable Personaggio getPartecipanteCorrente();

    void avanzaTurno();
}
