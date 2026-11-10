package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Combattimento.*;
import it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.*;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.*;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.*;
import it.unicam.cs.mpgc.rpg123058.Dominio.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PersonaggiTest {
    private Combattimento scontro(Giocatore g, List<Nemico> nemici, StrategiaNemico strategia) {
        return new Combattimento(g, nemici, GestoreTurniSequenziale::new, new CalcolatoreDannoBase(), strategia);
    }

    @Test
    void listaValidaImmodificabileDeveEssereAccettataDalGestore() {
        var g = new Giocatore("Ada", 10, 2, 0);
        var n = new Nemico("N", 10, 2, 0, null);
        assertSame(g, new GestoreTurniSequenziale(List.of(g, n)).getPartecipanteCorrente());
    }

    @Test
    void gestoreRuotaSaltaMortiECopiaLaLista() {
        var a = new Giocatore("A", 10, 2, 0);
        var b = new Nemico("B", 10, 2, 0, null);
        var c = new Nemico("C", 10, 2, 0, null);
        var origine = new ArrayList<Personaggio>(List.of(a, b, c));
        var turni = new GestoreTurniSequenziale(origine);
        origine.clear();
        assertSame(a, turni.getPartecipanteCorrente());
        b.subisciDanno(10);
        turni.avanzaTurno();
        assertSame(c, turni.getPartecipanteCorrente());
        turni.avanzaTurno();
        assertSame(a, turni.getPartecipanteCorrente());
        c.subisciDanno(10);
        turni.avanzaTurno();
        assertSame(a, turni.getPartecipanteCorrente());
        a.subisciDanno(10);
        turni.avanzaTurno();
        assertNull(turni.getPartecipanteCorrente());
    }

    @Test
    void gestoreRifiutaListeNonValide() {
        var a = new Giocatore("A", 10, 2, 0);
        var b = new Nemico("B", 10, 2, 0, null);
        assertThrows(NullPointerException.class, () -> new GestoreTurniSequenziale(null));
        assertThrows(NullPointerException.class, () -> new GestoreTurniSequenziale(Arrays.asList(a, null)));
        assertThrows(IllegalArgumentException.class, () -> new GestoreTurniSequenziale(new ArrayList<>(List.of(a))));
        assertThrows(IllegalArgumentException.class, () -> new GestoreTurniSequenziale(new ArrayList<>(List.of(a, a))));
        b.subisciDanno(10);
        assertThrows(IllegalArgumentException.class, () -> new GestoreTurniSequenziale(new ArrayList<>(List.of(a, b))));
    }

    @Test
    void bersagliNonValidiNonConsumanoIlTurno() {
        var g = new Giocatore("Ada", 30, 5, 0);
        var n = new Nemico("N", 30, 2, 0, null);
        var c = scontro(g, new ArrayList<>(List.of(n)), x -> AzioneNemico.ATTACCO);
        assertThrows(NullPointerException.class, () -> c.attaccaBersaglio(null));
        assertThrows(IllegalArgumentException.class, () -> c.attaccaBersaglio(g));
        assertThrows(IllegalArgumentException.class, () -> c.attaccaBersaglio(new Nemico("Altro", 10, 2, 0, null)));
        assertSame(g, c.getPersonaggioDiTurno());
        assertEquals(30, n.getSalute());
    }

    @Test
    void nemicoNonPuoColpireAlleatoOBersaglioMorto() {
        var g = new Giocatore("Ada", 30, 5, 0);
        var a = new Nemico("A", 5, 2, 0, null);
        var b = new Nemico("B", 30, 2, 0, null);
        var c = scontro(g, new ArrayList<>(List.of(a, b)), x -> AzioneNemico.ATTACCO);
        c.attaccaBersaglio(a);
        assertSame(b, c.getPersonaggioDiTurno());
        assertThrows(IllegalArgumentException.class, () -> c.attaccaBersaglio(a));
        assertThrows(IllegalArgumentException.class, () -> c.attaccaBersaglio(b));
        assertSame(b, c.getPersonaggioDiTurno());
        assertEquals(30, g.getSalute());
    }

    @Test
    void consumabileFallitoNonAvanzaTurno() {
        var g = new Giocatore("Ada", 30, 5, 0);
        var n = new Nemico("N", 30, 2, 0, null);
        var c = scontro(g, new ArrayList<>(List.of(n)), x -> AzioneNemico.ATTACCO);
        var pozione = new ConsumabileCurativo("Pozione", 10);
        assertFalse(c.usaConsumabile(pozione));
        assertSame(g, c.getPersonaggioDiTurno());
        assertEquals(30, g.getSalute());
        c.alzaDifesa();
        assertThrows(IllegalStateException.class, () -> c.usaConsumabile(pozione));
        assertSame(n, c.getPersonaggioDiTurno());
    }

    @Test
    void difesaProteggeDaPiuNemiciNelMedesimoGiro() {
        var g = new Giocatore("Ada", 30, 5, 0);
        var a = new Nemico("A", 30, 5, 0, null);
        var b = new Nemico("B", 30, 5, 0, null);
        var c = scontro(g, new ArrayList<>(List.of(a, b)), x -> AzioneNemico.ATTACCO);
        c.alzaDifesa();
        c.eseguiTurnoNemico();
        c.eseguiTurnoNemico();
        assertEquals(26, g.getSalute());
        assertSame(g, c.getPersonaggioDiTurno());
    }

    @Test
    void difesaDimezzaDannoDispariEScadeAlTurnoSuccessivo() {
        var g = new Giocatore("Ada", 30, 5, 0);
        var n = new Nemico("N", 30, 5, 0, null);
        var c = scontro(g, new ArrayList<>(List.of(n)), x -> AzioneNemico.ATTACCO);
        c.alzaDifesa();
        c.eseguiTurnoNemico();
        assertEquals(28, g.getSalute());
        c.attaccaBersaglio(n);
        c.eseguiTurnoNemico();
        assertEquals(23, g.getSalute());
    }

    @Test
    void strategiaNemicaPuoScegliereDifesa() {
        var g = new Giocatore("Ada", 30, 5, 0);
        var n = new Nemico("N", 30, 5, 0, null);
        var c = scontro(g, new ArrayList<>(List.of(n)), x -> AzioneNemico.DIFESA);
        c.alzaDifesa();
        c.eseguiTurnoNemico();
        assertEquals(30, g.getSalute());
        assertSame(g, c.getPersonaggioDiTurno());
    }

    @Test
    void bottinoENemiciViviConservanoOrdineEProteggonoListe() {
        var g = new Giocatore("Ada", 30, 10, 0);
        var premio = new Arma("Premio", 1);
        var a = new Nemico("A", 5, 1, 0, premio);
        var b = new Nemico("B", 5, 1, 0, null);
        var c = new Nemico("C", 5, 1, 0, premio);
        var s = scontro(g, new ArrayList<>(List.of(a, b, c)), x -> AzioneNemico.ATTACCO);
        assertEquals(List.of(a, b, c), s.getNemiciVivi());
        assertThrows(UnsupportedOperationException.class, () -> s.getNemiciVivi().clear());
        assertThrows(IllegalStateException.class, s::getBottino);
        a.subisciDanno(5);
        assertEquals(List.of(b, c), s.getNemiciVivi());
        b.subisciDanno(5); c.subisciDanno(5);
        assertEquals(List.of(premio, premio), s.getBottino());
        assertThrows(UnsupportedOperationException.class, () -> s.getBottino().clear());
    }

    @Test
    void calcolatoreIncludeEquipaggiamentoENonProduceDannoNegativo() {
        var g = new Giocatore("Ada", 20, 4, 0);
        var n = new Nemico("N", 20, 1, 6, null);
        var calcolo = new CalcolatoreDannoBase();
        assertEquals(0, calcolo.calcolaDanno(g, n));
        var arma = new Arma("Spada", 5);
        g.getInventario().inserisciOggetto(arma); g.equipaggiaArma(arma);
        assertEquals(3, calcolo.calcolaDanno(g, n));
        assertThrows(NullPointerException.class, () -> calcolo.calcolaDanno(null, n));
    }

    @Test
    void utilityConfrontaIdentitaEIgnoraNull() {
        var a = new Arma("Uguale", 1);
        assertFalse(Utility.trovaDuplicati(Arrays.asList(a, new Arma("Uguale", 1), null, null)));
        assertTrue(Utility.trovaDuplicati(List.of(a, a)));
    }

    @Test
    void strategiaBaseRifiutaNull() {
        assertThrows(NullPointerException.class, () -> new StrategiaNemicoBase().scegliAzione(null));
    }
}
