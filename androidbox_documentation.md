
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
| `password_hash` | String | Mã hóa SHA256 của mật khẩu quản trị. | `99e...fd09d1dc` |
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


# Hướng dẫn Vận hành & Kỹ thuật

## 1. Chế độ Kiosk (Kiosk Mode)
Để thiết lập ứng dụng làm launcher mặc định (App Kiosk):

- **Khoá chết (Set Kiosk):**
  ```bash
  adb shell cmd package set-home-activity com.reecotech.androidtvbox/.MainActivity
  ```

- **Mở khoá (Unlock/Open Settings):**
  ```bash
  adb shell cmd package set-home-activity com.android.settings/.Settings
  ```

- **Password thoát ứng dụng:** `vinhlong1@`

## 2. Thiết lập Quyền (Privileges)
Để ứng dụng có thể **Silent Update** mà không cần Root, cần set quyền **Device Owner**:

- **Lệnh set Device Owner:**
  ```bash
  adb shell dpm set-device-owner com.reecotech.androidtvbox/.receiver.AdminReceiver
  ```
  *(Lưu ý: Thiết bị phải chưa có tài khoản Google hoặc cần factory reset trước khi chạy lệnh này)*

> [!IMPORTANT]
> **Phân biệt Device Admin vs Device Owner:**
> *   **Device Admin:** Chỉ có thể khóa máy, wipe data. **KHÔNG THỂ** silent install/update ứng dụng.
> *   **Device Owner:** Có toàn quyền hệ thống, bao gồm cài đặt ứng dụng không cần người dùng xác nhận.
> *   Để làm App Kiosk tự update, bắt buộc phải set **Device Owner**.

## 3. Cơ chế Cập nhật Tự động (Update Mechanism)

Hệ thống cập nhật được thiết kế để hoạt động ngầm (silent) tối đa, giảm thiểu thao tác của người dùng cuối.

### 3.1. Quy trình Cập nhật (Update Flow)
1.  **Kiểm tra (Check):**
    *   Ứng dụng so sánh `versionCode` hiện tại với `latest_version_code` trên Firebase Remote Config.
    *   Nếu `remote > current` -> Bắt đầu quy trình cập nhật.

2.  **Tải xuống (Download):**
    *   Sử dụng `DownloadManager` của hệ thống Android.
    *   File được tải vào bộ nhớ tạm: `Running Download -> .tmp file -> Rename to .apk file`.
    *   **Giao diện:** Hiển thị thông báo Toast: *"Downloading version..."*.

3.  **Cài đặt (Install):**
    *   Ngay sau khi tải xong, ứng dụng tự động kích hoạt tiến trình cài đặt.
    *   **Giao diện:** Hiển thị Toast *"Download complete. Installing..."* hoặc *"Update file ready. Installing..."*.

### 3.2. Màn hình & Trải nghiệm khi Cập nhật (Update UX)
Do hướng tới trải nghiệm Kiosk/Digital Signage, ứng dụng **KHÔNG** có màn hình chờ cập nhật riêng biệt.
*   **Trường hợp chuẩn (Silent Update):**
    *   Ứng dụng vẫn hiển thị dữ liệu bình thường trong lúc tải.
    *   Khi tải xong, ứng dụng sẽ **tự động tắt và khởi động lại** phiên bản mới.
    *   Thời gian gián đoạn: ~5-10 giây (tùy tốc độ xử lý của Box).
*   **Trường hợp Fallback (Manual Update):**
    *   Nếu mất quyền Device Owner, màn hình cài đặt chuẩn của Android sẽ hiện lên đè lên ứng dụng.
    *   Người dùng cần bấm "Install" để xác nhận.

### 3.3. Các phương thức Cài đặt (Install Strategies)
Hệ thống `UpdateManager` sẽ thử lần lượt các phương pháp sau theo thứ tự ưu tiên:

