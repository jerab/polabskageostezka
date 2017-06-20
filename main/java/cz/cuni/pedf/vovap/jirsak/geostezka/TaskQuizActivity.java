package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Intent;
import android.os.Bundle;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.CamTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.QuizTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;


public class TaskQuizActivity extends BaseTaskActivity {
    QuizTask qt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_quiz);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        qt = (QuizTask) Config.vratUlohuPodleID(predaneID);

        UkazZadani(qt.getNazev(), qt.getZadani());

    }
}
