package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.GUI.ApplicazioneFX;
import javafx.application.Application;

/**
 * Fornisce il punto di ingresso dell'applicazione.
 */
public class Main {
    /**
     * Avvia l'applicazione e stampa il messaggio iniziale in console.
     *
     * @param args gli argomenti passati dalla riga di comando
     */
    public static void main(String[] args) {

        System.out.println("L'ultima lanterna");

        //avvia javaFX creando un istanza di ApplicazioneFX eseguendo automaticamente il metodo start
        Application.launch(ApplicazioneFX.class, args);
    }
}
