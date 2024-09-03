package de.uni_koeln.capitularia.lucene_tools;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

/**
 * Roman numerals filter.
 * <p>
 * This filter replaces roman numerals with arabic ones, eg. "XLII" => "42"
 */
public final class RomanNumeralsFilter extends TokenFilter {
    public static final boolean DEFAULT_PRESERVE_ORIGINAL = false;

    private final CharTermAttribute termAttr;
    private final KeywordAttribute keywordAttr;

    private final RomanNumerals romanNumerals = new RomanNumerals();
    private final boolean preserveOriginal;
    private String original;
    private State state;

    protected RomanNumeralsFilter(TokenStream input) {
        this(
            input,
            DEFAULT_PRESERVE_ORIGINAL
        );
    }

    protected RomanNumeralsFilter(TokenStream input, boolean preserveOriginal) {
        super(input);
        this.preserveOriginal = preserveOriginal;
        this.original = null;
        termAttr = addAttribute(CharTermAttribute.class);
        keywordAttr = addAttribute(KeywordAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            if (original != null) {
                restoreState(state);
                termAttr.copyBuffer(original.toCharArray(), 0, original.length());
                original = null;
                return true;
            }

            // get the next token
            if (!input.incrementToken()) {
                return false;
            }

            // skip tokens marked as keywords
            if (keywordAttr.isKeyword()) {
                return true;
            }

            state = captureState();

            String input = termAttr.toString();
            String roman = input.toUpperCase();
            if (romanNumerals.isRomanNumeral(roman)) {
                String arabic = romanNumerals.toArabic(roman).toString();
                termAttr.copyBuffer(arabic.toCharArray(), 0, arabic.length());
                if (preserveOriginal) {
                    original = input;
                }
            }
            return true;
        }
    }
}
