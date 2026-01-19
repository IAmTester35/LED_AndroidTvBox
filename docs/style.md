# Tài liệu Thiết kế UI (Style Guide)

Tài liệu này ghi lại các quy chuẩn về giao diện của ứng dụng Android TV Box (LED Screen), bao gồm màu sắc, font chữ và bố cục sắp xếp chi tiết.

## 1. Màu sắc (Colors)

Ứng dụng sử dụng bảng màu hiện đại với tông xanh chủ đạo, kết hợp với các màu cảnh báo tiêu chuẩn.

### 1.1. Màu nền và Thành phần chính
| Tên biến | Mã màu (HEX) | Minh họa | Ghi chú |
| :--- | :--- | :--- | :--- |
| `DarkBlue` | `#003366` | | Màu nền đậm. |
| `HeaderBackground` | `#BCF5FF` | | Màu nền của bảng tiêu đề (Header). |
| `DataCellBackground`| `#0FD7F9FF`| | Màu nền của ô dữ liệu (khi có giá trị). |
| `NullCellBackground`| `#C9C9C9` | | Màu nền khi dữ liệu trống (`--`). |
| `StationNameBG` | `#002C4D` | | Màu nền của cột tên trạm (Online). |
| `StationOfflineBG` | `#636363` | | Màu nền của cột tên trạm (Offline). |

### 1.2. Màu sắc Cảnh báo (Alarm Levels)
Màu chữ của các chỉ số sẽ thay đổi dựa theo mức độ cảnh báo (`alarmLevel` từ 0-5):

| Cấp | Tên biến | Mã màu (HEX) | Mô tả |
| :--- | :--- | :--- | :--- |
| **0** | `Level0Color` | `#29c717` | Bình thường (Xanh lá) |
| **1** | `Level1Color` | `#b1ffff` | Cấp 1 (Xanh dương nhạt) |
| **2** | `Level2Color` | `#faf58c` | Cấp 2 (Vàng nhạt) |
| **3** | `Level3Color` | `#ff9b00` | Cấp 3 (Cam) |
| **4** | `Level4Color` | `#ff0a00` | Cấp 4 (Đỏ) |
| **5** | `Level5Color` | `#a028a0` | Cấp 5 (Tím) |

---

## 2. Font chữ và Kích thước (Typography)

Tất cả kích thước được tối ưu cho màn hình LED và TV Box có độ phân giải lớn.

| Loại nội dung | Kích cỡ (sp) | Font Weight | Ghi chú |
| :--- | :--- | :--- | :--- |
| **Tiêu đề chính** | `22.sp` | Bold | "SỞ NÔNG NGHIỆP VÀ MÔI TRƯỜNG..." |
| **Tiêu đề phụ** | `24.sp` | Bold | "BẢNG TỔNG HỢP THÔNG TIN..." |
| **Giá trị bảng** | `16.sp` | Bold | Giá trị các thông số đo đạc. |
| **Tên Trạm** | `11.sp` | Normal | Hiển thị trong cột đầu tiên của bảng. |
| **Thời gian nhận tin**| `13.sp` | Normal | Hiển thị ở Header (Badge). |
| **Ghi chú/Hỗ trợ** | `13.sp` | Normal | "Hỗ trợ kỹ thuật: ..." |

---

## 3. Bố cục và Sắp xếp (Layout)

Giao diện được chia thành 3 phần chính theo chiều dọc (Header, Body, Footer).

### 3.1. Mô phỏng Giao diện (ASCII Art)

