package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cz.cuni.pedf.vovap.jirsak.geostezka.QRReadActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;

import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratUlohuPodleUri;

/**
 * Created by tomason on 29.09.2017.
 */

public class OpenStanovisteDialog extends Dialog {

	public Activity c;
	public ImageButton close;
	private ImageView resultImg;

	private Stanoviste st;

	private boolean closeTask = true;

	public OpenStanovisteDialog(@NonNull Context context, Stanoviste stan) {
		super(context);
		this.c = (Activity)context;
		st = stan;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.open_task_dialog);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		Button butWeb = (Button) findViewById(R.id.buttonWeb);
		Button butTask = (Button) findViewById(R.id.buttonTask);
		ImageButton closeBtn = (ImageButton) findViewById(R.id.closeButton);
		TextView title = (TextView) findViewById(R.id.title_txt);
		TextView popis = (TextView) findViewById(R.id.content_txt);
		title.setText("Stanoviště " + st.getNazev() + " (" + st.getCislo() + ")");

		final Task t = vratUlohuPodleUri(st.getUrl());
		Log.d("GEO QR", "Dialog Task: " + t.toString());
		if(t == null) {
			butTask.setVisibility(View.GONE);
			popis.setText("Toto stanoviště není součástí úloh v rámci aplikace. Můžete se podívat na webové stránky projektu pro bližší informace o " +
					"hornině.");
		}else {
			popis.setText("Podařilo se Vám načíst další úlohu. Buď můžete úlohu spustit nebo se podívat na webové stránky projektu pro bližší informace o " +
					"hornině.");
			butTask.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					((QRReadActivity) c).runNextQuest(t.getId(), c);
				}
			});
		}

		butWeb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(st.getUrl()));
				c.startActivity(i);
				c.finish();
			}
		});

		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("GEO QR", "Dialog closing...");
				((QRReadActivity)c).setCteckaAktivni(true);
				dismiss();
			}
		});

	/*
		Log.d("Geo - ResultDialog", result + " | " + closeTask);
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

		//this.getWindow().setLayout(w - w/4, 600);

		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (TaskResultDialogInterface) c;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(c.toString() + " must implement TaskResultDialogInterface");
		}
		*/
	}
}
