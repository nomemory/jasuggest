## jasuggest

A simple autosuggest library based on a [Trie](https://en.wikipedia.org/wiki/Trie) implementation. 

## Simple Example

```java

String[] words = { "us", "usa", "use", "useful", "useless", "user", "usurper" };

// All the searches will be cached in a ConcurrentHashMap with a maximum size of 512 elements.
// For this cache I have used the Expiring Map library from:
// https://github.com/jhalterman/expiringmap
JaCacheConfig jaCacheConfig =
                JaCacheConfig.builder()
                             .maxSize(512)
                             .build();

JaSuggest jaSuggest = JaSuggest.builder()
                               .ignoreCase()
                               .withCache(jaCacheConfig)
                               .buildFrom(words);
                               
List<String> result = jaSuggest.findSuggestions("use");

// [useful, useless, user]
```        

The above example creates internally creates a Trie based on the supplied `words`. In memory the Trie looks like:

![Image of the trie](https://github.com/nomemory/jasuggest/blob/master/media/Diagram.png)

**Notes:** 

* Each blue node has an additional property that marks the existence of an word. So when the `findSuggestions()` method is called a tree traversal is performed in order to determine all the possible outcomes. When a "blue node" is visited the result is added to the map.


