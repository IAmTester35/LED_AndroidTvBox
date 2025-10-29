### **Tài liệu Đặc tả Yêu cầu Phần mềm (SRS)**
**Dự án: Ứng dụng hiển thị thông số trên Android Box cho Nhà máy**
**Phiên bản: 1.0**
**Ngày: Oct 29 2025**

**1. Giới thiệu**

*   **1.1 Mục đích:** Tài liệu này đặc tả các yêu cầu cho ứng dụng phần mềm chạy trên thiết bị Android Box. Ứng dụng có nhiệm vụ nhận dữ liệu thời gian thực từ một máy chủ (server) qua mạng, xử lý và hiển thị lên màn hình LED được kết nối.
*   **1.2 Phạm vi:**
    *   **Trong phạm vi:** Phát triển, cài đặt và vận hành ứng dụng Android. Các chức năng bao gồm: kết nối server, nhận và hiển thị dữ liệu, xử lý lỗi, tự động cập nhật cấu hình và tự động cập nhật phiên bản ứng dụng.
    *   **Ngoài phạm vi:** Phát triển server WebSocket, cơ sở dữ liệu, Giao diện Quản trị (Admin Panel). Các thành phần này được xem là các hệ thống bên ngoài đã tồn tại và sẵn sàng cung cấp giao diện lập trình ứng dụng (API).
*   **1.3 Tổng quan:** Tài liệu bao gồm mô tả tổng quan về sản phẩm, các yêu cầu chức năng chi tiết, yêu cầu phi chức năng (hiệu suất, bảo mật, độ tin cậy) và các ràng buộc của hệ thống.

**2. Mô tả chung**

*   **2.1 Mô tả sản phẩm:** Ứng dụng là một thành phần trong một hệ thống giám sát lớn hơn. Nó hoạt động như một client, nhận dữ liệu và chỉ thị cấu hình từ một hệ thống server trung tâm và trình chiếu kết quả lên màn hình hiển thị chuyên dụng.
*   **2.2 Chức năng sản phẩm:**
    *   Khởi tạo và đăng ký thiết bị mới vào hệ thống.
    *   Kết nối thời gian thực đến server qua WebSocket.
    *   Nhận, phân tích và hiển thị dữ liệu theo layout được định nghĩa.
    *   Nhận và áp dụng cấu hình hiển thị được quản lý từ xa.
    *   Xử lý các trường hợp lỗi kết nối và lỗi dữ liệu.
    *   Tự động kiểm tra và cập nhật phiên bản phần mềm.
*   **2.3 Đặc điểm người dùng:**
    *   **Người quản trị (Administrator):** Sử dụng Admin Panel (hệ thống bên ngoài) để cấu hình, quản lý và giám sát các thiết bị Android Box.
    *   **Người quan sát (Observer):** Nhân viên trong nhà máy, theo dõi thông tin hiển thị trên màn hình LED.
*   **2.4 Ràng buộc:**
    *   **Phần cứng:** Phải hoạt động trên thiết bị Android Box có kết nối mạng và cổng ra HDMI.
    *   **Nền tảng:** Hệ điều hành Android.
    *   **Giao thức:** Sử dụng WebSocket (WSS) để nhận dữ liệu thời gian thực và HTTPS cho các yêu cầu khác (lấy cấu hình, cập nhật).
*   **2.5 Giả định và Phụ thuộc:**
    *   Hệ thống mạng tại nơi lắp đặt (nhà máy) ổn định.
    *   Server và Admin Panel đã được xây dựng và hoạt động, cung cấp các API cần thiết.
    *   Màn hình LED tương thích và kết nối thành công với Android Box qua HDMI.

**3. Yêu cầu Chức năng**
*(Chi tiết trong tài liệu FRD)*

**4. Yêu cầu Phi chức năng**

*   **4.1 Yêu cầu về Hiệu suất (Performance):**
    *   Độ trễ từ khi nhận được gói tin WebSocket đến khi cập nhật lên màn hình phải dưới 500ms.
    *   Ứng dụng phải hoạt động ổn định 24/7 mà không cần khởi động lại.
    *   Tài nguyên sử dụng (CPU, RAM) phải ở mức hợp lý, không gây quá tải cho thiết bị Android Box.
*   **4.2 Yêu cầu về Bảo mật (Security):**
    *   Kết nối WebSocket đến server phải được mã hóa (sử dụng WSS).
    *   Các yêu cầu HTTP đến server để lấy cấu hình hoặc cập nhật phải sử dụng HTTPS.
    *   Mỗi thiết bị phải có một định danh (Device ID) duy nhất để xác thực với server.
*   **4.3 Yêu cầu về Độ tin cậy (Reliability):**
    *   Ứng dụng phải có cơ chế tự động kết nối lại khi mất kết nối mạng.
    *   Ứng dụng phải có khả năng xử lý các gói tin JSON không hợp lệ mà không bị treo hoặc dừng hoạt động.
    *   Ứng dụng phải tự khởi động lại cùng hệ điều hành khi Android Box được bật nguồn.
*   **4.4 Yêu cầu về Khả năng sử dụng (Usability):**
    *   Giao diện hiển thị phải rõ ràng, dễ đọc từ khoảng cách xa trong môi trường nhà máy.
    *   Sử dụng font chữ lớn, độ tương phản cao.
    *   Thông báo lỗi phải đơn giản và dễ hiểu.
