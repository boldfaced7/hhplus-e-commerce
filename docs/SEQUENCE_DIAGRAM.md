# 🔄 시퀀스 다이어그램 (도메인 중심)

## 📦 시나리오 1: 잔액 충전

```mermaid
sequenceDiagram
    participant Client
    participant Balance

    Client ->> Balance: 잔액 충전 요청
    Balance ->> Balance: 사용자 잔액 조회
    Balance ->> Balance: 잔액 증가 및 저장
    Balance -->> Client: 충전된 잔액 반환
```

---

## 📦 시나리오 2: 상품 목록 조회

```mermaid
sequenceDiagram
    participant Client
    participant Product

    Client ->> Product: 상품 목록 조회 요청 (page, size)
    Product ->> Product: 상품 목록 조회 (페이징)
    Product -->> Client: 상품 목록 반환
```

---

## 📦 시나리오 3: 선착순 쿠폰 발급

```mermaid
sequenceDiagram
    participant Client
    participant Coupon
    participant Redis

    Client ->> Coupon: 쿠폰 발급 요청
    Coupon ->> Redis: 사용자 락 획득 시도
    alt 락 획득 실패
        Coupon -->> Client: 발급 실패 응답
    else 락 획득 성공
        Coupon ->> Coupon: 쿠폰 보유 여부 확인
        alt 이미 보유
            Coupon -->> Client: 이미 보유 응답
        else 미발급
            Coupon ->> Coupon: 쿠폰 발급 및 저장
            Coupon ->> Coupon: 재고 감소
            Coupon -->> Client: 발급 성공 응답
        end
    end
```

---

## 📦 시나리오 4: 상품 주문 및 결제

```mermaid
sequenceDiagram
    participant Client
    participant Order
    participant Balance
    participant Product
    participant Coupon
    participant Outbox

    Client ->> Order: 주문 요청 (상품 + 쿠폰)
    Order ->> Balance: 사용자 잔액 확인
    Order ->> Product: 재고 확인
    Order ->> Coupon: 쿠폰 유효성 검증

    alt 실패 조건 존재 (잔액 부족 / 재고 부족 / 쿠폰 무효)
        Order -->> Client: 주문 실패 응답
    else 성공 시
        Order ->> Balance: 잔액 차감
        Order ->> Product: 재고 차감
        Order ->> Coupon: 쿠폰 사용 처리
        Order ->> Order: 주문 저장
        Order ->> Outbox: 주문 이벤트 비동기 전송
        Order -->> Client: 주문 성공 응답
    end
```

---

## 📦 시나리오 5: 인기 상품 조회

```mermaid
sequenceDiagram
    participant Client
    participant ProductStats
    participant Product

    Client ->> ProductStats: 인기 상품 조회 요청
    ProductStats ->> ProductStats: 최근 3일간 집계된 상품 조회
    ProductStats ->> Product: 상품 상세 조회
    ProductStats -->> Client: 인기 상품 리스트 반환
```