```text
+--------------------------------------------------------------------------------------------------+
| HEADER SECTION (Background Layout Gradient)                                                      |
| +----------------------------------------------------------------------------------------------+ |
| |                                                                                              | |
| | [logo_government.png]            [ TIÊU ĐỀ CHÍNH ]                                         | |
| | (Left)                           [ TIÊU ĐỀ PHỤ   ]                                         | |
| |                                  (Center)                                                    | |
| |                                                                         [ Update Time Badge ]| |
| |                                                                         (Bottom Right)       | |
| +----------------------------------------------------------------------------------------------+ |
+--------------------------------------------------------------------------------------------------+
| BODY SECTION (Main Content)                                                                      |
| +----------------------------------------------------------------------------------------------+ |
| | [ STATION TABLE ]                                                                            | |
| |                                                                                              | |
| | - Cột 1: Tên Trạm                                                                            | |
| | - Cột 2..n: Thông số (Mực nước, Mưa, Pin...)                                                 | |
| |                                                                                              | |
| | ... (Danh sách cuộn tự động nếu nội dung dài)                                                | |
| +----------------------------------------------------------------------------------------------+ |
+--------------------------------------------------------------------------------------------------+
| FOOTER SECTION                                                                                   |
| +----------------------------------------------------------------------------------------------+ |
| | [ Legend Bar ] (Chú giải màu sắc cảnh báo - Chạy chữ marquee nếu cần)                        | |
| +----------------------------------------------------------------------------------------------+ |
| | [ Map Title Bar ] ("Bản đồ vị trí 11 trạm...")                                               | |
| +----------------------------------------------------------------------------------------------+ |
| | [ Map Image ] (map.png - Full Width)                                                         | |
| +----------------------------------------------------------------------------------------------+ |
| |  INFO AREA (2 Columns)                                                                       | |
| | +-----------------------------------------------------------+ +----------------------------+ |
| | | COLUMN 1 (75% Width)                                      | | COLUMN 2 (25% Width)       | |
| | |-----------------------------------------------------------| |----------------------------| |
| | | [Phone Icon] "Hỗ trợ kỹ thuật: 0901 880 386"              | |                            | |
| | |                                                           | |                            | |
| | | [Social QRs Row]                                          | |      [ QR Code ]           | |
| | | (qr_eec, qr_facebook, qr_tiktok, qr_youtube,             | |      (qr_code.png)         | |
| | |  qr_zalo, qr_linkedin)                                    | |      (White bg, large)     | |
| | |                                                           | |                            | |
| | |                                        [ logo_reeco.png ] | |                            | |
| | |                                        (Right Aligned)    | |                            | |
| | +-----------------------------------------------------------+ +----------------------------+ |
+--------------------------------------------------------------------------------------------------+
```

### 3.2. Chi tiết Thành phần

#### A. Header Section
- **Vị trí:** Phía trên cùng.
- **Thành phần:** 
    - `logo_government.png`: Logo Sở Nông nghiệp (trái).
    - **Tiêu đề:** Tên đơn vị và Tên bảng tin (giữa).
    - **Badge Thời gian:** Hiển thị thời gian cập nhật cuối ("Cập nhật lúc: 10:00 01/01/2026") ở góc dưới bên phải Header.
- **Background:** Linear Gradient.

#### B. Body Section
- **Thành phần:** Bảng dữ liệu (`StationTable`).
- **Cấu trúc cột:**
    - Cột Tên Trạm: `25%` chiều ngang.
    - Các cột Thông số: Chia đều phần còn lại (khoảng `7.81%` mỗi cột).

#### C. Footer Section
- **Vị trí:** Dưới cùng.
- **Thành phần:**
    1. **Legend Section:** Thanh chú giải các mức cảnh báo.
    2. **Map Section:** 
        - Title Bar: Màu xanh đậm.
        - Image: `map.png` hiển thị bản đồ vị trí trạm.
    3. **Info Section:**
        - **Cột Trái (75%):**
            - Dòng 1: Số điện thoại hỗ trợ kỹ thuật.
            - Dòng 2: Dãy QR Code mạng xã hội (6 icon: `qr_eec`, `qr_facebook`, `qr_tiktok`, `qr_youtube`, `qr_zalo`, `qr_linkedin`).
            - Dòng 3: Logo đơn vị thi công `logo_reeco.png` (nằm góc phải của cột này).
        - **Cột Phải (25%):**
            - QR Code tải app/thông tin chung (`qr_code.png`).

---

## 4. Các trạng thái đặc biệt

- **Disconnected Overlay:** Khi mất kết nối internet/API lỗi, lớp phủ hiển thị thông báo và đếm ngược thử lại.
- **Sleep Mode:** Màn hình đen hoàn toàn để bảo vệ LED (theo lịch tắt cố định).
- **Loading:** Progress bar khi đang tải dữ liệu ban đầu.
- **Admin Unlock:** Nhấn 5 lần vào Logo Sở (góc trái trên) để mở dialog nhập mật khẩu thoát ứng dụng/vào cài đặt.
