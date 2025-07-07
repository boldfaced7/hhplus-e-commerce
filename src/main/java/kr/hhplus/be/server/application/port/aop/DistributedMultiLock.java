package kr.hhplus.be.server.application.port.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedMultiLock {

    /**
     * 락 키를 생성할 대상 컬렉션을 가리키는 SpEL.
     * 이 컬렉션의 요소들은 Identifiable 인터페이스를 구현해야 합니다.
     * 예: "#event.productIds()"
     */
    String keys();

    /**
     * 각 락 키에 붙일 고정된 접두사(prefix).
     * 예: "product:lock:"
     */
    String prefix();

    /**
     * 락 획득을 위해 대기하는 시간
     */
    long waitTime() default 5L;

    /**
     * 락을 점유하는 시간 (자동 해제 시간)
     */
    long leaseTime() default 3L;

    /**
     * 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}