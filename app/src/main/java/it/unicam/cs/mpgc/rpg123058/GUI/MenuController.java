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

    @FXML
    private Button pulsanteContinua;

    private GestoreSalvataggi gestoreSalvataggi;

    private ConfigurazionePartita configurazione;

    //Consumer<T> è un'interfaccia funionale Java che rappresenta un'operazione che riceve un paramentro
    //di tipo T con cui esegue un azione senza restituire nulla (void)
    private Consumer<Partita> apriPartita;


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

        pulsanteContinua.setDisable(!gestoreSalvataggi.esisteSalvataggio());
    }

    /** Crea e apre una partita se il nome inserito è valido. */
    @FXML
    private void nuovaPartita() {
        //.strp elimina gli spazi all'inizio e alla fine per il nome
        String nome = campoNome.getText().strip();

        if (nome.isBlank()) {
            messaggioErrore.setText("Inserisci il nome del protagonista.");
            return;
        }

        messaggioErrore.setText("");

        Partita partita = configurazione.creaNuovaPartita(nome);
        //.accept è un metodo astratto di Consumer che invoca la lambda passando, in questo caso, l'oggetto partita
        apriPartita.accept(partita);
    }

    /** Ripristina e apre la partita salvata, segnalando eventuali errori nel menu. */
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

        apriPartita.accept(partita);
    }

    /** Chiude l'applicazione JavaFX. */
    @FXML
    private void esci() {
        Platform.exit();//chiusura
    }
}
