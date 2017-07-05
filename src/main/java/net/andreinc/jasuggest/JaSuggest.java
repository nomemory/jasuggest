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
import net.jodah.expiringmap.ExpiringMap;

import java.util.*;

import static java.util.Collections.sort;
import static lombok.AccessLevel.PRIVATE;

/**
 * Example:
 *
 * <pre>
 *     {@code
 *      JaSuggest js = JaSuggest.from("ABC", "AB", "ACD", "ABCDE", "XX");
 *      List<String> results = js.findSuggestionsInternal("AB");
 *      System.out.println(results);
 *
 *      // OUTPUT: [ABC, ABCDE]
 *     }
 * </pre>
 *
 */
public class JaSuggest {

    private boolean ignoreCase = false;
    private boolean prebuiltWords = false;

    private Map<String, List<String>> cache;
    private JaMap nodes;

    private JaSuggest(JaSuggestBuilder jaSuggestBuilder) {
        if (jaSuggestBuilder.cacheConfig!=null) {
            this.cache = ExpiringMap.builder()
                                    .maxSize(jaSuggestBuilder.cacheConfig.getMaxSize())
                                    .expirationPolicy(jaSuggestBuilder.cacheConfig.getExpirationPolicy())
                                    .expiration(jaSuggestBuilder.cacheConfig.getExpiration(),
                                                jaSuggestBuilder.cacheConfig.getExpirationUnit())
                                    .build();
        }

        this.ignoreCase = jaSuggestBuilder.ignoreCase;
        this.prebuiltWords = jaSuggestBuilder.prebuiltWords;

        this.nodes = new JaMap();
    }

    public static JaSuggestBuilder builder() { return new JaSuggestBuilder(); }

    public boolean hasCache() { return cache != null; }

    /**
     * Returns the current size of the map from memory.
     * If the Map doesn't exist
     *
     * @return
     */
    public int cacheSize() { return (hasCache()) ? cache.size() : -1; }

    /**
     * Creates a snapshot of the cache from the memory and returns it.
     *
     * @return A cloned representation of the cache from the memory. If the cache is not enabled it will always return an empty Map.
     */
    public Map<String, List<String>> cacheSnapshot() {
        final Map<String, List<String>> result = new HashMap<>();

        if (hasCache()) {
            /** This works because string are immutable */
            for(Map.Entry<String, List<String>> entry : cache.entrySet()) {
                result.put(new String(entry.getKey()), new ArrayList<>(entry.getValue()));
            }
        }

        return result;
    }

    private JaSuggest from(@NonNull String... terms) {
        addTerms(terms);
        return this;
    }

    private JaSuggest from(@NonNull Iterable<String> terms) {
        addTerms(terms);
        return this;
    }

    private void addTerms(@NonNull String... terms) {
        for(String term : terms) {
            this.nodes.addTerm(term, prebuiltWords);
        }
    }

    private void addTerms(Iterable<String> terms) {
        for(String term : terms) {
            this.nodes.addTerm(this.ignoreCase ? term.toLowerCase() : term, prebuiltWords);
        }
    }

    /**
     * Searches the current Trie for suggestions based on the given prefix.
     * All the possible suggestions will be retrieved and the results will be sorted.
     *
     * @param prefix The search prefix.
     *
     * @return A sorted list of all possible suggestions.
     */
    public List<String> findSuggestions(@NonNull String prefix) {
        return this.findSuggestionsInternal(prefix, Integer.MAX_VALUE, true);
    }

    /**
     *  Searches the current Trie for suggestion based on the given prefix.
     *  Not all the possible suggestions will be retrieved but only the first 'maxResult'.
     *  The results will be sorted.
     *
     * @param prefix The search prefix.
     * @param maxResults The maximum number of results.
     *
     * @return A sorted List of suggestions.
     */
    public List<String> findSuggestions(@NonNull String prefix, int maxResults) {
        return this.findSuggestionsInternal(prefix, maxResults, true);
    }

    /**
     * Searches the current Trie for suggestions based on the given prefix.
     * All the possible suggestions will be retrieved.
     *
     * @param prefix The search prefix.
     * @param sorted Sort the results or not
     *
     * @return A List of suggestions.
     */
    public List<String> findSuggestions(@NonNull String prefix, boolean sorted) {
        return this.findSuggestionsInternal(prefix, Integer.MAX_VALUE, sorted);
    }