1.  **Device Owner / System Permission (Ưu tiên số 1):**
    *   Sử dụng `PackageInstaller` API.
    *   **Yêu cầu:** App là Device Owner HOẶC App là System App có quyền `android.permission.INSTALL_PACKAGES`.
    *   **Kết quả:** Cài đặt im lặng, tự khởi động lại.

2.  **Silent Install via Shell (Ưu tiên số 2):**
    *   Lệnh: `pm install -r <path>`
    *   **Yêu cầu:** App là System App (hoặc ROM custom cho phép).
    *   **Kết quả:** Cài đặt im lặng.

3.  **Root Install (Ưu tiên số 3):**
    *   Lệnh: `su -c pm install -r <path>`
    *   **Yêu cầu:** Thiết bị đã Root.
    *   **Kết quả:** Cài đặt im lặng.

4.  **Standard Intent (Fallback cuối cùng):**
    *   Gửi Intent `ACTION_VIEW` loại `application/vnd.android.package-archive`.
    *   **Kết quả:** Hiện popup cài đặt của Android, cần người dùng bấm xác nhận.

### 3.4. Xử lý Lỗi & Edge Cases
*   **Mạng yếu/Mất mạng:** `DownloadManager` tự động retry (được cấu hình cho phép tải qua Roaming/Metered network).
*   **File lỗi/Corrupt:** Tiến trình đổi tên `tmp` -> `apk` sẽ thất bại, báo lỗi Toast và không cài đặt.
*   **Rủi ro bảo mật:** Ứng dụng kiểm tra Hash SHA256 (nếu được cấu hình nghiêm ngặt, hiện tại dựa trên HTTPS link).
*   **Thiết bị đầy bộ nhớ:** `DownloadManager` sẽ báo lỗi hệ thống.

## 4. Chú giải Giao diện & Số liệu (Main Screen Legend)

### 4.1. Các chỉ số Quan trắc (Parameters)
Màn hình chính hiển thị 4 chỉ số quan trọng từ trạm đo:

| Tên Chỉ số | Đơn vị | Mã hệ thống | Mô tả |
| :--- | :--- | :--- | :--- |
| **Lượng mưa 24h** | mm | `RAIN` | Tổng lượng mưa tích lũy trong 24 giờ qua. |
| **Mực nước** | m | `WATER_LEVEL` | Mực nước hiện tại so với mốc chuẩn. |
| **Độ mặn tầng mặt** | PPT (‰) | `SALT_SURFACE` | Độ mặn đo được ở lớp nước mặt. |
| **Độ mặn tầng đáy** | PPT (‰) | `SALT_BOTTOM` | Độ mặn đo được ở lớp nước đáy. |

### 4.2. Mã màu Cảnh báo (Color Codes)
Màu sắc của chữ số hiển thị mức độ cảnh báo (Alarm Level):

| Cấp độ | Màu sắc hiển thị | Ý nghĩa | Hành động khuyến nghị |
| :--- | :--- | :--- | :--- |
| **Cấp 0** | <span style="color:#29c717">■</span> Xanh lá cây | Bình thường |  |
| **Cấp 1** | <span style="color:#b1ffff">■</span> Xanh dương nhạt | Nguy cơ thấp | Theo dõi thời tiết. |
| **Cấp 2** | <span style="color:#faf58c">■</span> Vàng nhạt | Nguy cơ trung bình | Theo dõi thường xuyên. |
| **Cấp 3** | <span style="color:#ff9b00">■</span> Cam | Nguy cơ cao, cực đoan | Phòng ngừa, chuẩn bị. |
| **Cấp 4** | <span style="color:#ff0a00">■</span> Đỏ | Nguy cơ rất cao, cực đoan | Cảnh giác, làm theo hướng dẫn |
| **Cấp 5** | <span style="color:#a028a0">■</span> Tím | Thảm họa | Tuân thủ chỉ đạo, sẵn sàng ứng phó |
| **N/A** | <span style="color:#C9C9C9">■</span> Xám | Không có dữ liệu |  |

