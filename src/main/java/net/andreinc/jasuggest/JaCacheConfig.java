/**
 * Copyright 2017 Andrei N. Ciobanu

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

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
