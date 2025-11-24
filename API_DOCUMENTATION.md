# API Documentation - LED Android TV Box WebSocket Server

**Dự án**: Hệ thống hiển thị dữ liệu quan trắc khí tượng thủy văn tỉnh Vĩnh Long  
**Phiên bản**: 1.0  
**Ngày tạo**: 24/11/2025  
**Backend URL**: `https://bd.vncscc.com:3003`

---

## Tổng quan

Ứng dụng LED Android TV Box sử dụng **Socket.IO** để nhận dữ liệu real-time từ server. Client kết nối qua WebSocket và nhận dữ liệu cập nhật liên tục về 11 trạm quan trắc.

---

## 1. WebSocket Connection

### Connection URL
```
https://bd.vncscc.com:3003
```

### Protocol
- **Library**: Socket.IO (NOT raw WebSocket)
- **Transport**: WebSocket with fallback to long-polling
- **Reconnection**: Tự động reconnect khi mất kết nối

### Connection Query Parameters

Client gửi `deviceId` khi kết nối:

```
?deviceId=<DEVICE_ID>
```

**Ví dụ**:
```
https://bd.vncscc.com:3003?deviceId=android_tv_001
```

---

## 2. Socket.IO Events

### 2.1. Client → Server Events

#### `connect`
Tự động trigger khi connection thành công.

**Client không cần emit event này**, Socket.IO library tự động handle.

---

### 2.2. Server → Client Events

#### Event: `message`

Server gửi dữ liệu dạng JSON array chứa thông tin của tất cả các trạm.

**Event name**: `"message"`

**Payload**: JSON string

**Data Structure**:

```typescript
Array<{
  id: string;      // Format: "station{N}_{param_type}"
  title: string;   // Tên hiển thị của parameter
  value: string;   // Giá trị dữ liệu
  unit: string;    // Đơn vị (mm, m, PPT, v.v.)
}>
```

---

## 3. Data Format Specification

### 3.1. Station ID Format

Mỗi trạm được đánh số từ 1 đến 11:

| Station Number | Tên trạm                | ID Prefix  |
|---------------|-------------------------|------------|
| 1             | Cái Mười                | station1   |
| 2             | Phú Đức                 | station2   |
| 3             | Tân Thành               | station3   |
| 4             | Thị trấn Trà Ôn         | station4   |
| 5             | Tiểu Thiện              | station5   |
| 6             | Ngũ tự sống Trà Ngoà    | station6   |
| 7             | Nhà Đài                 | station7   |
| 8             | Năng Âm                 | station8   |
| 9             | Quới Ân                 | station9   |
| 10            | Cái Ngang               | station10  |
| 11            | Hòa Hiệp                | station11  |

### 3.2. Parameter Types

Mỗi trạm có 4 parameters:

| Parameter Key        | Alternative Keys           | Mô tả                    | Đơn vị | ID Suffix         |
|---------------------|---------------------------|--------------------------|--------|------------------|
| `rainfall`          | `rain`                    | Lượng mưa 24h           | mm     | `_rainfall`      |
| `water`             | `level`                   | Mực nước                | m      | `_water`         |
| `surface`           | `salinity_surface`        | Độ mặn tầng mặt         | PPT    | `_surface`       |
| `bottom`            | `salinity_bottom`         | Độ mặn tầng đáy         | PPT    | `_bottom`        |

### 3.3. Complete ID Examples

```
station1_rainfall     → Lượng mưa 24h tại Cái Mười
station1_water        → Mực nước tại Cái Mười
station1_surface      → Độ mặn tầng mặt tại Cái Mười
station1_bottom       → Độ mặn tầng đáy tại Cái Mười

station2_rainfall     → Lượng mưa 24h tại Phú Đức
station2_water        → Mực nước tại Phú Đức
...

station11_rainfall    → Lượng mưa 24h tại Hòa Hiệp
station11_water       → Mực nước tại Hòa Hiệp
station11_surface     → Độ mặn tầng mặt tại Hòa Hiệp
station11_bottom      → Độ mặn tầng đáy tại Hòa Hiệp
```

---

## 4. Example Payload

### Minimal Example (2 trạm, 2 parameters mỗi trạm)

```json
[
  {
    "id": "station1_rainfall",
    "title": "Lượng mưa 24h - Cái Mười",
    "value": "0.8",
    "unit": "mm"
  },
  {
    "id": "station1_water",
    "title": "Mực nước - Cái Mười",
    "value": "1.0",
    "unit": "m"
  },
  {
    "id": "station2_rainfall",
    "title": "Lượng mưa 24h - Phú Đức",
    "value": "0.8",
    "unit": "mm"
  },
  {
    "id": "station2_water",
    "title": "Mực nước - Phú Đức",
    "value": "2.0",
    "unit": "m"
  }
]
```