### 4.3. Cơ chế Xử lý & Hiển thị Lỗi (Error Handling Mechanism)

Khi gặp sự cố, màn hình sẽ hiển thị Overlay cảnh báo màu đỏ với các thông tin chi tiết để hỗ trợ Debug.

**Cấu trúc thông báo lỗi trên màn hình:**
1.  **Tiêu đề:** Tên lỗi ngắn gọn, dễ hiểu (VD: LỖI KẾT NỐI MẠNG).
2.  **Mô tả:** Giải thích nguyên nhân và hướng khắc phục cho người vận hành.
3.  **Mã lỗi kỹ thuật (Error Code):** Mã chi tiết bắt đầu bằng `E_...` để báo cho bộ phận kỹ thuật.
4.  **Retry Count:** Số lần ứng dụng đang tự động thử lại kết nối.

**Bảng mã lỗi chi tiết:**

| Mã lỗi (Technical Code) | Tiêu đề hiển thị | Mô tả & Hướng xử lý |
| :--- | :--- | :--- |
| `E_DNS_LOOKUP` | **LỖI KẾT NỐI MẠNG** | Không tìm thấy máy chủ. Vui lòng kiểm tra đường truyền Internet hoặc DNS. |
| `E_CONNECTION_REFUSED` | **KHÔNG THỂ KẾT NỐI** | Máy chủ từ chối kết nối hoặc không phản hồi (Port đóng/Firewall chặn). |
| `E_TIMEOUT` | **QUÁ THỜI GIAN CHỜ** | Phản hồi từ máy chủ quá lâu (>30s). Kiểm tra lại đường truyền mạng. |
| `E_HTTP_504` | **LỖI GATEWAY TIMEOUT** | Máy chủ không phản hồi kịp thời. Hệ thống có thể đang quá tải. |
| `E_HTTP_502` | **LỖI GATEWAY (502)** | Máy chủ gặp sự cố tạm thời (Bad Gateway). Ứng dụng sẽ tự động thử lại liên tục. |
| `E_HTTP_503` | **LỖI DỊCH VỤ (503)** | Máy chủ đang bảo trì hoặc quá tải. Ứng dụng sẽ tự động thử lại. |
| `E_PARSING` | **LỖI DỮ LIỆU** | Dữ liệu nhận được không đúng định dạng JSON (thường do nhận về trang lỗi HTML). |
| `E_API_LOGIC` | **LỖI API** | Máy chủ xử lý yêu cầu thất bại (`success: false`). |
| `STATUS: DISCONNECTED` | **MẤT KẾT NỐI** | Trạng thái khởi tạo hoặc đang nỗ lực kết nối lại. |

### 4.4. Quy tắc Tự động Xử lý Lỗi (Error Handling Rules)

Hệ thống được lập trình để **tự phục hồi** (self-healing) mà không cần can thiệp thủ công:

1.  **Chế độ Thử lại (Retry Policy):**
    *   Ngay khi gặp lỗi, hệ thống sẽ thực hiện thử lại theo chiến lược "Exponential Backoff" (Tăng dần thời gian chờ) để tránh spam server:
        *   Lần 1: Chờ 2 giây
        *   Lần 2: Chờ 4 giây
        *   Lần 3: Chờ 8 giây
        *   Lần 4+: Chờ 10 giây (Cố định).
    *   Quy trình này lặp lại vô tận cho đến khi kết nối thành công.

2.  **Thời gian chờ (Timeout):**
    *   Mỗi request có thời gian chờ tối đa là **10 giây**. Nếu quá 10 giây không có phản hồi, Request bị hủy và tính là 1 lần lỗi (chuyển sang chế độ retry).

3.  **Tần suất cập nhật chuẩn:**
    *   Khi hệ thống hoạt động bình thường, dữ liệu được làm mới mỗi **60 giây**.

