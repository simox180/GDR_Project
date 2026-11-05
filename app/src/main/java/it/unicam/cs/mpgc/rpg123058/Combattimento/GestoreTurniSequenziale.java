package it.unicam.cs.mpgc.rpg123058.Combattimento;

import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;
import it.unicam.cs.mpgc.rpg123058.Utility;

import java.util.List;
import java.util.Objects;

public class GestoreTurniSequenziale implements GestoreTurni{

    public GestoreTurniSequenziale(List<Personaggio> partecipanti){
        if(partecipanti == null || partecipanti.contains(null)) throw new NullPointerException("Ne la lista ne i partecipanti possono essere null");
        if(Utility.trovaDuplicati(partecipanti)) throw new IllegalArgumentException("Non ammessi riferimenti duplicati nella lista");



    }





}