### Complete Example (11 trạm, tất cả parameters)

```json
[
  // Station 1 - Cái Mười
  {
    "id": "station1_rainfall",
    "title": "Lượng mưa 24h - Cái Mười",
    "value": "0.8",
    "unit": "mm"
  },
  {
    "id": "station1_water",
    "title": "Mực nước - Cái Mười",
    "value": "1.0",
    "unit": "m"
  },
  {
    "id": "station1_surface",
    "title": "Độ mặn tầng mặt - Cái Mười",
    "value": "0.1",
    "unit": "PPT"
  },
  {
    "id": "station1_bottom",
    "title": "Độ mặn tầng đáy - Cái Mười",
    "value": "0.8",
    "unit": "PPT"
  },

  // Station 2 - Phú Đức
  {
    "id": "station2_rainfall",
    "title": "Lượng mưa 24h - Phú Đức",
    "value": "0.8",
    "unit": "mm"
  },
  {
    "id": "station2_water",
    "title": "Mực nước - Phú Đức",
    "value": "0.8",
    "unit": "m"
  },
  {
    "id": "station2_surface",
    "title": "Độ mặn tầng mặt - Phú Đức",
    "value": "12.2",
    "unit": "PPT"
  },
  {
    "id": "station2_bottom",
    "title": "Độ mặn tầng đáy - Phú Đức",
    "value": "1.0",
    "unit": "PPT"
  },

  // Station 3 - Tân Thành
  {
    "id": "station3_rainfall",
    "title": "Lượng mưa 24h - Tân Thành",
    "value": "0.8",
    "unit": "mm"
  },
  {
    "id": "station3_water",
    "title": "Mực nước - Tân Thành",
    "value": "0.8",
    "unit": "m"
  },
  {
    "id": "station3_surface",
    "title": "Độ mặn tầng mặt - Tân Thành",
    "value": "0.6",
    "unit": "PPT"
  },
  {
    "id": "station3_bottom",
    "title": "Độ mặn tầng đáy - Tân Thành",
    "value": "1.0",
    "unit": "PPT"
  },

  // Station 4 - Thị trấn Trà Ôn
  {
    "id": "station4_rainfall",
    "title": "Lượng mưa 24h - Thị trấn Trà Ôn",
    "value": "53.8",
    "unit": "mm"
  },
  {
    "id": "station4_water",
    "title": "Mực nước - Thị trấn Trà Ôn",
    "value": "0.7",
    "unit": "m"
  },
  {
    "id": "station4_surface",
    "title": "Độ mặn tầng mặt - Thị trấn Trà Ôn",
    "value": "1.2",
    "unit": "PPT"
  },
  {
    "id": "station4_bottom",
    "title": "Độ mặn tầng đáy - Thị trấn Trà Ôn",
    "value": "0.8",
    "unit": "PPT"
  },

  // Station 5 - Tiểu Thiện
  {
    "id": "station5_rainfall",
    "title": "Lượng mưa 24h - Tiểu Thiện",
    "value": "1.1",
    "unit": "mm"
  },
  {
    "id": "station5_water",
    "title": "Mực nước - Tiểu Thiện",
    "value": "0.8",
    "unit": "m"
  },
  {
    "id": "station5_surface",
    "title": "Độ mặn tầng mặt - Tiểu Thiện",
    "value": "2.1",
    "unit": "PPT"
  },
  {
    "id": "station5_bottom",
    "title": "Độ mặn tầng đáy - Tiểu Thiện",
    "value": "0.2",
    "unit": "PPT"
  },

  // Station 6 - Ngũ tự sống Trà Ngoà
  {
    "id": "station6_rainfall",
    "title": "Lượng mưa 24h - Ngũ tự sống Trà Ngoà",
    "value": "65.2",
    "unit": "mm"
  },
  {
    "id": "station6_water",
    "title": "Mực nước - Ngũ tự sống Trà Ngoà",
    "value": "20.8",
    "unit": "m"
  },
  {
    "id": "station6_surface",
    "title": "Độ mặn tầng mặt - Ngũ tự sống Trà Ngoà",
    "value": "0.8",
    "unit": "PPT"
  },
  {
    "id": "station6_bottom",
    "title": "Độ mặn tầng đáy - Ngũ tự sống Trà Ngoà",
    "value": "0.2",
    "unit": "PPT"
  },

  // Station 7 - Nhà Đài
  {
    "id": "station7_rainfall",
    "title": "Lượng mưa 24h - Nhà Đài",
    "value": "10.2",
    "unit": "mm"
  },
  {
    "id": "station7_water",
    "title": "Mực nước - Nhà Đài",
    "value": "1.6",
    "unit": "m"
  },
  {
    "id": "station7_surface",
    "title": "Độ mặn tầng mặt - Nhà Đài",
    "value": "4.3",
    "unit": "PPT"
  },
  {
    "id": "station7_bottom",
    "title": "Độ mặn tầng đáy - Nhà Đài",
    "value": "1.6",
    "unit": "PPT"
  },

  // Station 8 - Năng Âm
  {
    "id": "station8_rainfall",
    "title": "Lượng mưa 24h - Năng Âm",
    "value": "5.6",
    "unit": "mm"
  },
  {
    "id": "station8_water",
    "title": "Mực nước - Năng Âm",
    "value": "0.6",
    "unit": "m"
  },
  {
    "id": "station8_surface",
    "title": "Độ mặn tầng mặt - Năng Âm",
    "value": "1.1",
    "unit": "PPT"
  },
  {
    "id": "station8_bottom",
    "title": "Độ mặn tầng đáy - Năng Âm",
    "value": "0.6",
    "unit": "PPT"
  },

  // Station 9 - Quới Ân
  {
    "id": "station9_rainfall",
    "title": "Lượng mưa 24h - Quới Ân",
    "value": "1.0",
    "unit": "mm"
  },
  {
    "id": "station9_water",
    "title": "Mực nước - Quới Ân",
    "value": "1.0",
    "unit": "m"
  },
  {
    "id": "station9_surface",
    "title": "Độ mặn tầng mặt - Quới Ân",
    "value": "0.8",
    "unit": "PPT"
  },
  {
    "id": "station9_bottom",
    "title": "Độ mặn tầng đáy - Quới Ân",
    "value": "0.2",
    "unit": "PPT"
  },

  // Station 10 - Cái Ngang
  {
    "id": "station10_rainfall",
    "title": "Lượng mưa 24h - Cái Ngang",
    "value": "0.8",
    "unit": "mm"
  },
  {
    "id": "station10_water",
    "title": "Mực nước - Cái Ngang",
    "value": "0.8",
    "unit": "m"
  },
  {
    "id": "station10_surface",
    "title": "Độ mặn tầng mặt - Cái Ngang",
    "value": "0.8",
    "unit": "PPT"
  },
  {
    "id": "station10_bottom",
    "title": "Độ mặn tầng đáy - Cái Ngang",
    "value": "0.2",
    "unit": "PPT"
  },

  // Station 11 - Hòa Hiệp
  {
    "id": "station11_rainfall",
    "title": "Lượng mưa 24h - Hòa Hiệp",
    "value": "0.8",
    "unit": "mm"
  },
  {
    "id": "station11_water",
    "title": "Mực nước - Hòa Hiệp",
    "value": "0.8",
    "unit": "m"
  },
  {
    "id": "station11_surface",
    "title": "Độ mặn tầng mặt - Hòa Hiệp",
    "value": "0.8",
    "unit": "PPT"
  },
  {
    "id": "station11_bottom",
    "title": "Độ mặn tầng đáy - Hòa Hiệp",
    "value": "0.2",
    "unit": "PPT"
  }
]
```

