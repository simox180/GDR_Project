package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurniSequenziale;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.AzioneNemico;
import it.unicam.cs.mpgc.rpg123058.Configurazione.CatalogoOggetti;
import it.unicam.cs.mpgc.rpg123058.Configurazione.ConfigurazionePartita;
import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.Scelta;
import it.unicam.cs.mpgc.rpg123058.Persistenza.CaricatoreScene;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurazionePartitaIntegrazioneTest {
    private final ConfigurazionePartita configurazione = new ConfigurazionePartita();

    private List<String> ids(Partita partita) {
        return partita.getScelteDisponibili().stream().map(Scelta::id).toList();
    }

    private void scegli(Partita partita, String id) {
        Scelta scelta = partita.getScelteDisponibili().stream().filter(s -> s.id().equals(id)).findFirst().orElseThrow();
        partita.eseguiScelta(scelta);
    }

    @Test
    void nuovaPartitaParteDallaScenaESceltePreviste() {
        Partita partita = configurazione.creaNuovaPartita("Ada");
        assertEquals("sotto_le_macerie", partita.getScenaCorrente().getId());
        assertEquals(List.of("raccogli_acqua_macerie", "raccogli_ascia_macerie", "vai_strada_interrotta"), ids(partita));
        assertFalse(partita.isInCombattimento());
        assertFalse(partita.isSconfitta());
        assertEquals(Set.of(), partita.getEventiCompletati());
    }

    @Test
    void rifiutaNomiInvalidi() {
        assertThrows(NullPointerException.class, () -> configurazione.creaNuovaPartita(null));
        assertThrows(IllegalArgumentException.class, () -> configurazione.creaNuovaPartita(""));
        assertThrows(IllegalArgumentException.class, () -> configurazione.creaNuovaPartita("  "));
    }

    @Test
    void dueNuovePartiteHannoProgressiIndipendentiERaccoltaSingola() {
        Partita prima = configurazione.creaNuovaPartita("Ada");
        Partita seconda = configurazione.creaNuovaPartita("Balin");
        Scelta acqua = prima.getScelteDisponibili().get(0);
        scegli(prima, "raccogli_acqua_macerie");
        assertEquals(Set.of("raccogli_acqua_macerie"), prima.getEventiCompletati());
        assertFalse(ids(prima).contains("raccogli_acqua_macerie"));
        assertThrows(IllegalArgumentException.class, () -> prima.eseguiScelta(acqua));
        assertEquals(Set.of(), seconda.getEventiCompletati());
        assertEquals(List.of("raccogli_acqua_macerie", "raccogli_ascia_macerie", "vai_strada_interrotta"), ids(seconda));
        scegli(prima, "vai_strada_interrotta");
        assertEquals("strada_interrotta", prima.getScenaCorrente().getId());
        assertEquals("sotto_le_macerie", seconda.getScenaCorrente().getId());
    }

    @Test
    void partitaRifiutaDestinazioneInesistenteAlMomentoDellaScelta() {
        var scena = new CaricatoreScene().caricaScena("fixtures/valid.json");
        Partita partita = new Partita(new Giocatore("Ada", 100, 8, 2), List.of(scena), scena.getId(), Map.of(), Map.of());
        assertThrows(IllegalArgumentException.class, () -> scegli(partita, "vai"));
        assertEquals(scena.getId(), partita.getScenaCorrente().getId());
    }

    // Usa i dieci JSON reali. Solo gli incontri sono deterministici: questo verifica i collegamenti
    // e le condizioni narrative, non il bilanciamento dei combattimenti della configurazione reale.
    private Partita narrazioneDeterministica() {
        List<String> nomi = List.of("sotto_le_macerie", "strada_interrotta", "segni_sulle_pareti",
                "galleria_echi", "sala_registri", "compagno_ferito", "ultima_riserva",
                "qualcosa_nelle_profondita", "ponte_khazad_dum", "primo_sole");
        var scene = new CaricatoreScene().caricaScene(nomi.stream().map(n -> n + ".json").toList());
        var giocatore = new Giocatore("Ada", 100, 8, 2);
        java.util.function.Function<Giocatore, Combattimento> incontro = g -> new Combattimento(g,
                List.of(new Nemico("Guardia di prova", 10, 1, 0, null)),
                GestoreTurniSequenziale::new, (a, b) -> 10, n -> AzioneNemico.DIFESA);
        return new Partita(giocatore, scene, "sotto_le_macerie", new CatalogoOggetti().creaCatalogo(),
                Map.of("orchi_galleria", incontro, "orchi_inseguitori", incontro, "capitano_orchi", incontro));
    }

    private void vinci(Partita partita, String scelta, String idIncontro) {
        scegli(partita, scelta);
        assertTrue(partita.isInCombattimento());
        assertEquals(List.of(), ids(partita));
        assertThrows(IllegalStateException.class, () -> partita.eseguiScelta(
                new it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaNavigazione("bloccata", "Vai", "primo_sole")));
        partita.attaccaBersaglio(partita.getNemiciVivi().get(0));
        assertFalse(partita.isInCombattimento());
        assertTrue(partita.getEventiCompletati().contains(idIncontro));
        assertFalse(partita.getEventiCompletati().contains("vittoria:" + idIncontro));
        assertFalse(ids(partita).contains(scelta));
    }

    @Test
    void sceneSecondariePercorribiliPrimaEDopoLeVittorieEFinoAllepilogo() {
        Partita partita = narrazioneDeterministica();
        scegli(partita, "vai_strada_interrotta");
        scegli(partita, "esplora_posto_guardia");
        assertEquals("segni_sulle_pareti", partita.getScenaCorrente().getId());
        scegli(partita, "raccogli_pane_posto_guardia");
        assertTrue(partita.getEventiCompletati().contains("raccogli_pane_posto_guardia"));
        scegli(partita, "torna_strada_interrotta");
        scegli(partita, "scendi_galleria_echi");
        assertEquals(List.of("affronta_pattuglia_galleria", "esplora_sala_registri"), ids(partita));
        scegli(partita, "esplora_sala_registri");
        scegli(partita, "torna_galleria_echi");
        vinci(partita, "affronta_pattuglia_galleria", "orchi_galleria");
        assertEquals(List.of("esplora_sala_registri", "raggiungi_compagno"), ids(partita));
        scegli(partita, "esplora_sala_registri");
        scegli(partita, "raccogli_acqua_registri");
        scegli(partita, "raccogli_corazza_registri");
        scegli(partita, "torna_galleria_echi");
        scegli(partita, "raggiungi_compagno");
        assertEquals(List.of("affronta_inseguitori", "esplora_ultima_riserva"), ids(partita));
        scegli(partita, "esplora_ultima_riserva");
        scegli(partita, "torna_compagno");
        vinci(partita, "affronta_inseguitori", "orchi_inseguitori");
        assertEquals(List.of("esplora_ultima_riserva", "prosegui_profondita"), ids(partita));
        scegli(partita, "esplora_ultima_riserva");
        scegli(partita, "raccogli_pane_riserva");
        scegli(partita, "raccogli_acqua_riserva");
        scegli(partita, "torna_compagno");
        scegli(partita, "prosegui_profondita");
        assertEquals("qualcosa_nelle_profondita", partita.getScenaCorrente().getId());
        scegli(partita, "fuggi_ponte");
        assertEquals(List.of("affronta_capitano"), ids(partita));
        vinci(partita, "affronta_capitano", "capitano_orchi");
        assertEquals(List.of("attraversa_ponte"), ids(partita));
        scegli(partita, "attraversa_ponte");
        assertEquals("primo_sole", partita.getScenaCorrente().getId());
        assertEquals(List.of(), ids(partita));
        assertFalse(partita.isSconfitta());
    }
}
