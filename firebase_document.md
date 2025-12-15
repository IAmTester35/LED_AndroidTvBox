# Tài liệu Cấu hình Firebase cho Ứng dụng Android TV Box

Tài liệu này hướng dẫn cấu hình và quản lý các dịch vụ Firebase được tích hợp trong ứng dụng, bao gồm Crashlytics, Performance Monitoring, và Remote Config.

Sử dụng tài khoản developer.reecotech@gmail.com trên Firebase với project `Android TV Box`

## 1. Các dịch vụ Firebase sử dụng
*   **Firebase Crashlytics:** Theo dõi và báo cáo sự cố (crash) của ứng dụng.
*   **Firebase Performance Monitoring:** Theo dõi hiệu năng mạng và ứng dụng.
*   **Firebase Remote Config:** Quản lý cấu hình từ xa (cập nhật mật khẩu, phiên bản ứng dụng).
*   **App Release Monitoring:** Theo dõi việc phân phối phiên bản.

## 2. Cấu hình Firebase Remote Config
Để quản lý ứng dụng, bạn cần thiết lập các tham số sau trên Firebase Console -> Remote Config.
Thời gian cache là 15 phút nên khi thay đổi giá trị trong Remote Config, cần đợi tối đa 15 phút để giá trị mới được áp dụng trong app.
| Key | Kiểu dữ liệu | Mô tả | Giá trị ví dụ |
| :--- | :--- | :--- | :--- |
| `password_hash` | String | Mã hóa SHA256 của mật khẩu quản trị. | `99edc2b391da70f08d8aed876b0c2bb1e976bcaff860abc0f29dcd45fd09d1dc` |
| `latest_version_code` | Number | Mã phiên bản mới nhất của ứng dụng. Dùng để kích hoạt cập nhật. | `28` |
| `apk_download_url` | String | Đường dẫn tải trực tiếp file APK (Direct Link). | `https://example.com/app-release.apk` |

### Chi tiết các trường:

#### a. `password_hash`
*   Đây là chuỗi mã hóa **SHA256** của mật khẩu.
*   Mật khẩu mặc định hiện tại: `vinhlong1@`
*   Mã SHA256 tương ứng: `99edc2b391da70f08d8aed876b0c2bb1e976bcaff860abc0f29dcd45fd09d1dc`
*   **Cách tạo mã SHA256 mới:**
    *   **Mac/Linux:** Mở Terminal và gõ lệnh:
        ```bash
        echo -n "mat_khau_moi" | shasum -a 256
        ```
    *   **Windows:** Sử dụng PowerShell:
        ```powershell
        Get-FileHash -Algorithm SHA256 -InputStream ([IO.MemoryStream]::new([Text.Encoding]::UTF8.GetBytes("mat_khau_moi")))
        ```
    *   **Online:** Sử dụng các trang web tạo SHA256 online (lưu ý bảo mật).

#### b. `latest_version_code`
*   Đây là số nguyên đại diện cho phiên bản ứng dụng (`versionCode` trong file `build.gradle.kts`).
*   **Quy tắc cập nhật:**
    1.  Khi có bản cập nhật mới, hãy tăng `versionCode` trong file `app/build.gradle.kts` lên ít nhất 1 đơn vị (ví dụ từ 28 lên 29).
    2.  Build file APK release.
    3.  Upload file APK lên host lấy link.
    4.  Cập nhật giá trị `latest_version_code` trên Firebase Remote Config bằng với số `versionCode` mới (ví dụ: 29).
    5.  Ứng dụng sẽ tự động phát hiện bản mới (vì 29 > 28) và yêu cầu cập nhật.

#### c. `apk_download_url`
*   Đây là đường dẫn trực tiếp đến file APK.
*   **Yêu cầu quan trọng:**
    *   Link phải là **Direct Link** (Link trực tiếp).
    *   Khi dán link vào trình duyệt và nhấn Enter, trình duyệt phải **bắt đầu tải file ngay lập tức**.
    *   **KHÔNG ĐƯỢC** dùng link dẫn đến trang Google Drive xem trước, trang MediaFire, hay trang web trung gian yêu cầu bấm nút "Download".
    *   Hiện tại đang dùng Dropbox làm nơi lưu trữ các file apk. Không cần quan tâm đến tài khoản lưu trữ, tài khoản nào cũng được.

## 3. Lưu ý quan trọng cho Android Box
Trên một số thiết bị Android Box, quyền cài đặt ứng dụng từ nguồn ngoài có thể bị tắt hoặc bị reset sau khi khởi động lại.

**Hướng dẫn người dùng:**
Nếu ứng dụng tải bản cập nhật về nhưng không hiện bảng cài đặt, hãy kiểm tra:
1.  Vào **Cài đặt (Settings)** trên Android Box.
2.  Chọn **Ứng dụng (Apps)** -> **Quyền truy cập đặc biệt (Special app access)** -> **Cài đặt ứng dụng không rõ nguồn gốc (Install unknown apps)**.
3.  Tìm ứng dụng **AndroidTvBox** (hoặc tên app hiển thị).
4.  Bật **Cho phép (Allow)**.

> Lưu ý: Một số Box giao diện tùy biến có thể nằm ở mục: **Settings > Apps > Security & restrictions > Unknown sources**.
