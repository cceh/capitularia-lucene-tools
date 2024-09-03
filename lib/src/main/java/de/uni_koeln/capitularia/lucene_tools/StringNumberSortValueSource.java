package de.uni_koeln.capitularia.lucene_tools;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.StrDocValues;
import org.apache.lucene.queries.function.valuesource.SingleFunction;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Pruning;
import org.apache.lucene.search.SimpleFieldComparator;
import org.apache.lucene.search.SortField;

/**
 * Correctly sorts numbers in strings
 * <p>
 * This function replaces numbers in a string so that the string will sort correctly.
 * It does this by prepending the length of the number to the number itself, like this:
 *
 * <pre>
 *   "A1"   => "A11"
 *   "A2"   => "A12"
 *   "A10"  => "A210"
 *   "A100" => "A3100"
 * </pre>
 */
public final class StringNumberSortValueSource extends SingleFunction {
    /** The name of the value source */
    public static final String NAME = "strnumsort";
    /** The regex pattern used to find the numbers to convert */
    public static final Pattern REGEX = Pattern.compile("\\d+");

    /**
     * Constructor.
     * @param source the value source for this function
     */
    public StringNumberSortValueSource(ValueSource source) {
        super(source);
    }

    @Override
    protected String name() {
      return NAME;
    }

    /**
     * Replaces the numbers with keys that sort correctly.
     * @param doc The document
     * @param vals The function values
     * @return The string with numbers replaced
     * @throws IOException If anything bad happened in strVal
     */
    protected String func(int doc, FunctionValues vals) throws IOException {
        String s = vals.strVal(doc);
        return REGEX.matcher(s).replaceAll(mr -> mr.group().length() + mr.group());
    }

    @Override
    public FunctionValues getValues(Map<Object, Object> context, LeafReaderContext readerContext)
            throws IOException {
        final FunctionValues vals = source.getValues(context, readerContext);

        return new StrDocValues(this) {
            @Override
            public String strVal(int doc) throws IOException {
                return func(doc, vals);
            }

            @Override
            public boolean exists(int doc) throws IOException {
                return vals.exists(doc);
            }
        };
    }

    @Override
    public SortField getSortField(boolean reverse) {
        return new ValueSourceSortField(reverse);
    }

    class ValueSourceSortField extends SortField {
        ValueSourceSortField(boolean reverse) {
            super(description(), SortField.Type.REWRITEABLE, reverse);
        }

        @Override
        public SortField rewrite(IndexSearcher searcher) throws IOException {
            Map<Object, Object> context = newContext(searcher);
            createWeight(context, searcher);
            return new SortField(
                getField(),
                new ValueSourceComparatorSource(context),
                getReverse()
            );
        }
    }

    class ValueSourceComparatorSource extends FieldComparatorSource {
        private final Map<Object, Object> context;

        ValueSourceComparatorSource(Map<Object, Object> context) {
            this.context = context;
        }

        @Override
        public FieldComparator<String> newComparator(
            String fieldname, int numHits, Pruning pruning, boolean reversed) {
            return new ValueSourceComparator(context, numHits);
        }
    }

    /**
     * Implement a {@link org.apache.lucene.search.FieldComparator} that works off of the {@link
     * FunctionValues} for a ValueSource instead of the normal Lucene FieldComparator that works off
     * of a FieldCache.
     */
    class ValueSourceComparator extends SimpleFieldComparator<String> {
        private final Map<Object, Object> fcontext;
        private final String[] values;
        private String bottom;
        private String topValue;
        private FunctionValues docVals;

        ValueSourceComparator(Map<Object, Object> fcontext, int numHits) {
            this.fcontext = fcontext;
            values = new String[numHits];
        }

        @Override
        public int compare(int slot1, int slot2) {
            return compareValues(values[slot1], values[slot2]);
        }

        @Override
        public int compareBottom(int doc) throws IOException {
            return compareValues(bottom, docVals.strVal(doc));
        }

        @Override
        public int compareTop(int doc) throws IOException {
            return compareValues(topValue, docVals.strVal(doc));
        }

        @Override
        public void copy(int slot, int doc) throws IOException {
            values[slot] = docVals.strVal(doc);
        }

        @Override
        public void doSetNextReader(LeafReaderContext context) throws IOException {
            docVals = getValues(fcontext, context);
        }

        @Override
        public void setBottom(final int bottom) {
            this.bottom = values[bottom];
        }

        @Override
        public void setTopValue(final String value) {
            this.topValue = value;
        }

        @Override
        public String value(int slot) {
            return values[slot];
        }
    }
}
