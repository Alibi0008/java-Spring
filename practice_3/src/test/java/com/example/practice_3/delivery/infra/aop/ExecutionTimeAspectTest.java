package com.example.practice_3.delivery.infra.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit test for ExecutionTimeAspect (Practice 5).
 */
class ExecutionTimeAspectTest {

    private final ExecutionTimeAspect aspect = new ExecutionTimeAspect();

    @Test
    void proceedsAndReturnsResult() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("DeliveryService.quote(..)");
        when(pjp.proceed()).thenReturn("result-ok");

        Object result = aspect.time(pjp);

        assertThat(result).isEqualTo("result-ok");
        verify(pjp, times(1)).proceed();
    }

    @Test
    void logsTimeEvenWhenExceptionIsThrown() throws Throwable {
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("DeliveryService.quote(..)");
        when(pjp.proceed()).thenThrow(new IllegalStateException("simulated failure"));

        assertThatThrownBy(() -> aspect.time(pjp))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("simulated failure");

        verify(pjp, times(1)).proceed();
    }
}
