package org.rsa.wandbox.entities.compiler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Option {
    public String name;
    @JsonProperty("display-flags")
    public String display_flags;
    @JsonProperty("display-name")
    public String display_name;
}