package it.unicam.cs.mpgc.rpg123058.Persistenza;
//------------------------------------------------
import java.io.InputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
//------------------------------------------------
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
//------------------------------------------------
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.Scelta;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaNavigazione;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaRaccolta;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaCombattimento;
//------------------------------------------------
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
//------------------------------------------------
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.OpzioneScena;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.Scena;
//------------------------------------------------

public class CaricatoreScene {

    //Unico oggetto Gson per tutte le scene, STRICT per rifiutare sintassi JSON non valida
    private final Gson gson = new GsonBuilder()
            .setStrictness(Strictness.STRICT)
            .create();

    /**
     * Apre l'accesso al file. Controlla la validità del path, cerca la risorsa in 'resources' e se valida
     * apre un flusso per leggerla
     *
     * @param nomeFile il path della risorsa che parte da 'resources'
     * @return un InputStream, un flusso non ancora interpretato
     */
    private InputStream apriRisorsa(String nomeFile){
        if(nomeFile == null) throw new NullPointerException("Nome del file nullo");
        if(nomeFile.isBlank()) throw new IllegalArgumentException("Nome del file vuoto");

        String percorso = "/Scene/" + nomeFile;

        //getResourceAsStream evita di riferirsi a un percorso fragile che dipende da quale cartella si avvia
        //il programma, andando a cercare direttamente in 'resources' messo a disposizione da Gradle nel classpath
        InputStream input = CaricatoreScene.class.getResourceAsStream(percorso);

        if(input == null) throw new IllegalArgumentException("Impossibile trovare la scena");

        return input;
    }

