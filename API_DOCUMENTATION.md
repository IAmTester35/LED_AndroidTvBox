# Tài liệu Tích hợp API: Lấy dữ liệu trạm quan trắc mới nhất

Tài liệu này mô tả chi tiết về endpoint lấy dữ liệu quan trắc mới nhất từ các trạm (Stations) thuộc hệ thống Vinh Long API.

## 1. Thông tin chung

*   **Mô tả:** API trả về danh sách tất cả các trạm cùng với các chỉ số đo đạc (parameters) mới nhất tại thời điểm gọi.
*   **Protocol:** HTTPS
*   **Method:** `GET`
*   **Endpoint URL:**
    ```
    https://vinhlong-api.emisoft.vn/api/v2/stations/all/latest
    ```

## 2. Request

API này không yêu cầu body hoặc query params phức tạp.

**Headers:**
```http
Content-Type: application/json
Accept: application/json
```

## 3. Response

API trả về dữ liệu định dạng JSON.

### 3.1. Cấu trúc Response

| Field | Data Type | Nullable | Mô tả |
| :--- | :--- | :--- | :--- |
| `success` | Boolean | No | Trạng thái của request (`true` là thành công). |
| `data` | Array | No | Danh sách các trạm quan trắc. |

### 3.2. Cấu trúc Object `Station` (trong mảng `data`)

| Field | Data Type | Nullable | Mô tả |
| :--- | :--- | :--- | :--- |
| `stationId` | String | No | ID định danh duy nhất của trạm (VD: "277"). |
| `stationName` | String | No | Tên hiển thị của trạm (VD: "Test Station 277"). |
| `connectionStatus` | String | No | Trạng thái kết nối (`offline`, `online`). |
| `parameters` | Array | No | Danh sách các chỉ số đo của trạm. |

### 3.3. Cấu trúc Object `Parameter` (trong mảng `parameters`)

Mỗi object trong mảng này đại diện cho một loại cảm biến đo đạc.

| Field | Data Type | Nullable | Mô tả |
| :--- | :--- | :--- | :--- |
| `parameterId` | Number | No | ID loại chỉ số (Xem bảng Mapping bên dưới). |
| `parameterName` | String | No | Mã tên chỉ số (WATER_LEVEL, RAIN, ...). |
| `value` | Number | Yes | Giá trị đo được. Nếu `null` nghĩa là không có dữ liệu hoặc lỗi cảm biến. |
| `timestamp` | String | Yes | Thời gian đo (ISO 8601: `YYYY-MM-DDTHH:mm:ss.sssZ`). |
| `alarmLevel` | Number | Yes | Mức độ cảnh báo (0-5). 0 là bình thường, 5 là thảm họa. |

**Bảng Mapping Parameter ID:**

| ID | Name | Mô tả Tiếng Việt | Đơn vị |
| :--- | :--- | :--- | :--- |
| `2` | `WATER_LEVEL` | Mực nước | m (mét) |
| `3` | `RAIN` | Lượng mưa(24giờ) | mm |
| `9` | `SALT_SURFACE` | Độ mặn tầng mặt | ‰ (ppt) |
| `10` | `SALT_BOTTOM` | Độ mặn tầng đáy | ‰ (ppt) |

## 4. Example Response

```json
{
    "success": true,
    "data": [
        {
            "stationId": "277",
            "stationName": "Test Station 277",
            "connectionStatus": "offline",
            "parameters": [
                {
                    "parameterId": 2,
                    "parameterName": "WATER_LEVEL",
                    "value": 3.5,
                    "timestamp": "2025-12-05T00:49:13.000Z",
                    "alarmLevel": 2
                },
                {
                    "parameterId": 3,
                    "parameterName": "RAIN",
                    "value": 20.5,
                    "timestamp": "2025-12-05T00:49:13.000Z",
                    "alarmLevel": 2
                },
                {
                    "parameterId": 9,
                    "parameterName": "SALT_SURFACE",
                    "value": null,
                    "timestamp": null,
                    "alarmLevel": 0
                },
                {
                    "parameterId": 10,
                    "parameterName": "SALT_BOTTOM",
                    "value": 9.1,
                    "timestamp": "2025-12-05T00:49:13.000Z",
                    "alarmLevel": 5
                }
            ]
        }
    ]
}
```

## 5. Hướng dẫn Tích hợp (Integration Notes)

### 5.1. Xử lý hiển thị (Frontend Logic)

1.  **Hiển thị giá trị Null:**
    *   Trường `value` và `timestamp` có thể là `null` (như `SALT_SURFACE` trong ví dụ trên).
    *   **Logic:** Nếu `value === null`, hiển thị ký tự thay thế `--` thay vì để trống hoặc hiện lỗi.

2.  **Định dạng Thời gian:**
    *   Thời gian trả về dạng UTC (`...Z`).
    *   **Logic:** Cần convert sang múi giờ địa phương (Vietnam GMT+7) trước khi hiển thị.
    *   *Ví dụ:* `2025-12-05T00:49:13.000Z` -> `05/12/2025 07:49:13`.

3.  **Trạng thái Trạm (Connection Status):**
    *   Dựa vào `connectionStatus` để đổi màu sắc UI.
    *   `online`: Giữ nguyên UI hiện tại.
    *   `offline`: Màu xám của tên trạm đo đó và dùng ký tự `--` để thay thế cho value.

4.  **Tìm kiếm Parameter:**
    *   Mảng `parameters` không đảm bảo thứ tự cố định.
    *   **Không nên** truy cập theo index (ví dụ `parameters[0]`).
    *   **Nên** dùng hàm find theo `parameterName` hoặc `parameterId`.
    *   *Ví dụ JS:*
        ```javascript
        const rainInfo = station.parameters.find(p => p.parameterName === 'RAIN');
        const rainValue = rainInfo ? rainInfo.value : '--';
        ```

### 5.2. Type Definitions (TypeScript)

Dưới đây là Interface gợi ý nếu sử dụng TypeScript:

```typescript
export interface IParameter {
  parameterId: number;
  parameterName: 'WATER_LEVEL' | 'RAIN' | 'SALT_SURFACE' | 'SALT_BOTTOM';
  value: number | null;
  timestamp: string | null;
  alarmLevel: number;
}

export interface IStation {
  stationId: string;
  stationName: string;
  connectionStatus: 'online' | 'offline' | string;
  parameters: IParameter[];
}

export interface IStationResponse {
  success: boolean;
  data: IStation[];
}
```
