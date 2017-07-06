## jasuggest

A simple autosuggest library based on a [Trie](https://en.wikipedia.org/wiki/Trie) implementation. 

## Simple Example

```java

String[] words = { "us", "usa", "use", "useful", "useless", "user", "usurper" };

// All the searches will be cached in a ConcurrentHashMap with a maximum size of 512 elements.
// Check: https://github.com/jhalterman/expiringmap
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

* Each blue node has an additional property that marks the existence of an word. So when the `findSuggestions()` method is called a tree traversal is performed in order to determine all the possible outcomes. When a "blue node" is visited the result is added to the map and the traversing operation continues;

* The library supports caching the results. 

## Simple Example - Increasing performance at the cost of memory consumption

The builder() method `prebuiltWords()` adds more information to the Trie. Basically each "blue node" that marks the existence of a word also contains the same word as a property:

![Image of the trie](https://github.com/nomemory/jasuggest/blob/master/media/Diagram2.png)

So the only change you need to make is when you create the `JaSuggest` object:

```java
JaSuggest jaSuggest = JaSuggest.builder()
                               .ignoreCase()
                               .prebuiltWords() // !HERE!
                               .withCache(jaCacheConfig)
                               .buildFrom(words);
```
