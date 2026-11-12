package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NarrazioneTest {
    @Test
    void scelteRifiutanoCampiNulliEVuoti() {
        assertThrows(NullPointerException.class, () -> new SceltaNavigazione(null, "Vai", "b"));
        assertThrows(IllegalArgumentException.class, () -> new SceltaNavigazione(" ", "Vai", "b"));
        assertThrows(IllegalArgumentException.class, () -> new SceltaRaccolta("r", " ", "o"));
        assertThrows(IllegalArgumentException.class, () -> new SceltaCombattimento("c", "Lotta", " "));
    }

    @Test
    void opzioneRichiedeTuttiGliEventiEdEscludeQuelliCompletati() {
        var scelta = new SceltaNavigazione("n", "Vai", "b");
        var opzione = new OpzioneScena(scelta, Set.of("a", "b"), Set.of("x"));
        assertFalse(opzione.isDisponibile(Set.of("a")));
        assertTrue(opzione.isDisponibile(Set.of("a", "b", "estraneo")));
        assertFalse(opzione.isDisponibile(Set.of("a", "b", "x")));
        assertTrue(new OpzioneScena(scelta, Set.of(), Set.of()).isDisponibile(Set.of()));
        assertThrows(NullPointerException.class, () -> opzione.isDisponibile(null));
    }

    @Test
    void opzioneValidaCondizioniECopiaISet() {
        var scelta = new SceltaNavigazione("n", "Vai", "b");
        var richiesti = new HashSet<>(Set.of("a"));
        var esclusi = new HashSet<>(Set.of("x"));
        var opzione = new OpzioneScena(scelta, richiesti, esclusi);
        richiesti.clear();
        esclusi.clear();
        assertEquals(Set.of("a"), opzione.eventiRichiesti());
        assertEquals(Set.of("x"), opzione.eventiEsclusi());
        assertThrows(UnsupportedOperationException.class, () -> opzione.eventiRichiesti().add("b"));
        assertThrows(NullPointerException.class, () -> new OpzioneScena(null, Set.of(), Set.of()));
        assertThrows(NullPointerException.class, () -> new OpzioneScena(scelta, null, Set.of()));
        assertThrows(NullPointerException.class, () -> new OpzioneScena(scelta, Set.of(), null));
        assertThrows(NullPointerException.class, () -> new OpzioneScena(scelta, new HashSet<>(Arrays.asList((String) null)), Set.of()));
        assertThrows(IllegalArgumentException.class, () -> new OpzioneScena(scelta, Set.of(" "), Set.of()));
        assertThrows(IllegalArgumentException.class, () -> new OpzioneScena(scelta, Set.of(), Set.of(" ")));
        assertThrows(IllegalArgumentException.class, () -> new OpzioneScena(scelta, Set.of("a"), Set.of("a")));
    }

    @Test
    void scenaFiltraInOrdineSenzaModificareOrigineOProgressi() {
        var prima = new SceltaNavigazione("a", "Prima", "b");
        var seconda = new SceltaRaccolta("b", "Seconda", "o");
        var terza = new SceltaCombattimento("c", "Terza", "i");
        var opzioni = new ArrayList<>(List.of(new OpzioneScena(prima, Set.of(), Set.of()),
                new OpzioneScena(seconda, Set.of("ok"), Set.of()),
                new OpzioneScena(terza, Set.of(), Set.of())));
        var scena = new Scena("s", "Titolo", "/Immagini/prova.png", "Testo", opzioni);
        opzioni.clear();
        var eventi = new HashSet<>(Set.of("ok"));
        var visibili = scena.getScelteDisponibili(eventi);
        assertEquals(List.of(prima, seconda, terza), visibili);
        assertEquals(Set.of("ok"), eventi);
        assertThrows(UnsupportedOperationException.class, () -> visibili.clear());
        assertEquals(List.of(prima, terza), scena.getScelteDisponibili(Set.of()));
        assertEquals(List.of(prima, seconda, terza), scena.getScelteDisponibili(eventi));
    }

    @Test
    void scenaRifiutaDatiOpzioniEIdDuplicati() {
        var opzione = new OpzioneScena(new SceltaNavigazione("id", "Vai", "b"), Set.of(), Set.of());
        assertThrows(NullPointerException.class, () -> new Scena(null, "t", "/Immagini/prova.png", "d", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Scena(" ", "t", "/Immagini/prova.png", "d", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Scena("s", " ", "/Immagini/prova.png", "d", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Scena("s", "t", "/Immagini/prova.png", " ", List.of()));
        assertThrows(NullPointerException.class, () -> new Scena("s", "t", "/Immagini/prova.png", "d", null));
        assertThrows(NullPointerException.class, () -> new Scena("s", "t", "/Immagini/prova.png", "d", Arrays.asList((OpzioneScena) null)));
        assertThrows(IllegalArgumentException.class, () -> new Scena("s", "t", "/Immagini/prova.png", "d", List.of(opzione,
                new OpzioneScena(new SceltaRaccolta("id", "Altro", "o"), Set.of(), Set.of()))));
        assertTrue(new Scena("s", "t", "/Immagini/prova.png", "d", List.of()).getScelteDisponibili(Set.of()).isEmpty());
        assertThrows(NullPointerException.class, () -> new Scena("s", "t", "/Immagini/prova.png", "d", List.of()).getScelteDisponibili(null));
    }

    @Test
    void scenaConservaPercorsoImmagineEDescrizioneDistinti() {
        var scena = new Scena("s", "Titolo", "/Immagini/prova.png", "Descrizione", List.of());
        assertEquals("/Immagini/prova.png", scena.getPercorsoImmagine());
        assertEquals("Descrizione", scena.getDescrizione());
    }

    @Test
    void scenaRifiutaPercorsoImmagineNulloOVuoto() {
        assertThrows(NullPointerException.class, () -> new Scena("s", "t", null, "d", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Scena("s", "t", " \t ", "d", List.of()));
    }
}
