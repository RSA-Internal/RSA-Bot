package org.rsa.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rsa.exception.ValidationException;

import java.util.Objects;

public class ValidationUtil {
    @Contract("!null, null -> fail")
    public static void notNull(String fieldName, Object o) throws ValidationException {
        if (Objects.isNull(o)) {
            throw new ValidationException(fieldName, "expected value, but was null.");
        }
    }
    public static void areEqual(String fieldName, Object o, @NotNull Object other) throws ValidationException {
        notNull(fieldName, o);
        if (!o.equals(other)) {
            throw new ValidationException(fieldName, "expected value to be " + other + " but was " + o);
        }
    }
}
