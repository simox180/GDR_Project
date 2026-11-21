package it.unicam.cs.mpgc.rpg123058.GUI;

import com.google.gson.JsonIOException;
import it.unicam.cs.mpgc.rpg123058.Configurazione.ConfigurazionePartita;
import it.unicam.cs.mpgc.rpg123058.GUI.GestoreMusicaFX.GestoreMusica;
import it.unicam.cs.mpgc.rpg123058.GUI.GestoreMusicaFX.Traccia;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.GestoreSalvataggi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

/** Gestisce la finestra JavaFX, il menu, la partita e il salvataggio. */
public class ApplicazioneFX extends Application {
    //riferimento alla finestra principale del gioco
    private Stage finestra;
    //scena grafica che contiene dell'albero grafico dei nodi
    private Scene scenaGrafica;
    private final ConfigurazionePartita configurazione = new ConfigurazionePartita();
    //gestore dei salvataggi che recupera la directory dell'utente e compone il percorso
    private final GestoreSalvataggi gestoreSalvataggi =
            new GestoreSalvataggi(Path.of(System.getProperty("user.home"), ".ultima-lanterna", "partita.json"));

    private final GestoreMusica gestoreMusica = new GestoreMusica();

    /**
     * Riceve e avvia la finestra principale e carica il menu.
     * @param stage finestra principale fornita da JavaFX
     * @throws IOException se il menu FXML non può essere caricato
     */
    @Override
    public void start(Stage stage) throws IOException{
        this.finestra = stage;

        mostraMenu();

        finestra.setTitle("L'ultima lanterna");
        //dimensione non modificabile, le immagini sarebbero distorte
        finestra.setResizable(false);
        finestra.show();
    }



    /**
     * Carica il menu e collega il controller alle operazioni sulla partita.
     * @throws IOException se il menu FXML non può essere caricato
     */
    private void mostraMenu() throws IOException {
        URL risorsa = Objects.requireNonNull(getClass().getResource("/FXML/menu.fxml"),
                "Risorsa menu.fxml non trovata");


        FXMLLoader loader = new FXMLLoader(risorsa);
        //.load carica il menu FXML creando istanze dei controller e collega azioni per esempio ai bottoni come
        //Esci, onAction="#nomeMetodo" con # viene associato al metodo nomeMetodo
        //Parent è una classe base dei contenitori di nodi, conserva la radice caricata senza dipendere dal suo specifico tipo di contenitore
        Parent radice = loader.load();

        MenuController controller = loader.getController();

        //viene passato al controller un configurazionPartita e un GestoreSalvataggi per poterli usare nei bottoni del menu
        //il terzo parametro è il metodo mostraGioco di questa classe con cui il controller può cambiare root
        //senza essere a conoscenza di come viene costruita la schermata
        controller.configura(configurazione, gestoreSalvataggi, this::mostraGioco);

        if (scenaGrafica == null) {
            //prima apertura
            scenaGrafica = new Scene(radice, 1100, 1100.0 * 1143 / 2048);
            finestra.setScene(scenaGrafica);
        } else {
            //aperture successive, non viene ricreata ma viene usata la stessa Scene
            scenaGrafica.setRoot(radice);
        }

        gestoreMusica.riproduci(Traccia.MENU);
    }

    /**
     * Apre la schermata di gioco e segnala eventuali errori di caricamento.
     * @param partita partita da mostrare
     */
    private void mostraGioco(Partita partita) {
        URL risorsa = Objects.requireNonNull(getClass().getResource("/FXML/gioco.fxml"), "Risorsa gioco.fxml non trovata");

        FXMLLoader loader = new FXMLLoader(risorsa);

        //gestisco IOException perchè Consumer<Partita> non permette eccezioni checked
        try {
            Parent radice = loader.load();
            //qui il controller è GiocoController che si occupa di gestire il gioco avviato dopo il menu
            GiocoController controller = loader.getController();

            //la callback salva ed esci è necessaria per i bottoni in gioco e in quello dell'ultima scena
            controller.configura(partita, () -> salvaEdEsci(partita), this::tornaAlMenu, gestoreMusica);

            scenaGrafica.setRoot(radice);

            gestoreMusica.riproduci(Traccia.GIOCO);

        } catch (IOException e) {
            Alert avviso = new Alert(Alert.AlertType.ERROR);
            avviso.initOwner(finestra);
            avviso.setTitle("Errore di caricamento");
            avviso.setHeaderText("Impossibile aprire la schermata di gioco.");
            avviso.setContentText("Controlla il file gioco.fxml.");

            e.printStackTrace();
            avviso.showAndWait();
        }
    }

    /**
     * Salva la partita e torna al menu se il salvataggio riesce.
     * @param partita partita da salvare
     */
    private void salvaEdEsci(Partita partita) {
        try {
            //serializzazione dello stato della partita
            gestoreSalvataggi.salva(partita.esportaStato());

        } catch (IOException | JsonIOException | IllegalStateException e) {
            mostraErrore("Salvataggio non riuscito", "La partita rimane aperta. Impossibile completare "
                            + "il salvataggio.");
            //se il salvataggio fallisce non ritorna al menu
            e.printStackTrace();
            return;
        }

        tornaAlMenu();
    }

    /**
     * Ripristina il menu e mostra un errore se il caricamento fallisce.
     * A differenza di MostraMenu mostra un avviso in caso di fallimento
     * e non propaga l'errore.
     */
    private void tornaAlMenu() {
        try {
            mostraMenu();

        } catch (IOException e) {
            mostraErrore("Impossibile aprire il menu", "La schermata attuale rimane aperta.");

            e.printStackTrace();
        }
    }

    /**
     * Mostra un avviso di errore nella finestra principale.
     * @param titolo intestazione dell'avviso
     * @param messaggio descrizione dell'errore
     */
    private void mostraErrore(String titolo, String messaggio) {
        //dialogo figlio della schermata narrativa per un errore
        Alert avviso = new Alert(Alert.AlertType.ERROR);

        avviso.initOwner(finestra);
        avviso.setTitle("Errore");
        avviso.setHeaderText(titolo);
        avviso.setContentText(messaggio);
        //attende la chiusura del pop up per visualizzare l'errore
        avviso.showAndWait();
    }
}
