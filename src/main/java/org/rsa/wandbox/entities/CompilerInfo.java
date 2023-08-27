package org.rsa.wandbox.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.rsa.wandbox.entities.compiler.Switch;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilerInfo {
    @JsonProperty("compiler-option-raw")
    public boolean compiler_option_raw;
    @JsonProperty("runtime-option-raw")
    public boolean runtime_option_raw;
    @JsonProperty("display-compile-command")
    public String display_compile_command;
    public ArrayList<Switch> switches;
    public String name;
    public String version;
    public String language;
    @JsonProperty("display-name")
    public String display_name;
    public ArrayList<String> templates;
}
