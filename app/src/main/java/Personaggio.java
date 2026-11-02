public abstract class Personaggio {

    private String nome;
    private int salute;
    private int saluteMassima;
    private int attaccoBase;
    private int difesa;

    public Personaggio(String nome, int saluteMassima, int attaccoBase, int difesa){
        if(nome.isEmpty()) throw new IllegalArgumentException("Il nome deve essere assegnato");
        if(saluteMassima <= 0 || attaccoBase <= 0) throw new IllegalArgumentException("La saluteMassima o l'attaccoBase non possono essere minori o uguali a zero");

        this.nome = nome;
        this.saluteMassima = saluteMassima;
        this.salute = this.saluteMassima;
        this.attaccoBase = attaccoBase;
        this.difesa = difesa;
    }

    public void subisciDanno(int danno){
        if(danno <= 0) throw new IllegalArgumentException("Il danno non può essere negativo o nullo");

        this.salute = Math.max(0, this.salute - danno);
    }

    public void cura(int cure){
        if(cure <= 0) throw new IllegalArgumentException("Le cure non possono essere negative o nulle");

        this.salute = Math.min(this.salute += cure, this.saluteMassima);
    }

    public boolean isVivo(){ return (this.salute != 0); }

//------------------------------------------GETTER--------------------------------------
    public String getNome() { return nome; }

    public int getSalute() { return salute; }

    public int getSaluteMassima() { return saluteMassima; }

    public int getAttaccoBase() { return attaccoBase; }

    public int getDifesa() { return difesa; }
//---------------------------------------------------------------------------------------

}
