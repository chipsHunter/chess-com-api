package hvorostina.chesscomapi.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class CounterAspect {
    private static AtomicLong counter = new AtomicLong(0);
    @Pointcut("within(hvorostina.chesscomapi.controller..*)")
    public void controllerMethods() { }

    @Before("controllerMethods()")
    synchronized public void incrementCounter() {
        System.out.println("Counter value is " + counter.incrementAndGet());
    }
}
