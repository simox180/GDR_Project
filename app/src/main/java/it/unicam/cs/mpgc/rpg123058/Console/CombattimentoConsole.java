package it.unicam.cs.mpgc.rpg123058.Console;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StatoCombattimento;
import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class CombattimentoConsole {

    private final Combattimento combattimento;
    private final Giocatore giocatore;
    private final List<Nemico> nemici;
    private final Scanner scanner;

    public CombattimentoConsole(
            Combattimento combattimento,
            Giocatore giocatore,
            List<Nemico> nemici,
            Scanner scanner) {

        this.combattimento = Objects.requireNonNull(combattimento, "Combattimento nullo");
        this.giocatore = Objects.requireNonNull(giocatore, "Giocatore nullo");
        this.nemici = List.copyOf(nemici);
        this.scanner = Objects.requireNonNull(scanner, "Scanner nullo");
    }

    public void avvia(){

        while(combattimento.getStatoCombattimento() == StatoCombattimento.IN_CORSO){

        }
    }
}
