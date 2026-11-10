package it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni;

import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface GestoreTurni {

    /**
     * Consulta il partecipante corrente.
     * @return il partecipante corrente o {@code null} se non ne resta alcuno
     */
    @Nullable Personaggio getPartecipanteCorrente();

    /**
     * Passa al prossimo partecipante vivo, se presente.
     */
    void avanzaTurno();
}
