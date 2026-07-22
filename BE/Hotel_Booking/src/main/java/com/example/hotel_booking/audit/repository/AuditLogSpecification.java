package com.example.hotel_booking.audit.repository;

import com.example.hotel_booking.audit.entity.AuditLog;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class AuditLogSpecification {
    public static Specification<AuditLog> withFilters(
            String module, Long targetId, String action,
            Long employeeId, LocalDateTime fromDate, LocalDateTime toDate
    ) {
        return (root, query, cb) -> {
            
            if (Long.class != query.getResultType()) {
                root.fetch("employee", JoinType.LEFT);
            }

            var predicate = cb.conjunction();

            if (StringUtils.hasText(module)) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("module")), "%" + module.toLowerCase() + "%"));
            }
            if (targetId != null) {
                var targetIdExpression = root.get("targetId").as(String.class);

                predicate = cb.and(predicate, cb.like(targetIdExpression, "%" + targetId + "%"));
            }
            if (StringUtils.hasText(action)) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("action")), "%" + action.toLowerCase() + "%"));
            }

            
            if (employeeId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("employee").get("id"), employeeId));
            }

            
            if (fromDate != null && toDate != null) {
                predicate = cb.and(predicate, cb.between(root.get("createdAt"), fromDate, toDate));
            } else if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            } else if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }

            return predicate;
        };
    }
}