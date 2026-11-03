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
}
