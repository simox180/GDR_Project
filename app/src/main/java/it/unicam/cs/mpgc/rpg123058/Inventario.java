package it.unicam.cs.mpgc.rpg123058;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inventario {

    private final List<VoceInventario> oggetti = new ArrayList<>();

    public Inventario(){

    }

    public void inserisciOggetto(@NonNull Oggetto oggetto){
        Objects.requireNonNull(oggetto, "L'oggetto non può essere null");

        VoceInventario appVoceInventario = this.cercaOggetto(oggetto);
        if(appVoceInventario != null){
            appVoceInventario.aumentaQuantita();
            return;
        }

        oggetti.add(new VoceInventario(oggetto));
    }

    public boolean rimuoviOggetto(@NonNull Oggetto oggetto){
        Objects.requireNonNull(oggetto, "L'oggetto non può essere null");

        VoceInventario appVoceInventario = this.cercaOggetto(oggetto);
        if(appVoceInventario != null){
            boolean diminuito = appVoceInventario.diminuisciQuantita();
            if(!diminuito) oggetti.remove(new VoceInventario(oggetto));
            return true;
        }

        return false;
    }

    public @Nullable VoceInventario cercaOggetto(@NonNull Oggetto oggetto){
        Objects.requireNonNull(oggetto, "L'oggetto non può essere nullo");

        int i=0;
        while(i < oggetti.size()){
            VoceInventario appoggio = oggetti.get(i);
            if(appoggio.getOggetto().equals(oggetto)){
                return appoggio;
            }
            i++;
        }

        return null;
    }

}