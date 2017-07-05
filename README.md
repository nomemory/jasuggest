# jasuggest

A simple autosuggest library based on a [Trie](https://en.wikipedia.org/wiki/Trie) implementation. 

```java
JaSuggest js =
        JaSuggest.from("use", "useless", "useful", "usa",
                       "usurper", "water", "gin", "soda",
                       "uzo");

List<String> result = js.findSuggestions("us");

// OUTPUT: [usa, use, useful, useless, usurper]
```        
