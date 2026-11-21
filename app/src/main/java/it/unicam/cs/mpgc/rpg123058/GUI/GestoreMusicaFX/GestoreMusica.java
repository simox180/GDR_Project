package it.unicam.cs.mpgc.rpg123058.GUI.GestoreMusicaFX;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Objects;

public class GestoreMusica {

    private MediaPlayer musica;
    private Traccia ascoltata;

    public void riproduci(Traccia traccia) {
        Objects.requireNonNull(traccia, "Traccia nulla");

        if (traccia == ascoltata) {
            return;
        }

        URL risorsaMusicale = Objects.requireNonNull(getClass().getResource(traccia.getPercorso()),
                "File audio non trovato");

        Media brano = new Media(risorsaMusicale.toExternalForm());
        MediaPlayer nuovoPlayer = new MediaPlayer(brano);
        nuovoPlayer.setVolume(0.3);
        nuovoPlayer.setCycleCount(MediaPlayer.INDEFINITE); // riproduzione in loop

        if (musica != null) {
            musica.dispose();
        }

        musica = nuovoPlayer;
        ascoltata = traccia;
        musica.play();
    }
}
