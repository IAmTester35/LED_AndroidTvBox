# Tài Liệu Kỹ Thuật & Phân Chia Trách Nhiệm - Màn Hình LED P3.076

## 1. Thông Số Kỹ Thuật (Specs)

*   **Loại màn hình:** LED P3.076 Outdoor (Ngoài trời).
*   **Kích thước hiển thị:** 1.12m (Rộng) x 2.24m (Cao).
*   **Độ phân giải thực:** **364 x 728 Pixel**.
*   **Tỷ lệ khung hình (Aspect Ratio):** **1:2** (0.5).
    *   *Lưu ý:* Màn hình cao gấp đôi chiều rộng. Khác với tivi chuẩn 16:9 (1.78).
*   **Phần cứng điều khiển:**
    *   Bộ xử lý hình ảnh: **KP2H** (Hỗ trợ tối đa 1.3 triệu điểm ảnh).
    *   Card nhận: Zhanxin China.
    *   Tần số làm tươi: 3840Hz (Cao).

## 2. Yêu Cầu Kỹ Thuật Cho Ứng Dụng (Software Requirements)
Do màn hình có tỷ lệ đặc biệt (1:2), ứng dụng Android cần đáp ứng:
*   **Responsive:** Tự động co giãn giao diện theo kích thước màn hình.
*   **Hỗ trợ tỷ lệ lạ:** Cấu hình `android:maxAspectRatio` và `resizeableActivity` để không bị hiện tượng viền đen (Letterbox/Pillarbox).
*   **Tín hiệu đầu ra:** Xuất hình ảnh qua cổng HDMI của Android Box.

## 3. Bảng Phân Chia Trách Nhiệm (Responsibility Matrix)

Để đảm bảo dự án vận hành suôn sẻ, trách nhiệm được phân chia rõ ràng giữa hai bên:

| Hạng mục | Bên Phát Triển Phần Mềm (App Developer) | Bên Thi Công Lắp Đặt (Hardware Installer) |
| :--- | :--- | :--- |
| **Nhiệm vụ chính** | Xây dựng App Android chạy trên Android Box. | Lắp đặt khung màn hình, đấu nối điện, cáp tín hiệu. |
| **Xử lý hiển thị** | Đảm bảo App hiển thị đầy đủ, không vỡ layout trên màn hình Test (TV/Màn hình máy tính). | Cấu hình **Bộ xử lý hình ảnh (KP2H)** để cắt/ghép tín hiệu HDMI ra các module LED đúng vị trí. |
| **Nghiệm thu** | App chạy tốt, giao diện đẹp, full màn hình khi cắm vào TV chuẩn. | Hình ảnh trên màn hình LED hiển thị liền mạch, đúng màu, không bị sai lệch vị trí các tấm LED. |
| **Xử lý sự cố** | App bị crash, lỗi dữ liệu, hiển thị sai thông tin. | Hình ảnh bị nhiễu, mất màu, sọc màn hình, hoặc hình ảnh bị cắt ghép lộn xộn. |

### 📌 Kết luận
*   **Lập trình viên** chỉ cần đảm bảo App xuất ra tín hiệu hình ảnh "SẠCH" và "ĐÚNG TỶ LỆ" từ cổng HDMI.
*   Việc **MAPPING** tín hiệu đó lên từng cabin LED là trách nhiệm cấu hình phần cứng của đơn vị thi công.
