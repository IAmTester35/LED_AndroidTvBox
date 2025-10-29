### **Kế hoạch Sprint 2: Hoàn thiện Luồng Đăng ký và Tăng cường Độ tin cậy**

*   **Thời gian:** 2 Tuần
*   **Mục tiêu Sprint (Sprint Goal):** Hoàn thiện luồng đăng ký thiết bị end-to-end, cho phép Admin duyệt thiết bị từ xa. Đồng thời, triển khai các cơ chế xử lý lỗi kết nối cơ bản và tự động khởi động để tăng cường độ tin cậy và tính tự hành của ứng dụng.

*   **Cam kết Sprint (Definition of Done):**
    *   Mã nguồn đã được viết và được review (code review).
    *   Chức năng hoạt động đúng như mô tả trong các User Story.
    *   Có thể demo được các luồng xử lý lỗi và luồng đăng ký thành công.
    *   Ứng dụng phải tương tác được với một **API mô phỏng (mock API)** để kiểm tra trạng thái đăng ký.
    *   Tính năng tự khởi động cùng hệ điều hành hoạt động chính xác trên thiết bị thật hoặc trình giả lập (sau khi cấp quyền).
    *   Giao diện hiển thị được các thông báo lỗi trực quan khi mất kết nối.

---

### **User Stories cho Sprint 2**

Dưới đây là danh sách các User Story được chọn để thực hiện trong Sprint 2:

**Story 5: Hoàn thiện Luồng Đăng ký**

*   **ID:** `BOX-05`
*   **Tiêu đề:** Tự động chuyển trạng thái sau khi được Admin duyệt
*   **As a** System (Hệ thống)
*   **I want** ứng dụng đang ở màn hình chờ phải định kỳ gọi API để kiểm tra xem Device ID của mình đã được duyệt hay chưa
*   **So that** nó có thể tự động chuyển sang màn hình hiển thị dữ liệu ngay sau khi được Người quản trị cấp phép.
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Khi đang ở màn hình chờ (từ Story `BOX-02`), ứng dụng phải gửi một yêu cầu HTTPS (GET) đến một endpoint của server (ví dụ: `/api/devices/{deviceId}/status`) sau mỗi 10 giây để kiểm tra trạng thái.
    2.  Nếu server trả về trạng thái "pending" hoặc "not found", ứng dụng tiếp tục hiển thị màn hình chờ.
    3.  Nếu server trả về trạng thái "approved" (đã được duyệt), ứng dụng phải ngừng gọi API kiểm tra, thoát khỏi màn hình chờ và bắt đầu thực hiện kết nối WebSocket (như Story `BOX-03`).

**Story 6: Xử lý và Hiển thị Lỗi Mất kết nối**

*   **ID:** `BOX-06`
*   **Tiêu đề:** Hiển thị cảnh báo và dữ liệu cũ khi mất kết nối WebSocket
*   **As a** Factory Observer (Người Quan sát tại Nhà máy)
*   **I want** nhìn thấy một thông báo rõ ràng trên màn hình khi kết nối đến server bị ngắt, và xem lại được dữ liệu hợp lệ gần nhất
*   **So that** tôi biết rằng thông tin hiển thị không phải là thời gian thực và có thể báo cáo sự cố mạng.
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Nếu kết nối WebSocket đang hoạt động bị ngắt, màn hình phải ngay lập tức hiển thị một lớp phủ hoặc thông báo rõ ràng (ví dụ: "Mất kết nối..."). (Đáp ứng REQ-3.1.1)
    2.  Sau khoảng 10 giây, nếu kết nối chưa được khôi phục, thông báo lỗi trên sẽ được thu nhỏ hoặc thay đổi, đồng thời màn hình phải hiển thị lại dữ liệu hợp lệ cuối cùng đã nhận. (Đáp ứng REQ-3.1.2)
    3.  Một dòng thời gian (timestamp) ghi nhận thời điểm của dữ liệu cũ đó phải được hiển thị (ví dụ: "Cập nhật lần cuối: 10:30:15"). (Đáp ứng REQ-3.1.2)
    4.  Trong nền, ứng dụng phải tự động cố gắng kết nối lại sau mỗi 15 giây.
    5.  Khi kết nối được khôi phục, tất cả các cảnh báo phải biến mất và màn hình trở lại hiển thị dữ liệu thời gian thực. (Đáp ứng REQ-3.2)

**Story 7: Tự động khởi động cùng Hệ điều hành**

*   **ID:** `BOX-07`
*   **Tiêu đề:** Tự động khởi chạy ứng dụng khi bật nguồn thiết bị
*   **As a** System (Hệ thống)
*   **I want** ứng dụng tự động được mở lên sau khi thiết bị Android Box hoàn tất quá trình khởi động
*   **So that** nó có thể hoạt động mà không cần sự can thiệp thủ công sau khi bị mất điện hoặc khởi động lại.
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Ứng dụng phải đăng ký một `BroadcastReceiver` để lắng nghe sự kiện `BOOT_COMPLETED` của hệ thống.
    2.  Khi thiết bị được khởi động lại, ứng dụng phải tự động chạy và hiển thị giao diện của nó.
    3.  Ứng dụng phải khai báo quyền `RECEIVE_BOOT_COMPLETED` trong tệp `AndroidManifest.xml`.

**Story 8: Xử lý Dữ liệu JSON không hợp lệ**

*   **ID:** `BOX-08`
*   **Tiêu đề:** Bỏ qua message lỗi và tiếp tục hoạt động
*   **As a** System (Hệ thống)
*   **I want** ứng dụng bỏ qua các gói tin WebSocket có định dạng JSON sai hoặc thiếu trường dữ liệu
*   **So that** một gói tin lỗi không làm treo toàn bộ ứng dụng.
*   **Tiêu chí Chấp nhận (Acceptance Criteria):**
    1.  Khi nhận được một message từ WebSocket, ứng dụng phải đặt việc phân tích JSON trong một khối xử lý lỗi (try-catch).
    2.  Nếu quá trình phân tích thất bại, ứng dụng phải ghi nhận lỗi (log) vào hệ thống.
    3.  Ứng dụng không được treo, và phải tiếp tục lắng nghe các message tiếp theo.
    4.  Màn hình có thể tùy chọn hiển thị một thông báo ngắn "Dữ liệu lỗi" trong vài giây rồi tự ẩn đi. (Đáp ứng REQ-3.3)
