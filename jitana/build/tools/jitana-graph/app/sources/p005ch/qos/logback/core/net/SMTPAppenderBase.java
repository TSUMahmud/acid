package p005ch.qos.logback.core.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.CharEncoding;
import p005ch.qos.logback.core.AppenderBase;
import p005ch.qos.logback.core.Layout;
import p005ch.qos.logback.core.boolex.EvaluationException;
import p005ch.qos.logback.core.boolex.EventEvaluator;
import p005ch.qos.logback.core.helpers.CyclicBuffer;
import p005ch.qos.logback.core.pattern.PatternLayoutBase;
import p005ch.qos.logback.core.sift.DefaultDiscriminator;
import p005ch.qos.logback.core.sift.Discriminator;
import p005ch.qos.logback.core.spi.CyclicBufferTracker;
import p005ch.qos.logback.core.util.ContentTypeUtil;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.net.SMTPAppenderBase */
public abstract class SMTPAppenderBase<E> extends AppenderBase<E> {
    static InternetAddress[] EMPTY_IA_ARRAY = new InternetAddress[0];
    static final int MAX_DELAY_BETWEEN_STATUS_MESSAGES = 1228800000;
    boolean asynchronousSending = true;
    protected CyclicBufferTracker<E> cbTracker;
    private String charsetEncoding = CharEncoding.UTF_8;
    int delayBetweenStatusMessages = 300000;
    protected Discriminator<E> discriminator = new DefaultDiscriminator();
    private int errorCount = 0;
    protected EventEvaluator<E> eventEvaluator;
    private String from;
    long lastTrackerStatusPrint = 0;
    protected Layout<E> layout;
    String localhost;
    protected MimeMessage mimeMsg;
    String password;
    private String smtpHost;
    private int smtpPort = 25;
    private boolean ssl = false;
    private boolean starttls = false;
    protected Layout<E> subjectLayout;
    private String subjectStr = null;
    private List<PatternLayoutBase<E>> toPatternLayoutList = new ArrayList();
    String username;

    /* renamed from: ch.qos.logback.core.net.SMTPAppenderBase$SenderRunnable */
    class SenderRunnable implements Runnable {
        final CyclicBuffer<E> cyclicBuffer;

        /* renamed from: e */
        final E f52e;

        SenderRunnable(CyclicBuffer<E> cyclicBuffer2, E e) {
            this.cyclicBuffer = cyclicBuffer2;
            this.f52e = e;
        }

        public void run() {
            SMTPAppenderBase.this.sendBuffer(this.cyclicBuffer, this.f52e);
        }
    }

    private Session buildSessionFromProperties() {
        Properties properties = new Properties(OptionHelper.getSystemProperties());
        String str = this.smtpHost;
        if (str != null) {
            properties.put("mail.smtp.host", str);
        }
        properties.put("mail.smtp.port", Integer.toString(this.smtpPort));
        String str2 = this.localhost;
        if (str2 != null) {
            properties.put("mail.smtp.localhost", str2);
        }
        LoginAuthenticator loginAuthenticator = null;
        String str3 = this.username;
        if (str3 != null) {
            loginAuthenticator = new LoginAuthenticator(str3, this.password);
            properties.put("mail.smtp.auth", "true");
        }
        if (!isSTARTTLS() || !isSSL()) {
            if (isSTARTTLS()) {
                properties.put("mail.smtp.starttls.enable", "true");
            }
            if (isSSL()) {
                properties.put("mail.smtp.socketFactory.port", Integer.toString(this.smtpPort));
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.put("mail.smtp.socketFactory.fallback", "true");
            }
        } else {
            addError("Both SSL and StartTLS cannot be enabled simultaneously");
        }
        return Session.getInstance(properties, loginAuthenticator);
    }

