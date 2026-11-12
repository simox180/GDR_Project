package it.unicam.cs.mpgc.rpg123058.Salvataggio;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

public record StatoGiocatore(
        String nome,
        int salute,
        @Nullable String armaEquipaggiata,
        @Nullable String armaturaEquipaggiata,
        //invece di creare un record apposito solo per l'inventario inglobo le informazioni nello stato del giocatore
        Map<String, Integer> inventario
    ) {

    public StatoGiocatore{
        if(nome == null) throw new NullPointerException("Nome nullo non ammesso");
        if(nome.isBlank()) throw new IllegalArgumentException("Nome vuoto o contiene solo spazi");

        if(salute <= 0) throw new IllegalArgumentException("Giocatore senza vita non ammesso");

        if((armaEquipaggiata != null && armaEquipaggiata.isBlank())) throw new IllegalArgumentException("Equipaggiamento con nomi vuoti o di solo spazi");
        if((armaturaEquipaggiata != null && armaturaEquipaggiata.isBlank())) throw new IllegalArgumentException("Equipaggiamento con nomi vuoti o di solo spazi");

        //rende la mappa immutabile copiandola
        inventario = Map.copyOf(inventario);

        //.forEach con lambda è più compatto
        inventario.forEach((id, quantita) ->{
            if(id == null) throw new NullPointerException("Id nulli non ammessi");
            if(id.isBlank()) throw new IllegalArgumentException("Id vuoto o di solo spazi");

            if(quantita <= 0) throw new IllegalArgumentException("Quantità non positive non ammesse");
        });


    }

}
