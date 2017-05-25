package cz.cuni.pedf.vovap.jirsak.geostezka;

/**
 * Created by Fogs on 25.5.2017.
 */

public class Task {
    private int id;
    private int typ;

    public Task(int id, int typ) {
        this.id = id;
        this.typ = typ;
    }

    public int getId() {
        return id;
    }


    public int getTyp() {
        return typ;
    }

}
/// Vyvoreni staticke tridy uvnitr configu - tyto tridy by mohly byt samozrejme samostatne mimo config



