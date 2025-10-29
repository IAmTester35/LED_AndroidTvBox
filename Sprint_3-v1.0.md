### **Kế hoạch Sprint 3: Cấu hình Động và Nền tảng Cập nhật Từ xa (OTA)**

*   **Thời gian:** 2 Tuần
*   **Mục tiêu Sprint (Sprint Goal):** Nâng cấp ứng dụng từ một client hiển thị tĩnh thành một client được quản lý động hoàn toàn từ server. Cho phép Admin thay đổi nội dung, bố cục hiển thị mà không cần khởi động lại ứng dụng, đồng thời xây dựng cơ chế kiểm tra phiên bản mới để chuẩn bị cho tính năng cập nhật phần mềm (OTA).

*   **Cam kết Sprint (Definition of Done):**
    *   Mã nguồn đã được viết và được review (code review).
    *   Chức năng hoạt động đúng như mô tả trong các User Story.
    *   Có thể demo luồng nhận cấu hình động từ một WebSocket server mô phỏng.
    *   Giao diện người dùng được nâng cấp, dễ đọc hơn và có biểu tượng (icon).
    *   Luồng kiểm tra phiên bản mới hoạt động chính xác khi gọi đến API mô phỏng.
    *   Toàn bộ giao tiếp mạng (HTTPS, WSS) được mã hóa.

---

### **User Stories cho Sprint 3**

Dưới đây là danh sách các User Story được chọn để thực hiện trong Sprint 3:

**Story 9: Áp dụng Cấu hình Hiển thị được đẩy từ Server**

*   **ID:** `BOX-09`
*   **Tiêu đề:** Nhận và áp dụng cấu hình hiển thị động từ WebSocket
*   **As an** Administrator (Người quản trị)
*   **I want** có thể đẩy một cấu trúc cấu hình (layout, danh sách thông số) xuống thiết bị thông qua WebSocket
*   **So that** màn hình hiển thị có thể được thay đổi (thêm/bớt/sửa thông số) ngay lập tức mà không cần cài đặt lại hay khởi động lại ứng dụng. (Đáp ứng REQ-2.5)
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Ứng dụng có thể nhận diện và xử lý một loại message mới từ WebSocket, ví dụ: `{"type": "config", "payload": [...]}`.
    2.  `payload` chứa một mảng định nghĩa các mục cần hiển thị, bao gồm `id`, `title`, `unit`, và một `iconId`.
    3.  Khi nhận được message `config`, ứng dụng phải cập nhật lại cấu trúc hiển thị của nó.
    4.  Các message dữ liệu (`{"type": "data", ...}`) sau đó phải được hiển thị dựa trên cấu hình mới nhất này.
    5.  Demo được kịch bản: màn hình đang hiển thị 2 thông số, server đẩy cấu hình mới chỉ có 1 thông số, màn hình tự động cập nhật chỉ còn 1.

**Story 10: Nâng cấp Giao diện Hiển thị với Icon**

*   **ID:** `BOX-10`
*   **Tiêu đề:** Hiển thị thông số theo layout hoàn chỉnh có Icon
*   **As a** Factory Observer (Người Quan sát tại Nhà máy)
*   **I want** nhìn thấy một biểu tượng (icon) trực quan bên cạnh mỗi thông số
*   **So that** tôi có thể nhận diện loại thông tin nhanh hơn từ khoảng cách xa. (Đáp ứng REQ-4.4)
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Bố cục hiển thị cho mỗi mục phải tuân theo định dạng: `Icon - Title: Value (Unit)`.
    2.  Ứng dụng phải có một bộ icon được đóng gói sẵn (ví dụ: icon nhiệt độ, icon độ ẩm, icon áp suất).
    3.  `iconId` nhận được từ message cấu hình (trong Story `BOX-09`) sẽ được dùng để ánh xạ và hiển thị icon tương ứng.
    4.  Font chữ, kích thước, và độ tương phản phải được tối ưu để dễ đọc trong môi trường nhà máy.

**Story 11: Định kỳ Kiểm tra Phiên bản Phần mềm Mới**

*   **ID:** `BOX-11`
*   **Tiêu đề:** Gửi yêu cầu kiểm tra phiên bản mới đến server
*   **As a** System (Hệ thống)
*   **I want** ứng dụng định kỳ (ví dụ: mỗi 24 giờ) tự động hỏi server xem có phiên bản phần mềm nào mới hơn không
*   **So that** nó có thể chuẩn bị cho quá trình tự động cập nhật trong tương lai. (Đáp ứng REQ-4.1)
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Một tác vụ nền phải được lên lịch để chạy sau mỗi 24 giờ.
    2.  Tác vụ này sẽ gửi một yêu cầu HTTPS (GET) đến endpoint của server (ví dụ: `/api/ota/check`).
    3.  Yêu cầu phải chứa phiên bản hiện tại của ứng dụng (ví dụ: `versionCode` hoặc `versionName`) và Device ID. (Đáp ứng REQ-4.2)
    4.  Ứng dụng phải ghi nhận (log) phản hồi từ server. *Lưu ý: Sprint này chưa yêu cầu tải hay cài đặt file, chỉ dừng ở bước kiểm tra.*

**Story 12: Bảo mật Kênh Giao tiếp**

*   **ID:** `BOX-12`
*   **Tiêu đề:** Mã hóa toàn bộ giao tiếp với server
*   **As an** Administrator (Người quản trị)
*   **I want** mọi dữ liệu trao đổi giữa ứng dụng và server phải được mã hóa
*   **So that** hệ thống được bảo mật, chống lại các nguy cơ nghe lén hoặc giả mạo dữ liệu. (Đáp ứng REQ-4.2)
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Địa chỉ kết nối WebSocket phải được cấu hình để sử dụng giao thức `WSS` (WebSocket Secure).
    2.  Tất cả các yêu cầu API (kiểm tra đăng ký, kiểm tra cập nhật) phải sử dụng giao thức `HTTPS`.
    3.  Ứng dụng phải kết nối thành công đến các endpoint WSS/HTTPS trên server mô phỏng.
