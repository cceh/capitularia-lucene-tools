package de.uni_koeln.capitularia.lucene_tools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test of {@link LatinStemmer}.
 * <pre>
 * aquila      aquil       aquila
 * portat      portat      porta
 * portis      port        por
 * </pre>
 */
public class LatinStemmerTest {
    /**
     * Unit test of {@link LatinStemmer}.
     * @throws Exception
     */
    @Test
    public void testLatinStemmer() throws Exception {
        LatinStemmer stemmer = new LatinStemmer();

        // as nouns
        assertEquals("aquil",  stemmer.stemNoun("aquila"));
        assertEquals("portat", stemmer.stemNoun("portat"));
        assertEquals("port",   stemmer.stemNoun("portis"));
        assertEquals("",       stemmer.stemNoun("a"));
        // as verbs
        assertEquals("aquila", stemmer.stemVerb("aquila"));
        assertEquals("porta",  stemmer.stemVerb("portat"));
        assertEquals("por",    stemmer.stemVerb("portis"));
        assertEquals("a",      stemmer.stemVerb("a"));
    }
}
