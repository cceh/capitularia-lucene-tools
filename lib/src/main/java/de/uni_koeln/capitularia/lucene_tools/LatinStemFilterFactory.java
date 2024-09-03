package de.uni_koeln.capitularia.lucene_tools;

import java.util.Map;

import org.apache.lucene.analysis.TokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;

/**
 * Factory for {@link LatinStemFilter}.
 *
 * <pre class="prettyprint">
 * &lt;fieldType name="text_la_stem" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;
 *     &lt;filter class="solr.LowerCaseFilterFactory"/&gt;
 *     &lt;filter class="de.uni_koeln.capitularia.lucene_tools.LatinStemFilterFactory"
 *             preserveOriginal="true" minNounSize="3" minVerbSize="3"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 *
 * @lucene.spi {@value #NAME}
 */
public final class LatinStemFilterFactory extends TokenFilterFactory {

    /** The SPI name for this filter. */
    public static final String NAME = "latinStem";

    /**
     * If true outputs the original string along with the stems.
     */
    private final boolean preserveOriginal;
    /**
     * Minimum length for a noun stem to be output.
     * <p>A high value turns off the output of noun stems completely.
     */
    private final int minNounSize;
    /**
     * Minimum length for a verb stem to be output.
     * <p>A high value turns off the output of verb stems completely.
     */
    private final int minVerbSize;

    /**
     * Creates a new LatinStemFilterFactory.
     * @param args map of arguments to the stemmer
     */
    public LatinStemFilterFactory(Map<String, String> args) {
        super(args);
        minNounSize = getInt(args, "minNounSize",
            LatinStemFilter.DEFAULT_MIN_NOUN_SIZE);
        minVerbSize = getInt(args, "minVerbSize",
            LatinStemFilter.DEFAULT_MIN_VERB_SIZE);
        preserveOriginal = getBoolean(args, "preserveOriginal",
            LatinStemFilter.DEFAULT_PRESERVE_ORIGINAL);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    /** Default ctor for compatibility with SPI. */
    public LatinStemFilterFactory() {
        throw defaultCtorException();
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new LatinStemFilter(input, minNounSize, minVerbSize, preserveOriginal);
    }
}
