# jasuggest

A simple autosuggest library based on a Java Trie implementation. 

```java
JaSuggest js =
        JaSuggest.from("use", "useless", "useful", "usa",
                       "usurper", "water", "gin", "soda",
                       "uzo");

List<String> result = js.findSuggestions("us");
```        
