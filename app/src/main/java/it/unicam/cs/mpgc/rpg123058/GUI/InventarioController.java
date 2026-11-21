package it.unicam.cs.mpgc.rpg123058.GUI;

import it.unicam.cs.mpgc.rpg123058.Dominio.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/** Visualizza l'inventario e gestisce equipaggiamento e uso degli oggetti.*/
public class InventarioController {

    @FXML
    private Label armaEquipaggiata;

    @FXML
    private Label armaturaEquipaggiata;

    @FXML
    private VBox elencoOggetti;

    @FXML
    private Button pulsanteTogliArma;

    @FXML
    private Button pulsanteTogliArmatura;

    @FXML
    private Label messaggioInventario;

    private Partita partita;
    private Runnable richiestaChiusura;

    private Runnable inventarioModificato;

    /**
     * Collega la partita e le azioni di chiusura e aggiornamento al pannello.
     * @param partita partita di cui mostrare l'inventario
     * @param richiestaChiusura azione da eseguire alla chiusura del pannello
     * @param inventarioModificato azione da eseguire dopo una modifica
     */
    public void configura(Partita partita, Runnable richiestaChiusura, Runnable inventarioModificato) {
        this.partita = Objects.requireNonNull(partita, "Partita nulla");

        this.richiestaChiusura = Objects.requireNonNull(richiestaChiusura, "Callback di chiusura nulla");

        this.inventarioModificato = Objects.requireNonNull(inventarioModificato, "Callback di aggiornamento nulla");

        aggiornaInventario();
    }

    /** Aggiorna equipaggiamento, oggetti e disponibilità delle azioni. */
    public void aggiornaInventario() {
        Arma arma = partita.getArmaEquipaggiata();
        Armatura armatura = partita.getArmaturaEquipaggiata();

        armaEquipaggiata.setText(arma == null ? "Arma: nessuna" : "Arma: " + arma.getNome() + " - " + descriviProprieta(arma));

        armaturaEquipaggiata.setText(armatura == null ? "Armatura: nessuna" : "Armatura: " + armatura.getNome() + " - " + descriviProprieta(armatura));

        elencoOggetti.getChildren().clear();

        List<VoceInventario> voci = partita.consultaInventario();

        boolean cambioConsentito = !partita.isInCombattimento() && !partita.isSconfitta();

        pulsanteTogliArma.setDisable(!cambioConsentito || arma == null);

        pulsanteTogliArmatura.setDisable(!cambioConsentito || armatura == null);

        if (voci.isEmpty()) {
            Label messaggio = new Label("L'inventario è vuoto.");
            messaggio.getStyleClass().add("descrizione-scena");
            elencoOggetti.getChildren().add(messaggio);
            return;
        }

        for (VoceInventario voce : voci) {
            elencoOggetti.getChildren().add(creaRigaOggetto(voce));
        }
    }

    /**
     * Crea una riga con le informazioni e l'eventuale azione di un oggetto.
     * @param voce voce di inventario da mostrare
     * @return riga grafica dell'oggetto
     */
    private HBox creaRigaOggetto(VoceInventario voce) {
        Oggetto oggetto = voce.getOggetto();

        VBox informazioni = creaInformazioniOggetto(oggetto, voce.getQuantita());

        HBox.setHgrow(informazioni, Priority.ALWAYS);

        HBox riga = new HBox(10);
        riga.setAlignment(Pos.CENTER_LEFT);
        riga.getChildren().add(informazioni);

        Button pulsante = creaPulsanteOggetto(oggetto);

        if (pulsante != null) {
            riga.getChildren().add(pulsante);
        }

        return riga;
    }

    /**
     * Crea l'azione adatta al tipo di oggetto e allo stato della partita.
     * @param oggetto oggetto per cui creare l'azione
     * @return pulsante di equipaggiamento o uso, oppure null se non previsto
     */
    private Button creaPulsanteOggetto(Oggetto oggetto) {
        boolean cambioConsentito = !partita.isInCombattimento() && !partita.isSconfitta();
        //switch con pattern matching
        return switch (oggetto) {
            case Arma arma -> creaPulsante("Equipaggia", cambioConsentito, () -> partita.equipaggiaArma(arma));

            case Armatura armatura -> creaPulsante("Equipaggia", cambioConsentito, () -> partita.equipaggiaArmatura(armatura));

            case Consumabile consumabile -> creaPulsante("Usa", partita.isTurnoGiocatore(), () -> usaConsumabile(consumabile));

            default -> null;
        };
    }

