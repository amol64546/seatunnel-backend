package com.seatunnel.orchestrator.util;

import com.seatunnel.orchestrator.annotations.ActionLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggerAspect {

  @Around("@annotation(actionLog)")
  public Object log(ProceedingJoinPoint pjp, ActionLog actionLog) throws Throwable {

    Map<String, Object> inputs = new HashMap<>();
    Object[] args = pjp.getArgs();
    for (int i = 0; i < args.length; i++) {
      inputs.put("arg" + i, args[i]);
    }

    Object output = null;
    Throwable error = null;

    long start = System.currentTimeMillis();

    try {
      output = pjp.proceed(); // Execute the actual method
      return output;
    } catch (Throwable ex) {
      error = ex;
      throw ex;
    } finally {
      long duration = System.currentTimeMillis() - start;

      Map<String, Object> meta = Map.of(
        "class", pjp.getTarget().getClass().getName(),
        "method", pjp.getSignature().getName(),
        "durationMs", duration,
        "exception", error != null ? error.getMessage() : ""
      );

      record(
        actionLog.operation(),
        inputs,
        Map.of("return", output),
        meta
      );
    }
  }

  private void record(String operator, Map<String, Object> inputs, Map<String, Object> aReturn, Map<String, Object> meta) {
    log.info("Operation: {}", operator);
    log.info("Inputs: {}", inputs);
    log.info("Return: {}", aReturn);
    log.info("Meta: {}", meta);
  }
}
