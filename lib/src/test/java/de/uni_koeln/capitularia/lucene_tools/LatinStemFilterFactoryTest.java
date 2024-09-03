package de.uni_koeln.capitularia.lucene_tools;

import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.tests.analysis.BaseTokenStreamFactoryTestCase;
import org.junit.Test;

/**
 * Unit test of {@link LatinStemFilterFactory}.
 */
public class LatinStemFilterFactoryTest extends BaseTokenStreamFactoryTestCase {
    /**
     * Test SPI loading of factory.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void testFactory() throws Exception {
        Reader reader = new StringReader("aquila");
        TokenStream stream = whitespaceMockTokenizer(reader);
        stream = tokenFilterFactory("latinStem").create(stream);
        assertTokenStreamContents(stream, new String[] {"aquila", "aquil", "aquila"});
    }

    /** Test that bogus arguments result in exception. */
    @Test
    public void testBogusArguments() {
        IllegalArgumentException expected =
            expectThrows(
                IllegalArgumentException.class,
                () -> {
                    tokenFilterFactory("latinStem", "bogusArg", "bogusValue");
                });
        assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
}
