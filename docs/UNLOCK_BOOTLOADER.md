# Hướng Dẫn Unlock Bootloader & Root (Android TV Box)

Để cài App vào hệ thống (`/system/priv-app/`), thiết bị **bắt buộc** phải được Unlock Bootloader và Root (hoặc có quyền ghi vào phân vùng System).

## 1. Kiểm Tra Trạng Thái
Kết nối ADB và chạy lệnh:
```bash
adb shell getprop ro.boot.flash.locked
```
- Nếu trả về `1`: Bootloader đang KHÓA.
- Nếu trả về `0`: Bootloader đã MỞ.

## 2. Các Phương Pháp Unlock

### Cách 1: Sử Dụng Lệnh Fastboot (Cho các dòng Box chuẩn Google/Generic)
1.  Khởi động vào chế độ Fastboot:
    ```bash
    adb reboot bootloader
    ```
2.  Kiểm tra kết nối:
    ```bash
    fastboot devices
    ```
3.  Thực hiện Unlock (Thử một trong các lệnh sau):
    ```bash
    fastboot flashing unlock
    # Hoặc
    fastboot oem unlock
    ```
4.  Trên màn kết nối TV HDMI, nếu hiện bảng hỏi xác nhận -> Dùng phím âm lượng để chọn "Yes/Unlock" và phím nguồn để xác nhận.

### Cách 2: Cho Dòng Box Amlogic (Mibox, TX3, X96...)
Đa số các dòng này không dùng Fastboot chuẩn mà cần công cụ chuyên dụng như **Amlogic USB Burning Tool**.
- Bạn cần tải Firmware dạng `.img` đã được mod sẵn (Root/Unblocked) từ cộng đồng (như XDA, 4PDA).
- Dùng cáp USB 2 đầu đực (Male-to-Male) để nạp Firmware.

### Cách 3: Cho Dòng Box Rockchip
Sử dụng **Rockchip Batch Tool** hoặc **AndroidTool** để nạp file `boot.img` đã patch Magisk.

## 3. Tắt Kiểm Tra Chữ Ký (Disable DM-Verity)
Sau khi Unlock, nếu bạn gặp lỗi "Read-only file system" khi ghi vào `/system`, bạn cần tắt `dm-verity`:

1.  Root thiết bị:
    ```bash
    adb root
    ```
2.  Tắt Verity:
    ```bash
    adb disable-verity
    ```
    *Lưu ý: Lệnh này cần thiết bị hỗ trợ OverlayFS hoặc đã unlock bootloader.*
3.  Khởi động lại:
    ```bash
    adb reboot
    ```
4.  Remount lại để ghi:
    ```bash
    adb root
    adb remount
    ```

---
**Cảnh báo:** Việc Unlock Bootloader sẽ **XÓA SẠCH DỮ LIỆU** trên thiết bị (Factory Reset). Hãy sao lưu trước khi thực hiện.
