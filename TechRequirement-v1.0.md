### **Phân tích Chi tiết Công nghệ Dự kiến**

#### **1. Ngôn ngữ & Môi trường Phát triển**

*   **Tên:** **Kotlin**
    *   **Lý do sử dụng:**
        *   **Ngôn ngữ chính thức:** Được Google đề xuất là ngôn ngữ ưu tiên hàng đầu cho phát triển Android, đảm bảo sự hỗ trợ lâu dài và tích hợp tốt nhất với các công cụ mới.
        *   **An toàn và Ngắn gọn:** Tính năng "null safety" giúp loại bỏ gần như hoàn toàn lỗi `NullPointerException` - một trong những lỗi phổ biến nhất gây treo ứng dụng. Cú pháp hiện đại giúp viết ít mã hơn, dễ đọc và bảo trì hơn.
        *   **Hỗ trợ bất đồng bộ xuất sắc:** Tích hợp sẵn **Kotlin Coroutines**, một framework cực kỳ mạnh mẽ để xử lý các tác vụ nền (như gọi mạng, kết nối lại) một cách đơn giản, hiệu quả mà không làm phức tạp mã nguồn. Điều này là tối quan trọng cho một ứng dụng cần hoạt động 24/7 (Yêu cầu 4.1).
    *   **Chức năng trong dự án:**
        *   Viết toàn bộ logic của ứng dụng, từ giao diện đến xử lý dữ liệu.
        *   Sử dụng **Coroutines** để thực hiện các cuộc gọi mạng (kiểm tra đăng ký, kiểm tra cập nhật) và quản lý các tác vụ định kỳ (kết nối lại WebSocket) mà không làm ảnh hưởng đến luồng giao diện chính.

*   **Tên:** **Android Studio**
    *   **Lý do sử dụng:** Là Môi trường phát triển tích hợp (IDE) chính thức và toàn diện nhất cho Android.
    *   **Chức năng trong dự án:**
        *   Soạn thảo, biên dịch và đóng gói mã nguồn thành tệp APK.
        *   Cung cấp các công cụ gỡ lỗi (debugger) để tìm và sửa lỗi.
        *   Cung cấp công cụ phân tích hiệu năng (Profiler) để kiểm tra mức sử dụng CPU, RAM, đảm bảo ứng dụng không gây quá tải cho Android Box (Yêu cầu 4.1).

#### **2. Kiến trúc Phần mềm**

*   **Tên:** **MVVM (Model-View-ViewModel)**
    *   **Lý do sử dụng:**
        *   **Tách biệt logic:** Phân tách rõ ràng trách nhiệm giữa Giao diện (View), Logic giao diện (ViewModel) và Nguồn dữ liệu (Model). Điều này giúp mã nguồn dễ hiểu, dễ kiểm thử và dễ bảo trì, mở rộng (Yêu cầu 4.5).
        *   **An toàn với vòng đời:** ViewModel được thiết kế để tồn tại qua các thay đổi cấu hình (ví dụ: thay đổi độ phân giải), giúp giữ trạng thái dữ liệu ổn định, rất quan trọng cho ứng dụng cần chạy liên tục.
    *   **Chức năng trong dự án:**
        *   **View (Jetpack Compose):** Chỉ chịu trách nhiệm hiển thị dữ liệu được cung cấp bởi ViewModel.
        *   **ViewModel:** Giữ trạng thái hiện tại của màn hình (dữ liệu đang hiển thị, trạng thái kết nối, thông báo lỗi). Nhận dữ liệu từ Model và xử lý để View có thể hiển thị.
        *   **Model (Repository):** Là lớp trung gian, chịu trách nhiệm lấy dữ liệu từ các nguồn khác nhau (WebSocket, API HTTPS, bộ nhớ cục bộ) và cung cấp cho ViewModel.

#### **3. Giao diện Người dùng (UI)**

