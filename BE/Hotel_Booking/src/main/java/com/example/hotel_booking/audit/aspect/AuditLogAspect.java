package com.example.hotel_booking.audit.aspect;

import com.example.hotel_booking.audit.annotation.LogAction;
import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.audit.util.HasParent;
import com.example.hotel_booking.audit.util.ObjectCompareUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final ExpressionParser parser = new SpelExpressionParser();

    @PersistenceContext
    private EntityManager entityManager;

    private static final Set<String> COMPARABLE_ACTIONS = Set.of(
            "UPDATE", "ASSIGN_ROOM", "CHANGE_ROOM_NUMBER", "CHANGE_ROOM_TYPE", "UPDATE_SERVICE"
    );

    @Around("@annotation(logAction)")
    public Object logAround(ProceedingJoinPoint joinPoint, LogAction logAction) throws Throwable {
        Long idForEntity = extractTargetId(joinPoint, logAction.targetId());
        boolean isComparable = COMPARABLE_ACTIONS.contains(logAction.action());

        Object oldEntity = null;
        if (isComparable && logAction.entityClass() != Void.class && idForEntity != null) {
            Object dbEntity = entityManager.find(logAction.entityClass(), idForEntity);
            if (dbEntity != null) {
                entityManager.detach(dbEntity);
                oldEntity = dbEntity;
            }
        }

        Object result = joinPoint.proceed();

        String description = "";
        if (isComparable && logAction.entityClass() != Void.class && idForEntity != null) {
            entityManager.flush();
            entityManager.clear();

            Object newEntity = entityManager.find(logAction.entityClass(), idForEntity);
            description = ObjectCompareUtil.getDifferences(oldEntity, newEntity);
        } else {
            description = "Đã thực hiện: " + logAction.action();
        }

        if (description != null && !description.isEmpty()) {
            Long idForLog = idForEntity;
            if (logAction.resolveParent() && idForEntity != null) {
                Object entity = entityManager.find(logAction.entityClass(), idForEntity);
                if (entity instanceof HasParent) {
                    Long parentId = ((HasParent) entity).getParentId();
                    if (parentId != null) idForLog = parentId;
                }
            }
            auditLogService.saveLog(logAction.module(), logAction.action(), idForLog, description);
        }

        return result;
    }

    private Long extractTargetId(ProceedingJoinPoint joinPoint, String spelExpr) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();

            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], paramValues[i]);
            }

            Object val = parser.parseExpression(spelExpr).getValue(context);

            if (val instanceof Long) return (Long) val;
            if (val != null) return Long.parseLong(val.toString());

        } catch (Exception e) {
            System.err.println("⚠ Lỗi khi trích xuất ID qua SpEL: " + e.getMessage());
        }
        return null;
    }
}