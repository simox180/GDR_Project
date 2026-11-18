package it.unicam.cs.mpgc.rpg123058.GUI;

import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Objects;

/** Mostra il combattimento e gestisce le azioni del giocatore. */
public class CombattimentoController {

    @FXML
    private Label nomeGiocatore;

    @FXML
    private Label saluteGiocatore;

    @FXML
    private ProgressBar barraSaluteGiocatore;

    @FXML
    private Label statisticheGiocatore;

    @FXML
    private VBox elencoNemici;

    @FXML
    private Label indicazioneTurno;

    @FXML
    private Button pulsanteAttacca;

    @FXML
    private Button pulsanteDifendi;

    private Runnable combattimentoTerminato;

    private Partita partita;
    private Nemico bersaglioSelezionato;

    /**
     * Collega la partita e l'azione di fine combattimento al pannello.
     * @param partita partita che contiene il combattimento
     * @param combattimentoTerminato azione da eseguire alla fine dello scontro
     */
    public void configura(Partita partita, Runnable combattimentoTerminato) {
        this.partita = Objects.requireNonNull(partita, "Partita nulla");

        this.combattimentoTerminato = Objects.requireNonNull(
                combattimentoTerminato,
                "Callback di fine combattimento nulla"
        );

        this.bersaglioSelezionato = null;

        completaAzione();
    }

    /** Aggiorna giocatore, nemici e indicazione del turno. */
    public void aggiornaCombattimento() {
        aggiornaGiocatore();

        if (!partita.isInCombattimento()) {
            elencoNemici.getChildren().clear();
            bersaglioSelezionato = null;

            indicazioneTurno.setText(partita.isSconfitta() ? "Sei stato sconfitto." : "Nessun combattimento attivo.");

            return;
        }

        aggiornaNemici();

        indicazioneTurno.setText("Turno di: " + partita.getPersonaggioDiTurno().getNome());
    }

    /** Mostra nome, salute e statistiche del giocatore. */
    private void aggiornaGiocatore() {
        int salute = partita.getSaluteGiocatore();
        int massima = partita.getSaluteMassimaGiocatore();

        nomeGiocatore.setText(partita.getNomeGiocatore());
        saluteGiocatore.setText("Salute: " + salute + " / " + massima);

        barraSaluteGiocatore.setProgress((double) salute / massima);
        statisticheGiocatore.setText("Attacco: " + partita.getAttaccoGiocatore() + "   |   Difesa: " + partita.getDifesaGiocatore());
    }

    /** Ricrea le schede dei nemici vivi e verifica il bersaglio selezionato. */
    private void aggiornaNemici() {
        elencoNemici.getChildren().clear();

        ToggleGroup selezione = new ToggleGroup();

        if (bersaglioSelezionato != null && !partita.getNemiciVivi().contains(bersaglioSelezionato)) {
            bersaglioSelezionato = null;
        }

        for (Nemico nemico : partita.getNemiciVivi()) {
            elencoNemici.getChildren().add(creaSchedaNemico(nemico, selezione));
        }
    }

    /**
     * Crea una scheda selezionabile con salute e statistiche del nemico.
     * @param nemico nemico da rappresentare
     * @param selezione gruppo per la selezione di un solo bersaglio
     * @return scheda grafica del nemico
     */
    private VBox creaSchedaNemico(Nemico nemico, ToggleGroup selezione) {
        RadioButton scelta = new RadioButton(nemico.getNome() + " — " +
                nemico.getSalute() + " / " + nemico.getSaluteMassima());

        scelta.setToggleGroup(selezione);
        scelta.setSelected(nemico == bersaglioSelezionato);
        scelta.setWrapText(true);
        scelta.getStyleClass().add("selezione-nemico");

        scelta.setOnAction(evento -> {
            bersaglioSelezionato = nemico;
            aggiornaPulsanti();
        });

        ProgressBar salute = new ProgressBar((double) nemico.getSalute() / nemico.getSaluteMassima());

        salute.setMaxWidth(Double.MAX_VALUE);
        salute.getStyleClass().add("barra-salute");

        Label statistiche = new Label("Attacco: " + nemico.getAttacco() + "   |   Difesa: " + nemico.getDifesa());

        statistiche.getStyleClass().add("statistiche-combattimento");

        return new VBox(6, scelta, salute, statistiche);
    }

    /** Abilita le azioni consentite dal turno e dalla selezione del bersaglio. */
    private void aggiornaPulsanti() {
        boolean turnoGiocatore = partita.isTurnoGiocatore();

        pulsanteAttacca.setDisable(
                !turnoGiocatore || bersaglioSelezionato == null
        );

        pulsanteDifendi.setDisable(!turnoGiocatore);
    }

    /** Attacca il bersaglio selezionato durante il turno del giocatore. */
    @FXML
    private void attacca() {
        if (!partita.isTurnoGiocatore() || bersaglioSelezionato == null) {
            return;
        }

        partita.attaccaBersaglio(bersaglioSelezionato);
        completaAzione();
    }

    /** Attiva la difesa durante il turno del giocatore. */
    @FXML
    private void difendi() {
        if (!partita.isTurnoGiocatore()) {
            return;
        }

        partita.alzaDifesa();
        completaAzione();
    }

    /** Esegue i turni dei nemici fino al turno del giocatore o alla fine dello scontro. */
    private void eseguiTurniNemici() {
        while (partita.isInCombattimento()
                && !partita.isTurnoGiocatore()) {
            partita.eseguiTurnoNemico();
        }
    }

    /** Completa i turni, aggiorna il pannello e segnala la fine dello scontro. */
    private void completaAzione() {
        eseguiTurniNemici();
        aggiornaCombattimento();

        if (!partita.isInCombattimento()) {
            combattimentoTerminato.run();
        }
    }
}
