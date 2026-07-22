package com.example.hotel_booking.audit.util;

import com.example.hotel_booking.audit.annotation.LogField;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjectCompareUtil {

    public static String getDifferences(Object oldObj, Object newObj) {
        if (oldObj == null || newObj == null) return "Đối tượng không tồn tại";
        if (oldObj.getClass() != newObj.getClass()) return "Loại đối tượng không khớp";

        
        String entityContext = "";
        if (oldObj instanceof AuditDisplayable) {
            entityContext = " " + ((AuditDisplayable) oldObj).getAuditDisplayName();
        }

        List<String> changes = new ArrayList<>();
        Field[] fields = oldObj.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(LogField.class)) continue;

            field.setAccessible(true);
            try {
                Object oldVal = field.get(oldObj);
                Object newVal = field.get(newObj);

                if (isFieldChanged(oldVal, newVal)) {
                    
                    String label = field.getAnnotation(LogField.class).name();
                    String fullLabel = label + entityContext; 

                    changes.add(String.format("%s: [%s] -> [%s]",
                            fullLabel, formatValue(oldVal), formatValue(newVal)));
                }
            } catch (IllegalAccessException e) {
                
            }
        }
        return String.join(", ", changes);
    }

    private static boolean isFieldChanged(Object oldVal, Object newVal) {
        if (oldVal == null && newVal == null) return false;
        if (oldVal == null || newVal == null) return true;

        
        if (oldVal instanceof Identifiable && newVal instanceof Identifiable) {
            return !Objects.equals(((Identifiable) oldVal).getId(), ((Identifiable) newVal).getId());
        }
        return !Objects.equals(oldVal, newVal);
    }

    private static String formatValue(Object value) {
        if (value == null) return "Trống";
        
        if (value instanceof AuditDisplayable) {
            return ((AuditDisplayable) value).getAuditDisplayName();
        }
        return value.toString();
    }
}