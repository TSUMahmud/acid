package p005ch.qos.logback.classic.android;

import brut.androlib.res.decoder.AXmlResourceParser;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;
import org.xmlpull.v1.XmlPullParser;
import p005ch.qos.logback.core.joran.event.SaxEvent;
import p005ch.qos.logback.core.joran.event.SaxEventRecorder;
import p005ch.qos.logback.core.joran.spi.JoranException;

/* renamed from: ch.qos.logback.classic.android.ASaxEventRecorder */
public class ASaxEventRecorder extends SaxEventRecorder {
    private Map<String, String> elemAttrs = null;
    private String elemNameToWatch = null;
    private StatePassFilter filter = new StatePassFilter(new String[0]);
    private int[] holderForStartAndLength = new int[2];

    /* renamed from: ch.qos.logback.classic.android.ASaxEventRecorder$StatePassFilter */
    static class StatePassFilter {
        private int _depth = 0;
        private final String[] _states;

        public StatePassFilter(String... strArr) {
            this._states = strArr == null ? new String[0] : strArr;
        }

        public boolean checkEnd(String str) {
            int i = this._depth;
            if (i <= 0 || !str.equals(this._states[i - 1])) {
                return this._depth == this._states.length;
            }
            this._depth--;
            return false;
        }

        public boolean checkStart(String str) {
            int i = this._depth;
            String[] strArr = this._states;
            if (i == strArr.length) {
                return true;
            }
            if (!str.equals(strArr[i])) {
                return false;
            }
            this._depth++;
            return false;
        }

        public int depth() {
            return this._depth;
        }

        public boolean passed() {
            return this._depth == this._states.length;
        }

        public void reset() {
            this._depth = 0;
        }

        public int size() {
            return this._states.length;
        }
    }

    private void characters(XmlPullParser xmlPullParser) {
        if (this.filter.passed()) {
            char[] textCharacters = xmlPullParser.getTextCharacters(this.holderForStartAndLength);
            int[] iArr = this.holderForStartAndLength;
            super.characters(textCharacters, iArr[0], iArr[1]);
        }
    }

    private void checkForWatchedAttributes(XmlPullParser xmlPullParser) {
        String str;
        int i;
        if (this.elemNameToWatch != null && this.elemAttrs == null && xmlPullParser.getName().equals(this.elemNameToWatch)) {
            HashMap hashMap = new HashMap();
            for (int i2 = 0; i2 < xmlPullParser.getAttributeCount(); i2++) {
                String attributeNamespace = xmlPullParser.getAttributeNamespace(i2);
                if (attributeNamespace.length() > 0) {
                    int lastIndexOf = attributeNamespace.lastIndexOf("/");
                    if (lastIndexOf > -1 && (i = lastIndexOf + 1) < attributeNamespace.length()) {
                        attributeNamespace = attributeNamespace.substring(i);
                    }
                    str = attributeNamespace + ":";
                } else {
                    str = "";
                }
                hashMap.put(str + xmlPullParser.getAttributeName(i2), xmlPullParser.getAttributeValue(i2));
            }
            this.elemAttrs = hashMap;
        }
    }

    private void endElement(XmlPullParser xmlPullParser) {
        String name = xmlPullParser.getName();
        if (this.filter.checkEnd(name)) {
            endElement(xmlPullParser.getNamespace(), name, name);
        }
    }

    private void startDocument(XmlPullParser xmlPullParser) {
        super.startDocument();
        super.setDocumentLocator(new LocatorImpl());
    }

    private void startElement(XmlPullParser xmlPullParser) {
        String name = xmlPullParser.getName();
        if (this.filter.checkStart(name)) {
            AttributesImpl attributesImpl = new AttributesImpl();
            for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
                attributesImpl.addAttribute(xmlPullParser.getAttributeNamespace(i), xmlPullParser.getAttributeName(i), xmlPullParser.getAttributeName(i), xmlPullParser.getAttributeType(i), xmlPullParser.getAttributeValue(i));
            }
            startElement(xmlPullParser.getNamespace(), name, name, attributesImpl);
        }
        checkForWatchedAttributes(xmlPullParser);
    }

    public Map<String, String> getAttributeWatchValues() {
        return this.elemAttrs;
    }

    public List<SaxEvent> recordEvents(InputSource inputSource) throws JoranException {
        InputStream byteStream = inputSource.getByteStream();
        if (byteStream != null) {
            try {
                AXmlResourceParser aXmlResourceParser = new AXmlResourceParser(byteStream);
                this.elemAttrs = null;
                while (true) {
                    int next = aXmlResourceParser.next();
                    if (next <= -1) {
                        break;
                    } else if (next == 0) {
                        this.filter.reset();
                        startDocument(aXmlResourceParser);
                    } else if (1 == next) {
                        this.filter.reset();
                        endDocument();
                        break;
                    } else if (2 == next) {
                        startElement(aXmlResourceParser);
                    } else if (3 == next) {
                        endElement(aXmlResourceParser);
                    } else if (4 == next) {
                        characters(aXmlResourceParser);
                    }
                }
                return getSaxEventList();
            } catch (Exception e) {
                addError(e.getMessage(), e);
                throw new JoranException("Can't parse Android XML resource", e);
            }
        } else {
            throw new IllegalArgumentException("Input source must specify an input stream");
        }
    }

    public void setAttributeWatch(String str) {
        this.elemNameToWatch = str;
    }

    public void setFilter(String... strArr) {
        this.filter = new StatePassFilter(strArr);
    }
}
