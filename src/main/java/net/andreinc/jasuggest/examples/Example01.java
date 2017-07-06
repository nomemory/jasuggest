package net.andreinc.jasuggest.examples;

import net.andreinc.jasuggest.JaCacheConfig;
import net.andreinc.jasuggest.JaSuggest;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.List;

/**
 * Created by andreinicolinciobanu on 06/07/17.
 */
public class Example01 {
    public static void example01() {

        String[] words = { "us", "usa", "use", "useful", "useless", "user", "usurper", "ux", "util", "utility" };

        JaCacheConfig jaCacheConfig =
                JaCacheConfig.builder()
                             .maxSize(512)
                             .expirationPolicy(ExpirationPolicy.ACCESSED)
                             .build();

        JaSuggest jaSuggest = JaSuggest.builder()
                                       .ignoreCase()
                                       .withCache(jaCacheConfig)
                                       .buildFrom(words);

        List<String> result = jaSuggest.findSuggestions("use");

        System.out.println(result);
    }

    public static void main(String[] args) {
        example01();
    }
}
