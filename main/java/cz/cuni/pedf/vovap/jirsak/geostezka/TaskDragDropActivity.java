package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TaskDragDropActivity extends BaseTaskActivity {
    DragDropTask dd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_drag_drop);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        dd = (DragDropTask) Config.vratUlohuPodleID(predaneID);

    }
    
}
