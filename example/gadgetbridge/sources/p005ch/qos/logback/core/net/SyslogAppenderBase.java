package p005ch.qos.logback.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import nodomain.freeyourgadget.gadgetbridge.entities.UserDao;
import p005ch.qos.logback.core.AppenderBase;
import p005ch.qos.logback.core.Layout;

/* renamed from: ch.qos.logback.core.net.SyslogAppenderBase */
public abstract class SyslogAppenderBase<E> extends AppenderBase<E> {
    static final int MAX_MESSAGE_SIZE_LIMIT = 65000;
    static final String SYSLOG_LAYOUT_URL = "http://logback.qos.ch/codes.html#syslog_layout";
    String facilityStr;
    boolean initialized = false;
    Layout<E> layout;
    private boolean lazyInit = false;
    int maxMessageSize;
    int port = SyslogConstants.SYSLOG_PORT;
    protected SyslogOutputStream sos;
    protected String suffixPattern;
    String syslogHost;

    private boolean connect() {
        try {
            this.sos = new SyslogOutputStream(this.syslogHost, this.port);
            int sendBufferSize = this.sos.getSendBufferSize();
            if (this.maxMessageSize == 0) {
                this.maxMessageSize = Math.min(sendBufferSize, MAX_MESSAGE_SIZE_LIMIT);
                addInfo("Defaulting maxMessageSize to [" + this.maxMessageSize + "]");
            } else if (this.maxMessageSize > sendBufferSize) {
                addWarn("maxMessageSize of [" + this.maxMessageSize + "] is larger than the system defined datagram size of [" + sendBufferSize + "].");
                addWarn("This may result in dropped logs.");
            }
        } catch (UnknownHostException e) {
            addError("Could not create SyslogWriter", e);
        } catch (SocketException e2) {
            addWarn("Failed to bind to a random datagram socket. Will try to reconnect later.", e2);
        }
        return this.sos != null;
    }

    public static int facilityStringToint(String str) {
        if ("KERN".equalsIgnoreCase(str)) {
            return 0;
        }
        if (UserDao.TABLENAME.equalsIgnoreCase(str)) {
            return 8;
        }
        if ("MAIL".equalsIgnoreCase(str)) {
            return 16;
        }
        if ("DAEMON".equalsIgnoreCase(str)) {
            return 24;
        }
        if ("AUTH".equalsIgnoreCase(str)) {
            return 32;
        }
        if ("SYSLOG".equalsIgnoreCase(str)) {
            return 40;
        }
        if ("LPR".equalsIgnoreCase(str)) {
            return 48;
        }
        if ("NEWS".equalsIgnoreCase(str)) {
            return 56;
        }
        if ("UUCP".equalsIgnoreCase(str)) {
            return 64;
        }
        if ("CRON".equalsIgnoreCase(str)) {
            return 72;
        }
        if ("AUTHPRIV".equalsIgnoreCase(str)) {
            return 80;
        }
        if ("FTP".equalsIgnoreCase(str)) {
            return 88;
        }
        if ("NTP".equalsIgnoreCase(str)) {
            return 96;
        }
        if ("AUDIT".equalsIgnoreCase(str)) {
            return 104;
        }
        if ("ALERT".equalsIgnoreCase(str)) {
            return 112;
        }
        if ("CLOCK".equalsIgnoreCase(str)) {
            return 120;
        }
        if ("LOCAL0".equalsIgnoreCase(str)) {
            return 128;
        }
        if ("LOCAL1".equalsIgnoreCase(str)) {
            return SyslogConstants.LOG_LOCAL1;
        }
        if ("LOCAL2".equalsIgnoreCase(str)) {
            return SyslogConstants.LOG_LOCAL2;
        }
        if ("LOCAL3".equalsIgnoreCase(str)) {
            return SyslogConstants.LOG_LOCAL3;
        }
        if ("LOCAL4".equalsIgnoreCase(str)) {
            return SyslogConstants.LOG_LOCAL4;
        }
        if ("LOCAL5".equalsIgnoreCase(str)) {
            return SyslogConstants.LOG_LOCAL5;
        }
        if ("LOCAL6".equalsIgnoreCase(str)) {
            return SyslogConstants.LOG_LOCAL6;
        }
        if ("LOCAL7".equalsIgnoreCase(str)) {
            return SyslogConstants.LOG_LOCAL7;
        }
        throw new IllegalArgumentException(str + " is not a valid syslog facility string");
    }

    /* access modifiers changed from: protected */
    public void append(E e) {
        if (isStarted()) {
            if (!this.initialized && this.lazyInit) {
                this.initialized = true;
                connect();
            }
            if (this.sos != null) {
                try {
                    String doLayout = this.layout.doLayout(e);
                    if (doLayout != null) {
                        if (doLayout.length() > this.maxMessageSize) {
                            doLayout = doLayout.substring(0, this.maxMessageSize);
                        }
                        this.sos.write(doLayout.getBytes());
                        this.sos.flush();
                        postProcess(e, this.sos);
                    }
                } catch (IOException e2) {
                    addError("Failed to send diagram to " + this.syslogHost, e2);
                }
            }
        }
    }

    public abstract Layout<E> buildLayout();

    public String getFacility() {
        return this.facilityStr;
    }

    public Layout<E> getLayout() {
        return this.layout;
    }

    public boolean getLazy() {
        return this.lazyInit;
    }

    public int getMaxMessageSize() {
        return this.maxMessageSize;
    }

    public int getPort() {
        return this.port;
    }

    public abstract int getSeverityForEvent(Object obj);

    public String getSuffixPattern() {
        return this.suffixPattern;
    }

    public String getSyslogHost() {
        return this.syslogHost;
    }

    /* access modifiers changed from: protected */
    public void postProcess(Object obj, OutputStream outputStream) {
    }

    public void setFacility(String str) {
        if (str != null) {
            str = str.trim();
        }
        this.facilityStr = str;
    }

    public void setLayout(Layout<E> layout2) {
        addWarn("The layout of a SyslogAppender cannot be set directly. See also http://logback.qos.ch/codes.html#syslog_layout");
    }

    public void setLazy(boolean z) {
        this.lazyInit = z;
    }

    public void setMaxMessageSize(int i) {
        this.maxMessageSize = i;
    }

    public void setPort(int i) {
        this.port = i;
    }

    public void setSuffixPattern(String str) {
        this.suffixPattern = str;
    }

    public void setSyslogHost(String str) {
        this.syslogHost = str;
    }

    public void start() {
        int i;
        if (this.facilityStr == null) {
            addError("The Facility option is mandatory");
            i = 1;
        } else {
            i = 0;
        }
        if (!this.lazyInit && !connect()) {
            i++;
        }
        if (this.layout == null) {
            this.layout = buildLayout();
        }
        if (i == 0) {
            super.start();
        }
    }

    public void stop() {
        this.sos.close();
        super.stop();
    }
}
