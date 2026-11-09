package it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico;

import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;

import java.util.Random;

public class StrategiaNemicoBase implements StrategiaNemico {

    private final Random scelta = new Random();

    @Override
    public AzioneNemico scegliAzione(Nemico nemico) {
        if(nemico == null) throw new NullPointerException("Nemico non può essere nullo");

        double probabilitaAttacco;
        double saluteRelativa = (double) nemico.getSalute() / nemico.getSaluteMassima();

        if(saluteRelativa <= 0.30){
            probabilitaAttacco = 0.6;
        }else {
            probabilitaAttacco = 0.8;
        }

        if(scelta.nextDouble() < probabilitaAttacco){
            return AzioneNemico.ATTACCO;
        }else{
            return AzioneNemico.DIFESA;
        }
    }
}
