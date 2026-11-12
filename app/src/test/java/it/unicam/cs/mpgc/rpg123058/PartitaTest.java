package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.CalcolatoreDannoBase;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurniSequenziale;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.AzioneNemico;
import it.unicam.cs.mpgc.rpg123058.Dominio.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PartitaTest {
    private static final String RACCOLTA = "prendi";
    private static final String VITTORIA = "guardia";

    private Combattimento incontro(Giocatore giocatore, Oggetto bottino) {
        return new Combattimento(giocatore, List.of(new Nemico("Guardia", 5, 1, 0, bottino)),
                GestoreTurniSequenziale::new, new CalcolatoreDannoBase(), n -> AzioneNemico.ATTACCO);
    }

    @Test
    void percorsoIntegratoRaccoglieVinceESbloccaNavigazione() {
        var giocatore = new Giocatore("Ada", 30, 10, 0);
        var oggetto = new Arma("Chiave", 1);
        var bottino = new Armatura("Trofeo", 0);
        var raccolta = new SceltaRaccolta("prendi", "Prendi", "chiave");
        var battaglia = new SceltaCombattimento("lotta", "Lotta", "guardia");
        var uscita = new SceltaNavigazione("esci", "Esci", "fine");
        var inizio = new Scena("inizio", "Inizio", "/Immagini/inizio.png", "Descrizione", List.of(
                new OpzioneScena(raccolta, Set.of(), Set.of(RACCOLTA)),
                new OpzioneScena(battaglia, Set.of(RACCOLTA), Set.of(VITTORIA)),
                new OpzioneScena(uscita, Set.of(VITTORIA), Set.of())));
        var fine = new Scena("fine", "Fine", "/Immagini/fine.png", "Conclusione", List.of());
        var scene = new ArrayList<>(List.of(inizio, fine));
        var oggetti = new HashMap<String, Oggetto>(Map.of("chiave", oggetto));
        var incontri = new HashMap<String, Function<Giocatore, Combattimento>>(Map.of("guardia", g -> incontro(g, bottino)));
        var partita = new Partita(giocatore, scene, "inizio", oggetti, incontri);
        scene.clear(); oggetti.clear(); incontri.clear();

        assertEquals(List.of(raccolta), partita.getScelteDisponibili());
        assertThrows(IllegalArgumentException.class, () -> partita.eseguiScelta(new SceltaRaccolta("prendi", "Diverso", "chiave")));
        assertEquals(Set.of(), partita.getEventiCompletati());
        partita.eseguiScelta(raccolta);
        assertSame(inizio, partita.getScenaCorrente());
        assertEquals(Set.of(RACCOLTA), partita.getEventiCompletati());
        assertThrows(UnsupportedOperationException.class, () -> partita.getEventiCompletati().add("falso"));
        assertEquals(1, giocatore.getInventario().consultaInventario().size());
        assertEquals(List.of(battaglia), partita.getScelteDisponibili());
        assertThrows(IllegalArgumentException.class, () -> partita.eseguiScelta(raccolta));
        partita.eseguiScelta(battaglia);
        assertTrue(partita.isInCombattimento());
        assertEquals(Set.of(RACCOLTA), partita.getEventiCompletati());
        assertTrue(partita.getScelteDisponibili().isEmpty());
        assertThrows(IllegalStateException.class, () -> partita.eseguiScelta(uscita));
        assertSame(giocatore, partita.getPersonaggioDiTurno());
        partita.attaccaBersaglio(partita.getNemiciVivi().get(0));
        assertFalse(partita.isInCombattimento());
        assertSame(inizio, partita.getScenaCorrente());
        assertEquals(Set.of(RACCOLTA, VITTORIA), partita.getEventiCompletati());
        assertEquals(2, giocatore.getInventario().consultaInventario().size());
        assertEquals(List.of(uscita), partita.getScelteDisponibili());
        assertThrows(IllegalArgumentException.class, () -> partita.eseguiScelta(battaglia));
        assertThrows(IllegalStateException.class, () -> partita.alzaDifesa());
        assertThrows(IllegalStateException.class, () -> partita.eseguiTurnoNemico());
        assertThrows(IllegalStateException.class, () -> partita.getNemiciVivi());
        partita.eseguiScelta(uscita);
        assertSame(fine, partita.getScenaCorrente());
    }

    @Test
    void raccolteDistinteDelloStessoOggettoSonoConsentite() {
        var giocatore = new Giocatore("Ada", 30, 10, 0);
        var a = new SceltaRaccolta("a", "Uno", "o");
        var b = new SceltaRaccolta("b", "Due", "o");
        var scena = new Scena("s", "T", "/Immagini/prova.png", "D", List.of(new OpzioneScena(a, Set.of(), Set.of()), new OpzioneScena(b, Set.of(), Set.of())));
        var partita = new Partita(giocatore, List.of(scena), "s", Map.of("o", new Arma("Spada", 1)), Map.of());
        partita.eseguiScelta(a);
        assertThrows(IllegalStateException.class, () -> partita.eseguiScelta(a));
        partita.eseguiScelta(b);
        assertEquals(2, giocatore.getInventario().consultaInventario().get(0).getQuantita());
        assertEquals(Set.of("a", "b"), partita.getEventiCompletati());
    }

    @Test
    void riferimentiMancantiEFactoryNullNonCambianoProgressi() {
        var giocatore = new Giocatore("Ada", 30, 10, 0);
        var r = new SceltaRaccolta("r", "R", "assente");
        var c = new SceltaCombattimento("c", "C", "assente");
        var n = new SceltaNavigazione("n", "N", "assente");
        var z = new SceltaCombattimento("z", "Z", "nullo");
        var scena = new Scena("s", "T", "/Immagini/prova.png", "D", List.of(r, c, n, z).stream().map(x -> new OpzioneScena(x, Set.of(), Set.of())).toList());
        var partita = new Partita(giocatore, List.of(scena), "s", Map.of(), Map.of("nullo", g -> null));
        for (var scelta : List.of(r, c, n)) assertThrows(IllegalArgumentException.class, () -> partita.eseguiScelta(scelta));
        assertThrows(IllegalStateException.class, () -> partita.eseguiScelta(z));
        assertSame(scena, partita.getScenaCorrente());
        assertTrue(giocatore.getInventario().consultaInventario().isEmpty());
        assertTrue(partita.getEventiCompletati().isEmpty());
        assertFalse(partita.isInCombattimento());
    }

    @Test
    void costruttoreRifiutaDatiInvalidi() {
        var g = new Giocatore("Ada", 10, 2, 0);
        var s = new Scena("s", "T", "/Immagini/prova.png", "D", List.of());
        assertThrows(NullPointerException.class, () -> new Partita(null, List.of(s), "s", Map.of(), Map.of()));
        assertThrows(NullPointerException.class, () -> new Partita(g, null, "s", Map.of(), Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new Partita(g, List.of(s), " ", Map.of(), Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new Partita(g, List.of(s), "x", Map.of(), Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new Partita(g, List.of(s, s), "s", Map.of(), Map.of()));
        assertThrows(NullPointerException.class, () -> new Partita(g, Arrays.asList((Scena) null), "s", Map.of(), Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new Partita(g, List.of(s), "s", Map.of(" ", new Arma("A", 1)), Map.of()));
        assertThrows(NullPointerException.class, () -> new Partita(g, List.of(s), "s", Collections.singletonMap("x", null), Map.of()));
        assertThrows(NullPointerException.class, () -> new Partita(g, List.of(s), "s", Map.of(), Collections.singletonMap("x", null)));
        assertThrows(IllegalArgumentException.class, () -> new Partita(g, List.of(s), "s", Map.of(), Map.of(" ", x -> incontro(x, null))));
        g.subisciDanno(10);
        assertThrows(IllegalArgumentException.class, () -> new Partita(g, List.of(s), "s", Map.of(), Map.of()));
    }

    @Test
    void sconfittaNonAssegnaBottinoNeSbloccaScelte() {
        var g = new Giocatore("Ada", 1, 1, 0);
        var bottino = new Arma("Premio", 1);
        var lotta = new SceltaCombattimento("lotta", "Lotta", "i");
        var uscita = new SceltaNavigazione("via", "Via", "s");
        var s = new Scena("s", "T", "/Immagini/prova.png", "D", List.of(
                new OpzioneScena(lotta, Set.of(), Set.of()),
                new OpzioneScena(uscita, Set.of("vittoria:i"), Set.of())));
        var p = new Partita(g, List.of(s), "s", Map.of(), Map.of("i", x ->
                new Combattimento(x, List.of(new Nemico("N", 10, 10, 0, bottino)),
                        GestoreTurniSequenziale::new, new CalcolatoreDannoBase(), n -> AzioneNemico.ATTACCO)));
        p.eseguiScelta(lotta);
        p.alzaDifesa();
        p.eseguiTurnoNemico();
        assertTrue(p.isSconfitta());
        assertFalse(p.isInCombattimento());
        assertSame(s, p.getScenaCorrente());
        assertTrue(p.getScelteDisponibili().isEmpty());
        assertTrue(p.getEventiCompletati().isEmpty());
        assertTrue(g.getInventario().consultaInventario().isEmpty());
        assertThrows(IllegalStateException.class, () -> p.eseguiScelta(lotta));
        assertThrows(IllegalStateException.class, () -> p.getPersonaggioDiTurno());
    }
}
