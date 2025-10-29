Mục tiêu của Sprint 1 là xây dựng "bộ khung" (walking skeleton) cho ứng dụng – một phiên bản tối giản nhưng hoạt động được từ đầu đến cuối (end-to-end). Chúng ta sẽ tập trung vào việc thiết bị có thể tự khởi tạo, kết nối và hiển thị dữ liệu mẫu, tạo nền tảng vững chắc cho các tính năng phức tạp hơn trong các Sprint sau.

---

### **Kế hoạch Sprint 1: Xây dựng Khung xương và Luồng hoạt động Cơ bản**

*   **Thời gian:** 2 Tuần
*   **Mục tiêu Sprint (Sprint Goal):** Hoàn thành luồng hoạt động cơ bản (end-to-end) cho một thiết bị. Mục tiêu là ứng dụng có thể tự khởi tạo, đăng ký định danh, kết nối tới WebSocket server và hiển thị được dữ liệu mẫu. Sprint này tập trung vào việc xây dựng "khung xương" của ứng dụng, chưa bao gồm logic cấu hình động từ Admin Panel hay xử lý lỗi phức tạp.

*   **Cam kết Sprint (Definition of Done):**
    *   Mã nguồn đã được viết và được review (code review).
    *   Chức năng hoạt động đúng như mô tả trong các User Story.
    *   Có thể demo được trên thiết bị Android Box thật hoặc trình giả lập.
    *   Ứng dụng có thể kết nối thành công tới một WebSocket server **mô phỏng (mock server)** để nhận dữ liệu.
    *   Ứng dụng không bị treo (crash) trong quá trình demo luồng hoạt động chính.

---

### **User Stories cho Sprint 1**

Dưới đây là danh sách các User Story được chọn để thực hiện trong Sprint 1:

**Story 1: Khởi tạo Định danh Thiết bị**

*   **ID:** `BOX-01`
*   **Tiêu đề:** Tự động tạo và lưu trữ Device ID duy nhất
*   **As a** System (Hệ thống)
*   **I want** ứng dụng, trong lần chạy đầu tiên, tự động tạo và lưu trữ một định danh thiết bị (Device ID) duy nhất
*   **So that** thiết bị này có thể được nhận diện và quản lý một cách độc lập trong toàn bộ hệ thống.
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Khi ứng dụng được khởi chạy lần đầu tiên, một chuỗi định danh duy nhất (ví dụ: UUID) phải được tạo ra.
    2.  Device ID này phải được lưu trữ bền vững trên bộ nhớ cục bộ của thiết bị (ví dụ: SharedPreferences).
    3.  Trong những lần khởi chạy tiếp theo, ứng dụng phải đọc lại Device ID đã được lưu thay vì tạo một cái mới.

**Story 2: Hiển thị Màn hình Chờ Đăng ký**

*   **ID:** `BOX-02`
*   **Tiêu đề:** Hiển thị Device ID và nút yêu cầu kích hoạt
*   **As a** System Administrator (Người Quản trị Hệ thống)
*   **I want** nhìn thấy Device ID và một nút bấm trên màn hình khi thiết bị chưa được đăng ký
*   **So that** tôi có thể cấp phép hoạt động cho thiết bị và người dùng có thể chủ động yêu cầu kiểm tra kích hoạt.
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Nếu thiết bị chưa được đăng ký, màn hình phải hiển thị rõ ràng Device ID đã được tạo ở story `BOX-01`.
    2.  Một nút bấm với tiêu đề "Yêu cầu Kích hoạt" phải được hiển thị.
    3.  Khi nhấn nút, ứng dụng phải ngay lập tức gửi yêu cầu đến server để kiểm tra trạng thái kích hoạt.
    4.  Màn hình phải có một dòng trạng thái rõ ràng, ví dụ: "Đang chờ kích hoạt từ hệ thống..."

**Story 3: Thiết lập Kết nối WebSocket Cơ bản**

*   **ID:** `BOX-03`
*   **Tiêu đề:** Kết nối đến WebSocket server và gửi thông tin định danh
*   **As a** System (Hệ thống)
*   **I want** ứng dụng tự động thiết lập kết nối đến WebSocket server sau khi khởi động
*   **So that** nó có thể bắt đầu nhận dữ liệu thời gian thực.
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Ứng dụng sử dụng một địa chỉ WebSocket URL được cấu hình sẵn (hard-coded) để kết nối.
    2.  Ngay sau khi kết nối thành công, ứng dụng phải gửi một gói tin (message) đầu tiên chứa Device ID của nó để định danh.
    3.  Nếu kết nối ban đầu thất bại, ứng dụng phải thử kết nối lại một cách đơn giản (ví dụ: thử lại sau mỗi 10 giây).

**Story 4: Nhận và Hiển thị Dữ liệu Mẫu**

*   **ID:** `BOX-04`
*   **Tiêu đề:** Hiển thị dữ liệu nhận được từ WebSocket
*   **As a** Factory Observer (Người Quan sát tại Nhà máy)
*   **I want** xem các thông số được gửi từ server hiển thị trên màn hình
*   **So that** tôi có thể bắt đầu theo dõi được hoạt động.
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Ứng dụng có thể phân tích (parse) thành công một cấu trúc JSON mẫu được gửi từ server (cấu trúc đã định nghĩa trong FRD).
    2.  Tất cả các mục trong mảng JSON phải được hiển thị lên màn hình.
    3.  Layout hiển thị trong sprint này có thể ở dạng đơn giản, chưa cần có icon (ví dụ: `Title: Value Unit`).
    4.  Màn hình phải tự động cập nhật khi có gói tin dữ liệu mới được gửi đến.
