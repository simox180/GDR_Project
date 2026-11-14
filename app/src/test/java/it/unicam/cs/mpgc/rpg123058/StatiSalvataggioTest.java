package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoGiocatore;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoPartita;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StatiSalvataggioTest {
    private StatoGiocatore giocatore() {
        return new StatoGiocatore("Ada", 75, null, null, Map.of());
    }

    @Test
    void giocatoreAccettaDatiValidiESenzaEquipaggiamento() {
        StatoGiocatore stato = giocatore();
        assertEquals("Ada", stato.nome());
        assertEquals(75, stato.salute());
        assertNull(stato.armaEquipaggiata());
        assertNull(stato.armaturaEquipaggiata());
        assertEquals(Map.of(), stato.inventario());
        assertEquals(Map.of("pane", 2), new StatoGiocatore("Ada", 101, "spada", "scudo", Map.of("pane", 2)).inventario());
    }

    @Test
    void giocatoreRifiutaCampiInvalidi() {
        assertThrows(NullPointerException.class, () -> new StatoGiocatore(null, 1, null, null, Map.of()));
        for (String nome : new String[]{"", " \t"})
            assertThrows(IllegalArgumentException.class, () -> new StatoGiocatore(nome, 1, null, null, Map.of()));
        for (int salute : new int[]{0, -1})
            assertThrows(IllegalArgumentException.class, () -> new StatoGiocatore("Ada", salute, null, null, Map.of()));
        assertThrows(NullPointerException.class, () -> new StatoGiocatore("Ada", 1, null, null, null));
        Map<String, Integer> chiaveNulla = new HashMap<>();
        chiaveNulla.put(null, 1);
        assertThrows(NullPointerException.class, () -> new StatoGiocatore("Ada", 1, null, null, chiaveNulla));
        Map<String, Integer> valoreNullo = new HashMap<>();
        valoreNullo.put("acqua", null);
        assertThrows(NullPointerException.class, () -> new StatoGiocatore("Ada", 1, null, null, valoreNullo));
        assertThrows(IllegalArgumentException.class, () -> new StatoGiocatore("Ada", 1, null, null, Map.of(" ", 1)));
        for (int quantita : new int[]{0, -1})
            assertThrows(IllegalArgumentException.class, () -> new StatoGiocatore("Ada", 1, null, null, Map.of("pane", quantita)));
        assertThrows(IllegalArgumentException.class, () -> new StatoGiocatore("Ada", 1, " ", null, Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new StatoGiocatore("Ada", 1, null, " ", Map.of()));
    }

    @Test
    void giocatoreCopiaInventarioEloEsponeImmutabile() {
        Map<String, Integer> inventario = new HashMap<>(Map.of("pane", 2));
        StatoGiocatore stato = new StatoGiocatore("Ada", 75, null, null, inventario);
        inventario.put("pane", 9);
        assertEquals(Map.of("pane", 2), stato.inventario());
        assertThrows(UnsupportedOperationException.class, () -> stato.inventario().put("acqua", 1));
    }

    @Test
    void partitaAccettaEventiVuotiERifiutaCampiInvalidi() {
        assertEquals(Set.of(), new StatoPartita(1, "inizio", Set.of(), giocatore()).eventiCompletati());
        assertEquals(Set.of("raccolta"), new StatoPartita(2, "inizio", Set.of("raccolta"), giocatore()).eventiCompletati());
        for (int versione : new int[]{0, -1})
            assertThrows(IllegalArgumentException.class, () -> new StatoPartita(versione, "inizio", Set.of(), giocatore()));
        assertThrows(NullPointerException.class, () -> new StatoPartita(1, null, Set.of(), giocatore()));
        assertThrows(IllegalArgumentException.class, () -> new StatoPartita(1, " \t", Set.of(), giocatore()));
        assertThrows(NullPointerException.class, () -> new StatoPartita(1, "inizio", null, giocatore()));
        Set<String> eventoNullo = new HashSet<>();
        eventoNullo.add(null);
        assertThrows(NullPointerException.class, () -> new StatoPartita(1, "inizio", eventoNullo, giocatore()));
        assertThrows(IllegalArgumentException.class, () -> new StatoPartita(1, "inizio", Set.of(" "), giocatore()));
        assertThrows(NullPointerException.class, () -> new StatoPartita(1, "inizio", Set.of(), null));
    }

    @Test
    void partitaCopiaEventiEliEsponeImmutabili() {
        Set<String> eventi = new HashSet<>(Set.of("raccolta"));
        StatoPartita stato = new StatoPartita(1, "inizio", eventi, giocatore());
        eventi.add("vittoria");
        assertEquals(Set.of("raccolta"), stato.eventiCompletati());
        assertThrows(UnsupportedOperationException.class, () -> stato.eventiCompletati().add("altro"));
    }
}