---

## 5. Data Validation Rules

### 5.1. Required Fields
Tất cả 4 fields phải có mặt trong mỗi object:
- ✅ `id` (string, not null)
- ✅ `title` (string, not null)
- ✅ `value` (string, not null)
- ✅ `unit` (string, not null)

### 5.2. Value Format

#### Numeric Values
```json
{
  "value": "12.5"    // ✅ Số thập phân dạng string
}
```

#### No Data
Khi không có dữ liệu, backend có thể:

**Option 1**: Không gửi object đó (khuyến nghị)
```json
// Không có station1_rainfall trong array
```

**Option 2**: Gửi giá trị "--" hoặc empty string
```json
{
  "id": "station1_rainfall",
  "title": "Lượng mưa 24h - Cái Mười",
  "value": "--",    // hoặc ""
  "unit": "mm"
}
```

> **Lưu ý**: Client sẽ hiển thị "--" và màu xám (#C9C9C9) khi không có dữ liệu.

### 5.3. ID Validation

Format: `station{N}_{param_type}`

**Valid examples**:
- ✅ `station1_rainfall`
- ✅ `station11_bottom`
- ✅ `station5_water`

**Invalid examples**:
- ❌ `station_1_rainfall` (underscore before number)
- ❌ `station1rainfall` (missing underscore)
- ❌ `station0_water` (số 0, phải từ 1-11)
- ❌ `station12_water` (vượt quá 11)

---

## 6. Color Coding Logic (Client-side)

Client sẽ tự động áp dụng màu sắc dựa trên giá trị số:

| Giá trị (numeric) | Cấp độ | Màu      | Hex Code  | Màu chữ |
|------------------|--------|----------|-----------|---------|
| < 1.0            | Cấp 0  | Xanh lá  | #29c717   | Trắng   |
| < 5.0            | Cấp 1  | Xanh nhạt| #b1ffff   | **Đen** |
| < 10.0           | Cấp 2  | Vàng nhạt| #faf58c   | **Đen** |
| < 20.0           | Cấp 3  | Cam      | #ff9b00   | Trắng   |
| < 50.0           | Cấp 4  | Đỏ       | #ff0a00   | Trắng   |
| >= 50.0          | Cấp 5  | Tím      | #a028a0   | Trắng   |
| "--" or invalid  | No Data| Xám      | #C9C9C9   | Trắng   |

> **Backend không cần gửi thông tin màu sắc**. Client tự động xử lý dựa trên giá trị.

---

## 7. Update Frequency

### Recommended
- **Cập nhật**: Mỗi 30 giây đến 5 phút (tùy yêu cầu thực tế)
- **Method**: Emit event `"message"` với full data array mỗi lần cập nhật

### Client Behavior
- Client lắng nghe sự kiện `"message"` liên tục
- Khi nhận được data mới, client sẽ:
  1. Parse JSON
  2. Transform vào StationData model
  3. Update UI với màu sắc tương ứng
  4. Hiển thị thời gian cập nhật (client-side timestamp)

---

## 8. Error Handling

### 8.1. Connection Errors

Client sẽ tự động reconnect khi mất kết nối.

**Server nên support**:
- Reconnection handling
- Gửi lại data mới nhất khi client reconnect

### 8.2. JSON Parse Errors

Nếu JSON invalid, client sẽ:
- Hiển thị overlay "LỖI DỮ LIỆU"
- Giữ nguyên data cũ
- Tiếp tục lắng nghe message mới

**Server phải đảm bảo**:
- JSON luôn valid
- Format đúng theo spec
- Không gửi null hoặc undefined

### 8.3. Missing Data

Nếu thiếu stations hoặc parameters:
- Client sẽ hiển thị "--" cho các giá trị missing
- Màu nền: xám (#C9C9C9)

---

## 9. Testing

### Manual Testing với Socket.IO Client

```javascript
const io = require('socket.io-client');

const socket = io('https://bd.vncscc.com:3003', {
  query: {
    deviceId: 'test_device_001'
  },
  reconnection: true
});

socket.on('connect', () => {
  console.log('✅ Connected to server');
});

socket.on('message', (data) => {
  console.log('📩 Received data:', data);
  // Parse and validate
  try {
    const parsed = JSON.parse(data);
    console.log('✅ Valid JSON, items:', parsed.length);
  } catch (e) {
    console.error('❌ Invalid JSON:', e);
  }
});

socket.on('disconnect', () => {
  console.log('❌ Disconnected from server');
});

socket.on('connect_error', (error) => {
  console.error('❌ Connection error:', error);
});
```

### Test Checklist

- [ ] Connection thành công với deviceId
- [ ] Nhận được event `"message"`
- [ ] JSON parse thành công
- [ ] Có đủ 44 objects (11 stations × 4 parameters)
- [ ] Tất cả IDs đúng format `station{1-11}_{param_type}`
- [ ] Tất cả values là string (có thể parse thành number hoặc "--")
- [ ] Reconnection hoạt động khi disconnect
- [ ] Data được gửi định kỳ

---

## 10. Backend Implementation Checklist

### Required Features

- [ ] Socket.IO server running on `https://bd.vncscc.com:3003`
- [ ] Accept connection với query parameter `deviceId`
- [ ] Emit event `"message"` với JSON array data
- [ ] Support reconnection
- [ ] CORS configuration cho domain của client
- [ ] SSL/TLS enabled (HTTPS)

### Data Preparation

- [ ] Lấy dữ liệu từ 11 trạm
- [ ] Format theo structure đã spec
- [ ] Validate tất cả IDs đúng format
- [ ] Đảm bảo values là string
- [ ] Handle missing data (không gửi hoặc gửi "--")

### Performance

- [ ] Optimize JSON size (không gửi thừa fields)
- [ ] Implement caching nếu cần
- [ ] Log connections và errors
- [ ] Monitor active connections

### Security

- [ ] Validate deviceId
- [ ] Rate limiting
- [ ] Authentication nếu cần
- [ ] Không expose sensitive data

---

## 11. Support & Contact

**Nếu có thắc mắc về API spec, vui lòng liên hệ:**

- **Mobile App Team**: [Thông tin liên hệ]
- **Hỗ trợ kỹ thuật**: 0901 880 386

---

## Changelog

| Version | Date       | Changes                                      |
|---------|------------|----------------------------------------------|
| 1.0     | 24/11/2025 | Initial API documentation                   |

---

**Lưu ý quan trọng**:  
Tài liệu này mô tả CHÍNH XÁC format mà Mobile App đang expect. Backend team cần implement y chang để đảm bảo tương thích 100%.
