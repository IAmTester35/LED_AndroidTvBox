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

## 3. Cơ chế Cập nhật (Silent Update)
Ứng dụng tự động kiểm tra cập nhật từ **Firebase Remote Config**:
- **Cache time:** 3 phút.
- **Cơ chế:** So sánh `latest_version_code` server với version hiện tại.
- **Hành vi:** Nếu có bản mới -> Tự động tải về -> Tự động cài đặt (yêu cầu quyền Device Owner) -> Tự khởi động lại.

**Các trường Remote Config:**
- `latest_version_code`: (Number) Version code mới nhất.
- `apk_download_url`: (String) Link tải file APK.

## 4. Deployment
- Chạy script để deploy app vào system partition (nếu cần):
  ```bash
  ./deploy_system_app.sh
  ```

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
