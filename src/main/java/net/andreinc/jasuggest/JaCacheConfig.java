package net.andreinc.jasuggest;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@Builder
@ToString
@EqualsAndHashCode
@FieldDefaults(level = PRIVATE)
public class JaCacheConfig {
    @Builder.Default @Getter
    int maxSize = 2048;

    @Builder.Default @Getter @NonNull
    ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;

    @Builder.Default @Getter
    long expiration = 24;

    @Builder.Default @Getter @NonNull
    public final TimeUnit expirationUnit =  TimeUnit.HOURS;

    public static JaCacheConfig defaultConfig() {
        return JaCacheConfig.builder().build();
    }
}
