package de.uni_koeln.capitularia.lucene_tools;

import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.tests.analysis.BaseTokenStreamFactoryTestCase;
import org.junit.Test;

/**
 * Unit test of {@link LatinStemFilterFactory}.
 */
public class RomanNumeralsFilterFactoryTest extends BaseTokenStreamFactoryTestCase {
    /**
     * Test SPI loading of factory.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void testFactory() throws Exception {
        Reader reader = new StringReader("XLII");
        TokenStream stream = whitespaceMockTokenizer(reader);
        stream = tokenFilterFactory("romanNumerals").create(stream);
        assertTokenStreamContents(stream, new String[] {"42"});
    }

    /** Test that bogus arguments result in exception. */
    @Test
    public void testBogusArguments() {
        IllegalArgumentException expected =
            expectThrows(
                IllegalArgumentException.class,
                () -> tokenFilterFactory("romanNumerals", "bogusArg", "bogusValue")
            );
        assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
}