4.  **Bảo toàn dữ liệu cũ:**
    *   Khi gặp sự cố kết nối, dữ liệu cũ (nếu có) sẽ **được giữ nguyên** trên màn hình nền.
    *   Overlay cảnh báo lỗi sẽ hiện **đè lên trên** để người dùng nhận biết dữ liệu có thể không còn mới nhất.


## 5. Quy trình Cài đặt Chuẩn & Khắc phục lỗi Device Owner
Nếu gặp lỗi `java.lang.RuntimeException: Can't set package ... as device owner` dù đã reset máy, hãy làm theo đúng từng bước sau:

### Bước 1: Factory Reset (Khôi phục cài đặt gốc)
Đây là bước bắt buộc để xoá sạch các tài khoản cũ. Bất kỳ tài khoản nào (Google, Samsung, sync...) tồn tại cũng sẽ chặn việc set Device Owner.
*   **Cách 1 (ADB - Khuyên dùng):** 
    ```bash
    adb reboot recovery
    ```
    Dùng điều khiển chọn **Wipe data/factory reset** -> **Rebut system now**.
*   **Cách 2 (Trên giao diện TV):** Vào Settings -> Device Preferences -> Reset.

### Bước 2: Setup Wizard (Thiết lập ban đầu) - QUAN TRỌNG NHẤT
*   Khi máy khởi động lại sau khi reset, **TUYỆT ĐỐI KHÔNG ĐĂNG NHẬP GOOGLE ACCOUNT**.
*   Kết nối Wifi -> Chọn "Skip" (Bỏ qua) ở bước Sign in Google.
*   Nếu lỡ đăng nhập -> Phải Reset lại từ đầu.

### Bước 3: Bật ADB & Cài đặt Ứng dụng
1.  Vào Settings -> About -> Click 7 lần "Build Number" để bật Developer Options.
2.  Vào Developer Options -> Bật "USB Debugging".
3.  Kết nối máy tính và cài đặt App:
    ```bash
    adb install -r ./app/build/outputs/apk/release/app-release.apk
    ```

### Bước 4: Set Device Owner
Chạy lệnh sau:
```bash
adb shell dpm set-device-owner com.reecotech.androidtvbox/.receiver.AdminReceiver
```

### Bước 5: Gỡ lỗi nếu vẫn thất bại
Nếu vẫn báo lỗi, hãy kiểm tra lần lượt:

1.  **Kiểm tra tài khoản tồn dư:**
    ```bash
    adb shell pm list users
    adb shell dumpsys account
    ```
    Nếu danh sách account không rỗng, bạn chưa reset sạch. Hãy vào Settings -> Accounts xoá hết hoặc Reset lại máy.

2.  **Kiểm tra tính hợp lệ của App:**
    Thử set làm Admin thường trước:
    ```bash
    adb shell dpm set-active-admin com.reecotech.androidtvbox/.receiver.AdminReceiver
    ```
    Nếu lệnh này thất bại -> App chưa cài đúng hoặc file XML `device_admin_receiver.xml` bị lỗi (ví dụ thiếu `xmlns:android`).

3.  **Kiểm tra User:**
    Đảm bảo chỉ có `UserInfo{0:Owner:...}`. Nếu có Guest/User khác, xoá bằng lệnh:
    ```bash
    adb shell pm remove-user <USER_ID>
    ```

### Giải pháp thay thế: System App (Root/Deploy Script)
Nếu **KHÔNG THỂ** set Device Owner do ROM chặn, bạn có thể chuyển sang phương án **System App**:

1.  Đẩy file APK vào `/system/priv-app/com.reecotech.androidtvbox/`.
2.  Đẩy file `privapp-permissions-com.reecotech.androidtvbox.xml` vào `/system/etc/permissions/` để whitelist quyền `INSTALL_PACKAGES`.
3.  App đã được update code để tự nhận diện quyền `INSTALL_PACKAGES` và kích hoạt Silent Update.
4.  Khi là System App có quyền này, tính năng Silent Update sẽ hoạt động tự động mà không cần Device Owner.

