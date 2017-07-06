## jasuggest

A simple autosuggest library based on a [Trie](https://en.wikipedia.org/wiki/Trie) implementation. 

## Simple Example

```java

String[] words = { "us", "usa", "use", "useful", "useless", "user", "usurper" };

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

// [useful, useless, user]
```        

The above example creates internally creates a Trie based on the supplied `words`. In memory the Trie looks like:

[[https://github.com/nomemory/jasuggest/blob/master/media/Diagram.png]]
