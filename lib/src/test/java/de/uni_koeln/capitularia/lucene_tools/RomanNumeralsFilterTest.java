package de.uni_koeln.capitularia.lucene_tools;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.tests.analysis.MockTokenizer;
import org.junit.Test;

/**
 * Unit test of {@link RomanNumeralsFilter}.
 */
public class RomanNumeralsFilterTest extends BaseTokenStreamTestCase {
    private Analyzer getAnalyzer(boolean preserveOriginal) {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                MockTokenizer source = new MockTokenizer(
                    MockTokenizer.WHITESPACE, false);
                return new TokenStreamComponents(
                    source, new RomanNumeralsFilter(source, preserveOriginal));
            }
        };
    }

    /**
     * Unit test of {@link RomanNumeralsFilter}.
     * @throws Exception
     */
    @Test
    @SuppressWarnings("checkstyle:linelength")
    public void testRomanNumeralsFilter() throws Exception {
        String input  = "I II III IV IIII V VI VII VIII IX X  XI XII XX XXX XL XLII L  LXIX XCIX C   CI  CMXCIX";
        String output = "1 2  3   4  4    5 6  7   8    9  10 11 12  20 30  40 42   50 69   99   100 101 999";

        assertAnalyzesTo(getAnalyzer(false), input, output.split("\\s+"));

        input  = "et    LX solidos componat";
        output = "et 60 LX solidos componat";

        assertAnalyzesTo(getAnalyzer(true), input, output.split("\\s+"));
    }
}
