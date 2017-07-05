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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;

public class TestUtils {

    public static final String ENGLISH_WORDS_TXT = "english_words.text";

    public static List<String> getEnglishWords() {
        ClassLoader loader = TestUtils.class.getClassLoader();
        List<String> result = new ArrayList<>();
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(ENGLISH_WORDS_TXT), defaultCharset()))) {
            for (String line = buff.readLine(); line != null; line = buff.readLine()) {
                result.add(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result;
    }

    public static boolean isStringListSorted(List<String> list) {
        String crt = "";
        for(String str : list) {
            if (crt.compareTo(str) > 0) {
                return false;
            }
            crt = str;
        }
        return true;
    }
}
