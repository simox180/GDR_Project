package it.unicam.cs.mpgc.rpg123058;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.GestoreSalvataggi;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoGiocatore;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoPartita;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GestoreSalvataggiTest {
    @TempDir Path directory;

    private StatoPartita stato(String nome, Map<String, Integer> inventario, String arma, String armatura) {
        return new StatoPartita(1, "sotto_le_macerie", Set.of("raccolta", "vittoria"),
                new StatoGiocatore(nome, 73, arma, armatura, inventario));
    }

    @Test
    void validaArgomenti() {
        assertThrows(NullPointerException.class, () -> new GestoreSalvataggi(null));
        GestoreSalvataggi gestore = new GestoreSalvataggi(directory.resolve("slot.json"));
        assertThrows(NullPointerException.class, () -> gestore.salva(null));
    }

    @Test
    void salvaNelPercorsoRichiestoCreaCartelleESostituisceSlot() throws IOException {
        Path file = directory.resolve("nuova/cartella/slot.json");
        GestoreSalvataggi gestore = new GestoreSalvataggi(file);
        StatoPartita vuoto = stato("Éowyn 雪", Map.of(), null, null);
        gestore.salva(vuoto);
        assertTrue(Files.isRegularFile(file));
        assertEquals(vuoto, gestore.carica());
        var json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        var giocatore = json.getAsJsonObject("giocatore");
        assertEquals("Éowyn 雪", giocatore.get("nome").getAsString());
        assertTrue(giocatore.has("armaEquipaggiata"));
        assertTrue(giocatore.get("armaEquipaggiata").isJsonNull());
        assertTrue(giocatore.has("armaturaEquipaggiata"));
        assertTrue(giocatore.get("armaturaEquipaggiata").isJsonNull());

        StatoPartita aggiornato = stato("Björk 漢字", Map.of("pane", 2, "acqua", 1), "spada_nanica", "scudo_nanico");
        gestore.salva(aggiornato);
        assertEquals(aggiornato, gestore.carica());
        try (var filePresenti = Files.list(file.getParent())) {
            assertEquals(1, filePresenti.count(), "Nessun temporaneo deve restare dopo il salvataggio");
        }
    }

    @Test
    void fileAssenteEVuoto() throws IOException {
        Path file = directory.resolve("slot.json");
        GestoreSalvataggi gestore = new GestoreSalvataggi(file);
        assertThrows(NoSuchFileException.class, gestore::carica);
        Files.writeString(file, "", StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, gestore::carica);
        Files.writeString(file, " \n\t", StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, gestore::carica);
        Files.writeString(file, "null", StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, gestore::carica);
    }

    @Test
    void jsonErratoOStrutturaIncompatibile() throws IOException {
        Path file = directory.resolve("slot.json");
        GestoreSalvataggi gestore = new GestoreSalvataggi(file);
        Files.writeString(file, "{bad", StandardCharsets.UTF_8);
        assertThrows(JsonParseException.class, gestore::carica);
        Files.writeString(file, "[]", StandardCharsets.UTF_8);
        assertThrows(JsonParseException.class, gestore::carica);
        Files.writeString(file, "{\"versione\":2,\"idScenaCorrente\":\"inizio\",\"eventiCompletati\":[],\"giocatore\":{\"nome\":\"Ada\",\"salute\":1,\"inventario\":{}}}", StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, gestore::carica);
    }

    @Test
    void datiObbligatoriInvalidiSonoRifiutati() throws IOException {
        Path file = directory.resolve("slot.json");
        GestoreSalvataggi gestore = new GestoreSalvataggi(file);
        String valido = "{\"versione\":1,\"idScenaCorrente\":\"inizio\",\"eventiCompletati\":[],\"giocatore\":{\"nome\":\"Ada\",\"salute\":1,\"inventario\":{}}}";
        String[] invalidi = {
                valido.replace("\"idScenaCorrente\":\"inizio\",", ""),
                valido.replace("\"eventiCompletati\":[],", ""),
                valido.replace("{\"nome\":\"Ada\",\"salute\":1,\"inventario\":{}}", "null"),
                valido.replace("\"salute\":1", "\"salute\":0"),
                valido.replace("\"eventiCompletati\":[]", "\"eventiCompletati\":[\" \"]"),
                valido.replace("\"nome\":\"Ada\"", "\"nome\":\" \""),
                valido.replace("\"inventario\":{}", "\"inventario\":null"),
                valido.replace("\"inventario\":{}", "\"inventario\":{\"pane\":0}"),
                valido.replace("\"inventario\":{}", "\"inventario\":{\" \":1}"),
                valido.replace("\"eventiCompletati\":[]", "\"eventiCompletati\":[null]")
        };
        Class<?>[] causeAttese = {
                NullPointerException.class, NullPointerException.class, NullPointerException.class,
                IllegalArgumentException.class, IllegalArgumentException.class, IllegalArgumentException.class,
                NullPointerException.class, IllegalArgumentException.class, IllegalArgumentException.class,
                NullPointerException.class
        };
        for (int i = 0; i < invalidi.length; i++) {
            Files.writeString(file, invalidi[i], StandardCharsets.UTF_8);
            RuntimeException errore = assertThrows(RuntimeException.class, gestore::carica, invalidi[i]);
            assertEquals(RuntimeException.class, errore.getClass(), "Gson avvolge l'errore del costruttore record");
            assertEquals(causeAttese[i], errore.getCause().getClass(), invalidi[i]);
        }
    }
}
