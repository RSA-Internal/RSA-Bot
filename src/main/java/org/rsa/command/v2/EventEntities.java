package org.rsa.command.v2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class EventEntities<T> {

    @NotNull
    private final T event;

    @NotNull
    private final Guild guild;

    @NotNull
    private final Member requester;
}
