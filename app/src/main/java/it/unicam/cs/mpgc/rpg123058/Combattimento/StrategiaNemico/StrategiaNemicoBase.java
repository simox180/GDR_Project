package it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico;

import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;

import java.util.Random;

public class StrategiaNemicoBase implements StrategiaNemico {

    private final Random scelta = new Random();

    @Override
    /**
     * Estrae un attacco con probabilità 0,6 quando la salute è al massimo il 30% della massima, altrimenti 0,8; negli altri casi sceglie difesa.
     * @param nemico nemico da valutare, non nullo
     * @return azione estratta
     * @throws NullPointerException se il nemico è nullo
     */
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
