package org.rsa.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;
import org.rsa.aws.ddb.TaskDAO;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TaskCache {
    public static final LoadingCache<String, Set<String>> guildTaskListCache =
        CacheBuilder.newBuilder()
        .maximumSize(10)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build(
            new CacheLoader<>() {
                @NotNull
                @Override
                public Set<String> load(@NotNull String key) {
                    return TaskDAO.getGuildTaskNameList(key);
                }
            }
        );
}
