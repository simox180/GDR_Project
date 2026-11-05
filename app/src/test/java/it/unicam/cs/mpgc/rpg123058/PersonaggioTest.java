package it.unicam.cs.mpgc.rpg123058;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonaggioTest {

    @Test
    void saluteInizialeUgualeAllaMassima() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);

        assertEquals(giocatore.getSaluteMassima(), giocatore.getSalute());
        assertTrue(giocatore.isVivo());
    }

    @Test
    void dannoEccessivoPortaLaSaluteAZero() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);

        giocatore.subisciDanno(150);

        assertEquals(0, giocatore.getSalute());
        assertFalse(giocatore.isVivo());
    }

    @Test
    void curaEccessivaSiFermaAllaSaluteMassima() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);
        giocatore.subisciDanno(30);

        giocatore.cura(80);

        assertEquals(100, giocatore.getSalute());
    }

    @Test
    void nomeDiSoliSpaziEDifesaNegativaSonoRifiutati() {
        assertThrows(IllegalArgumentException.class, () -> new Giocatore("   ", 100, 10, 3));
        assertThrows(IllegalArgumentException.class, () -> new Giocatore("Ada", 100, 10, -1));
    }
}
