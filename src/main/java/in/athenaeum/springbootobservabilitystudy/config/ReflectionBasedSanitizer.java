package in.athenaeum.springbootobservabilitystudy.config;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReflectionBasedSanitizer {

    public String sanitizeObject(Object obj) {
        if (obj == null) return "null";

        try {
            return sanitizeObjectRecursive(obj, new HashSet<>());
        } catch (Exception e) {
            return "[SANITIZATION_ERROR: " + e.getMessage() + "]";
        }
    }

    private String sanitizeObjectRecursive(Object obj, Set<Object> visited) {
        if (obj == null) return "null";
        if (visited.contains(obj)) return "[CIRCULAR_REFERENCE]";

        visited.add(obj);

        // Handle primitives and strings
        if (isPrimitive(obj)) {
            Sensitive sensitiveAnnotation = obj.getClass().getAnnotation(Sensitive.class);
            
            if (sensitiveAnnotation != null) {
                return sensitiveAnnotation.mask();
            }

            return obj.toString();
        }

        // Handle collections
        if (obj instanceof Collection) {
            return sanitizeCollection((Collection<?>) obj, visited);
        }

        // Handle complex objects
        return sanitizeComplexObject(obj, visited);
    }

    private String sanitizeComplexObject(Object obj, Set<Object> visited) {
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getSimpleName()).append("{");

        Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            if (!first) sb.append(", ");
            first = false;

            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                sb.append(field.getName()).append("=");

                if (field.isAnnotationPresent(Sensitive.class)) {
                    Sensitive annotation = field.getAnnotation(Sensitive.class);
                    sb.append(annotation.mask());
                } else {
                    sb.append(sanitizeObjectRecursive(value, visited));
                }
            } catch (IllegalAccessException e) {
                sb.append("[ACCESS_ERROR]");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    private boolean isPrimitive(Object obj) {
        return obj instanceof String || obj instanceof Number ||
                obj instanceof Boolean || obj instanceof Character ||
                obj.getClass().isPrimitive();
    }

    private String sanitizeCollection(Collection<?> collection, Set<Object> visited) {
        if (collection.size() > 10) { // Limit large collections
            return String.format("[Collection of %d items - truncated for logging]", collection.size());
        }

        return collection.stream()
                .map(item -> sanitizeObjectRecursive(item, visited))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
