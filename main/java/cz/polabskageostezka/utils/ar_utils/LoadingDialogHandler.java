package cz.polabskageostezka.utils.ar_utils;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;


public final class LoadingDialogHandler extends Handler
{
    private final WeakReference<Activity> mActivity;
    // Constants for Hiding/Showing Loading dialog
    public static final int HIDE_DIALOG = 0;
    public static final int SHOW_DIALOG = 1;
    
    public View mLoadingDialogContainer;
	public View mLoadingDialogText;
    
    
    public LoadingDialogHandler(Activity activity)
    {
        mActivity = new WeakReference<Activity>(activity);
    }
    
    @Override
    public void handleMessage(Message msg) {
        Activity imageTargets = mActivity.get();
        if (imageTargets == null) {
            return;
        }
        
        if (msg.what == SHOW_DIALOG) {
            mLoadingDialogContainer.setVisibility(View.VISIBLE);
			mLoadingDialogText.setVisibility(View.VISIBLE);
            
        } else if (msg.what == HIDE_DIALOG) {
            mLoadingDialogContainer.setVisibility(View.GONE);
			mLoadingDialogText.setVisibility(View.GONE);
        }
    }
    
}