*   **Tên:** **Jetpack Compose**
    *   **Lý do sử dụng:**
        *   **UI hiện đại:** Là bộ công cụ xây dựng giao diện người dùng theo phương pháp khai báo (declarative) mới nhất của Google. Giúp xây dựng UI nhanh hơn, với ít mã hơn so với hệ thống XML cũ.
        *   **Phản ứng tự động:** Giao diện được xây dựng bằng Compose sẽ tự động cập nhật khi dữ liệu nền thay đổi. Điều này hoàn hảo cho việc hiển thị dữ liệu thời gian thực từ WebSocket, đảm bảo độ trễ thấp (Yêu cầu 4.1).
    *   **Chức năng trong dự án:**
        *   Xây dựng toàn bộ các thành phần giao diện người dùng.
        *   **Màn hình chờ đăng ký:** Hiển thị Device ID và mã QR (REQ-1.3).
        *   **Màn hình chính:** Hiển thị danh sách các thông số nhận từ server theo layout yêu cầu (REQ-2.4).
        *   **Các lớp phủ thông báo:** Hiển thị các thông báo trạng thái như "Mất kết nối...", "Dữ liệu bị lỗi" một cách linh hoạt (REQ-3.1.1, REQ-3.3).

#### **4. Mạng (Networking)**

*   **Tên:** **Retrofit 2**
    *   **Lý do sử dụng:** Là thư viện client HTTP tiêu chuẩn, mạnh mẽ và được sử dụng rộng rãi nhất trong cộng đồng Android. Giúp việc định nghĩa và thực hiện các cuộc gọi API trở nên cực kỳ đơn giản và an toàn.
    *   **Chức năng trong dự án:**
        *   Thực hiện các yêu cầu HTTPS đến server.
        *   Gửi yêu cầu kiểm tra trạng thái đăng ký của thiết bị (REQ-1.4).
        *   Gửi yêu cầu kiểm tra phiên bản phần mềm mới (REQ-4.1, REQ-4.2).

*   **Tên:** **OkHttp 4**
    *   **Lý do sử dụng:** Là một thư viện nền tảng hiệu suất cao cho các giao tiếp HTTP và WebSocket. Retrofit được xây dựng dựa trên OkHttp. Client WebSocket của nó rất linh hoạt và đáng tin cậy, cho phép kiểm soát sâu vào quá trình kết nối.
    *   **Chức năng trong dự án:**
        *   **Quản lý kết nối WebSocket (WSS):** Thiết lập, duy trì và đóng kết nối thời gian thực đến server (REQ-2.1).
        *   **Lắng nghe sự kiện:** Bắt các sự kiện quan trọng như kết nối thành công, nhận được message mới, và đặc biệt là khi kết nối bị ngắt đột ngột (REQ-3.1).
        *   **Nền tảng cho cơ chế kết nối lại:** Cung cấp các callback cần thiết để kích hoạt logic kết nối lại khi có sự cố.

*   **Tên:** **Moshi**
    *   **Lý do sử dụng:** Thư viện phân tích (parse) JSON hiện đại, được tối ưu cho Kotlin. Nó nhanh, nhẹ và xử lý các trường hợp đặc biệt của Kotlin (như nullability) rất tốt, giúp tránh lỗi khi dữ liệu từ server không như mong đợi.
    *   **Chức năng trong dự án:**
        *   Chuyển đổi chuỗi JSON nhận được từ WebSocket thành các đối tượng dữ liệu (data class) trong Kotlin để ứng dụng có thể sử dụng (REQ-2.3).
        *   Xử lý an toàn các message JSON không hợp lệ để ứng dụng không bị treo (REQ-3.3).

#### **5. Lưu trữ Cục bộ**

*   **Tên:** **Jetpack DataStore (Preferences)**
    *   **Lý do sử dụng:** Là giải pháp lưu trữ key-value đơn giản và hiện đại được Google đề xuất để thay thế cho `SharedPreferences`. Nó hoạt động bất đồng bộ, đảm bảo không làm chậm hoặc treo luồng giao diện chính khi đọc/ghi dữ liệu.
    *   **Chức năng trong dự án:**
        *   Lưu trữ và truy xuất chuỗi Device ID duy nhất của thiết bị một cách bền vững, kể cả khi ứng dụng bị tắt hoặc khởi động lại (REQ-1.1, REQ-1.2).

#### **6. Thành phần Hệ thống Android**

*   **Tên:** **BroadcastReceiver**
    *   **Lý do sử dụng:** Là thành phần tiêu chuẩn của Android để lắng nghe và phản hồi các thông báo (broadcast) trên toàn hệ thống.
    *   **Chức năng trong dự án:**
        *   Lắng nghe sự kiện `BOOT_COMPLETED` (hệ điều hành đã khởi động xong) để tự động khởi chạy ứng dụng, đảm bảo ứng dụng luôn chạy khi Android Box được bật nguồn (Yêu cầu 4.3).

*   **Tên:** **DownloadManager**
    *   **L