    /**
     * Crea un pulsante associato a un'operazione sull'inventario.
     * @param testo etichetta del pulsante
     * @param abilitato indica se l'azione è consentita
     * @param operazione azione da eseguire alla selezione
     * @return pulsante configurato
     */
    private Button creaPulsante(String testo, boolean abilitato, Runnable operazione) {
        Button pulsante = new Button(testo);

        pulsante.setMinWidth(Button.USE_PREF_SIZE);
        pulsante.setDisable(!abilitato);
        pulsante.getStyleClass().add("pulsante-scelta");
        pulsante.setOnAction(evento -> eseguiOperazione(operazione));

        return pulsante;
    }

    /**
     * Esegue un'operazione, mostra eventuali errori e aggiorna la vista.
     * @param operazione operazione da eseguire
     */
    private void eseguiOperazione(Runnable operazione) {
        messaggioInventario.setText("");

        try {
            operazione.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            messaggioInventario.setText(e.getMessage());
        }

        aggiornaInventario();
        inventarioModificato.run();
    }

    /**
     * Prova a usare un consumabile e ne mostra l'esito.
     * @param consumabile oggetto da usare
     */
    private void usaConsumabile(Consumabile consumabile) {
        boolean utilizzato = partita.usaConsumabile(consumabile);

        messaggioInventario.setText(utilizzato ? "Oggetto utilizzato." : "L'oggetto non può essere utilizzato adesso.");
    }

    /** Ripone l'arma equipaggiata nell'inventario e mostra l'esito. */
    @FXML
    private void disequipaggiaArma() {
        eseguiOperazione(() -> {
            boolean rimossa = partita.disequipaggiaArma();

            messaggioInventario.setText(rimossa ? "Arma riposta nell'inventario." : "Nessuna arma equipaggiata.");
        });
    }

    /** Ripone l'armatura equipaggiata nell'inventario e mostra l'esito. */
    @FXML
    private void disequipaggiaArmatura() {
        eseguiOperazione(() -> {
            boolean rimossa = partita.disequipaggiaArmatura();

            messaggioInventario.setText(rimossa ? "Armatura riposta nell'inventario." : "Nessuna armatura equipaggiata.");
        });
    }

    /** Richiede la chiusura del pannello dell'inventario. */
    @FXML
    private void chiudi() {
        richiestaChiusura.run();
    }

    /**
     * Descrive il bonus o l'effetto mostrato per un oggetto.
     * @param oggetto oggetto da descrivere
     * @return testo delle proprietà visualizzate
     */
    private String descriviProprieta(Oggetto oggetto) {

        return switch (oggetto) {
            case Arma arma -> "Attacco: +" + arma.getAttaccoBonus();

            case Armatura armatura -> "Difesa: +" + armatura.getDifesaBonus();

            case ConsumabileCurativo curativo -> "Recupera fino a " + curativo.getPuntiCura() + " punti salute";

            default -> "";
        };
    }

    /**
     * Crea le etichette con nome, quantità e proprietà di un oggetto.
     * @param oggetto oggetto da mostrare
     * @param quantita numero di unità nell'inventario
     * @return contenitore delle informazioni dell'oggetto
     */
    private VBox creaInformazioniOggetto(Oggetto oggetto, int quantita) {
        Label nome = new Label(oggetto.getNome() + " × " + quantita);
        nome.setWrapText(true);
        nome.getStyleClass().add("nome-oggetto");

        Label proprieta = new Label(descriviProprieta(oggetto));
        proprieta.setWrapText(true);
        proprieta.getStyleClass().add("proprieta-oggetto");

        VBox informazioni = new VBox(3, nome, proprieta);
        informazioni.setMaxWidth(Double.MAX_VALUE);

        return informazioni;
    }
}
