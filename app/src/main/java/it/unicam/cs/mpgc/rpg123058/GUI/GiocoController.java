package it.unicam.cs.mpgc.rpg123058.GUI;

import it.unicam.cs.mpgc.rpg123058.GUI.GestoreMusicaFX.GestoreMusica;
import it.unicam.cs.mpgc.rpg123058.GUI.GestoreMusicaFX.Traccia;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.Scelta;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaCombattimento;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.Scena;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/** Coordina la scena narrativa, i pannelli di combattimento e inventario.*/
public class GiocoController {

    @FXML
    private StackPane contenitoreGioco;

    @FXML
    private Label titoloScena;

    @FXML
    private Label descrizioneScena;

    @FXML
    private VBox contenitoreScelte;

    @FXML
    private StackPane pannelloNarrativo;

    @FXML
    private Button pulsanteSalvaEdEsci;

    private Runnable richiestaMenu;
    private Runnable richiestaSalvataggio;

    private Parent pannelloCombattimento;
    private CombattimentoController combattimentoController;

    private Partita partita;

    private Parent pannelloInventario;
    private InventarioController inventarioController;

    private GestoreMusica gestoreMusica;

    /**
     * Collega la partita e le azioni di salvataggio e ritorno al menu.
     * @param partita partita da visualizzare
     * @param richiestaSalvataggio azione che salva la partita ed esce
     * @param richiestaMenu azione che riapre il menu
     */
    public void configura(Partita partita, Runnable richiestaSalvataggio, Runnable richiestaMenu, GestoreMusica gestoreMusica) {
        this.partita = Objects.requireNonNull(partita, "Partita nulla");

        this.richiestaSalvataggio = Objects.requireNonNull(richiestaSalvataggio, "Operazione di salvataggio nulla");

        this.richiestaMenu = Objects.requireNonNull(richiestaMenu, "Operazione di ritorno al menu nulla");

        this.gestoreMusica = gestoreMusica;

        aggiornaScena();
    }

    /** Mostra titolo, descrizione, sfondo e scelte della scena corrente. */
    private void aggiornaScena() {
        Scena scena = partita.getScenaCorrente();

        titoloScena.setText(scena.getTitolo());
        descrizioneScena.setText(scena.getDescrizione());

        aggiornaSfondo(scena.getPercorsoImmagine());
        aggiornaScelte();

        if (pannelloInventario != null && pannelloInventario.isVisible()) {
            inventarioController.aggiornaInventario();
        }
    }

    /**
     * Carica l'immagine della scena e la applica come sfondo del gioco.
     * @param percorso percorso della risorsa immagine
     */
    private void aggiornaSfondo(String percorso) {
        URL risorsa = Objects.requireNonNull(getClass().getResource(percorso), "Immagine non trovata");

        Image immagine = new Image(risorsa.toExternalForm());

        if (immagine.isError()) throw new IllegalStateException("Impossibile caricare l'immagine");
        //b3 è il parametro 'cover' che copre l'intera area mantenendo le proporzioni
        BackgroundSize dimensioni = new BackgroundSize(
                100, 100,
                true, true,
                false, true
        );

        BackgroundImage sfondo = new BackgroundImage(
                immagine,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                dimensioni
        );

        contenitoreGioco.setBackground(new Background(sfondo));
    }

    /** Ricrea le scelte disponibili o il pulsante di uscita alla fine della storia. */
    private void aggiornaScelte() {
        //clear dei bottoni precedenti per aggiornare le scelte
        contenitoreScelte.getChildren().clear();

        //nell'ultima scena è necessario avere un punto di ritorno al menu
        if ("primo_sole".equals(partita.getScenaCorrente().getId())) {
            mostraPulsanteEsci();
            return;
        }

        for (Scelta scelta : partita.getScelteDisponibili()) {
            Button pulsante = new Button(scelta.testo());

            pulsante.setMaxWidth(Double.MAX_VALUE);
            pulsante.setWrapText(true);
            pulsante.getStyleClass().add("pulsante-scelta");

            //la lmbda viene eseguita quando si preme il pulsante
            pulsante.setOnAction(evento -> eseguiScelta(scelta));

            contenitoreScelte.getChildren().add(pulsante);
        }
    }

    /**
     * Esegue una scelta e aggiorna la scena o apre un combattimento a seconda del tipo di scelta.
     * @param scelta scelta narrativa selezionata
     */
    private void eseguiScelta(Scelta scelta) {

        if (scelta instanceof SceltaCombattimento) {
            try {
                preparaPannelloCombattimento();
            } catch (IOException e) {
                Alert avviso = new Alert(Alert.AlertType.ERROR);
                avviso.initOwner(contenitoreGioco.getScene().getWindow());
                avviso.setHeaderText("Impossibile aprire il pannello di combattimento.");
                avviso.setContentText("Controlla il file combattimento.fxml.");

                e.printStackTrace();
                avviso.showAndWait();
                return;
            }
        }

        partita.eseguiScelta(scelta);

        if (partita.isInCombattimento()) {
            mostraCombattimento();
        } else {
            aggiornaScena();
        }
    }

