# 📖 동시성 이슈/설계

## 1️⃣ 다수 사용자가 동시에 같은 상품을 주문

### 문제 상황 및 내부 구조

- 다수의 사용자가 동시에 같은 상품을 주문하면, 재고 oversell이 발생할 수도
- 상품 정보 조회 -> 상품 재고에서 상품 주문 요청량을 뺌 -> 상품 갱신
- 상품 도메인에서 잔여 상품이 음수면 예외가 발생

### 해결 전략

- 상품 재고 차감은 데이터 정합성이 중요하고, 인기 상품의 경우 충돌이 많아질 수 있어 비관적 락으로 설정
  - **DB Lock : 비관적 락**
https://github.com/boldfaced7/hhplus-e-commerce/blob/e558280ab295cf6d39828a8096c98eaaaf1f7371/src/main/java/kr/hhplus/be/server/adapter/out/persistence/product/ProductJpaRepository.java#L18-L23

- 트랜잭션 격리 수준은 처음 한번 조회만 하므로 Dirty Read 만 방지, READ COMMITTED 로 설정
  - **트랜잭션 격리 수준 : READ COMMITED**
https://github.com/boldfaced7/hhplus-e-commerce/blob/e558280ab295cf6d39828a8096c98eaaaf1f7371/src/main/java/kr/hhplus/be/server/application/service/saga/product/impl/DeductStockServiceImpl.java#L24-L26

- 데드락 방지
  - **트랜잭션 타임아웃 설정**

---

## 2️⃣ 동일 유저가 동시에 여러 번 결제 요청
### 문제 상황 및 내부 구조

- 동일 유저가 동시에 여러 번 결제 요청 시 잔액 조회 -> 계산 -> 갱신 과정 중 포인트 잔액이 음수가 발생할 수도
- 잔액 정보 조회 -> 잔여 금액에서 결제 금액을 뺌 -> 잔액을 갱신
- 잔액 도메인에서 잔여 금액이 음수면 예외가 발생

### 해결 전략

- 잔액 차감은 데이터 정합성이 매우 중요한 로직이므로, 비관적 락 사용
  - **DB Lock : 비관적 락**
https://github.com/boldfaced7/hhplus-e-commerce/blob/e558280ab295cf6d39828a8096c98eaaaf1f7371/src/main/java/kr/hhplus/be/server/adapter/out/persistence/balance/BalanceJpaRepository.java#L16-L21

- 트랜잭션 격리 수준은 처음 한번 조회만 하므로 Dirty Read 만 방지, READ COMMITTED 로 설정
  - **트랜잭션 격리 수준 : READ COMMITED**
https://github.com/boldfaced7/hhplus-e-commerce/blob/e558280ab295cf6d39828a8096c98eaaaf1f7371/src/main/java/kr/hhplus/be/server/application/service/saga/balance/impl/DeductBalanceServiceImpl.java#L24-L26

- 데드락 방지
  - **트랜잭션 타임아웃 설정**

---

## 3️⃣ 상세 테스트 코드
  - [PlaceOrderSagaConcurrencyTest.java](https://github.com/boldfaced7/hhplus-e-commerce/blob/e558280ab295cf6d39828a8096c98eaaaf1f7371/src/test/java/kr/hhplus/be/server/application/service/saga/PlaceOrderSagaConcurrencyTest.java)
