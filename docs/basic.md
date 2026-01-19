# Hướng dẫn cài đặt ứng dụng cho Android TV Box qua ADB

Tài liệu này hướng dẫn cách cài đặt tệp APK lên Android TV Box bằng công cụ ADB (Android Debug Bridge). Phù hợp cho người dùng có kiến thức kỹ thuật cơ bản.

---

## 1. Chuẩn bị
*   **Thiết bị:** Một máy tính (Windows/macOS) và một Android TV Box.
*   **Kết nối:** Cả hai thiết bị phải kết nối cùng một mạng Wi-Fi (hoặc LAN), hoặc kết nối trực tiếp qua cáp USB (thường là cáp USB hai đầu đực nếu box hỗ trợ).
*   **Tệp APK:** Tải xuống ứng dụng tại đây: [Link tải APK](https://www.dropbox.com/scl/fi/pp9hoks33haxnll1l5uwl/39.apk?rlkey=veoe376n9j04o3w595rm7pldu&st=fmbg1a8a&dl=1) (Lưu tệp vào một thư mục dễ nhớ trên máy tính, ví dụ: `C:\adb\app.apk`).

---

## 2. Kích hoạt Chế độ nhà phát triển trên Android TV Box ( thường thì đã bật rồi)
Để máy tính có thể giao tiếp với Box, bạn cần bật "USB Debugging":
1.  Trên Android TV Box, vào **Cài đặt (Settings)** -> **Tùy chọn thiết bị (Device Preferences)** -> **Giới thiệu (About)**.
2.  Tìm đến mục **Số bản dựng (Build number)** và nhấn phím OK liên tục 7 lần cho đến khi hiện thông báo "Bạn đã là nhà phát triển".
3.  Quay lại menu trước đó, chọn **Tùy chọn nhà phát triển (Developer Options)**.
4.  Tìm và bật mục **Gỡ lỗi USB (USB Debugging)**.

---

## 3. Cài đặt ADB trên máy tính
1.  Tải bộ công cụ Platform Tools từ Google: [Tải tại đây](https://developer.android.com/tools/releases/platform-tools).
2.  Giải nén tệp vừa tải về (ví dụ giải nén vào thư mục `C:\platform-tools`).
3.  Mở Command Prompt/PowerShell (Windows):
    *   Sử dụng lệnh `cd` để di chuyển vào thư mục vừa giải nén.
    *   Ví dụ: `cd C:\platform-tools`
4.  Kiểm tra xem ADB đã hoạt động chưa bằng lệnh: `adb version`

---

## 4. Kết nối máy tính với Android TV Box
### Cách 1: Qua mạng Wi-Fi (Khuyên dùng)
1.  Tìm địa chỉ IP của Box: **Cài đặt** -> **Mạng và Internet** -> Chọn Wi-Fi đang kết nối -> Xem mục **Địa chỉ IP** (Ví dụ: `172.16.11.x`).
2.  Trên máy tính, nhập lệnh:
    ```bash
    adb connect [ĐỊA_CHỈ_IP]
    ```
    *(Ví dụ: `adb connect 172.16.11.x`)*
3.  **Lưu ý:** Trên màn hình TV sẽ hiện một bảng hỏi "Allow USB Debugging?", hãy chọn **Always allow** và nhấn **OK**. ( Có thể xem không yêu cầu cái này )

### Cách 2: Qua cáp USB
1.  Cắm cáp nối máy tính và Box.
2.  Nhập lệnh: `adb devices`. Nếu thấy mã thiết bị hiện ra kèm chữ `device` là thành công.

---

## 5. Cài đặt tệp APK
Sử dụng lệnh sau để tiến hành cài đặt ứng dụng:
```bash
adb install [đường_dẫn_đến_file_apk]
```
**Ví dụ thực tế:**
*   Nếu tệp APK nằm cùng thư mục với adb: `adb install 39.apk`
*   Nếu tệp nằm ở ổ C: `adb install C:\Downloads\39.apk`

Khi hiện chữ **Success** là quá trình cài đặt hoàn tất.

---

## 6. Cấu hình quyền Trợ năng (Accessibility)
Sau khi cài đặt xong, bạn cần cấp quyền để ứng dụng hoạt động chính xác:
1.  Mở ứng dụng vừa cài đặt trên Android TV Box.
2.  Vào phần **Cài đặt (Settings)** của hệ thống TV Box.
3.  Tìm mục **Trợ năng (Accessibility)**.
4.  Kéo xuống dưới cùng tìm mục có tên **androidtvbox** (hoặc tên dịch vụ liên quan đến app).
5.  Chọn nó và gạt công tắc sang **Bật (On)**.

---
*Chúc bạn thành công!*
