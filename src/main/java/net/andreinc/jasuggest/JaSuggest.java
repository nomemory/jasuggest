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

import java.util.*;

import static java.util.Collections.sort;

/**
 * Example:
 *
 * <pre>
 *     {@code
 *      JaSuggest js = JaSuggest.from("ABC", "AB", "ACD", "ABCDE", "XX");
 *      List<String> results = js.findSuggestions("AB");
 *      System.out.println(results);
 *
 *      // OUTPUT: [ABC, ABCDE]
 *     }
 * </pre>
 *
 */
public class JaSuggest {

    private JaMap nodes = new JaMap();

    private JaSuggest() {}

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
    public static final JaSuggest from(@NonNull String... terms) {
        final JaSuggest result = new JaSuggest();

        for(String term : terms) {
            result.addTerm(term);
        }

        return result;
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
    public static final JaSuggest from(@NonNull Iterable<String> terms) {
        final JaSuggest result = new JaSuggest();

        for(String term : terms) {
            result.addTerm(term);
        }

        return result;
    }

    /**
     * This method allows to add a termn in the Trie after the JaSuggest object was created.
     *
     * @param term The term to be added in the Trie.
     */
    public void addTerm(String term) {
        JaMap map = this.nodes;
        boolean isWord;
        char[] chars = term.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            map.putIfAbsent(chars[i], new JaMap());
            map = map.get(chars[i]);
            isWord = (chars.length - 1) == i;
            if (!map.isWord() && isWord) {
                map.setWord(isWord);
            }
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
        return this.findSuggestions(prefix, Integer.MAX_VALUE, true);
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
        return this.findSuggestions(prefix, maxResults, true);
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
        return this.findSuggestions(prefix, Integer.MAX_VALUE, sorted);
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
    public List<String> findSuggestions(@NonNull String prefix, int maxResults, boolean sorted) {
       final List<String> list = new ArrayList<>();

        JaMap local = getLocationByPrefix(prefix);

        if (null == local) {
            return list;
        }

        Iterator<Character> it;
        Character c;
        Stack<JaSolution> stack = new Stack<>();
        JaSolution current = new JaSolution(local, prefix);
        stack.push(current);


        while(!stack.isEmpty() && list.size() <= maxResults) {
            current = stack.pop();

            if (current.getNode().isWord() && !current.getSolution().equals(prefix)) {
                list.add(current.getSolution());
            }

            it = current.getNode().keySet().iterator();

            while(it.hasNext()) {
                c = it.next();
                stack.push(new JaSolution(current.getNode().get(c), current.getSolution() + c));
            }
        }

        if (sorted) {
            sort(list);
        }

        return list;
    }

    private JaMap getLocationByPrefix(@NonNull String prefix) {
        char[] letters = prefix.toCharArray();
        JaMap local = this.nodes;
        for(char letter : letters) {
            local = local.get(letter);
            if (null == local) {
                return null;
            }
        }
        return local;
    }
}

@ToString
class JaMap extends java.util.HashMap<Character, JaMap> {
    @Getter @Setter private boolean word;
}

@Data
@AllArgsConstructor
class JaSolution {
    private JaMap node;
    private String solution;
}