> [!IMPORTANT]
> **Đối với Android 14+ (Lỗi Read-only file system):**
> Android 14 sử dụng **EROFS** và bật sẵn **dm-verity**, ngăn cản việc ghi vào `/system` dù đã `adb root`.
> Bạn CẦN thực hiện các bước sau **MỘT LẦN DUY NHẤT** trước khi chạy script deploy:
> 1. `adb root`
> 2. `adb disable-verity` (Nếu báo lỗi -> Cần Unlock Bootloader trước, xem file `UNLOCK_BOOTLOADER.md`)
> 3. `adb reboot`
> 4. Đợi máy khởi động lại, sau đó chạy `adb root` và `adb remount`.
> 5. Lúc này mới chạy script `./deploy_system_app.sh`.

### Giải pháp cuối cùng: Magisk Module (Cho thiết bị khóa cứng /system)
Nếu firmware của thiết bị sử dụng file system chỉ đọc (EROFS/SquashFS) mà **KHÔNG THỂ** remount R/W dù đã root, hãy dùng Magisk Module.

1.  **Yêu cầu:** Thiết bị đã cài đặt **Magisk**.
2.  **Tạo & Cài Đặt Module:**
    Chạy script sau trên máy tính. Script sẽ tự động build, push và cài đặt vào box (yêu cầu ADB):
    ```bash
    ./build_magisk_module.sh
    ```
    Script sẽ tự động reboot thiết bị sau khi cài xong.

3.  **Kiểm tra:**
    Sau khi reboot, ứng dụng sẽ tự động nằm trong `/system/priv-app` và có đủ quyền Silent Update.

**Cách deploy:**
Chạy script tự động (đã được update để xử lý cả permissions):
```bash
./deploy_system_app.sh
```

**Cách kiểm tra thành công:**
Sau khi reboot, chạy lệnh:
```bash
adb shell dumpsys package com.reecotech.androidtvbox | grep "android.permission.INSTALL_PACKAGES"
```
Nếu thấy `granted=true`, ứng dụng đã sẵn sàng Silent Update.

> [!WARNING]
> **Lưu ý quan trọng:**
> 1. **Kiosk Mode:** Lệnh `set-home-activity` có thể báo lỗi nếu chạy ngay lập tức. Hãy đợi máy reboot xong rồi chạy lại lệnh đó nếu cần.
> 2. **Native Libs (.so):** Script đã tự động push file `.so` nếu có. Nếu app bị crash, hãy kiểm tra lại xem file .so đã nằm trong `/system/priv-app/com.reecotech.androidtvbox/lib/` chưa.
> 3. **Manifest:** Quyền trong `AndroidManifest.xml` phải khớp với file whitelist XML. Script đã tự động tạo file XML khớp, bạn chỉ cần đảm bảo không xóa quyền trong Manifest.


# Tài liệu Tích hợp API: Lấy dữ liệu trạm quan trắc mới nhất

Tài liệu này mô tả chi tiết về endpoint lấy dữ liệu quan trắc mới nhất từ các trạm (Stations) thuộc hệ thống Vinh Long API.

## 1. Thông tin chung

*   **Mô tả:** API trả về danh sách tất cả các trạm cùng với các chỉ số đo đạc (parameters) mới nhất tại thời điểm gọi.
*   **Protocol:** HTTPS
*   **Method:** `GET`
*   **Endpoint URL:**
    ```
    https://vinhlong-api.emisoft.vn/api/v2/stations/all/latest
    ```
*   **Cơ chế & Bảo mật (Security & Mechanism):**
    *   **Thư viện:** Retrofit 2 + OkHttp 3.
    *   **SSL/TLS:** API có chứng chỉ SSL/TLS hợp lệ.
    *   **Authentication:** API này là Public Endpoint, **không yêu cầu** API Key hoặc Token xác thực trong Header.
    *   **Retry Policy:** Timeout kết nối và đọc dữ liệu là 60 giây.

