# Sơ đồ và Quy chuẩn của Dự án Android TV Box

Tài liệu này cung cấp một cái nhìn tổng quan về cấu trúc các file mã nguồn chính trong dự án và thiết lập các quy tắc viết code (coding conventions) để đảm bảo sự nhất quán và dễ bảo trì.

---

## 1. Sơ đồ File Mã nguồn chính

Dự án được cấu trúc theo mô hình Clean Architecture, chia thành các lớp (layers) riêng biệt: `data`, `domain`, và `ui`.

### **`app/src/main/java/com/reecotech/androidtvbox/`**

-   **`MainActivity.kt`**: Là điểm vào (entry point) duy nhất của ứng dụng. Nhiệm vụ chính là quan sát `MainUiState` từ `MainViewModel` và hiển thị màn hình tương ứng (Màn hình chờ, Hiển thị dữ liệu, Bị vô hiệu hóa).
-   **`MainApplication.kt`**: Lớp Application của ứng dụng, được dùng để khởi tạo Hilt cho việc chèn phụ thuộc (dependency injection).

### **Lớp `ui` (Giao diện người dùng)**

-   **`ui/viewmodel/MainViewModel.kt`**: Hạt nhân xử lý logic của lớp UI. `ViewModel` điều phối các `UseCase` và `Repository` để quản lý trạng thái của ứng dụng và cung cấp trạng thái đó cho `MainActivity` thông qua `StateFlow`.
-   **`ui/screen/`**: Thư mục chứa các hàm Composable định nghĩa các màn hình của ứng dụng.
    -   `WaitingForActivationScreen.kt`: Màn hình chờ kích hoạt, hiển thị Device ID.
    -   `MainDataScreen.kt`: Màn hình hiển thị dữ liệu từ WebSocket.
    -   `DeviceDisabledScreen.kt`: Màn hình thông báo thiết bị đã bị vô hiệu hóa.
-   **`ui/theme/`**: Các file định nghĩa theme cho Jetpack Compose (màu sắc, font chữ,...).

### **Lớp `domain` (Logic nghiệp vụ)**

-   **`domain/`**: Chứa các `interface` Repository định nghĩa "hợp đồng" cho các hoạt động dữ liệu và các `UseCase` chứa logic nghiệp vụ cụ thể.
    -   `DeviceRepository.kt`, `FirebaseRepository.kt`, `WebSocketRepository.kt`.
-   **`domain/usecase/`**:
    -   `GetDeviceIDUseCase.kt`: Đóng gói logic lấy Device ID đã lưu hoặc tạo mới.
    -   `GetDeviceStatusUseCase.kt`: Cung cấp luồng dữ liệu (Flow) về trạng thái của thiết bị.
    -   `ParseDisplayDataUseCase.kt`: Chịu trách nhiệm phân tích chuỗi JSON từ WebSocket thành dữ liệu có thể hiển thị.

### **Lớp `data` (Dữ liệu)**

-   **`data/repository/`**: Chứa các lớp triển khai (implementation) của các `interface` trong `domain`.
    -   `DeviceRepositoryImpl.kt`: Quản lý Device ID bằng Jetpack DataStore.
    -   `FirebaseRepositoryImpl.kt`: Xử lý toàn bộ giao tiếp với Firebase Realtime Database.
    -   `WebSocketRepositoryImpl.kt`: Quản lý kết nối real-time bằng thư viện Socket.IO để tương thích với backend.

### **`di` (Dependency Injection)**

-   **`di/`**: Chứa các Hilt Module (`AppModule.kt`, `DataModule.kt`) để cung cấp các phụ thuộc cần thiết cho toàn bộ ứng dụng, ví dụ như `DataStore`, `OkHttpClient`, và các `Repository`.

### **Receiver (Broadcast Receivers)**
- **`receiver/BootCompletedReceiver.kt`**: Một `BroadcastReceiver` lắng nghe sự kiện `BOOT_COMPLETED` của hệ điều hành. Khi thiết bị khởi động xong, nó sẽ tự động khởi chạy lại ứng dụng để đảm bảo ứng dụng luôn hoạt động.

### **File Cấu hình (Liệt kê đơn giản)**

-   **`build.gradle.kts`** (cấp app và project): Quản lý các thư viện phụ thuộc và cấu hình build.
-   **`settings.gradle.kts`**: Cấu hình các module trong project.
-   **`gradle/libs.versions.toml`**: Quản lý tập trung phiên bản của các thư viện.

---

## 2. Quy chuẩn Viết Code

### **a. Quy tắc Đặt tên (Naming Conventions)**

1.  **Tên Class**: Sử dụng `PascalCase` (ví dụ: `MainViewModel`, `DeviceRepositoryImpl`).
2.  **Tên Hàm/Phương thức**: Sử dụng `camelCase` (ví dụ: `observeDeviceStatus`). Tên hàm nên thể hiện rõ hành động mà nó thực hiện.
3.  **Tên Biến**: Sử dụng `camelCase`. Đối với hằng số (`const val` hoặc thuộc tính trong `object`), sử dụng `SCREAMING_SNAKE_CASE` (ví dụ: `WEBSOCKET_URL`).
4.  **UseCases**: Tên lớp nên đặt theo hành động, kết thúc bằng `UseCase` (ví dụ: `GetDeviceIDUseCase`).
5.  **Composables**: Sử dụng `PascalCase`. Tên hàm nên mô tả thành phần UI mà nó tạo ra (ví dụ: `WaitingForActivationScreen`).

### **b. Kiến trúc (Clean Architecture)**

1.  **Luồng phụ thuộc**: Các lớp phụ thuộc phải hướng vào trong: `UI` -> `Domain` <- `Data`.
2.  **Lớp UI**: Chỉ chứa logic liên quan đến hiển thị và xử lý sự kiện người dùng. `ViewModel` không nên biết về chi tiết của lớp `Data`.
3.  **Lớp Domain**: Là trái tim của ứng dụng, chứa logic nghiệp vụ cốt lõi và không phụ thuộc vào bất kỳ framework Android nào.
4.  **Lớp Data**: Chịu trách nhiệm hoàn toàn về việc lấy và lưu trữ dữ liệu từ các nguồn (mạng, database, DataStore).

### **c. Coroutines và Flow**

1.  **Phạm vi Coroutine**: Sử dụng `viewModelScope` trong `ViewModel` để khởi chạy các coroutine có vòng đời gắn với `ViewModel`.
2.  **Sử dụng Flow**: `Flow` nên được sử dụng để đại diện cho các luồng dữ liệu có thể thay đổi theo thời gian (ví dụ: trạng thái thiết bị từ Firebase, tin nhắn từ WebSocket).
3.  **Hàm `suspend`**: Các hàm trong Repository thực hiện tác vụ một lần (one-shot) như lưu dữ liệu nên là hàm `suspend`.

### **d. Dependency Injection (Hilt)**

1.  **Constructor Injection**: Ưu tiên truyền các phụ thuộc qua hàm khởi tạo (`@Inject constructor`).
2.  **Hilt Modules**: Sử dụng `@Module` và `@Provides` để cung cấp các thực thể của interface hoặc các lớp từ thư viện bên ngoài (ví dụ: `OkHttpClient`).
3.  **Điểm vào Android**: Các lớp như `Activity`, `Application` phải được chú thích bằng `@AndroidEntryPoint`.
