package pro1041.utils;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class EmailHelper {

    private static final String MY_EMAIL = "shoestoresys@gmail.com";
    private static final String MY_PASSWORD = "hell0w0rld";

    public static void sendEmail(String toEmail, String subject, String message) throws EmailException {
        Email email = new SimpleEmail();
        // Cấu hình thông tin Email Server
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(MY_EMAIL,
                MY_PASSWORD));
        // Với gmail cái này là bắt buộc.
        email.setSSLOnConnect(true);
        // Người gửi
        email.setFrom(MY_EMAIL);
        // Tiêu đề
        email.setSubject(subject);
        // Nội dung email
        email.setMsg(message);
        // Người nhận
        email.addTo(toEmail);
        email.send();
    }

    public static void sendVerifyCode(String email, String verifyCode) throws EmailException {
        sendEmail(email, "[ShoeStoreSys - Sneaker.Beatt] Yêu cầu thay đổi mật khẩu mới?", textingVerifyEmail(email, verifyCode));
    }

    private static String textingVerifyEmail(String email, String verifyCode) {
        return "Hey " + email + "!\r\nCó phải bạn đang quên mật khẩu ?\r\n"
                + "Nếu vậy hãy nhập mã xác minh sau!\r\n\r\nMã xác minh : " + verifyCode + "\r\n\r\nNếu"
                + " bạn đang không cố gắng đăng nhập vào tài khoản của mình, có thể có"
                + " người đang cố gắng xâm phạm tài khoản của bạn. Hãy bỏ qua email này !";
    }
}
