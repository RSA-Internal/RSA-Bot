package org.rsa.wandbox.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompileParameter {
    String code;
    String options;
    String compiler;
    @JsonProperty("compiler-option-raw")
    String compiler_option_raw;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
