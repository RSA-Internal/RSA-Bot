package org.rsa.wandbox.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompileResult {
    String status;
    String compiler_output;
    String compiler_error;
    String compiler_message;
    String program_output;
    String program_error;
    String program_message;
}
