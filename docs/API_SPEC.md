# 📘 E-Commerce 주문 서비스 API 명세

## 🧾 공통 정보

* **Base URL**: `/api`
* **Content-Type**: `application/json`

---

## 📌 1. 잔액 충전

### ✅ POST /balance/charge

> 사용자 잔액을 충전합니다.

**요청 바디**

```json
{
  "userId": 1,
  "amount": 10000
}
```

**응답 바디**

```json
{
  "userId": 1,
  "balance": 15000
}
```

---

## 📌 2. 잔액 조회

### ✅ GET /balance?userId=1

> 사용자 현재 잔액을 조회합니다.

**응답 바디**

```json
{
  "userId": 1,
  "balance": 15000
}
```

---

## 📌 3. 상품 목록 조회 (페이징 지원)

### ✅ GET /products?page=0\&size=10

> 전체 상품 정보를 페이지 단위로 조회합니다.

**Query Parameters**

| 파라미터 | 타입  | 설명                  |
| ---- | --- | ------------------- |
| page | int | 조회할 페이지 번호 (0부터 시작) |
| size | int | 페이지당 항목 수           |

**응답 바디**

```json
{
  "content": [
    {
      "id": 1,
      "name": "아메리카노",
      "price": 3000,
      "stock": 10
    },
    {
      "id": 2,
      "name": "라떼",
      "price": 3500,
      "stock": 5
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 22,
  "totalPages": 3
}
```

---

## 📌 4. 쿠폰 발급 요청

### ✅ POST /coupons/issue?userId=1

> 사용자에게 선착순 쿠폰을 발급합니다.

**응답 바디**

```json
{
  "couponId": 5,
  "userId": 1,
  "discountRate": 10,
  "issuedAt": "2025-05-29T10:00:00"
}
```

---

## 📌 5. 보유 쿠폰 목록 조회 (페이징 지원)

### ✅ GET /coupons?userId=1\&page=0\&size=5

> 해당 사용자가 보유한 쿠폰 목록을 페이지 단위로 조회합니다.

**Query Parameters**

| 파라미터   | 타입   | 설명                  |
| ------ | ---- | ------------------- |
| userId | long | 사용자 ID              |
| page   | int  | 조회할 페이지 번호 (0부터 시작) |
| size   | int  | 페이지당 항목 수           |

**응답 바디**

```json
{
  "content": [
    {
      "couponId": 5,
      "discountRate": 10,
      "used": false
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 1,
  "totalPages": 1
}
```

---

## 📌 6. 주문 생성

### ✅ POST /orders

> 사용자 주문을 등록하고 결제를 수행합니다.

**요청 바디**

```json
{
  "userId": 1,
  "couponId": 5,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

**응답 바디 (성공 시)**

```json
{
  "orderId": 12,
  "totalPrice": 9450,
  "discount": 1050,
  "finalPrice": 9450,
  "orderedAt": "2025-05-29T10:30:00"
}
```

**예외 응답 예시 (재고 부족)**

```json
{
  "error": "Insufficient stock for productId: 2"
}
```

---

## 📌 7. 인기 상품 조회

### ✅ GET /products/top

> 최근 3일간 가장 많이 판매된 상위 5개 상품을 조회합니다.

**응답 바디**

```json
[
  {
    "productId": 1,
    "name": "아메리카노",
    "price": 3000,
    "soldCount": 120
  },
  {
    "productId": 2,
    "name": "라떼",
    "price": 3500,
    "soldCount": 95
  }
]
```

---

## ⚠️ 예외 응답 형식 (공통)

```json
{
  "error": "쿠폰이 이미 사용되었거나 존재하지 않습니다."
}
```
