package it.unicam.cs.mpgc.rpg123058.Salvataggio;

import java.util.Set;

public record StatoPartita(
        int versione,
        String idScenaCorrente,
        Set<String> eventiCompletati,
        StatoGiocatore giocatore
        ) {

        public StatoPartita{
             if(versione <= 0) throw new IllegalArgumentException("Versione negativa non ha senso");

             if(idScenaCorrente == null) throw new NullPointerException("Id della scena corrente non può essere nullo");
             if(idScenaCorrente.isBlank()) throw new IllegalArgumentException("Id della scena vuoto o con solo spazi");

             eventiCompletati = Set.copyOf(eventiCompletati);

             //questo record non ha la responsabilità di controllare l'esistenza di un scena tramite il suo id
             for(String evento : eventiCompletati){
                     if(evento == null) throw new NullPointerException("Id evento nullo non ammesso");
                     if(evento.isBlank()) throw new IllegalArgumentException("Id evento vuoto non ammesso");
             }

             if(giocatore == null) throw new NullPointerException("Deve essere costruito prima il record sullo stato del giocatore");
        }
}
