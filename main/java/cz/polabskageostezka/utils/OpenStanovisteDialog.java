package cz.polabskageostezka.utils;

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

import cz.polabskageostezka.QRReadActivity;
import cz.polabskageostezka.R;

import static cz.polabskageostezka.utils.Config.vratUlohuPodleUri;

/**
 * Created by tomason on 29.09.2017.
 */

public class OpenStanovisteDialog extends Dialog {

	public Activity c;
	public ImageButton close;

	private Stanoviste st;

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
		title.setText("Stanoviště č. " + st.getCislo() + "\n" + st.getNazev());

		final Task t = vratUlohuPodleUri(st.getUrl());
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
	}
}
