package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import androidx.core.app.NavUtils;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview.GBChromeClient;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview.GBWebClient;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview.JSInterface;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.WebViewSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalPebbleJSActivity extends AbstractGBActivity {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ExternalPebbleJSActivity.class);
    public static final String SHOW_CONFIG = "configure";
    public static final String START_BG_WEBVIEW = "start_webview";
    private Uri confUri;
    private WebView myWebView;

    /* JADX WARNING: type inference failed for: r4v8, types: [android.os.Parcelable] */
    /* JADX WARNING: type inference failed for: r4v10, types: [java.io.Serializable] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r12) {
        /*
            r11 = this;
            super.onCreate(r12)
            android.content.Intent r0 = r11.getIntent()
            android.os.Bundle r0 = r0.getExtras()
            r1 = 0
            r2 = 0
            r3 = 0
            java.lang.String r4 = "device"
            if (r0 != 0) goto L_0x00d3
            android.content.Intent r5 = r11.getIntent()
            android.net.Uri r5 = r5.getData()
            r11.confUri = r5
            android.net.Uri r5 = r11.confUri
            java.lang.String r5 = r5.getScheme()
            java.lang.String r6 = "gadgetbridge"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x00f6
            android.net.Uri r5 = r11.confUri     // Catch:{ IllegalArgumentException -> 0x0036 }
            java.lang.String r5 = r5.getHost()     // Catch:{ IllegalArgumentException -> 0x0036 }
            java.util.UUID r5 = java.util.UUID.fromString(r5)     // Catch:{ IllegalArgumentException -> 0x0036 }
            r2 = r5
            goto L_0x0053
        L_0x0036:
            r5 = move-exception
            org.slf4j.Logger r6 = LOG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "UUID in incoming configuration is not a valid UUID: "
            r7.append(r8)
            android.net.Uri r8 = r11.confUri
            java.lang.String r8 = r8.toString()
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.error(r7)
        L_0x0053:
            android.app.Application r5 = r11.getApplication()
            nodomain.freeyourgadget.gadgetbridge.GBApplication r5 = (nodomain.freeyourgadget.gadgetbridge.GBApplication) r5
            nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager r5 = r5.getDeviceManager()
            java.util.List r6 = r5.getDevices()
            java.util.Iterator r7 = r6.iterator()
        L_0x0065:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x0093
            java.lang.Object r8 = r7.next()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r8 = (nodomain.freeyourgadget.gadgetbridge.impl.GBDevice) r8
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice$State r9 = r8.getState()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice$State r10 = nodomain.freeyourgadget.gadgetbridge.impl.GBDevice.State.INITIALIZED
            if (r9 != r10) goto L_0x0092
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r7 = r8.getType()
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r9 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.PEBBLE
            boolean r7 = r7.equals(r9)
            if (r7 == 0) goto L_0x0087
            r3 = r8
            goto L_0x0093
        L_0x0087:
            org.slf4j.Logger r4 = LOG
            java.lang.String r7 = "attempting to load pebble configuration but a different device type is connected!!!"
            r4.error(r7)
            r11.finish()
            return
        L_0x0092:
            goto L_0x0065
        L_0x0093:
            if (r3 != 0) goto L_0x00d1
            nodomain.freeyourgadget.gadgetbridge.util.Prefs r7 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getPrefs()
            android.content.SharedPreferences r7 = r7.getPreferences()
            r8 = 0
            java.lang.String r9 = "last_device_address"
            java.lang.String r7 = r7.getString(r9, r8)
            if (r7 == 0) goto L_0x00d1
            nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper r8 = nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper.getInstance()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r8 = r8.findAvailableDevice(r7, r11)
            boolean r9 = r8.isConnected()
            if (r9 != 0) goto L_0x00d1
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r9 = r8.getType()
            nodomain.freeyourgadget.gadgetbridge.model.DeviceType r10 = nodomain.freeyourgadget.gadgetbridge.model.DeviceType.PEBBLE
            if (r9 != r10) goto L_0x00d1
            android.content.Intent r9 = new android.content.Intent
            java.lang.Class<nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService> r10 = nodomain.freeyourgadget.gadgetbridge.service.DeviceCommunicationService.class
            r9.<init>(r11, r10)
            java.lang.String r10 = "nodomain.freeyourgadget.gadgetbridge.devices.action.connect"
            android.content.Intent r9 = r9.setAction(r10)
            android.content.Intent r4 = r9.putExtra(r4, r3)
            r11.startService(r4)
            r3 = r8
        L_0x00d1:
            r1 = 1
            goto L_0x00f6
        L_0x00d3:
            android.os.Parcelable r4 = r0.getParcelable(r4)
            r3 = r4
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = (nodomain.freeyourgadget.gadgetbridge.impl.GBDevice) r3
            java.lang.String r4 = "app_uuid"
            java.io.Serializable r4 = r0.getSerializable(r4)
            r2 = r4
            java.util.UUID r2 = (java.util.UUID) r2
            r4 = 0
            java.lang.String r5 = "start_webview"
            boolean r5 = r0.getBoolean(r5, r4)
            if (r5 == 0) goto L_0x00f0
            r11.startBackgroundWebViewAndFinish()
            return
        L_0x00f0:
            java.lang.String r5 = "configure"
            boolean r1 = r0.getBoolean(r5, r4)
        L_0x00f6:
            nodomain.freeyourgadget.gadgetbridge.util.GBPrefs r4 = nodomain.freeyourgadget.gadgetbridge.GBApplication.getGBPrefs()
            boolean r4 = r4.isBackgroundJsEnabled()
            if (r4 == 0) goto L_0x0117
            if (r1 == 0) goto L_0x0113
            java.lang.String r4 = "Must provide a device when invoking this activity"
            java.util.Objects.requireNonNull(r3, r4)
            java.lang.String r4 = "Must provide a uuid when invoking this activity"
            java.util.Objects.requireNonNull(r2, r4)
            nodomain.freeyourgadget.gadgetbridge.util.WebViewSingleton r4 = nodomain.freeyourgadget.gadgetbridge.util.WebViewSingleton.getInstance()
            r4.runJavascriptInterface(r11, r3, r2)
        L_0x0113:
            r11.setupBGWebView()
            goto L_0x0124
        L_0x0117:
            java.lang.String r4 = "Must provide a device when invoking this activity without bgjs"
            java.util.Objects.requireNonNull(r3, r4)
            java.lang.String r4 = "Must provide a uuid when invoking this activity without bgjs"
            java.util.Objects.requireNonNull(r2, r4)
            r11.setupLegacyWebView(r3, r2)
        L_0x0124:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.activities.ExternalPebbleJSActivity.onCreate(android.os.Bundle):void");
    }

    private void startBackgroundWebViewAndFinish() {
        if (GBApplication.getGBPrefs().isBackgroundJsEnabled()) {
            WebViewSingleton.ensureCreated(this);
        } else {
            LOG.warn("BGJs disabled, not starting webview");
        }
        finish();
    }

    private void setupBGWebView() {
        setContentView((int) C0889R.layout.activity_external_pebble_js);
        this.myWebView = WebViewSingleton.getInstance().getWebView(this);
        if (this.myWebView.getParent() != null) {
            ((ViewGroup) this.myWebView.getParent()).removeView(this.myWebView);
        }
        this.myWebView.setWillNotDraw(false);
        this.myWebView.removeJavascriptInterface("GBActivity");
        this.myWebView.addJavascriptInterface(new ActivityJSInterface(), "GBActivity");
        ((FrameLayout) findViewById(C0889R.C0891id.webview_placeholder)).addView(this.myWebView);
        this.myWebView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View v) {
                v.setLayerType(2, (Paint) null);
            }

            public void onViewDetachedFromWindow(View v) {
                v.removeOnAttachStateChangeListener(this);
                ((FrameLayout) ExternalPebbleJSActivity.this.findViewById(C0889R.C0891id.webview_placeholder)).removeAllViews();
            }
        });
    }

    private void setupLegacyWebView(GBDevice device, UUID uuid) {
        setContentView((int) C0889R.layout.activity_legacy_external_pebble_js);
        this.myWebView = (WebView) findViewById(C0889R.C0891id.configureWebview);
        this.myWebView.clearCache(true);
        this.myWebView.setWebViewClient(new GBWebClient());
        this.myWebView.setWebChromeClient(new GBChromeClient());
        WebSettings webSettings = this.myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        this.myWebView.addJavascriptInterface(new JSInterface(device, uuid), "GBjs");
        this.myWebView.addJavascriptInterface(new ActivityJSInterface(), "GBActivity");
        this.myWebView.loadUrl("file:///android_asset/app_config/configure.html");
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        String queryString = "";
        if (this.confUri != null) {
            Logger logger = LOG;
            logger.debug("WEBVIEW returned config: " + this.confUri.toString());
            try {
                queryString = this.confUri.getEncodedQuery();
            } catch (IllegalArgumentException e) {
                C1238GB.toast("returned uri: " + this.confUri.toString(), 1, 3);
            }
            this.myWebView.stopLoading();
            WebView webView = this.myWebView;
            webView.loadUrl("file:///android_asset/app_config/configure.html?" + queryString);
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent incoming) {
        incoming.setFlags(268468224);
        super.onNewIntent(incoming);
        this.confUri = incoming.getData();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    private class ActivityJSInterface {
        private ActivityJSInterface() {
        }

        @JavascriptInterface
        public void closeActivity() {
            NavUtils.navigateUpFromSameTask(ExternalPebbleJSActivity.this);
        }
    }
}
