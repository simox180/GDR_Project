package it.unicam.cs.mpgc.rpg123058.GUI;


import it.unicam.cs.mpgc.rpg123058.Configurazione.ConfigurazionePartita;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.GestoreSalvataggi;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoPartita;
import com.google.gson.JsonParseException;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/** Gestisce la creazione, il ripristino e la chiusura della partita dal menu. */
public class MenuController {

    @FXML
    private TextField campoNome;

    @FXML
    private Label messaggioErrore;

    //gli altri bottoni non hanno un campo perchè non devono essere direttamente manipolati,
    // Continua invece è visibile in base alla presenza di un salvataggio
    @FXML
    private Button pulsanteContinua;

    private GestoreSalvataggi gestoreSalvataggi;

    private ConfigurazionePartita configurazione;

    //Consumer<T> è un'interfaccia funzionale Java che rappresenta un'operazione che riceve un paramentro
    //di tipo T con cui viene eseguita un'azione con .accept senza restituire nulla (void)
    private Consumer<Partita> apriPartita;

    //Qui non serve un costruttore, perchè viene fornito da Java e la classe viene creata dall'FXMLLoader dal campo fx:controller=...
    //,e con getController, e lo stesso caricatore collegherà anche gli elementi grafici

    /**
     * Collega il menu ai servizi della partita e all'azione di apertura.
     * @param configurazione servizio per creare o ripristinare partite
     * @param gestoreSalvataggi servizio per leggere i salvataggi
     * @param apriPartita azione che apre la partita selezionata
     */
    public void configura(ConfigurazionePartita configurazione, GestoreSalvataggi gestoreSalvataggi, Consumer<Partita> apriPartita) {

        this.configurazione = Objects.requireNonNull(configurazione, "Configurazione nulla");

        this.gestoreSalvataggi = Objects.requireNonNull(gestoreSalvataggi, "Gestore salvataggi nullo");

        this.apriPartita = Objects.requireNonNull(apriPartita, "Operazione di apertura nulla");

        //se non esiste un salvataggio il bottone Continua è ovviamente disabilitato
        pulsanteContinua.setDisable(!gestoreSalvataggi.esisteSalvataggio());
    }

    /** Crea e apre una partita se il nome inserito è valido.*/
    @FXML
    private void nuovaPartita() {
        //.strip elimina gli spazi all'inizio e alla fine per il nome
        String nome = campoNome.getText().strip();

        if (nome.isBlank()) {
            messaggioErrore.setText("Inserisci il nome del protagonista.");
            return;
        }
        //in caso di messaggio precedente cleariamo il messaggio di errore
        messaggioErrore.setText("");

        Partita partita = configurazione.creaNuovaPartita(nome);
        //.accept è un metodo astratto di Consumer che invoca la lambda passando, in questo caso, l'oggetto partita
        apriPartita.accept(partita);
    }

    /** Ripristina e apre la partita salvata, segnalando eventuali errori nel menu.*/
    @FXML
    private void continuaPartita() {
        messaggioErrore.setText("");

        Partita partita;

        try {
            StatoPartita stato = gestoreSalvataggi.carica();
            partita = configurazione.ripristinaPartita(stato);

        } catch (IOException e) {
            messaggioErrore.setText("Impossibile leggere il salvataggio.");
            e.printStackTrace();
            return;

        } catch (JsonParseException | IllegalArgumentException | IllegalStateException | NullPointerException e) {
            messaggioErrore.setText("Il salvataggio contiene dati non validi.");
            e.printStackTrace();
            return;
        }
        //callback per aprire la partita ricevuta come parametro
        apriPartita.accept(partita);
    }

    /** Chiude l'applicazione JavaFX. */
    @FXML
    private void esci() {
        Platform.exit();//chiusura
    }
}
