package de.uni_koeln.capitularia.lucene_tools;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.tests.analysis.MockTokenizer;
import org.junit.Test;

/**
 * Unit test of {@link LatinStemFilter}.
 */
public class LatinStemFilterTest extends BaseTokenStreamTestCase {
    private Analyzer getAnalyzer(final boolean preserveOriginal) {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                MockTokenizer source = new MockTokenizer(
                    MockTokenizer.WHITESPACE, false);
                return new TokenStreamComponents(
                    source, new LatinStemFilter(source, 3, 3, preserveOriginal));
            }
        };
    }

    /**
     * Unit test of {@link LatinStemFilter}.
     * @throws Exception
     */
    @Test
    public void testLatinStemFilter() throws Exception {
        String input =
            """
            apparebunt aquila colluxisset deprehendebatur dexisse
            ducibus ducimus elucidatione fratre fratrem fratres fratri fratrum
            portat portis
            """;

        assertAnalyzesTo(getAnalyzer(true), input, new String[] {
            "apparebunt",       "apparebu", /*!*/   "apparebi",
            "aquila",           "aquil",            "aquila",
            "colluxisset",      "colluxisset",      "colluxisse",   /*!*/
            "deprehendebatur",  "deprehendebatur",  "deprehendeba", /*!*/
            "dexisse",          "dexiss",           "dexisse",      /*!*/
            "ducibus",          "duc",              "ducibu",
            "ducimus",          "ducim",            "duci",
            "elucidatione",     "elucidation",      "elucidatione",
            "fratre",           "fratr",            "fratre",
            "fratrem",          "fratr",            "fratre",
            "fratres",          "fratr",            "fratre",
            "fratri",           "fratr",            "frat", /*!*/
            "fratrum",          "fratr",            "fratru",
            "portat",           "portat",           "porta",
            "portis",           "port",             "por"
        });

        input = "aquila et portat portis";

        assertAnalyzesTo(getAnalyzer(true), input, new String[] {
            "aquila",      "aquil",       "aquila",
            "et",
            "portat",      "portat",      "porta",
            "portis",      "port",        "por"
        });

        assertAnalyzesTo(getAnalyzer(false), input, new String[] {
            "aquil",  "aquila",
            "portat", "porta",
            "port",   "por"
        });
    }
}
