package org.rsa.entity.adventure;

import lombok.Getter;
import lombok.Setter;
import org.rsa.entity.BaseEntity;
import org.rsa.model.adventure.Currency;

@Getter
@Setter
public class CurrencyEntity extends BaseEntity {

    private Integer quantity;

    public static CurrencyEntity fromEnum(Currency currency) {
        return new CurrencyEntity(currency.getId(), currency.getName(), 0);
    }

    public CurrencyEntity(Integer id, String name, int quantity) {
        super(id, name);
        this.quantity = quantity;
    }

    @Override
    public String getAsDetails() {
        return null;
    }
}
