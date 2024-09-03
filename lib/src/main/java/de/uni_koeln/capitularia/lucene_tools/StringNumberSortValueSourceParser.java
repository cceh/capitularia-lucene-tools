package de.uni_koeln.capitularia.lucene_tools;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;


/**
 * Correctly sorts numbers in strings.
 * <p>
 * Put the {@code lib.jar} in your solr lib dir and then add the following line to the
 * {@code <config>} section of your {@code solrconfig.xml}:
 * <pre>
 * {@code <valueSourceParser name="strnumsort"
 *    class="de.uni_koeln.capitularia.lucene_tools.StringNumberSortValueSourceParser" />}
 */
public final class StringNumberSortValueSourceParser extends ValueSourceParser {
    /**
     * Parse the user input into a {@link StringNumberSortValueSource}.
     * @param fp a function parser for the argument to this function
     * @return a function parser for this function
     */
    public ValueSource parse(FunctionQParser fp) throws SyntaxError {
        return new StringNumberSortValueSource(fp.parseValueSource());
    }
}
