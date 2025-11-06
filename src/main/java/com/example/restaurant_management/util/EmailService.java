package com.example.restaurant_management.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBookingConfirmation(
            String toEmail,
            String customerName,
            String bookingDate,
            String bookingTime
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("✅ Xác nhận đặt bàn tại Riverside Terrace Restaurant");
            helper.setFrom("duonghongminh6bqxk@gmail.com", "Riverside Terrace Restaurant");

            // HTML nội dung email
            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 10px; overflow: hidden;">
                        <div style="background-color: #d32f2f; color: white; padding: 20px; text-align: center;">
                            <h2 style="margin: 0;">Riverside Terrace Restaurant</h2>
                            <p style="margin: 5px 0 0;">Xác nhận đặt bàn thành công</p>
                        </div>
                        <div style="padding: 25px;">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Cảm ơn bạn đã đặt bàn tại <strong>Riverside Terrace Restaurant</strong>! Dưới đây là thông tin đặt bàn của bạn:</p>
                            <table style="width: 100%%; border-collapse: collapse; margin-top: 10px;">
                                <tr>
                                    <td style="padding: 8px 0;"><strong>📅 Ngày:</strong></td>
                                    <td>%s</td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px 0;"><strong>⏰ Giờ:</strong></td>
                                    <td>%s</td>
                                </tr>
                            </table>
                            <p style="margin-top: 20px;">Vui lòng có mặt đúng giờ để chúng tôi phục vụ tốt nhất cho bạn 💖.</p>
                            <p>Hẹn gặp lại bạn tại <strong>Riverside Terrace Restaurant</strong>!</p>
                        </div>
                        <div style="background-color: #f5f5f5; padding: 15px; text-align: center; font-size: 13px; color: #777;">
                            <p style="margin: 5px 0;">📍 123 Đường Nguyễn Trãi, Quận 1, TP.HCM</p>
                            <p style="margin: 5px 0;">📞 Hotline: 0123 456 789 | ✉️ contact@nhahangxyz.com</p>
                            <p style="margin: 5px 0;">&copy; 2025 Riverside Terrace Restaurant. All rights reserved.</p>
                        </div>
                    </div>
                    """.formatted(customerName, bookingDate, bookingTime);

            helper.setText(htmlContent, true); // true = gửi HTML

            mailSender.send(message);

            System.out.println("📩 Email xác nhận đã được gửi tới: " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Gửi email thất bại: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
