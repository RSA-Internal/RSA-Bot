package org.rsa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class BaseEntity {

    private final Integer id;
    private final String name;

    public abstract String getAsDetails();
}
