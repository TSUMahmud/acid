package nodomain.freeyourgadget.gadgetbridge.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview.GBChromeClient;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview.GBWebClient;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview.JSInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class WebViewSingleton {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) WebViewSingleton.class);
    private static WebViewSingleton instance = new WebViewSingleton();
    private MutableContextWrapper contextWrapper;
    private UUID currentRunningUUID;
    /* access modifiers changed from: private */
    public Messenger internetHelper = null;
    public boolean internetHelperBound;
    private ServiceConnection internetHelperConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            WebViewSingleton.LOG.info("internet helper service bound");
            WebViewSingleton webViewSingleton = WebViewSingleton.this;
            webViewSingleton.internetHelperBound = true;
            Messenger unused = webViewSingleton.internetHelper = new Messenger(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            WebViewSingleton.LOG.info("internet helper service unbound");
            Messenger unused = WebViewSingleton.this.internetHelper = null;
            WebViewSingleton.this.internetHelperBound = false;
        }
    };
    private boolean internetHelperInstalled;
    private Messenger internetHelperListener;
    /* access modifiers changed from: private */
    public WebResourceResponse internetResponse;
    /* access modifiers changed from: private */
    public CountDownLatch latch;
    private Looper mainLooper;
    /* access modifiers changed from: private */
    public WebView webView = null;

    public interface WebViewRunnable {
        void invoke(WebView webView);
    }

    private WebViewSingleton() {
    }

    public static synchronized void ensureCreated(Activity context) {
        synchronized (WebViewSingleton.class) {
            if (instance.webView == null) {
                instance.contextWrapper = new MutableContextWrapper(context);
                instance.mainLooper = context.getMainLooper();
                instance.webView = new WebView(instance.contextWrapper);
                WebView.setWebContentsDebuggingEnabled(true);
                instance.webView.setWillNotDraw(true);
                instance.webView.clearCache(true);
                instance.webView.setWebViewClient(new GBWebClient());
                instance.webView.setWebChromeClient(new GBChromeClient());
                WebSettings webSettings = instance.webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setDatabaseEnabled(true);
            }
        }
    }

    public static WebViewSingleton getInstance() {
        return instance;
    }

    public WebResourceResponse send(Message webRequest) throws RemoteException, InterruptedException {
        webRequest.replyTo = this.internetHelperListener;
        this.latch = new CountDownLatch(1);
        this.internetHelper.send(webRequest);
        this.latch.await();
        return this.internetResponse;
    }

    private class IncomingHandler extends Handler {
        private IncomingHandler() {
        }

        private String getCharsetFromHeaders(String contentType) {
            if (contentType == null || !contentType.toLowerCase().trim().contains("charset=")) {
                return null;
            }
            String[] parts = contentType.toLowerCase().trim().split("=");
            if (parts.length > 0) {
                return parts[1];
            }
            return null;
        }

        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            Logger access$000 = WebViewSingleton.LOG;
            access$000.debug("WEBVIEW: internet helper returned: " + data.getString("response"));
            Map<String, String> headers = new HashMap<>();
            headers.put("Access-Control-Allow-Origin", Marker.ANY_MARKER);
            if (Build.VERSION.SDK_INT >= 21) {
                WebViewSingleton webViewSingleton = WebViewSingleton.this;
                String string = data.getString("content-type");
                WebResourceResponse unused = webViewSingleton.internetResponse = new WebResourceResponse(string, data.getString("content-encoding"), ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, "OK", headers, new ByteArrayInputStream(data.getString("response").getBytes(Charset.forName(getCharsetFromHeaders(data.getString("content-type"))))));
            } else {
                WebResourceResponse unused2 = WebViewSingleton.this.internetResponse = new WebResourceResponse(data.getString("content-type"), data.getString("content-encoding"), new ByteArrayInputStream(data.getString("response").getBytes(Charset.forName(getCharsetFromHeaders(data.getString("content-type"))))));
            }
            WebViewSingleton.this.latch.countDown();
        }
    }

    public WebView getWebView(Context context) {
        this.contextWrapper.setBaseContext(context);
        return this.webView;
    }

    public void checkAppRunning(UUID uuid) {
        if (this.webView == null) {
            throw new IllegalStateException("instance.webView is null!");
        } else if (!uuid.equals(this.currentRunningUUID)) {
            throw new IllegalStateException("Expected app " + uuid + " is not running, but " + this.currentRunningUUID + " is.");
        }
    }

    public void runJavascriptInterface(Activity context, GBDevice device, UUID uuid) {
        ensureCreated(context);
        runJavascriptInterface(device, uuid);
    }

    public void runJavascriptInterface(GBDevice device, UUID uuid) {
        if (uuid.equals(this.currentRunningUUID)) {
            LOG.debug("WEBVIEW uuid not changed keeping the old context");
            return;
        }
        final JSInterface jsInterface = new JSInterface(device, uuid);
        LOG.debug("WEBVIEW uuid changed, restarting");
        this.currentRunningUUID = uuid;
        invokeWebview(new WebViewRunnable() {
            public void invoke(WebView webView) {
                webView.onResume();
                webView.removeJavascriptInterface("GBjs");
                webView.addJavascriptInterface(jsInterface, "GBjs");
                webView.loadUrl("file:///android_asset/app_config/configure.html?rand=" + (Math.random() * 500.0d));
            }
        });
        if (this.contextWrapper != null && !this.internetHelperBound && !this.internetHelperInstalled) {
            String internetHelperCls = "nodomain.freeyourgadget.internethelper" + ".MyService";
            try {
                this.contextWrapper.getPackageManager().getApplicationInfo("nodomain.freeyourgadget.internethelper", 0);
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("nodomain.freeyourgadget.internethelper", internetHelperCls));
                this.contextWrapper.getApplicationContext().bindService(intent, this.internetHelperConnection, 1);
                this.internetHelperListener = new Messenger(new IncomingHandler());
                this.internetHelperInstalled = true;
            } catch (PackageManager.NameNotFoundException e) {
                this.internetHelperInstalled = false;
                LOG.info("WEBVIEW: Internet helper not installed, only mimicked HTTP requests will work.");
            }
        }
    }

    public void stopJavascriptInterface() {
        invokeWebview(new WebViewRunnable() {
            public void invoke(WebView webView) {
                webView.removeJavascriptInterface("GBjs");
                webView.loadUrl("about:blank");
            }
        });
    }

    public void disposeWebView() {
        if (this.internetHelperBound) {
            LOG.debug("WEBVIEW: will unbind the internet helper");
            this.contextWrapper.getApplicationContext().unbindService(this.internetHelperConnection);
            this.internetHelperBound = false;
        }
        this.currentRunningUUID = null;
        invokeWebview(new WebViewRunnable() {
            public void invoke(WebView webView) {
                webView.removeJavascriptInterface("GBjs");
                webView.clearHistory();
                webView.clearCache(true);
                webView.loadUrl("about:blank");
                webView.pauseTimers();
            }
        });
    }

    public void invokeWebview(final WebViewRunnable runnable) {
        Looper looper;
        if (this.webView == null || (looper = this.mainLooper) == null) {
            LOG.warn("Webview already disposed, ignoring runnable");
        } else {
            new Handler(looper).post(new Runnable() {
                public void run() {
                    if (WebViewSingleton.this.webView == null) {
                        WebViewSingleton.LOG.warn("Webview already disposed, cannot invoke runnable");
                    } else {
                        runnable.invoke(WebViewSingleton.this.webView);
                    }
                }
            });
        }
    }
}