    /**
     * Searches the current Trie for suggestions based on a given prefix.
     *
     * @param prefix The search prefix.
     * @param maxResults The total number of results.
     * @param sorted Sort the result or not.
     *
     * @return A List of suggestions.
     */
    public List<String> findSuggestionsInternal(@NonNull String prefix, int maxResults, boolean sorted) {
        List<String> list = new ArrayList<>();
        List<String> tmp;

        if (ignoreCase) {
            prefix = prefix.toLowerCase();
        }

        if (hasCache() && (tmp=cache.get(prefix))!=null) {
            list = new ArrayList<>(tmp);
        } else {
            JaMap local = getLocationByPrefix(prefix);

            if (null == local) {
                // Return empty list if prefix is not present
                return list;
            }

            if (prebuiltWords) {
                findSuggestionsWithPrebuiltWords(prefix, maxResults, list);
            }
            else {
                findSuggestionsInternalWithJaSolution(prefix, maxResults, list);
            }

            if (sorted) {
                sort(list);
            }

            if (list.size() > maxResults) {
                List<String> newList = new ArrayList<>(maxResults);
                for(int i = 0; i < maxResults; ++i) {
                    newList.add(list.get(i));
                }
                list = newList;
            }
        }

        if (hasCache()) {
            cache.put(prefix, list);
        }

        return list;
    }

    private List<String> findSuggestionsInternalWithJaSolution(@NonNull String prefix, int maxResults, List<String> list) {
        JaMap local = getLocationByPrefix(prefix);
        Iterator<Character> it;
        Character c;

        Stack<JaSolution> stack = new Stack<>();
        JaSolution current = new JaSolution(local, prefix);
        stack.push(current);

        while (!stack.isEmpty()) {
            current = stack.pop();

            if (current.getNode().isLeaf() && !current.getTerm().equals(prefix)) {
                list.add(current.getTerm());
            }

            it = current.getNode().keySet().iterator();

            while (it.hasNext()) {
                c = it.next();
                stack.push(new JaSolution(current.getNode().get(c), current.getTerm() + c));
            }
        }

        return list;
    }

    private List<String> findSuggestionsWithPrebuiltWords(@NonNull String prefix, int maxResults, List<String> list) {
        Iterator<JaMap> it;

        Stack<JaMap> stack = new Stack<>();
        JaMap current = getLocationByPrefix(prefix);
        stack.push(current);

        while(!stack.isEmpty()) {
            current = stack.pop();

            if (current.isLeaf() && !current.getTerm().equals(prefix)) {
                list.add(current.getTerm());
            }

            it = current.values().iterator();

            while(it.hasNext()) {
                stack.push(it.next());
            }
        }

        return list;
    }

    private JaMap getLocationByPrefix(@NonNull String prefix) {
        JaMap local = this.nodes;
        for(int i = 0; i < prefix.length(); ++i) {
            local = local.get(prefix.charAt(i));
            if (null == local) {
                return null;
            }
        }
        return local;
    }

    @NoArgsConstructor
    @FieldDefaults(level = PRIVATE)
    public static class JaSuggestBuilder {

        JaCacheConfig cacheConfig;
        boolean ignoreCase = false;
        boolean prebuiltWords = false;

        public JaSuggestBuilder withCache(JaCacheConfig config) {
            this.cacheConfig = config;
            return this;
        }

        public JaSuggestBuilder withCache() {
            this.cacheConfig = JaCacheConfig.defaultConfig();
            return this;
        }

        public JaSuggestBuilder ignoreCase() {
            this.ignoreCase = true;
            return this;
        }

        public JaSuggestBuilder prebuiltWords() {
            this.prebuiltWords = true;
            return this;
        }

        /**
         * Creates a JaSuggest object from a given array of terms.
         * If one of the terms in the array is NULL, a NullPointerException will be thrown.
         * Validate input before calling this method.
         *
         * @param terms An array of String[] that contains the list of terms we are going to (later) auto-suggest.
         *              Eg.: An array of countries.
         *
         * @return An instance of JaSuggest
         */
        public JaSuggest buildFrom(String... terms) {
            return new JaSuggest(this).from(terms);
        }

        /**
         * Creates a JaSuggest object from a given Iterable (eg.: a List of Strings).
         * If one of the terms in the Iterable object is NULL, a NullPointerException will be thrown.
         * Validate input before calling this method.
         *
         * @param terms An Iterable of String that contains the terms we are going to (later) auto-suggest.
         *              Eg.: An List of countries.
         *
         * @return An instance of JaSuggest
         */
        public JaSuggest buildFrom(Iterable<String> terms) {
            return new JaSuggest(this).from(terms);
        }
    }
}

@ToString
class JaMap extends HashMap<Character, JaMap> {

    @Getter @Setter private boolean isLeaf;
    @Getter @Setter private String term;

    protected void addTerm(String term, boolean prebuiltWords) {

        JaMap current = this;
        int lastLetterIndex = term.length() - 1;

        for (int i = 0; i < term.length(); i++) {

            current.putIfAbsent(term.charAt(i), new JaMap());
            current = current.get(term.charAt(i));

            if (!current.isLeaf() && lastLetterIndex == i) {
                current.setLeaf(true);
                if (prebuiltWords) {
                    current.setTerm(term);
                }
            }
        }
    }
}

@Data
@AllArgsConstructor
class JaSolution {
    private JaMap node;
    private String term;
}