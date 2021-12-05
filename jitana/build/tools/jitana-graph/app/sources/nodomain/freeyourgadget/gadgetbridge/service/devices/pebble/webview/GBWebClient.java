package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.ByteArrayInputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.SPA;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import nodomain.freeyourgadget.gadgetbridge.util.WebViewSingleton;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class GBWebClient extends WebViewClient {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) GBWebClient.class);
    private String[] AllowedDomains = {"openweathermap.org", "rawgit.com", "tagesschau.de"};

    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Uri parsedUri = request.getUrl();
        Logger logger = LOG;
        logger.debug("WEBVIEW shouldInterceptRequest URL: " + parsedUri.toString());
        WebResourceResponse mimickedReply = mimicReply(parsedUri);
        if (mimickedReply != null) {
            return mimickedReply;
        }
        return super.shouldInterceptRequest(view, request);
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        Logger logger = LOG;
        logger.debug("WEBVIEW shouldInterceptRequest URL (legacy): " + url);
        WebResourceResponse mimickedReply = mimicReply(Uri.parse(url));
        if (mimickedReply != null) {
            return mimickedReply;
        }
        return super.shouldInterceptRequest(view, url);
    }

    private WebResourceResponse mimicReply(Uri requestedUri) {
        if (requestedUri.getHost() == null || StringUtils.indexOfAny((CharSequence) requestedUri.getHost(), (CharSequence[]) this.AllowedDomains) == -1) {
            Logger logger = LOG;
            logger.debug("WEBVIEW request:" + requestedUri.toString() + " not intercepted");
            return null;
        } else if (GBApplication.getGBPrefs().isBackgroundJsEnabled() && WebViewSingleton.getInstance().internetHelperBound) {
            LOG.debug("WEBVIEW forwarding request to the internet helper");
            Bundle bundle = new Bundle();
            bundle.putString("URL", requestedUri.toString());
            Message webRequest = Message.obtain();
            webRequest.setData(bundle);
            try {
                return WebViewSingleton.getInstance().send(webRequest);
            } catch (RemoteException | InterruptedException e) {
                Logger logger2 = LOG;
                logger2.warn("Error downloading data from " + requestedUri, (Throwable) e);
                return null;
            }
        } else if (StringUtils.endsWith(requestedUri.getHost(), "openweathermap.org")) {
            Logger logger3 = LOG;
            logger3.debug("WEBVIEW request to openweathermap.org detected of type: " + requestedUri.getPath() + " params: " + requestedUri.getQuery());
            return mimicOpenWeatherMapResponse(requestedUri.getPath(), requestedUri.getQueryParameter("units"));
        } else if (StringUtils.endsWith(requestedUri.getHost(), "rawgit.com")) {
            Logger logger4 = LOG;
            logger4.debug("WEBVIEW request to rawgit.com detected of type: " + requestedUri.getPath() + " params: " + requestedUri.getQuery());
            return mimicRawGitResponse(requestedUri.getPath());
        } else {
            Logger logger5 = LOG;
            logger5.debug("WEBVIEW request to allowed domain detected but not intercepted: " + requestedUri.toString());
            return null;
        }
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri parsedUri = Uri.parse(url);
        if (parsedUri.getScheme().startsWith("http")) {
            Intent i = new Intent("android.intent.action.VIEW", Uri.parse(url));
            i.setFlags(268468224);
            GBApplication.getContext().startActivity(i);
            return true;
        } else if (parsedUri.getScheme().startsWith("pebblejs")) {
            view.loadUrl(url.replaceFirst("^pebblejs://close#", "file:///android_asset/app_config/configure.html?config=true&json="));
            return true;
        } else if (parsedUri.getScheme().equals("data")) {
            view.loadUrl(url);
            return true;
        } else {
            Logger logger = LOG;
            logger.debug("WEBVIEW Ignoring unhandled scheme: " + parsedUri.getScheme());
            return true;
        }
    }

    private WebResourceResponse mimicRawGitResponse(String path) {
        if (!"/aHcVolle/TrekVolle/master/online.html".equals(path)) {
            return null;
        }
        if (Build.VERSION.SDK_INT < 21) {
            return new WebResourceResponse("text/html", "utf-8", new ByteArrayInputStream(MiBandConst.MI_1.getBytes()));
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", Marker.ANY_MARKER);
        return new WebResourceResponse("text/html", "utf-8", ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, "OK", headers, new ByteArrayInputStream(MiBandConst.MI_1.getBytes()));
    }

    private WebResourceResponse mimicOpenWeatherMapResponse(String type, String units) {
        if (Weather.getInstance() == null) {
            LOG.warn("WEBVIEW - Weather instance is null, cannot update weather");
            return null;
        }
        CurrentPosition currentPosition = new CurrentPosition();
        try {
            JSONObject resp = Weather.getInstance().createReconstructedOWMWeatherReply();
            if (!"/data/2.5/weather".equals(type) || resp == null) {
                Logger logger = LOG;
                logger.warn("WEBVIEW - cannot mimick request of type " + type + " (unsupported or lack of data)");
                return null;
            }
            convertTemps(resp.getJSONObject("main"), units);
            convertSpeeds(resp.getJSONObject("wind"), units);
            resp.put("cod", ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
            resp.put("coord", coordObject(currentPosition));
            resp.put(NotificationCompat.CATEGORY_SYSTEM, sysObject(currentPosition));
            Logger logger2 = LOG;
            logger2.info("WEBVIEW - mimic openweather response" + resp.toString());
            Map<String, String> headers = new HashMap<>();
            headers.put("Access-Control-Allow-Origin", Marker.ANY_MARKER);
            if (Build.VERSION.SDK_INT < 21) {
                return new WebResourceResponse("application/json", "utf-8", new ByteArrayInputStream(resp.toString().getBytes()));
            }
            return new WebResourceResponse("application/json", "utf-8", ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, "OK", headers, new ByteArrayInputStream(resp.toString().getBytes()));
        } catch (JSONException e) {
            LOG.warn("Error building the JSON weather message.", (Throwable) e);
            return null;
        }
    }

    private static JSONObject sysObject(CurrentPosition currentPosition) throws JSONException {
        GregorianCalendar[] sunrise = SPA.calculateSunriseTransitSet(new GregorianCalendar(), (double) currentPosition.getLatitude(), (double) currentPosition.getLongitude(), DeltaT.estimate(new GregorianCalendar()));
        JSONObject sys = new JSONObject();
        sys.put("country", "World");
        sys.put("sunrise", sunrise[0].getTimeInMillis() / 1000);
        sys.put(MiBandConst.PREF_NIGHT_MODE_SUNSET, sunrise[2].getTimeInMillis() / 1000);
        return sys;
    }

    private static void convertSpeeds(JSONObject wind, String units) throws JSONException {
        if ("metric".equals(units)) {
            wind.put("speed", wind.getDouble("speed") * 3.5999999046325684d);
        } else if ("imperial".equals(units)) {
            wind.put("speed", wind.getDouble("speed") * 2.236999988555908d);
        }
    }

    private static void convertTemps(JSONObject main, String units) throws JSONException {
        if ("metric".equals(units)) {
            main.put("temp", ((Integer) main.get("temp")).intValue() - 273);
            main.put("temp_min", ((Integer) main.get("temp_min")).intValue() - 273);
            main.put("temp_max", ((Integer) main.get("temp_max")).intValue() - 273);
        } else if ("imperial".equals(units)) {
            main.put("temp", (double) (((((float) ((Integer) main.get("temp")).intValue()) - 273.15f) * 1.8f) + 32.0f));
            main.put("temp_min", (double) (((((float) ((Integer) main.get("temp_min")).intValue()) - 273.15f) * 1.8f) + 32.0f));
            main.put("temp_max", (double) (((((float) ((Integer) main.get("temp_max")).intValue()) - 273.15f) * 1.8f) + 32.0f));
        }
    }

    private static JSONObject coordObject(CurrentPosition currentPosition) throws JSONException {
        JSONObject coord = new JSONObject();
        coord.put("lat", (double) currentPosition.getLatitude());
        coord.put("lon", (double) currentPosition.getLongitude());
        return coord;
    }
}
