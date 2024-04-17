package hvorostina.chesscomapi.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Slf4j
@Component
public class LoggerAspect {
    @Pointcut("within(hvorostina.chesscomapi.controller..*)")
    public void controllerMethods() { }
    @Pointcut("@annotation("
            + "hvorostina.chesscomapi.annotations.AspectAnnotation)")
    public void serviceMethodWithAnnotation() { }
    @Pointcut("within(hvorostina.chesscomapi.controller..*)"
            + " || within(hvorostina.chesscomapi.service..*)"
            + " || within(hvorostina.chesscomapi.in_memory_cache..*)")
    public void allMethods() { }
    @Around("serviceMethodWithAnnotation()")
    public Object logEnteringAPI(final ProceedingJoinPoint joinPoint)
            throws Throwable {
        log.info("Enter: {}.{}() with argument[s] = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("Exit: {}.{}() with result = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()",
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }
    @AfterThrowing(pointcut = "allMethods()", throwing = "exception")
    public void logsExceptionsFromAnyLocation(
            final JoinPoint joinPoint, final Throwable exception) {
        log.error("Exception in : {}.{}() cause = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), exception.getMessage());
    }
}
