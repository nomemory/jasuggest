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

import org.junit.Test;

import java.util.List;

import static net.andreinc.jasuggest.TestUtils.getEnglishWords;
import static net.andreinc.jasuggest.TestUtils.isStringListSorted;
import static org.junit.Assert.assertTrue;

public class JaSuggestNoCacheTest {

    private static final List<String> ENGLISH_WORDS = getEnglishWords();

    @Test
    public void testFindSuggestionsEmptyList() throws Exception {
        JaSuggest jaSuggest = JaSuggest.builder().buildFrom();

        assertTrue(jaSuggest.findSuggestions("") != null);
        assertTrue(jaSuggest.findSuggestions("").size()==0);

        assertTrue(jaSuggest.findSuggestions("", false) != null);
        assertTrue(jaSuggest.findSuggestions("", false).size() == 0);

        assertTrue(jaSuggest.findSuggestions("", 100) != null);
        assertTrue(jaSuggest.findSuggestions("", 100).size()==0);
    }

    @Test
    public void testFindSuggestionsCorrectResults() throws Exception {

        JaSuggest jaSuggest = JaSuggest.builder().buildFrom(ENGLISH_WORDS);

        List<String> resultAB = jaSuggest.findSuggestions("ab");

        for(String suggestion : resultAB) {
            assertTrue(suggestion.startsWith("ab"));
        }

        assertTrue(isStringListSorted(resultAB));
    }

    @Test
    public void testFindSuggestionsCorrectResultsNotSorted() throws Exception {
        JaSuggest jaSuggest = JaSuggest.builder().buildFrom(ENGLISH_WORDS);

        List<String> resultABNotSorted = jaSuggest.findSuggestions("ab");

        for(String suggestion: resultABNotSorted) {
            assertTrue(suggestion.startsWith("ab"));
        }
    }

    @Test
    public void testFindSuggestionsCorrectResultsMaxSize() throws Exception {
        JaSuggest jaSuggest = JaSuggest.builder().buildFrom(ENGLISH_WORDS);

        List<String> resultABMax = jaSuggest.findSuggestions("ab", 10);

        assertTrue(resultABMax.size() == 10);

        for(String suggestion : resultABMax) {
            assertTrue(suggestion.startsWith("ab"));
        }

        assertTrue(isStringListSorted(resultABMax));
    }
}
