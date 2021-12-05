package p005ch.qos.logback.core.net;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/* renamed from: ch.qos.logback.core.net.LoginAuthenticator */
public class LoginAuthenticator extends Authenticator {
    String password;
    String username;

    LoginAuthenticator(String str, String str2) {
        this.username = str;
        this.password = str2;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password);
    }
}
