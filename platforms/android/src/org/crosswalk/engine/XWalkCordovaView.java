package org.crosswalk.engine;

import org.apache.cordova.LOG;
import org.apache.cordova.CordovaPreferences;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.KeyEvent;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;

public class XWalkCordovaView extends XWalkView implements CordovaWebViewEngine.EngineView {
    protected XWalkCordovaResourceClient resourceClient;
    protected XWalkCordovaUiClient uiClient;
    protected XWalkWebViewEngine parentEngine;

    private static final String TAG = "XWalkCordovaView";

    private static boolean hasSetStaticPref;
    // This needs to run before the super's constructor.
    private static Context setGlobalPrefs(Context context, CordovaPreferences preferences) {
        LOG.d(TAG, "*** XWalkCordovaView.setGlobalPrefs start");
        if (!hasSetStaticPref) {
            hasSetStaticPref = true;
            ApplicationInfo ai = null;
            try {
                ai = context.getPackageManager().getApplicationInfo(context.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
            boolean prefAnimatable = preferences == null ? false : preferences.getBoolean("CrosswalkAnimatable", false);
            boolean manifestAnimatable = ai.metaData == null ? false : ai.metaData.getBoolean("CrosswalkAnimatable");
            // Selects between a TextureView (obeys framework transforms applied to view) or a SurfaceView (better performance).
            XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, prefAnimatable || manifestAnimatable);
            if ((ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            }
            XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
            XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
        }
        LOG.d(TAG, "*** XWalkCordovaView.setGlobalPrefs");
        return context;
    }

    public XWalkCordovaView(Context context, CordovaPreferences preferences) {
        super(setGlobalPrefs(context, preferences), (AttributeSet)null);
        LOG.d(TAG, "*** XWalkCordovaView initialize(context, preferences)");
    }

    public XWalkCordovaView(Context context, AttributeSet attrs) {
        super(setGlobalPrefs(context, null), attrs);
        LOG.d(TAG, "*** XWalkCordovaView initialize(context, attributes)");
    }

    void init(XWalkWebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        if (resourceClient == null) {
            LOG.d(TAG, "*** XWalkCordovaView init(parentEngine) setting resourceClient");
            setResourceClient(new XWalkCordovaResourceClient(parentEngine));
        } else {
          LOG.d(TAG, "*** XWalkCordovaView init(parentEngine) resourceClient already set");
        }

        if (uiClient == null) {
            LOG.d(TAG, "*** XWalkCordovaView init(parentEngine) setting UIClient");
            setUIClient(new XWalkCordovaUiClient(parentEngine));
        } else {
          LOG.d(TAG, "*** XWalkCordovaView init(parentEngine) uiClient already set");
        }
    }

    @Override
    public void setResourceClient(XWalkResourceClient client) {
        LOG.d(TAG, "*** XWalkCordovaView setResourceClient");
        // XWalk calls this method from its constructor.
        if (client instanceof XWalkCordovaResourceClient) {
            LOG.d(TAG, "*** XWalkCordovaView setResourceClient client is XWalkCordovaResourceClient");
            this.resourceClient = (XWalkCordovaResourceClient)client;
        } else {
            LOG.d(TAG, "*** XWalkCordovaView setResourceClient client is NOT XWalkCordovaResourceClient");
        }
        super.setResourceClient(client);
    }

    @Override
    public void setUIClient(XWalkUIClient client) {
        // XWalk calls this method from its constructor.
        if (client instanceof XWalkCordovaUiClient) {
            this.uiClient = (XWalkCordovaUiClient)client;
        }
        super.setUIClient(client);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Boolean ret = parentEngine.client.onDispatchKeyEvent(event);
        if (ret != null) {
            return ret.booleanValue();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void pauseTimers() {
        // This is called by XWalkViewInternal.onActivityStateChange().
        // We don't want them paused by default though.
    }

    public void pauseTimersForReal() {
        super.pauseTimers();
    }

    @Override
    public CordovaWebView getCordovaWebView() {
        return parentEngine == null ? null : parentEngine.getCordovaWebView();
    }
}