## 2. Request

API này không yêu cầu body hoặc query params phức tạp.

**Headers:**
```http
Content-Type: application/json
Accept: application/json
```

## 3. Response

API trả về dữ liệu định dạng JSON.

### 3.1. Cấu trúc Response

| Field | Data Type | Nullable | Mô tả |
| :--- | :--- | :--- | :--- |
| `success` | Boolean | No | Trạng thái của request (`true` là thành công). |
| `data` | Array | No | Danh sách các trạm quan trắc. |

### 3.2. Cấu trúc Object `Station` (trong mảng `data`)

| Field | Data Type | Nullable | Mô tả |
| :--- | :--- | :--- | :--- |
| `stationId` | String | No | ID định danh duy nhất của trạm (VD: "277"). |
| `stationName` | String | No | Tên hiển thị của trạm (VD: "Test Station 277"). |
| `connectionStatus` | String | No | Trạng thái kết nối (`offline`, `online`). |
| `parameters` | Array | No | Danh sách các chỉ số đo của trạm. |

### 3.3. Cấu trúc Object `Parameter` (trong mảng `parameters`)

Mỗi object trong mảng này đại diện cho một loại cảm biến đo đạc.

| Field | Data Type | Nullable | Mô tả |
| :--- | :--- | :--- | :--- |
| `parameterId` | Number | No | ID loại chỉ số (Xem bảng Mapping bên dưới). |
| `parameterName` | String | No | Mã tên chỉ số (WATER_LEVEL, RAIN, ...). |
| `value` | Number | Yes | Giá trị đo được. Nếu `null` nghĩa là không có dữ liệu hoặc lỗi cảm biến. |
| `timestamp` | String | Yes | Thời gian đo (ISO 8601: `YYYY-MM-DDTHH:mm:ss.sssZ`). |
| `alarmLevel` | Number | Yes | Mức độ cảnh báo (0-5). 0 là bình thường, 5 là thảm họa. |

**Bảng Mapping Parameter ID:**

| ID | Name | Mô tả Tiếng Việt | Đơn vị |
| :--- | :--- | :--- | :--- |
| `2` | `WATER_LEVEL` | Mực nước | m (mét) |
| `3` | `RAIN` | Lượng mưa(24giờ) | mm |
| `9` | `SALT_SURFACE` | Độ mặn tầng mặt | ‰ (ppt) |
| `10` | `SALT_BOTTOM` | Độ mặn tầng đáy | ‰ (ppt) |

## 4. Example Response

```json
{
    "success": true,
    "data": [
        {
            "stationId": "277",
            "stationName": "Test Station 277",
            "connectionStatus": "offline",
            "parameters": [
                {
                    "parameterId": 2,
                    "parameterName": "WATER_LEVEL",
                    "value": 3.5,
                    "timestamp": "2025-12-05T00:49:13.000Z",
                    "alarmLevel": 2
                },
                {
                    "parameterId": 3,
                    "parameterName": "RAIN",
                    "value": 20.5,
                    "timestamp": "2025-12-05T00:49:13.000Z",
                    "alarmLevel": 2
                },
                {
                    "parameterId": 9,
                    "parameterName": "SALT_SURFACE",
                    "value": null,
                    "timestamp": null,
                    "alarmLevel": 0
                },
                {
                    "parameterId": 10,
                    "parameterName": "SALT_BOTTOM",
                    "value": 9.1,
                    "timestamp": "2025-12-05T00:49:13.000Z",
                    "alarmLevel": 5
                }
            ]
        }
    ]
}
```

## 5. Hướng dẫn Tích hợp (Integration Notes)

### 5.1. Xử lý hiển thị (Frontend Logic)

