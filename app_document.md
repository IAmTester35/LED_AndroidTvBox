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
