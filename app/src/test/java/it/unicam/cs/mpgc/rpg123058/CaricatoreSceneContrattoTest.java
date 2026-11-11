package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.Scena;
import it.unicam.cs.mpgc.rpg123058.Persistenza.CaricatoreScene;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CaricatoreSceneContrattoTest {
    private final CaricatoreScene caricatore = new CaricatoreScene();

    @Test
    void leggeCampiTipiRiferimentiOrdineECondizioni() {
        Scena scena = caricatore.caricaScena("fixtures/valid.json");
        assertEquals("fixtures/valid", scena.getId());
        assertEquals("Titolo di prova", scena.getTitolo());
        assertEquals("Descrizione di prova", scena.getDescrizione());
        SceltaNavigazione vai = new SceltaNavigazione("vai", "Vai", "altra");
        SceltaRaccolta prendi = new SceltaRaccolta("prendi", "Prendi", "acqua");
        SceltaCombattimento lotta = new SceltaCombattimento("lotta", "Combatti", "orchi_galleria");
        assertEquals(List.of(vai, lotta), scena.getScelteDisponibili(Set.of()));
        assertEquals(List.of(vai, prendi, lotta), scena.getScelteDisponibili(Set.of("chiave", "luce")));
        assertEquals(List.of(vai, lotta), scena.getScelteDisponibili(Set.of("chiave", "luce", "gia_preso")));
        assertEquals(List.of(vai, lotta), scena.getScelteDisponibili(Set.of("chiave")));
    }

    @Test
    void accettaScenaSenzaOpzioniECaricaTutteLeSceneReali() {
        assertEquals(List.of(), caricatore.caricaScena("fixtures/empty_options.json").getScelteDisponibili(Set.of()));
        List<String> nomi = List.of("sotto_le_macerie", "strada_interrotta", "segni_sulle_pareti",
                "galleria_echi", "sala_registri", "compagno_ferito", "ultima_riserva",
                "qualcosa_nelle_profondita", "ponte_khazad_dum", "primo_sole");
        List<Scena> scene = caricatore.caricaScene(nomi.stream().map(n -> n + ".json").toList());
        assertEquals(nomi, scene.stream().map(Scena::getId).toList());
        assertEquals(10, scene.size());
        assertEquals(List.of(), scene.get(9).getScelteDisponibili(Set.of()));
    }

    @Test
    void rifiutaPathInvalidiEDuplicatiNellaLista() {
        assertThrows(NullPointerException.class, () -> caricatore.caricaScena(null));
        for (String nome : List.of("", "  "))
            assertThrows(IllegalArgumentException.class, () -> caricatore.caricaScena(nome), nome);
        assertThrows(IllegalArgumentException.class, () -> caricatore.caricaScena("inesistente.json"));
        assertThrows(NullPointerException.class, () -> caricatore.caricaScene(null));
        assertThrows(IllegalArgumentException.class,
                () -> caricatore.caricaScene(List.of("sotto_le_macerie.json", "sotto_le_macerie.json")));
    }

    @Test
    void rifiutaDocumentiEStruttureNonValidi() {
        for (String nome : List.of("malformed", "empty_document", "wrong_root", "wrong_filename",
                "missing_id", "missing_title", "missing_description", "missing_options",
                "null_id", "null_title", "null_description", "null_options",
                "number_id", "number_title", "number_description", "blank_id", "blank_title",
                "blank_description", "object_options", "invalid_option", "null_option"))
            assertThrows(IllegalArgumentException.class, () -> caricatore.caricaScena("fixtures/" + nome + ".json"), nome);
    }

    @Test
    void rifiutaScelteNonValide() {
        for (String nome : List.of("missing_choice_type", "missing_choice_id", "missing_choice_text",
                "missing_destination", "missing_item", "missing_encounter", "unknown_type",
                "blank_choice_id", "wrong_choice_text", "blank_destination", "duplicate_choice_id",
                "null_choice_type", "null_choice_id", "null_choice_text", "null_destination",
                "null_item", "null_encounter", "wrong_choice_type", "wrong_choice_id",
                "wrong_destination", "wrong_item", "wrong_encounter", "blank_choice_type",
                "blank_choice_text", "blank_item", "blank_encounter"))
            assertThrows(IllegalArgumentException.class, () -> caricatore.caricaScena("fixtures/" + nome + ".json"), nome);
    }

    @Test
    void rifiutaCondizioniNonValide() {
        for (String nome : List.of("missing_required", "missing_excluded", "wrong_required",
                "wrong_excluded", "nonstring_required", "nonstring_excluded", "blank_required",
                "blank_excluded", "duplicate_required", "duplicate_excluded", "overlap",
                "null_required", "null_excluded", "null_element_required", "null_element_excluded"))
            assertThrows(IllegalArgumentException.class, () -> caricatore.caricaScena("fixtures/" + nome + ".json"), nome);
    }
}