1.  **Hiển thị giá trị Null:**
    *   Trường `value` và `timestamp` có thể là `null` (như `SALT_SURFACE` trong ví dụ trên).
    *   **Logic:** Nếu `value === null`, hiển thị ký tự thay thế `--` thay vì để trống hoặc hiện lỗi.

2.  **Định dạng Thời gian:**
    *   Thời gian trả về dạng UTC (`...Z`).
    *   **Logic:** Cần convert sang múi giờ địa phương (Vietnam GMT+7) trước khi hiển thị.
    *   *Ví dụ:* `2025-12-05T00:49:13.000Z` -> `05/12/2025 07:49:13`.

3.  **Trạng thái Trạm (Connection Status):**
    *   Dựa vào `connectionStatus` để đổi màu sắc UI.
    *   `online`: Giữ nguyên UI hiện tại.
    *   `offline`: Màu xám của tên trạm đo đó và dùng ký tự `--` để thay thế cho value.

4.  **Tìm kiếm Parameter:**
    *   Mảng `parameters` không đảm bảo thứ tự cố định.
    *   **Không nên** truy cập theo index (ví dụ `parameters[0]`).
    *   **Nên** dùng hàm find theo `parameterName` hoặc `parameterId`.
    *   *Ví dụ JS:*
        ```javascript
        const rainInfo = station.parameters.find(p => p.parameterName === 'RAIN');
        const rainValue = rainInfo ? rainInfo.value : '--';
        ```

### 5.2. Type Definitions

```
export interface IParameter {
  parameterId: number;
  parameterName: 'WATER_LEVEL' | 'RAIN' | 'SALT_SURFACE' | 'SALT_BOTTOM';
  value: number | null;
  timestamp: string | null;
  alarmLevel: number;
}

export interface IStation {
  stationId: string;
  stationName: string;
  connectionStatus: 'online' | 'offline' | string;
  parameters: IParameter[];
}

export interface IStationResponse {
  success: boolean;
  data: IStation[];
}
```

## 6. Tính năng Chế độ Ngủ (Sleep Mode) & Tiết kiệm năng lượng

Để bảo vệ màn hình LED và tiết kiệm năng lượng vào ban đêm, ứng dụng được tích hợp chế độ ngủ thông minh (Sleep Mode).

### 6.1. Cấu hình Remote Config
Key mới được hỗ trợ: `sleep_time`
*   **Format:** JSON String `{"fr": "HH:mm", "to": "HH:mm"}`
*   **Ví dụ:** `{"fr": "23:00", "to": "05:00"}` (Ngủ từ 23h đêm đến 5h sáng hôm sau).
*   **Mặc định:** `17:00` - `07:00` (Nếu không cấu hình).

### 6.2. Cơ chế Hoạt động
1.  **Cảnh báo:** 60 giây trước giờ ngủ (ví dụ 22:59), một hộp thoại cảnh báo sẽ xuất hiện. Người dùng có thể chọn "Hủy" để bỏ qua chế độ ngủ cho phiên làm việc này.
2.  **Kích hoạt ngủ:** Khi hết giờ đếm ngược mà không có thao tác hủy:
    *   **Hiển thị:** Màn hình chuyển sang màu đen hoàn toàn (Black Overlay) -> Giúp tắt các bóng LED trên màn hình hiển thị.
    *   **Phần cứng:** Ứng dụng tự động hạ độ sáng hệ thống (Window Brightness) xuống mức thấp nhất (`0.0f`) để giảm tối đa tín hiệu HDMI và tiết kiệm điện cho Box.
    *   **Tần suất gọi API:** Giảm còn 1 tiếng gọi API 1 lần để cập nhật trạng thái thông số trạm.
3.  **Thức dậy:** Khi qua thời gian ngủ (ví dụ 05:01):
    *   Màn hình đen biến mất.
    *   Độ sáng hệ thống được trả về mặc định (`-1.0f`).
    *   Tần suất gọi API được khôi phục.