*   **4.5 Yêu cầu về Khả năng bảo trì và Mở rộng (Maintainability & Scalability):**
    *   Quy trình thêm một thiết bị mới vào hệ thống phải được tự động hóa tối đa.
    *   Cơ chế cập nhật phần mềm từ xa (Over-the-Air) phải được tích hợp để dễ dàng nâng cấp hàng loạt thiết bị.

---
### **Tài liệu Yêu cầu Chức năng (FRD)**
**Dự án: Ứng dụng hiển thị thông số trên Android Box cho Nhà máy**
**Phiên bản: 1.0**

**Module 1: Khởi tạo và Đăng ký thiết bị**

*   **REQ-1.1:** Khi ứng dụng chạy lần đầu tiên, nó phải kiểm tra xem đã có Device ID được lưu cục bộ hay chưa.
*   **REQ-1.2:** Nếu chưa có Device ID, ứng dụng phải tự động tạo một chuỗi định danh duy nhất (UUID) và lưu trữ nó trên thiết bị.
*   **REQ-1.3:** Ứng dụng phải hiển thị Device ID này lên màn hình, kèm theo một mã QR chứa thông tin Device ID, để Người quản trị có thể sử dụng Admin Panel đăng ký thiết bị.
*   **REQ-1.4:** Trong trạng thái chờ đăng ký, ứng dụng phải định kỳ (ví dụ: mỗi 10 giây) gửi yêu cầu tới server để kiểm tra xem Device ID của mình đã được duyệt hay chưa.
*   **REQ-1.5:** Sau khi được server xác nhận đã đăng ký, ứng dụng sẽ chuyển sang Module 2.

**Module 2: Đồng bộ hóa Cấu hình và Hiển thị Dữ liệu**

*   **REQ-2.1:** Sau khi đăng ký thành công, ứng dụng phải thực hiện một kết nối WebSocket đến địa chỉ server đã được định cấu hình.
*   **REQ-2.2:** Sau khi kết nối WebSocket thành công, ứng dụng phải gửi một message định danh chứa Device ID của mình.
*   **REQ-2.3:** Ứng dụng phải lắng nghe các message JSON được gửi từ server qua WebSocket. Cấu trúc JSON mẫu:
    ```json
    [
      {
        "id": "TEMP01",
        "title": "Nhiệt độ Lò nung",
        "value": "98.5",
        "unit": "°C"
      },
      {
        "id": "HUMID01",
        "title": "Độ ẩm Kho",
        "value": "65",
        "unit": "%"
      }
    ]
    ```
*   **REQ-2.4:** Ứng dụng phải phân tích (parse) mảng JSON và hiển thị từng đối tượng lên màn hình theo layout đã thống nhất: `Title & Icon: Value (unit)`.
*   **REQ-2.5:** Ứng dụng phải có khả năng nhận và áp dụng các thay đổi về cấu hình (ví dụ: thay đổi title, thêm/bớt thông số) được đẩy từ server mà không cần khởi động lại.

**Module 3: Xử lý Lỗi và Phục hồi**

*   **REQ-3.1:** Nếu kết nối WebSocket bị ngắt, ứng dụng phải:
    *   **REQ-3.1.1:** Ngay lập tức hiển thị một thông báo rõ ràng trên màn hình (ví dụ: "Mất kết nối...").
    *   **REQ-3.1.2:** Sau 10 giây, nếu kết nối chưa được khôi phục, ẩn thông báo lỗi và hiển thị lại dữ liệu hợp lệ cuối cùng nhận được. Kèm theo đó là mốc thời gian của dữ liệu đó (ví dụ: "Cập nhật lần cuối: 10:30:15") và/hoặc một biểu tượng cảnh báo.
    *   **REQ-3.1.3:** Cố gắng kết nối lại với server trong nền sau mỗi 15 giây.
*   **REQ-3.2:** Khi kết nối được khôi phục, ứng dụng phải tự động ẩn các cảnh báo và quay lại hiển thị dữ liệu thời gian thực.
*   **REQ-3.3:** Nếu nhận được một message có định dạng JSON không hợp lệ hoặc dữ liệu không đầy đủ, ứng dụng phải bỏ qua message đó, ghi nhận lỗi (log) và tiếp tục chờ message tiếp theo mà không bị treo. Màn hình sẽ hiển thị thông báo "Dữ liệu bị lỗi" trong 5 giây.
*   **REQ-3.4:** Đối với các lỗi không xác định khác, màn hình hiển thị thông báo "Lỗi không xác định".

**Module 4: Cập nhật Phần mềm Từ xa (OTA - Over-the-Air)**

*   **REQ-4.1:** Ứng dụng phải định kỳ (ví dụ: mỗi 24 giờ) gửi một yêu cầu đến server để kiểm tra xem có phiên bản phần mềm mới hay không.
*   **REQ-4.2:** Yêu cầu này phải bao gồm phiên bản hiện tại của ứng dụng và Device ID.
*   **REQ-4.3:** Nếu server phản hồi có phiên bản mới, ứng dụng sẽ tự động tải tệp cài đặt (APK) về máy.
*   **REQ-4.4:** Sau khi tải về thành công, ứng dụng sẽ tự động chạy quy trình cài đặt để nâng cấp lên phiên bản mới.
