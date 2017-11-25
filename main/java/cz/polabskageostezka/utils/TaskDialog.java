package cz.polabskageostezka.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cz.polabskageostezka.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskDialog.TaskDialogHandlerListener} interface
 * to handle interaction events.
 */
public class TaskDialog extends DialogFragment {

	private TaskDialogHandlerListener mListener;
	private String name;
	private String description;
	private int taskNumber;
	private static final String LOGTAG = "GEO TaskDialog";

	public static TaskDialog newInstance(int taskNumber, String name, String descrip) {
		TaskDialog d = new TaskDialog();
		Bundle args = new Bundle();
		args.putString("name", name);
		args.putString("text", descrip);
		args.putInt("taskNumber", taskNumber);
		d.setArguments(args);
		return d;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOGTAG, "onCreate");
		if (getArguments() != null) {
			name = getArguments().getString("name");
			description = getArguments().getString("text");
			taskNumber = getArguments().getInt("taskNumber");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(LOGTAG, "onResume");
		/*int width = getResources().getDimensionPixelSize(R.);
		int height = getResources().getDimensionPixelSize(R.dimen.popup_height);*/
		getDialog().getWindow().setLayout(300, 600);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(LOGTAG, "onCreateDialog");
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialView = inflater.inflate(R.layout.fragment_taskdialog, null);

		TextView tv = (TextView) dialView.findViewById(R.id.taskdialog_desc);
		tv.setText(description);
		b.setView(dialView)
			//.setTitle(getString(R.string.taskdialogTitle) + taskNumber)
			.setTitle(name + " - " + taskNumber)
			.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onTaskDialogConfirmed(TaskDialog.this);
				}
			});

		return b.create();
	}

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (TaskDialogHandlerListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.	 *
	 */
	public interface TaskDialogHandlerListener {
		public void onTaskDialogConfirmed(DialogFragment dialog);
	}
}
