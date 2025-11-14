package com.example.restaurant_management.booking_test;

import com.example.restaurant_management.util.EmailService;
import java.io.FileWriter;
import java.io.IOException;

/**
 * MockEmailExporter dùng để mô phỏng việc gửi email xác nhận booking.
 * - Khi simulateFailure = false: tạo file HTML như thật.
 * - Khi simulateFailure = true: giả lập lỗi (không tạo file).
 */
public class MockEmailExporter extends EmailService {

    private boolean simulateFailure = false; // Cờ mô phỏng lỗi

    /**
     * Cho phép test bật/tắt chế độ mô phỏng lỗi gửi email.
     */
    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    @Override
    public void sendBookingConfirmation(String toEmail, String customerName, String bookingDate, String bookingTime) {
        if (simulateFailure) {
            System.out.println("⚠️ Giả lập lỗi: Không thể tạo file mock_email.html (email sending failed)");
            return; // Không tạo file
        }

        String htmlContent = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans&display=swap" rel="stylesheet">
                </head>
                <body style="font-family: 'Noto Sans', Arial, sans-serif; color: #333;">
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
                </body>
                </html>
                """.formatted(customerName, bookingDate, bookingTime);

        try (FileWriter writer = new FileWriter("mock_email.html")) {
            writer.write(htmlContent);
            System.out.println("✅ File mock_email.html đã được tạo!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
