package cz.polabskageostezka;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import cz.polabskageostezka.utils.Config;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SettingsActivity extends PreferenceActivity {

	SharedPreferences sharedPref;
	SharedPreferences.OnSharedPreferenceChangeListener sharedPrefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);

		sharedPref = getDefaultSharedPreferences(this);
		sharedPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				Log.d("GEO PREF key: ", key);
				if(key.equals(getResources().getString(R.string.pref_key_debugmod))) {
					Config.nastavDebugMode(prefs.getBoolean(key, false), getBaseContext());
				}
			}
		};

		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

	public static class MyPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings_pref);
		}
	}

	@Override
	protected void onResume() {
		Log.d("GEO PREF: ", "resume LISTENER");
		super.onResume();
		sharedPref.registerOnSharedPreferenceChangeListener(sharedPrefListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sharedPref.unregisterOnSharedPreferenceChangeListener(sharedPrefListener);
	}
}
