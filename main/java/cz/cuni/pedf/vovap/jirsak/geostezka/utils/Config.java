package cz.cuni.pedf.vovap.jirsak.geostezka.utils;


import android.graphics.Point;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.CamTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DragDropTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;

public class Config {
    private static final String[] CamTask0 = {"0", "1", "2", "3"};
    private static final String[] CamTask1 = {"0", "1", "2", "3", "4","5","6","7"};
    private static final String[] CamTask2 = {"0", "1", "2", "3", "4","5"};
    private static final String[] CamTask3 = {"0", "1", "2", "3", "4","5","6"};
    private static final int[] DD1 = {R.drawable.zula0, R.drawable.plagioklas1, R.drawable.kremen2, R.drawable.ortoklas3, R.drawable.biotit4};

    private static final Point[] DD1_OBJ_POS = {new Point(20,10), new Point(20,170), new Point(20,330), new Point(20,490)};
    private static final Point[] DD1_TGT_POS = {new Point(420,250), new Point(620,230), new Point(30,900), new Point(700,800)};
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
            new CamTask(0, CamTask0.length, CamTask0, "Uloha 0","Zadani ulohy 0"),
            new CamTask(1, CamTask1.length, CamTask1, "Uloha 1","Zadani ulohy 1"),
            new CamTask(2, CamTask2.length, CamTask2, "Uloha 2","Zadani ulohy 2"),
            new CamTask(3, CamTask3.length, CamTask3, "Uloha 3","Zadani ulohy 3"),
            new DragDropTask(4,"Uloha DD 4","Zadani ulohy 4",DD1, DD1_OBJ_POS, DD1_TGT_POS)
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
    public static final int vratPocetUloh()
    {
        return SEZNAM_ULOH.length;
    }

}
