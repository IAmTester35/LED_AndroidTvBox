# Hướng Dẫn Sử Dụng Bảng Tổng Hợp Thông Tin Quan Trắc - Android TV Box

Chào mừng bạn đến với tài liệu hướng dẫn sử dụng cho ứng dụng hiển thị thông tin quan trắc môi trường trên Android TV Box (Màn hình LED). Tài liệu này sẽ giúp bạn hiểu rõ các thông số hiển thị, ý nghĩa màu sắc cảnh báo và cách xử lý sự cố cơ bản.

## 1. Giao Diện Màn Hình Chính

Dưới đây là sơ đồ hướng dẫn các thành phần trên màn hình hiển thị:

![Hướng dẫn màn hình](docs/images/dashboard_guide.png)

### Các thành phần chính:

1.  **Tiêu đề & Đơn vị quản lý:** Hiển thị tên Sở Nông Nghiệp và Môi Trường Tỉnh Vĩnh Long.
2.  **Thời gian kiểm tra dữ liệu:**
    *   Hiển thị thời gian (Giờ/Ngày) của **lần cập nhật dữ liệu thành công gần nhất**.
    *   Giúp người xem biết dữ liệu đang xem có phải là mới nhất hay không.
3.  **Tên Trạm (Cột Dọc):** Danh sách 11 trạm quan trắc (Cái Muối, Phú Đức, Tân Thành, v.v.).
4.  **Thông Số (Hàng Ngang):** Gồm 4 thông số chính đo đạc tại mỗi trạm:
    *   **Lượng mưa 24h (mm):** Tổng lượng mưa tích lũy trong 1 ngày đêm.
    *   **Mực nước (m):** Mực nước hiện tại.
    *   **Độ mặn tầng mặt (PPT):** Nồng độ muối trên mặt nước (phần nghìn - ‰).
    *   **Độ mặn tầng đáy (PPT):** Nồng độ muối dưới đáy sông (phần nghìn - ‰).
5.  **Khu vực dữ liệu (Lưới):**
    *   Các ô số thể hiện giá trị đo được.
    *   Màu sắc của chữ số thay đổi theo mức độ cảnh báo (xem mục 2).
    *   Các ô hiển thị số `0.0` hoặc `--` nghĩa là chưa có dữ liệu hoặc giá trị bằng 0.
6.  **Chú giải & QR Code:** Giải thích nhanh ý nghĩa các màu và QR code để truy cập thông tin trên điện thoại.

---

## 2. Ý Nghĩa Màu Sắc Cảnh Báo

Hệ thống sử dụng màu sắc để giúp bạn nhận biết nhanh mức độ nguy hiểm của các chỉ số:

| Cấp độ | Màu sắc hiển thị | Ý nghĩa | Hành động khuyến nghị |
| :--- | :--- | :--- | :--- |
| **Cấp 0** | <span style="color:#29c717">■</span> Xanh lá cây | Bình thường |  |
| **Cấp 1** | <span style="color:#b1ffff">■</span> Xanh dương nhạt | Nguy cơ thấp | Theo dõi thời tiết. |
| **Cấp 2** | <span style="color:#faf58c">■</span> Vàng nhạt | Nguy cơ trung bình | Theo dõi thường xuyên. |
| **Cấp 3** | <span style="color:#ff9b00">■</span> Cam | Nguy cơ cao, cực đoan | Phòng ngừa, chuẩn bị. |
| **Cấp 4** | <span style="color:#ff0a00">■</span> Đỏ | Nguy cơ rất cao, cực đoan | Cảnh giác, làm theo hướng dẫn |
| **Cấp 5** | <span style="color:#a028a0">■</span> Tím | Thảm họa | Tuân thủ chỉ đạo, sẵn sàng ứng phó |
| **N/A** | <span style="color:#C9C9C9">■</span> Xám | Không có dữ liệu |  |

---

## 3. Các Tính Năng Tự Động

### Cập nhật dữ liệu
*   Dữ liệu trên màn hình sẽ tự động làm mới sau mỗi **60 giây**.
*   Bạn không cần thao tác gì thêm, ứng dụng chạy hoàn toàn tự động 24/7.

