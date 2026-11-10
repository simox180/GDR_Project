package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta;

/**
 * Interfaccia comune a tutti i tipi di Scelta
 * (La decisione di usare un'interfaccia invece di una classe astratta deriva dai
 * vari tipi di Scelta che sono record, quindi non estendibili da altre classi in Java)
 */
public interface Scelta {
    //Utilizzo di questi nomi perché coincidono con gli accessor generati automaticamente dai record
    String id();
    String testo();
}
