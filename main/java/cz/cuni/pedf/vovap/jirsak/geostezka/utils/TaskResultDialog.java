package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskQuizActivity;

/**
 * Created by tomason on 29.09.2017.
 */

public class TaskResultDialog extends Dialog implements View.OnClickListener{

	public Activity c;
	//public Dialog d;
	public ImageButton close;
	private ImageView resultImg;

	private boolean result;
	private String title;
	private String desc;

	private boolean nextQuest = false;
	private boolean closeTask = true;

	private TaskResultDialogInterface mListener;

	public TaskResultDialog(@NonNull Context context, String title, String text, boolean result, boolean closeActivity) {
		super(context);
		this.c = (Activity)context;
		this.title = title;
		this.desc = text;
		this.result = result;
		this.closeTask = closeActivity;
	}

	/**
	 * Strictly for QUIZ TASK
	 * @param context
	 * @param title
	 * @param text
	 * @param result
	 * @param goToNextQuestion
	 */
	public TaskResultDialog(@NonNull TaskQuizActivity context, String title, String text, boolean result, boolean goToNextQuestion) {
		super(context);
		this.c = (TaskQuizActivity)context;
		this.title = title;
		this.desc = text;
		this.result = result;
		this.nextQuest = goToNextQuestion;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.task_result_dialog);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		resultImg = (ImageView) findViewById(R.id.img_result);
		if(result) {
			resultImg.setImageResource(R.drawable.ic_check_ok);
			resultImg.setOnClickListener(this);
		}else {
			resultImg.setImageResource(R.drawable.ic_check_no);
			resultImg.setOnClickListener(this);
		}

		close = (ImageButton) findViewById(R.id.closeButton);
		close.setOnClickListener(this);

		((TextView) findViewById(R.id.title_txt)).setText(this.title);
		((TextView) findViewById(R.id.result_txt)).setText(this.desc);

		int w = c.getResources().getDisplayMetrics().widthPixels;
		this.getWindow().setLayout(w - w/4, 600);

		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (TaskResultDialogInterface) c;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(c.toString() + " must implement TaskResultDialogInterface");
		}
	}

	@Override
	public void onClick(View view) {
		mListener.runFromResultDialog(true, closeTask);
		this.dismiss();
	}

	public interface TaskResultDialogInterface {
		public void runFromResultDialog(boolean result, boolean closeTask);
	}
}