    /** Carica o riapre il pannello dell'inventario sopra la scena di gioco. */
    @FXML
    private void apriInventario() {
        //caricamento e creazione al primo utilizzo
        if (pannelloInventario == null) {
            URL risorsa = Objects.requireNonNull(getClass().getResource("/FXML/inventario.fxml"), "Risorsa inventario.fxml non trovata");

            FXMLLoader loader = new FXMLLoader(risorsa);

            try {
                Parent pannello = loader.load();
                InventarioController controller = loader.getController();

                controller.configura(partita, this::chiudiInventario, this::aggiornaDopoModificaInventario);

                StackPane.setAlignment(pannello, Pos.TOP_RIGHT);
                StackPane.setMargin(pannello, new Insets(65, 30, 0, 0));

                contenitoreGioco.getChildren().add(pannello);

                pannelloInventario = pannello;
                inventarioController = controller;

            } catch (IOException e) {
                Alert avviso = new Alert(Alert.AlertType.ERROR);
                avviso.initOwner(contenitoreGioco.getScene().getWindow());
                avviso.setHeaderText("Impossibile aprire l'inventario.");
                avviso.setContentText("Controlla il file inventario.fxml.");

                e.printStackTrace();
                avviso.showAndWait();
            }
        } else {//rende i pannello visibile nuovamente senza crearlo mantenendo il riferimento
            inventarioController.aggiornaInventario();
            pannelloInventario.setVisible(true);
            pannelloInventario.setManaged(true);
            pannelloInventario.toFront();
        }
    }

    /** Nasconde il pannello dell'inventario, se presente. */
    private void chiudiInventario() {
        if(pannelloInventario == null) return;

        pannelloInventario.setVisible(false);
        pannelloInventario.setManaged(false);
    }

    /**
     * Carica e aggiunge il pannello di combattimento alla schermata di gioco.
     * @throws IOException se il pannello FXML non può essere caricato
     */
    private void preparaPannelloCombattimento() throws IOException {
        if (pannelloCombattimento != null) return;

        URL risorsa = Objects.requireNonNull(getClass().getResource("/FXML/combattimento.fxml"),
                "Risorsa combattimento.fxml non trovata");

        FXMLLoader loader = new FXMLLoader(risorsa);
        Parent pannello = loader.load();

        StackPane.setAlignment(pannello, Pos.CENTER);

        pannello.setVisible(false);
        pannello.setManaged(false);

        contenitoreGioco.getChildren().add(pannello);

        pannelloCombattimento = pannello;
        combattimentoController = loader.getController();
    }

    /** Nasconde la narrazione e mostra il combattimento corrente. */
    private void mostraCombattimento() {
        chiudiInventario();

        pannelloNarrativo.setVisible(false);
        pannelloNarrativo.setManaged(false);

        pannelloCombattimento.setManaged(true);
        pannelloCombattimento.setVisible(true);
        pannelloCombattimento.toFront();

        combattimentoController.configura(partita, this::gestisciFineCombattimento);

        gestoreMusica.riproduci(Traccia.COMBATTIMENTO);
    }

    /** Chiude il combattimento e mostra la scena narrativa o la sconfitta. */
    private void gestisciFineCombattimento() {
        chiudiInventario();

        pannelloCombattimento.setVisible(false);
        pannelloCombattimento.setManaged(false);

        pannelloNarrativo.setManaged(true);
        pannelloNarrativo.setVisible(true);

        gestoreMusica.riproduci(Traccia.GIOCO);

        if (partita.isSconfitta()) {
            mostraSconfitta();
        } else {
            aggiornaScena();
        }
    }

    /** Mostra il messaggio di sconfitta e il pulsante di ritorno al menu. */
    private void mostraSconfitta() {
        titoloScena.setText("La lanterna si spegne");

        descrizioneScena.setText("Sei stato sconfitto. Il tuo viaggio nelle profondità " + "di Moria termina qui.");

        mostraPulsanteEsci();
    }

    /** Aggiorna il combattimento dopo una modifica dell'inventario. */
    private void aggiornaDopoModificaInventario() {
        if (partita.isInCombattimento() && combattimentoController != null) {
            combattimentoController.aggiornaCombattimento();
        }
    }

    /** Richiede il salvataggio e l'uscita quando lo stato della partita lo consente. */
    @FXML
    private void salvaEdEsci() {
        if (partita.isInCombattimento() || partita.isSconfitta()) return;

        richiestaSalvataggio.run();
    }

    /** Abilita il salvataggio fuori dal combattimento e prima della sconfitta. */
    private void aggiornaPulsanteSalvataggio() {
        pulsanteSalvaEdEsci.setDisable(partita.isInCombattimento() || partita.isSconfitta());
    }

    /** Sostituisce le scelte con il pulsante di ritorno al menu. */
    private void mostraPulsanteEsci() {
        Button pulsante = new Button("Esci al menu");

        pulsante.setMaxWidth(Double.MAX_VALUE);
        pulsante.getStyleClass().add("pulsante-scelta");

        pulsante.setOnAction(evento -> richiestaMenu.run());

        contenitoreScelte.getChildren().setAll(pulsante);
    }
}
