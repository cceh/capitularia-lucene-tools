package de.uni_koeln.capitularia.lucene_tools;

import java.util.Map;

import org.apache.lucene.analysis.TokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;

/**
 * Factory for {@link RomanNumeralsFilter}.
 *
 * <pre class="prettyprint">
 * &lt;fieldType name="text_la_stem" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;
 *     &lt;filter class="solr.LowerCaseFilterFactory"/&gt;
 *     &lt;filter class="de.uni_koeln.capitularia.lucene_tools.RomanNumeralsFilterFactory"
 *             preserveOriginal="true"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 *
 * @lucene.spi {@value #NAME}
 */
public final class RomanNumeralsFilterFactory extends TokenFilterFactory {

    /** The SPI name for this filter. */
    public static final String NAME = "romanNumerals";

    /**
     * If true outputs the original string along with the stems.
     */
    private final boolean preserveOriginal;

    /**
     * Creates a new RomanNumeralsFilterFactory.
     * @param args map of arguments to the stemmer
     */
    public RomanNumeralsFilterFactory(Map<String, String> args) {
        super(args);
        preserveOriginal = getBoolean(args, "preserveOriginal",
            RomanNumeralsFilter.DEFAULT_PRESERVE_ORIGINAL);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    /** Default ctor for compatibility with SPI. */
    public RomanNumeralsFilterFactory() {
        throw defaultCtorException();
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new RomanNumeralsFilter(input, preserveOriginal);
    }
}
