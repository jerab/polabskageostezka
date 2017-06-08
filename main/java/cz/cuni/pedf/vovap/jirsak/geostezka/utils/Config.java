package cz.cuni.pedf.vovap.jirsak.geostezka.utils;


import android.graphics.Point;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.CamTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DragDropTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.QuizTask;

public class Config {

    public static final int TYP_ULOHY_CAM = 1;
    public static final int TYP_ULOHY_DRAGDROP = 2;
    public static final int TYP_ULOHY_QUIZ = 3;
    public static final int TYP_ULOHY_AR = 4;
/*
    /// Vyvoreni staticke tridy uvnitr configu - tyto tridy by mohly byt samozrejme samostatne mimo config
    public static class Uloha {
        private int id;
        private int typ;

        public Uloha(int id, int typ) {
            this.id = id;
            this.typ = typ;
        }
    }

    /// dalsi mozne rozsireni pro vetsi specifikaci uloh
    public static class UlohaCam extends Task {

        int pocetCilu;
        public UlohaCam(int id, int pocetCilu) {
            super(id, TYP_ULOHY_CAM);
            this.pocetCilu = pocetCilu;
        }
    }*/

    /// vytvoreni pole se seznamem uloh
    //// Take by to slo udelat pomoci definovani 3 samostatnych trid - UlohaCam, UlohaNormal, ... s ruznymi parametry a vlastnostmi a nasledne pouzivat
    /// ruzne konstruktory (viz 3. prvek nize)
    private static final Task[] SEZNAM_ULOH = {
           // new Task(1,R.integer.TYP_ULOHY_CAM),
           // new Task(2,R.integer.TYP_ULOHY_DRAGDROP),
            // obecne task: id | pocet cilu | vysledky | nazev | zadani
            new CamTask(0,
                    new String[] {"0", "1", "2", "3"},
                    "Uloha 0",
                    "Zadani ulohy 0",
                    "http://0.cz"),
            new CamTask(1,
                    new String[] {"0", "1", "2", "3", "4","5","6","7"},
                    "Uloha 1",
                    "Zadani ulohy 1",
                    "http://1.cz"),
            new CamTask(2,
                    new String[] {"0", "1", "2", "3", "4","5"},
                    "Uloha 2",
                    "Zadani ulohy 2",
                    "http://2.cz"),
            new CamTask(3,
                    new String[] {"0", "1", "2", "3", "4","5","6"},
                    "Uloha 3",
                    "Zadani ulohy 3",
                    "http://3.cz"),
            new DragDropTask(4,
                    "Uloha DD 4",
                    "Zadani ulohy 4",
                    new int[]{R.drawable.zula0, R.drawable.plagioklas1, R.drawable.kremen2, R.drawable.ortoklas3, R.drawable.biotit4},
                    new Point[] {new Point(20,10), new Point(20,170), new Point(20,330), new Point(20,490)},
                    new Point[] {new Point(420,250), new Point(620,230), new Point(30,900), new Point(700,800)},
                    "http://4"),
            new QuizTask(5,
                    "Uloha Quiz 5",
                    "Vyberte spravne odpovedi na otazky",
                    new String[] {"Z ceho se sklada zula?", "Jaky je nejcasteji se vyskytujici se kamen?"},
                    new int[] {4,4},
                    new String[] {  "A) Biotit",
                                    "B) Slida",
                                    "C) Kremen",
                                    "D) Moznost D neni k dispozici, ale ukazuje priklad dlouheho retezce v odpovedi",
                                    "A) Tohle nevypada nejlepe",
                                    "B) Uvidime jak to dopadne",
                                    "C) Uvidime jak to dopadne",
                                    "D) Uvidime jak to dopadne"},
                    "http://5")
    } ;


    /**
     * Metoda vraci objekt Uloha podle pozadovaneho id
     * @param hledaneid
     * @return
     */
    public static final Task vratUlohuPodleID(int hledaneid) {
        for(int i = 0; i < SEZNAM_ULOH.length; i++) {
            if(SEZNAM_ULOH[i].getId() == hledaneid) {
                return SEZNAM_ULOH[i];
            }
        }
        return null;
    }
    public static final Task vratUlohuPodleUri(String URI) {
        for(int i = 0; i < SEZNAM_ULOH.length; i++) {
            if(SEZNAM_ULOH[i].getUri().equals(URI)) {
                return SEZNAM_ULOH[i];
            }
        }
        return null;
    }
    public static final int vratPocetUloh()
    {
        return SEZNAM_ULOH.length;
    }

}
