package it.unicam.cs.mpgc.rpg123058.GUI;

import com.google.gson.JsonIOException;
import it.unicam.cs.mpgc.rpg123058.Configurazione.ConfigurazionePartita;
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
    private Stage finestra;
    private Scene scenaGrafica;
    private final ConfigurazionePartita configurazione = new ConfigurazionePartita();
    private final GestoreSalvataggi gestoreSalvataggi =
            new GestoreSalvataggi(Path.of(System.getProperty("user.home"), ".ultima-lanterna", "partita.json"));

    /**
     * Avvia la finestra principale e carica il menu.
     * @param stage finestra principale fornita da JavaFX
     * @throws IOException se il menu FXML non può essere caricato
     */
    @Override
    public void start(Stage stage) throws IOException{
        this.finestra = stage;

        mostraMenu();

        finestra.setTitle("L'ultima lanterna");
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
        //Esci, che onAction="#nomeMetodo" con # viene associato al metodo nomeMetodo in questo caso
        Parent radice = loader.load();

        MenuController controller = loader.getController();

        controller.configura(configurazione, gestoreSalvataggi, this::mostraGioco);

        if (scenaGrafica == null) {
            scenaGrafica = new Scene(radice, 1100, 1100.0 * 1143 / 2048);
            finestra.setScene(scenaGrafica);
        } else {
            scenaGrafica.setRoot(radice);
        }
    }

    /**
     * Apre la schermata di gioco e segnala eventuali errori di caricamento.
     * @param partita partita da mostrare
     */
    private void mostraGioco(Partita partita) {
        URL risorsa = Objects.requireNonNull(
                getClass().getResource("/FXML/gioco.fxml"),
                "Risorsa gioco.fxml non trovata"
        );

        FXMLLoader loader = new FXMLLoader(risorsa);

        //gestisco IOException perchè Consumer<Partita> non permette eccezioni checked
        try {
            Parent radice = loader.load();

            GiocoController controller = loader.getController();
            controller.configura(partita, () -> salvaEdEsci(partita), this::tornaAlMenu);

            scenaGrafica.setRoot(radice);

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
            gestoreSalvataggi.salva(partita.esportaStato());

        } catch (IOException | JsonIOException | IllegalStateException e) {
            mostraErrore("Salvataggio non riuscito", "La partita rimane aperta. Impossibile completare "
                            + "il salvataggio.");

            e.printStackTrace();
            return;
        }

        tornaAlMenu();
    }

    /** Ripristina il menu e mostra un errore se il caricamento fallisce. */
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
        Alert avviso = new Alert(Alert.AlertType.ERROR);

        avviso.initOwner(finestra);
        avviso.setTitle("Errore");
        avviso.setHeaderText(titolo);
        avviso.setContentText(messaggio);

        avviso.showAndWait();
    }
}
