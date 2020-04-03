package co.com.UI;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnClickListenerImpl implements OnClickListener {
	
    private boolean clickable = true;

    @Override
    public final void onClick(View view) {
    	
        if (clickable) {
        	
            clickable = false;
            onOneClick(view);
        }
    }
    
    public abstract void onOneClick(View view);

    public void reset() {
    	
        clickable = true;
    }
}
