# Contract API 사용 예시

## 업체 계약 가능 시간 조회 API

### 엔드포인트
```
POST /api/v1/contracts/{vendorId}/availability
```

### 요청 파라미터
- `vendorId`: 업체 ID (Path Variable)
- `page`: 페이지 번호 (Query Parameter, default: 0)
- `size`: 페이지 크기 (Query Parameter, default: 3)
- `sort`: 정렬 방향 (Query Parameter, default: asc)

### 요청 바디 예시
```json
{
  "year": 2025,
  "months": [1, 2, 3, 4, 5, 6]
}
```

### 응답 예시
```json
{
  "status": "SUCCESS",
  "message": "계약 가능 시간 조회 성공",
  "data": {
    "content": [
      {
        "vendorId": 1,
        "vendorName": "웨딩홀 ABC",
        "monthlyAvailabilities": [
          {
            "year": 2025,
            "month": 1,
            "totalDays": 31,
            "availableDays": 25,
            "dateDetails": [
              {
                "date": "2025-01-01",
                "isAvailable": true,
                "totalSlots": 8,
                "reservedSlots": 2,
                "contractedSlots": 1,
                "availableSlots": 5
              },
              {
                "date": "2025-01-02",
                "isAvailable": false,
                "totalSlots": 8,
                "reservedSlots": 4,
                "contractedSlots": 4,
                "availableSlots": 0
              }
            ]
          }
        ],
        "summary": {
          "totalMonths": 3,
          "totalDays": 90,
          "totalAvailableDays": 75,
          "availabilityRate": 83.33
        }
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 3,
      "sort": {
        "sorted": true,
        "ascending": true
      }
    },
    "totalElements": 6,
    "totalPages": 2,
    "first": true,
    "last": false
  }
}
```

### 기능 설명
1. **여러 달 조회**: 한 번의 요청으로 여러 달의 계약 가능 시간을 조회할 수 있습니다.
2. **페이징 처리**: 3개씩 월별로 페이징하여 응답합니다.
3. **상세 정보**: 각 날짜별로 예약된 슬롯, 계약된 슬롯, 사용 가능한 슬롯을 확인할 수 있습니다.
4. **요약 정보**: 전체 조회 기간에 대한 가용성 요약을 제공합니다.

### cURL 예시
```bash
curl -X POST "http://localhost:8080/api/v1/contracts/1/availability?page=0&size=3&sort=asc" \
  -H "Content-Type: application/json" \
  -d '{
    "year": 2025,
    "months": [1, 2, 3, 4, 5, 6]
  }'
```

### 주요 특징
- **기존 예약과의 차별화**: 예약 API는 한 달씩만 조회 가능하지만, 계약 API는 여러 달을 한번에 조회 가능
- **충돌 방지**: 기존 예약과 계약을 모두 고려하여 실제 사용 가능한 시간만 표시
- **확장 가능**: 향후 계약 생성, 수정, 삭제 등의 기능 추가 용이
