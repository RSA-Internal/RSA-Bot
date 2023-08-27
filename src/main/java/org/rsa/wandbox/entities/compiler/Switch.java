package org.rsa.wandbox.entities.compiler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Switch{
    @JsonProperty("default")
    public Object mydefault;
    public String name;
    @JsonProperty("display-flags")
    public String display_flags;
    @JsonProperty("display-name")
    public String display_name;
    public ArrayList<Option> options;
    public String type;
}