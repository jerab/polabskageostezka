package cz.polabskageostezka.tasks;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.GridTaskItem;
import cz.polabskageostezka.utils.Stanoviste;
import cz.polabskageostezka.utils.Task;

/**
 * Created by Fogs on 05.09.2017.
 */

public class GridTask extends Task {
    private int[] images;
    private String[] texts;
    private String[] feedbacks;

    public GridTask(int id, Stanoviste stanoviste, String zadani, String zpetVazbaOk, int[] images, String[] texts, String[] feedbacks, int	retez ){
        super(id, stanoviste, Config.TYP_ULOHY_GRID, zadani, new String[]{zpetVazbaOk}, retez);
        this.images = images;
        this.texts = texts;
        this.feedbacks = feedbacks;
    }

    public int[] getImages() {
        return images;
    }


	/**
	 *
	 * @param poradi - cislo sady od 1 (sada cita 4 polozky)
	 * @return
	 */
	public ArrayList<GridTaskItem> getSada(int poradi) {
        /*if(poradi > Math.ceil(images.length / 4)) {
        	return null;
		}*/
        Log.d("Geo - GRID TASK", poradi + " | " + images.length + " | " + Math.ceil(images.length / 4));
		ArrayList<GridTaskItem> sada = new ArrayList<GridTaskItem>();
        int start = (4 * (poradi -1));
        for(int i = start; i < 4 * poradi; i++) {
        	if(i >= images.length) {
				break;
			}
        	sada.add(new GridTaskItem(i-start, images[i], texts[i], feedbacks[i], (i-start) == 0));
		}
		if(sada.size() > 1) {
			Collections.shuffle(sada);
		}
		return sada;
    }

    public String[] getTexts() {
        return texts;
    }
    public String getCorrectAnswer(int sada){
	    return feedbacks[sada*4];
    }
    public int getPocetSad(){
	    return (int) Math.ceil((float)(images.length) / 4.0);
    }
}
