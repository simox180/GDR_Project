package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurniSequenziale;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.AzioneNemico;
import it.unicam.cs.mpgc.rpg123058.Configurazione.CatalogoOggetti;
import it.unicam.cs.mpgc.rpg123058.Configurazione.ConfigurazionePartita;
import it.unicam.cs.mpgc.rpg123058.Dominio.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.*;
import it.unicam.cs.mpgc.rpg123058.Persistenza.CaricatoreScene;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class RipristinoPartitaTest {
    @TempDir Path directory;
    private final ConfigurazionePartita configurazione = new ConfigurazionePartita();

    private static Scelta scelta(Partita partita, String id) {
        return partita.getScelteDisponibili().stream().filter(s -> s.id().equals(id)).findFirst().orElseThrow();
    }

    private static void scegli(Partita partita, String id) {
        partita.eseguiScelta(scelta(partita, id));
    }

    private StatoPartita stato(String scena, int salute, String arma, String armatura, Map<String, Integer> inventario) {
        return new StatoPartita(1, scena, Set.of("raccogli_acqua_macerie"),
                new StatoGiocatore("Ada", salute, arma, armatura, inventario));
    }

    @Test
    void configurazioneCreaNuovaPartitaEsportabile() {
        StatoPartita stato = configurazione.creaNuovaPartita("Ada").esportaStato();
        assertEquals(1, stato.versione());
        assertEquals("sotto_le_macerie", stato.idScenaCorrente());
        assertEquals(Set.of(), stato.eventiCompletati());
        assertEquals(new StatoGiocatore("Ada", 100, null, null, Map.of()), stato.giocatore());
    }

    @Test
    void ripristinoRifiutaStatiIncoerenti() {
        assertThrows(NullPointerException.class, () -> configurazione.ripristinaPartita(null));
        assertThrows(IllegalArgumentException.class, () -> configurazione.ripristinaPartita(
                new StatoPartita(2, "sotto_le_macerie", Set.of(), new StatoGiocatore("Ada", 1, null, null, Map.of()))));
        assertThrows(IllegalArgumentException.class, () -> configurazione.ripristinaPartita(stato("inesistente", 50, null, null, Map.of())));
        assertThrows(IllegalArgumentException.class, () -> configurazione.ripristinaPartita(stato("sotto_le_macerie", 101, null, null, Map.of())));
        assertThrows(IllegalArgumentException.class, () -> configurazione.ripristinaPartita(stato("sotto_le_macerie", 50, null, null, Map.of("inesistente", 1))));
        assertThrows(IllegalArgumentException.class, () -> configurazione.ripristinaPartita(stato("sotto_le_macerie", 50, "pane", null, Map.of())));
        assertThrows(IllegalArgumentException.class, () -> configurazione.ripristinaPartita(stato("sotto_le_macerie", 50, null, "spada_nanica", Map.of())));
    }

    @Test
    void ripristinoPreservaQuantitaEquipaggiamentoEdEIndipendente() {
        StatoPartita stato = stato("sotto_le_macerie", 64, "spada_nanica", "scudo_nanico",
                Map.of("spada_nanica", 2, "scudo_nanico", 1, "pane", 3));
        Partita prima = configurazione.ripristinaPartita(stato);
        Partita seconda = configurazione.ripristinaPartita(stato);
        assertEquals(stato, prima.esportaStato());
        assertEquals(stato, seconda.esportaStato());
        assertEquals("sotto_le_macerie", prima.getScenaCorrente().getId());
        assertFalse(prima.getScelteDisponibili().stream().anyMatch(s -> s.id().equals("raccogli_acqua_macerie")));
        scegli(prima, "raccogli_ascia_macerie");
        assertEquals(1, prima.esportaStato().giocatore().inventario().get("ascia_nanica_danneggiata"));
        assertEquals(stato, seconda.esportaStato());
        scegli(prima, "vai_strada_interrotta");
        assertEquals("strada_interrotta", prima.getScenaCorrente().getId());
        assertEquals(stato, seconda.esportaStato());
    }

    @Test
    void costruttoriEdEsportazioneCopianoEventiEStato() {
        Arma arma = new Arma("Spada", 2);
        Giocatore giocatore = new Giocatore("Ada", 30, 5, 0);
        SceltaRaccolta raccogli = new SceltaRaccolta("prendi", "Prendi", "spada");
        Scena scena = new Scena("inizio", "Inizio", "/Immagini/prova.png", "D",
                List.of(new OpzioneScena(raccogli, Set.of(), Set.of())));
        Set<String> eventi = new HashSet<>(Set.of("fatto"));
        Partita partita = new Partita(giocatore, List.of(scena), "inizio", Map.of("spada", arma), Map.of(), eventi);
        eventi.add("nuovo");
        assertEquals(Set.of("fatto"), partita.getEventiCompletati());
        StatoPartita prima = partita.esportaStato();
        assertEquals(Set.of("fatto"), prima.eventiCompletati());
        partita.eseguiScelta(raccogli);
        giocatore.subisciDanno(4);
        assertEquals(new StatoGiocatore("Ada", 30, null, null, Map.of()), prima.giocatore());
        assertEquals(Set.of("fatto"), prima.eventiCompletati());
        assertEquals(new StatoGiocatore("Ada", 26, null, null, Map.of("spada", 1)), partita.esportaStato().giocatore());
        Partita nuova = new Partita(new Giocatore("Ada", 30, 5, 0), List.of(scena), "inizio", Map.of("spada", arma), Map.of());
        assertEquals(Set.of(), nuova.getEventiCompletati());
    }

    @Test
    void equipaggiamentiNonSonoDuplicatiNelloZainoEIdentitaCatalogoConta() {
        Arma arma = new Arma("Spada", 2);
        Armatura armatura = new Armatura("Scudo", 1);
        Giocatore giocatore = new Giocatore("Ada", 30, 5, 0);
        Scena scena = new Scena("s", "T", "/Immagini/prova.png", "D", List.of());
        Partita partita = new Partita(giocatore, List.of(scena), "s", Map.of("arma", arma, "armatura", armatura), Map.of());
        giocatore.getInventario().inserisciOggetto(arma);
        giocatore.getInventario().inserisciOggetto(arma);
        giocatore.getInventario().inserisciOggetto(armatura);
        giocatore.equipaggiaArma(arma);
        giocatore.equipaggiaArmatura(armatura);
        assertEquals(new StatoGiocatore("Ada", 30, "arma", "armatura", Map.of("arma", 1)), partita.esportaStato().giocatore());
        giocatore.getInventario().inserisciOggetto(new Arma("Estranea", 2));
        assertThrows(IllegalStateException.class, partita::esportaStato);

        Giocatore copiaPerNome = new Giocatore("Ada", 30, 5, 0);
        copiaPerNome.getInventario().inserisciOggetto(new Arma("Spada", 2));
        Partita oggettoNonIdentico = new Partita(copiaPerNome, List.of(scena), "s", Map.of("arma", arma), Map.of());
        assertThrows(IllegalStateException.class, oggettoNonIdentico::esportaStato);

        Giocatore altro = new Giocatore("Ada", 30, 5, 0);
        altro.getInventario().inserisciOggetto(arma);
        Partita ambigua = new Partita(altro, List.of(scena), "s", Map.of("uno", arma, "due", arma), Map.of());
        assertThrows(IllegalStateException.class, ambigua::esportaStato);
    }

    @Test
    void esportazioneVietataInCombattimentoEDopoSconfitta() {
        Giocatore giocatore = new Giocatore("Ada", 10, 5, 0);
        SceltaCombattimento lotta = new SceltaCombattimento("lotta", "Lotta", "i");
        Scena scena = new Scena("s", "T", "/Immagini/prova.png", "D", List.of(new OpzioneScena(lotta, Set.of(), Set.of())));
        Function<Giocatore, Combattimento> incontro = g -> new Combattimento(g,
                List.of(new Nemico("N", 20, 5, 0, null)), GestoreTurniSequenziale::new, (a, b) -> 10, n -> AzioneNemico.ATTACCO);
        Partita partita = new Partita(giocatore, List.of(scena), "s", Map.of(), Map.of("i", incontro));
        partita.eseguiScelta(lotta);
        assertThrows(IllegalStateException.class, partita::esportaStato);
        giocatore.subisciDanno(10);
        assertThrows(IllegalStateException.class, partita::esportaStato);
    }

    @Test
    void roundTripRealeConRaccolteVittoriaSaluteRidottaEProsecuzione() throws IOException {
        Map<String, Oggetto> catalogo = new CatalogoOggetti().creaCatalogo();
        Giocatore giocatore = new Giocatore("Éowyn", 100, 8, 2);
        List<Scena> scene = new CaricatoreScene().caricaScene(List.of("sotto_le_macerie.json", "strada_interrotta.json",
                "segni_sulle_pareti.json", "galleria_echi.json", "sala_registri.json"));
        Function<Giocatore, Combattimento> incontro = g -> new Combattimento(g,
                List.of(new Nemico("Orco", 10, 1, 0, catalogo.get("spada_nanica"))),
                GestoreTurniSequenziale::new, (a, b) -> 10, n -> AzioneNemico.DIFESA);
        Partita partita = new Partita(giocatore, scene, "sotto_le_macerie", catalogo, Map.of("orchi_galleria", incontro));
        scegli(partita, "raccogli_acqua_macerie");
        scegli(partita, "raccogli_ascia_macerie");
        scegli(partita, "vai_strada_interrotta");
        scegli(partita, "esplora_posto_guardia");
        scegli(partita, "raccogli_pane_posto_guardia");
        scegli(partita, "raccogli_scudo_posto_guardia");
        scegli(partita, "torna_strada_interrotta");
        scegli(partita, "scendi_galleria_echi");
        scegli(partita, "esplora_sala_registri");
        scegli(partita, "raccogli_acqua_registri");
        scegli(partita, "raccogli_corazza_registri");
        scegli(partita, "torna_galleria_echi");
        giocatore.equipaggiaArma((Arma) catalogo.get("ascia_nanica_danneggiata"));
        giocatore.equipaggiaArmatura((Armatura) catalogo.get("corazza_maglia_nanica"));
        giocatore.subisciDanno(17);
        scegli(partita, "affronta_pattuglia_galleria");
        assertThrows(IllegalStateException.class, partita::esportaStato);
        partita.attaccaBersaglio(partita.getNemiciVivi().get(0));
        StatoPartita prima = partita.esportaStato();
        assertEquals(1, prima.versione());
        assertEquals("galleria_echi", prima.idScenaCorrente());
        assertEquals(Set.of("raccogli_acqua_macerie", "raccogli_ascia_macerie", "raccogli_pane_posto_guardia",
                "raccogli_scudo_posto_guardia", "raccogli_acqua_registri", "raccogli_corazza_registri", "orchi_galleria"), prima.eventiCompletati());
        assertEquals(new StatoGiocatore("Éowyn", 83, "ascia_nanica_danneggiata", "corazza_maglia_nanica",
                Map.of("acqua", 2, "pane", 1, "scudo_nanico", 1, "spada_nanica", 1)), prima.giocatore());
        GestoreSalvataggi gestore = new GestoreSalvataggi(directory.resolve("slot.json"));
        gestore.salva(prima);
        StatoPartita caricato = gestore.carica();
        assertEquals(prima, caricato);
        Partita ripristinata = configurazione.ripristinaPartita(caricato);
        assertEquals(prima, ripristinata.esportaStato());
        assertFalse(ripristinata.isInCombattimento());
        assertFalse(ripristinata.isSconfitta());
        assertFalse(ripristinata.getScelteDisponibili().stream().anyMatch(s -> s.id().equals("affronta_pattuglia_galleria")));
        scegli(ripristinata, "raggiungi_compagno");
        assertEquals("compagno_ferito", ripristinata.esportaStato().idScenaCorrente());
        assertEquals(prima, partita.esportaStato());
    }
}
