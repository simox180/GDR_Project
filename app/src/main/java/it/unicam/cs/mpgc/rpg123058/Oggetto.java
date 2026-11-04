package it.unicam.cs.mpgc.rpg123058;

import java.util.Objects;

public abstract class Oggetto {

    private final String nome;

    protected Oggetto(String nome){
        Objects.requireNonNull(nome, "Il nome non può essere null");

        if (nome.isBlank())
            throw new IllegalArgumentException("Nome vuoto");

        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if((!(obj instanceof Oggetto oggetto))) return false;

        return this.nome.equals(oggetto.getNome());
    }

    @Override
    public int hashCode() {
        return this.nome.hashCode();
    }
}
