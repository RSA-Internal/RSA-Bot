package org.rsa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public abstract class BaseEntity {

    private Integer id;
    private final String name;

    public abstract String getAsDetails();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BaseEntity other)) return false;
        boolean idMatch = other.id.equals(id);
        boolean nameMatch = other.name.equals(name);

        return idMatch && nameMatch;
    }
}
