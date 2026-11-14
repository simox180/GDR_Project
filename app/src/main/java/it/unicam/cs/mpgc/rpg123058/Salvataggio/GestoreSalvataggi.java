package it.unicam.cs.mpgc.rpg123058.Salvataggio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class GestoreSalvataggi {
     private final Gson gson;
     private final Path percorsoSalvataggio;

     public GestoreSalvataggi(Path percorsoSalvataggio){
         if(percorsoSalvataggio == null) throw new NullPointerException("Percorso nullo non ammesso");
         this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setStrictness(Strictness.STRICT).create();
         this.percorsoSalvataggio = percorsoSalvataggio;
     }

    public void salva(StatoPartita stato) throws IOException{
        Objects.requireNonNull(stato, "Uno stato nullo non è ammeso");

        //new GestoreSalvataggi(Path.of("salvataggi", "partita.json")); percorso relativo
        Path destinazione = this.percorsoSalvataggio.toAbsolutePath();
        Path cartella = destinazione.toAbsolutePath().getParent(); // cartella

        Files.createDirectories(cartella);

        //creo un file temporaneo per non sovrascrivere direttamente
        //il principale e non perdere dati in caso di errore
        Path pFileTemporaneo = Files.createTempFile(cartella, "salvataggio-", ".tmp");

        try {
            //try-with-resources
            try (BufferedWriter writer = Files.newBufferedWriter(pFileTemporaneo, StandardCharsets.UTF_8)) {
                gson.toJson(stato, writer);
            }
            //sostituisce il file temporaneo con il file precedente nella destinazione specificata
            Files.move(pFileTemporaneo, destinazione, StandardCopyOption.ATOMIC_MOVE);

        } finally{
            Files.deleteIfExists(pFileTemporaneo);
        }
    }

    public StatoPartita carica() throws IOException{

         try (BufferedReader reader = Files.newBufferedReader(this.percorsoSalvataggio, StandardCharsets.UTF_8))
         {
            StatoPartita statoPartita = gson.fromJson(reader, StatoPartita.class);

             if (statoPartita == null) throw new IllegalArgumentException("Il salvataggio è vuoto o contiene null");


            if(statoPartita.versione() != 1) throw new IllegalArgumentException("Versione del salvataggio non supportata");

            return statoPartita;
         }
    }
}
