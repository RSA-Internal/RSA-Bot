package org.rsa.model.adventure.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActivityResponse {
    private final boolean result;
    private final String response;
}

