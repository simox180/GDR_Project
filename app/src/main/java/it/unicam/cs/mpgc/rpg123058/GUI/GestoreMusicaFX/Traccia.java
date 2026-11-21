package it.unicam.cs.mpgc.rpg123058.GUI.GestoreMusicaFX;

public enum Traccia {
    MENU("/Museeca/Rivendell.mp3"),
    GIOCO("/Museeca/Khazad-dûm.mp3"),
    COMBATTIMENTO("/Museeca/THE LORD OF THE RING - URUK HAI THEME.mp3");

    private final String percorso;

    Traccia(String percorso){
        this.percorso = percorso;
    }

    public String getPercorso(){
        return percorso;
    }
}