    private List<InternetAddress> parseAddress(E e) {
        int size = this.toPatternLayoutList.size();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < size) {
            try {
                String doLayout = this.toPatternLayoutList.get(i).doLayout(e);
                if (doLayout != null) {
                    if (doLayout.length() != 0) {
                        arrayList.addAll(Arrays.asList(InternetAddress.parse(doLayout, true)));
                    }
                }
                i++;
            } catch (AddressException e2) {
                addError("Could not parse email address for [" + this.toPatternLayoutList.get(i) + "] for event [" + e + "]", e2);
            }
        }
        return arrayList;
    }

    public void addTo(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("Null or empty <to> property");
        }
        PatternLayoutBase makeNewToPatternLayout = makeNewToPatternLayout(str.trim());
        makeNewToPatternLayout.setContext(this.context);
        makeNewToPatternLayout.start();
        this.toPatternLayoutList.add(makeNewToPatternLayout);
    }

    /* access modifiers changed from: protected */
    public void append(E e) {
        if (checkEntryConditions()) {
            String discriminatingValue = this.discriminator.getDiscriminatingValue(e);
            long currentTimeMillis = System.currentTimeMillis();
            CyclicBuffer cyclicBuffer = (CyclicBuffer) this.cbTracker.getOrCreate(discriminatingValue, currentTimeMillis);
            subAppend(cyclicBuffer, e);
            try {
                if (this.eventEvaluator.evaluate(e)) {
                    CyclicBuffer cyclicBuffer2 = new CyclicBuffer(cyclicBuffer);
                    cyclicBuffer.clear();
                    if (this.asynchronousSending) {
                        this.context.getExecutorService().execute(new SenderRunnable(cyclicBuffer2, e));
                    } else {
                        sendBuffer(cyclicBuffer2, e);
                    }
                }
            } catch (EvaluationException e2) {
                this.errorCount++;
                if (this.errorCount < 4) {
                    addError("SMTPAppender's EventEvaluator threw an Exception-", e2);
                }
            }
            if (eventMarksEndOfLife(e)) {
                this.cbTracker.endOfLife(discriminatingValue);
            }
            this.cbTracker.removeStaleComponents(currentTimeMillis);
            if (this.lastTrackerStatusPrint + ((long) this.delayBetweenStatusMessages) < currentTimeMillis) {
                addInfo("SMTPAppender [" + this.name + "] is tracking [" + this.cbTracker.getComponentCount() + "] buffers");
                this.lastTrackerStatusPrint = currentTimeMillis;
                int i = this.delayBetweenStatusMessages;
                if (i < MAX_DELAY_BETWEEN_STATUS_MESSAGES) {
                    this.delayBetweenStatusMessages = i * 4;
                }
            }
        }
    }

    public boolean checkEntryConditions() {
        StringBuilder sb;
        String str;
        String str2;
        if (!this.started) {
            sb = new StringBuilder();
            sb.append("Attempting to append to a non-started appender: ");
            str = getName();
        } else if (this.mimeMsg == null) {
            str2 = "Message object not configured.";
            addError(str2);
            return false;
        } else if (this.eventEvaluator == null) {
            sb = new StringBuilder();
            sb.append("No EventEvaluator is set for appender [");
            sb.append(this.name);
            str = "].";
        } else if (this.layout != null) {
            return true;
        } else {
            sb = new StringBuilder();
            sb.append("No layout set for appender named [");
            sb.append(this.name);
            str = "]. For more information, please visit http://logback.qos.ch/codes.html#smtp_no_layout";
        }
        sb.append(str);
        str2 = sb.toString();
        addError(str2);
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract boolean eventMarksEndOfLife(E e);

    /* access modifiers changed from: protected */
    public abstract void fillBuffer(CyclicBuffer<E> cyclicBuffer, StringBuffer stringBuffer);

    /* access modifiers changed from: package-private */
    public InternetAddress getAddress(String str) {
        try {
            return new InternetAddress(str);
        } catch (AddressException e) {
            addError("Could not parse address [" + str + "].", e);
            return null;
        }
    }

    public String getCharsetEncoding() {
        return this.charsetEncoding;
    }

    public CyclicBufferTracker<E> getCyclicBufferTracker() {
        return this.cbTracker;
    }

    public Discriminator<E> getDiscriminator() {
        return this.discriminator;
    }

    public String getFrom() {
        return this.from;
    }

    public Layout<E> getLayout() {
        return this.layout;
    }

    public String getLocalhost() {
        return this.localhost;
    }

    public Message getMessage() {
        return this.mimeMsg;
    }

    public String getPassword() {
        return this.password;
    }

    public String getSMTPHost() {
        return getSmtpHost();
    }

    public int getSMTPPort() {
        return getSmtpPort();
    }

    public String getSmtpHost() {
        return this.smtpHost;
    }

    public int getSmtpPort() {
        return this.smtpPort;
    }

    public String getSubject() {
        return this.subjectStr;
    }

    public List<String> getToAsListOfString() {
        ArrayList arrayList = new ArrayList();
        for (PatternLayoutBase<E> pattern : this.toPatternLayoutList) {
            arrayList.add(pattern.getPattern());
        }
        return arrayList;
    }

    public List<PatternLayoutBase<E>> getToList() {
        return this.toPatternLayoutList;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isAsynchronousSending() {
        return this.asynchronousSending;
    }

    public boolean isSSL() {
        return this.ssl;
    }

    public boolean isSTARTTLS() {
        return this.starttls;
    }

    /* access modifiers changed from: protected */
    public abstract PatternLayoutBase<E> makeNewToPatternLayout(String str);

    /* access modifiers changed from: protected */
    public abstract Layout<E> makeSubjectLayout(String str);

    /* access modifiers changed from: protected */
    public void sendBuffer(CyclicBuffer<E> cyclicBuffer, E e) {
        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            StringBuffer stringBuffer = new StringBuffer();
            String fileHeader = this.layout.getFileHeader();
            if (fileHeader != null) {
                stringBuffer.append(fileHeader);
            }
            String presentationHeader = this.layout.getPresentationHeader();
            if (presentationHeader != null) {
                stringBuffer.append(presentationHeader);
            }
            fillBuffer(cyclicBuffer, stringBuffer);
            String presentationFooter = this.layout.getPresentationFooter();
            if (presentationFooter != null) {
                stringBuffer.append(presentationFooter);
            }
            String fileFooter = this.layout.getFileFooter();
            if (fileFooter != null) {
                stringBuffer.append(fileFooter);
            }
            String str = "Undefined subject";
            if (this.subjectLayout != null) {
                str = this.subjectLayout.doLayout(e);
                int indexOf = str != null ? str.indexOf(10) : -1;
                if (indexOf > -1) {
                    str = str.substring(0, indexOf);
                }
            }
            this.mimeMsg.setSubject(str, this.charsetEncoding);
            List<InternetAddress> parseAddress = parseAddress(e);
            if (parseAddress.isEmpty()) {
                addInfo("Empty destination address. Aborting email transmission");
                return;
            }
            InternetAddress[] internetAddressArr = (InternetAddress[]) parseAddress.toArray(EMPTY_IA_ARRAY);
            this.mimeMsg.setRecipients(Message.RecipientType.TO, internetAddressArr);
            String contentType = this.layout.getContentType();
            if (ContentTypeUtil.isTextual(contentType)) {
                mimeBodyPart.setText(stringBuffer.toString(), this.charsetEncoding, ContentTypeUtil.getSubType(contentType));
            } else {
                mimeBodyPart.setContent(stringBuffer.toString(), this.layout.getContentType());
            }
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(mimeBodyPart);
            this.mimeMsg.setContent(mimeMultipart);
            this.mimeMsg.setSentDate(new Date());
            addInfo("About to send out SMTP message \"" + str + "\" to " + Arrays.toString(internetAddressArr));
            Transport.send(this.mimeMsg);
        } catch (Exception e2) {
            addError("Error occurred while sending e-mail notification.", e2);
        }
    }

    public void setAsynchronousSending(boolean z) {
        this.asynchronousSending = z;
    }

    public void setCharsetEncoding(String str) {
        this.charsetEncoding = str;
    }

    public void setCyclicBufferTracker(CyclicBufferTracker<E> cyclicBufferTracker) {
        this.cbTracker = cyclicBufferTracker;
    }

    public void setDiscriminator(Discriminator<E> discriminator2) {
        this.discriminator = discriminator2;
    }

    public void setEvaluator(EventEvaluator<E> eventEvaluator2) {
        this.eventEvaluator = eventEvaluator2;
    }

    public void setFrom(String str) {
        this.from = str;
    }

    public void setLayout(Layout<E> layout2) {
        this.layout = layout2;
    }

    public void setLocalhost(String str) {
        this.localhost = str;
    }

    public void setMessage(MimeMessage mimeMessage) {
        this.mimeMsg = mimeMessage;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setSMTPHost(String str) {
        setSmtpHost(str);
    }

    public void setSMTPPort(int i) {
        setSmtpPort(i);
    }

    public void setSSL(boolean z) {
        this.ssl = z;
    }

    public void setSTARTTLS(boolean z) {
        this.starttls = z;
    }

    public void setSmtpHost(String str) {
        this.smtpHost = str;
    }

    public void setSmtpPort(int i) {
        this.smtpPort = i;
    }

    public void setSubject(String str) {
        this.subjectStr = str;
    }

    public void setUsername(String str) {
        this.username = str;
    }

    public void start() {
        if (this.cbTracker == null) {
            this.cbTracker = new CyclicBufferTracker<>();
        }
        Session buildSessionFromProperties = buildSessionFromProperties();
        if (buildSessionFromProperties == null) {
            addError("Failed to obtain javax.mail.Session. Cannot start.");
            return;
        }
        this.mimeMsg = new MimeMessage(buildSessionFromProperties);
        try {
            if (this.from != null) {
                this.mimeMsg.setFrom(getAddress(this.from));
            } else {
                this.mimeMsg.setFrom();
            }
            this.subjectLayout = makeSubjectLayout(this.subjectStr);
            this.started = true;
        } catch (MessagingException e) {
            addError("Could not activate SMTPAppender options.", e);
        }
    }

    public synchronized void stop() {
        this.started = false;
    }

    /* access modifiers changed from: protected */
    public abstract void subAppend(CyclicBuffer<E> cyclicBuffer, E e);
}