### Chế độ Ngủ (Tiết kiệm điện)
*   Để bảo vệ tuổi thọ màn hình LED, hệ thống có thể tự động tắt màn hình vào ban đêm (Mặc định thường từ **19:00 đến 05:00** hoặc tùy chỉnh).
*   Trong thời gian này, màn hình sẽ tối đen. Đây là tính năng, **không phải lỗi**.

> **Hướng dẫn bật màn hình khẩn cấp trong giờ ngủ:**
> Nếu bạn cần xem dữ liệu gấp vào ban đêm (lúc màn hình đang tắt):
> 1.  **Khởi động lại:** Rút nguồn điện Android Box và cắm lại (hoặc tắt/mở nguồn).
> 2.  **Hủy chế độ ngủ:** Khi ứng dụng vừa khởi động, một bảng cảnh báo **"Đã đến giờ ngủ..."** sẽ hiện ra đếm ngược.
> 3.  **Thao tác:** Hãy nhanh tay bấm nút **"Không"** trên bảng thông báo này.
> 4.  **Kết quả:** Màn hình sẽ giữ sáng và hoạt động bình thường trong suốt phiên làm việc đó.

---

## 4. Xử Lý Sự Cố (Troubleshooting)

Nếu bạn thấy màn hình hiển thị thông báo lỗi màu đỏ, hãy kiểm tra theo hướng dẫn sau:

### Lỗi kết nối mạng & Mã lỗi Chi tiết
Khi màn hình báo lỗi, bạn sẽ thấy một **Mã lỗi** (bắt đầu bằng `E_...`) ở giữa màn hình. Hãy tra cứu bảng dưới đây để biết nguyên nhân:

| Mã lỗi hiển thị | Tiêu đề | Nguyên nhân & Khắc phục |
| :--- | :--- | :--- |
| `E_DNS_LOOKUP` | **LỖI KẾT NỐI MẠNG** | Mất mạng Internet hoặc DNS lỗi. Hãy kiểm tra dây mạng/Wifi. |
| `E_CONNECTION_REFUSED` | **KHÔNG THỂ KẾT NỐI** | Máy chủ chặn kết nối. Liên hệ kỹ thuật kiểm tra Server. |
| `E_TIMEOUT` | **QUÁ THỜI GIAN CHỜ** | Mạng quá yếu, phản hồi chậm > 30s. Kiểm tra lại đường truyền. |
| `E_HTTP_502` / `503` / `504` | **LỖI GATEWAY/DỊCH VỤ** | Lỗi từ phía Máy chủ (Server quá tải hoặc bảo trì). Ứng dụng sẽ tự thử lại. |
| `E_PARSING` | **LỖI DỮ LIỆU** | Dữ liệu trả về bị lỗi (thường do Wifi đăng nhập hoặc tường lửa chặn). |
| `E_UNKNOWN` | **LỖI KHÔNG XÁC ĐỊNH** | Lỗi lạ chưa xác định. Hãy thử khởi động lại thiết bị. |

*   **Lưu ý:** Bên dưới mã lỗi có dòng chữ **"Số lần thử lại: X"**. Đây là số lần ứng dụng đang tự động cố gắng kết nối lại. Bạn hãy kiên nhẫn đợi trong giây lát.

### Lỗi cập nhật (Update Error)
*   **Dấu hiệu:** Màn hình hiển thị yêu cầu cập nhật nhưng không tự cài đặt được.
*   **Khắc phục:** Vui lòng vào **Cài đặt (Settings) > Ứng dụng > Truy cập đặc biệt > Cài đặt ứng dụng không rõ nguồn gốc** và cấp quyền cho ứng dụng **AndroidTvBox**.

---

## 5. Liên Hệ Hỗ Trợ

Nếu gặp sự cố không thể tự khắc phục, vui lòng liên hệ bộ phận hỗ trợ kỹ thuật:

*   **Hotline Kỹ thuật:** `0901 880 386`
*   Hoặc quét mã QR Code dưới góc phải màn hình để lấy thông tin liên hệ chi tiết.
