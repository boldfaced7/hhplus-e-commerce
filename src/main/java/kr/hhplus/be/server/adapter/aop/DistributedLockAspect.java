package kr.hhplus.be.server.adapter.aop;

import kr.hhplus.be.server.application.port.aop.DistributedLock;
import kr.hhplus.be.server.application.port.aop.DistributedMultiLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(kr.hhplus.be.server.application.port.aop.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock annotation = signature.getMethod().getAnnotation(DistributedLock.class);

        String lockKey = createDynamicKey(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                annotation.key(),
                annotation.prefix()
        );
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(
                    annotation.waitTime(),
                    annotation.leaseTime(),
                    annotation.timeUnit()
            );
            if (!isLocked) {
                throw new IllegalStateException("락을 획득할 수 없습니다: " + lockKey);
            }
            log.info("분산 락 획득: {}", lockKey);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new RuntimeException("락 획득 중 오류가 발생했습니다.", e);
        } finally {
            // 단일 락은 isLocked()와 isHeldByCurrentThread()를 지원
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("분산 락 해제: {}", lockKey);
            }
        }
    }

    @Around("@annotation(kr.hhplus.be.server.application.port.aop.DistributedMultiLock)")
    public Object multiLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedMultiLock annotation = signature.getMethod().getAnnotation(DistributedMultiLock.class);

        List<String> lockKeys = createDynamicKeys(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                annotation.keys(),
                annotation.prefix()
        );

        if (lockKeys.isEmpty()) {
            return joinPoint.proceed();
        }

        RLock multiLock = redissonClient.getMultiLock(
                lockKeys.stream()
                        .map(redissonClient::getLock)
                        .toArray(RLock[]::new)
        );

        boolean isLocked = false;

        try {
            isLocked = multiLock.tryLock(
                    annotation.waitTime(),
                    annotation.leaseTime(),
                    annotation.timeUnit()
            );
            if (!isLocked) {
                throw new IllegalStateException("멀티 락을 획득할 수 없습니다: " + lockKeys);
            }
            log.info("분산 멀티 락 획득: {}", lockKeys);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new RuntimeException("멀티 락 획득 중 오류가 발생했습니다.", e);
        } finally {
            // 3. MultiLock은 isLocked()를 지원하지 않으므로, 락 획득 성공 여부(isLocked)로만 판단
            if (isLocked) {
                multiLock.unlock();
                log.info("분산 멀티 락 해제: {}", lockKeys);
            }
        }
    }

    private List<String> createDynamicKeys(
            String[] parameterNames,
            Object[] args,
            String expression,
            String prefix
    ) {
        Collection<?> items = parseExpression(parameterNames, args, expression, Collection.class);
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        return items.stream().sorted()
                .map(item -> createKey(prefix, item))
                .toList();
    }

    private String createDynamicKey(
            String[] parameterNames,
            Object[] args,
            String expression,
            String prefix
    ) {
        Object parsed = parseExpression(parameterNames, args, expression, Object.class);
        return createKey(prefix, parsed);
    }

    private <T> T parseExpression(
            String[] parameterNames,
            Object[] args,
            String expression,
            Class<T> returnType
    ) {
        var parser  = new SpelExpressionParser();
        var context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return parser.parseExpression(expression).getValue(context, returnType);
    }

    private static String createKey(String prefix, Object value) {
        return Optional.ofNullable(value)
                .map(i -> prefix + i)
                .orElseThrow(() -> new IllegalArgumentException("키는 null이 될 수 없습니다."));
    }

}