    /**
     * Interpreta il contenuto del flusso di byte ricevuto usando apriRisorsa. Legge il flusso di byte,
     * lo traduce in UTF-8 così da interpretare correttamente anche i caratteri particolari.
     * Gson legge i caratteri forniti dal reader e interpreta il JSON costruendo una rappresentazione in memoria.
     *
     * @param nomeFile il path della risorsa che parte da 'resources'
     * @return un oggetto di tipo JSON derivato dalla risorsa
     */
    private JsonObject leggiDocumento(String nomeFile) {
        //try-with-resources chiude automaticamente il lettore e il flusso
        try (   InputStream input = apriRisorsa(nomeFile);//byte della risorsa
                Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)//interpretazione dei byte in chat UTF-8
        ) {
            JsonElement documento = gson.fromJson(reader, JsonElement.class);

            if (documento == null || !documento.isJsonObject()) throw new IllegalArgumentException("La scena deve contenere un oggetto JSON: " + nomeFile);

            return documento.getAsJsonObject();//da qui leggiamo i campidell 'oggetto

        } catch (JsonParseException e) { throw new IllegalArgumentException("JSON non valido nel file");
        } catch (IOException e) {throw new IllegalStateException("Errore di lettura del file");
        }
    }

    /**
     * Recupera uno specifico campo testuale. Cerca il campo, verifica il tip ed estrae la stringa Java.
     *
     * @param oggetto oggetto di tipo JSON da cui recuperare il campo
     * @param campo specifico
     * @return la stringa del campo in questione
     */
    private String leggiStringa(JsonObject oggetto, String campo) {
        JsonElement valore = oggetto.get(campo);

        if (valore == null || !valore.isJsonPrimitive() || !valore.getAsJsonPrimitive().isString()) throw new IllegalArgumentException("Un campo non è una stringa");

        String testo = valore.getAsString();

        if (testo.isBlank()) throw new IllegalArgumentException("Il campo non può essere vuoto");

        return testo;
    }

    /**
     * Crea il tipo di scelta nella specifica scena a seconda del suo tipo.
     *
     * @param opzione
     * @return
     */
    private Scelta leggiScelta(JsonObject opzione) {
        //campi comuni a tutti tipi di scelta
        String tipo = leggiStringa(opzione, "tipo");
        String id = leggiStringa(opzione, "id");
        String testo = leggiStringa(opzione, "testo");

        //legge il tipo restituendo/costruendo il record che rappresenta il tipo di scelta corrispondente
        return switch (tipo) {
            case "NAVIGAZIONE" -> new SceltaNavigazione(
                    id,
                    testo,
                    leggiStringa(opzione, "idScenaDestinazione")
            );

            case "RACCOLTA" -> new SceltaRaccolta(
                    id,
                    testo,
                    leggiStringa(opzione, "idOggetto")
            );

            case "COMBATTIMENTO" -> new SceltaCombattimento(
                    id,
                    testo,
                    leggiStringa(opzione, "idCombattimento")
            );

            default -> throw new IllegalArgumentException("Tipo di scelta non riconosciuto: ");
        };
    }

    /**
     * Legge un insieme di eventi in una array JSON
     *
     * @param opzione oggetto JSON
     * @param campo il nome dell'array (eventiRichiesti/eventiEsclusi)
     * @return gli eventi nell'array JSON trasformati in Set
     */
    private Set<String> leggiEventi(JsonObject opzione, String campo) {
        JsonElement valore = opzione.get(campo);

        if (valore == null || !valore.isJsonArray()) throw new IllegalArgumentException("Il campo deve essere un array");

        Set<String> eventi = new HashSet<>();

        for (JsonElement elemento : valore.getAsJsonArray()) {
            if (!elemento.isJsonPrimitive() || !elemento.getAsJsonPrimitive().isString()) throw new IllegalArgumentException("Il campo deve contenere solo stringhe");

            String evento = elemento.getAsString();

            if (evento.isBlank()) throw new IllegalArgumentException("Il campo contiene un evento vuoto");

            if (!eventi.add(evento)) throw new IllegalArgumentException("Evento duplicato");
        }

        return Set.copyOf(eventi);
    }

    /**
     * Coordina i metodi precedenti in modo da caricare la scelta e le condizioni prese dal file.
     *
     * @param opzione
     * @return
     */
    private OpzioneScena leggiOpzione(JsonObject opzione) {
        Scelta scelta = leggiScelta(opzione);

        Set<String> eventiRichiesti = leggiEventi(opzione, "eventiRichiesti");

        Set<String> eventiEsclusi = leggiEventi(opzione, "eventiEsclusi");

        return new OpzioneScena(scelta, eventiRichiesti, eventiEsclusi);
    }

    /**
     * Permette, tramite l'utilizzo dei metodi privati della classe, di costruire una scena partendo dal nome del File
     *
     * @param nomeFile nome del file cercato
     * @return una scena estrapolata dal file della scena
     */
    public Scena caricaScena(String nomeFile) {
        //apre il file e prende il documento
        JsonObject documento = leggiDocumento(nomeFile);

        //recupera i campi principali
        String id = leggiStringa(documento, "id");
        String titolo = leggiStringa(documento, "titolo");
        String descrizione = leggiStringa(documento, "descrizione");

        if (!nomeFile.equals(id + ".json")) throw new IllegalArgumentException("Il nome del file deve corrispondere all'id della scena");

        //recupero opzioni
        JsonElement elementoOpzioni = documento.get("opzioni");

        if (elementoOpzioni == null || !elementoOpzioni.isJsonArray()) throw new IllegalArgumentException("Il campo 'opzioni' deve essere un array");

        List<OpzioneScena> opzioni = new ArrayList<>();

        //ogni elemento viene letto come oggetto JSON
        for (JsonElement elemento : elementoOpzioni.getAsJsonArray()) {

            if (!elemento.isJsonObject()) throw new IllegalArgumentException("Ogni opzione deve essere un oggetto JSON");

            OpzioneScena opzione = leggiOpzione(elemento.getAsJsonObject());

            opzioni.add(opzione);
        }

        return new Scena(id, titolo, descrizione, opzioni);
    }

    /**
     * Utilizza il metodo di recupero e costruzione di un scena per tutta la lista dei file,
     * cioè tutte le scene, fornite tramite parametro.
     *
     * @param nomiFile la lista di tutti i file/scene che compongono la storia completa
     * @return la lista di tutti gli oggetti Scena
     */
    public List<Scena> caricaScene(List<String> nomiFile) {
        if (nomiFile == null) throw new NullPointerException("Lista dei file nulla");

        List<Scena> scene = new ArrayList<>();
        Set<String> idScene = new HashSet<>();

        for (String nomeFile : nomiFile) {
            Scena scena = caricaScena(nomeFile);

            if (!idScene.add(scena.getId())) throw new IllegalArgumentException("Id di scena duplicato");

            scene.add(scena);
        }

        return List.copyOf(scene);
    }

    /*Per utilizzare caricaScene con tutte le scene della storia
    (essendo tutte collegate tramite idScenaDestinazione non devono necessariamente essere in ordine)

    CaricatoreScene caricatore = new CaricatoreScene();

    List<Scena> scene = caricatore.caricaScene(List.of(
        "sotto_le_macerie.json",
        "strada_interrotta.json",
        "segni_sulle_pareti.json",
        "galleria_echi.json",
        "sala_registri.json",
        "compagno_ferito.json",
        "ultima_riserva.json",
        "qualcosa_nelle_profondita.json",
        "ponte_khazad_dum.json",
        "primo_sole.json"
    ));*/

}
