package de.uni_koeln.capitularia.lucene_tools;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

/**
 * Stemmer for Latin.
 * <p>
 * This stemmer uses the algorithm by Schinke et al.
 * See: {@link LatinStemmer}
 */
public final class LatinStemFilter extends TokenFilter {
    public static final int DEFAULT_MIN_NOUN_SIZE = 2;
    public static final int DEFAULT_MIN_VERB_SIZE = 2;
    public static final boolean DEFAULT_PRESERVE_ORIGINAL = true;

    private static final Locale LOCALE =
        new Locale.Builder().setLanguageTag("la").build();

    private final LatinStemmer stemmer = new LatinStemmer();
    private final CharTermAttribute termAttr;
    private final KeywordAttribute keywordAttr;
    private final PositionIncrementAttribute posIncrAtt;

    private final int minNounSize;
    private final int minVerbSize;
    private final boolean preserveOriginal;
    private final Deque<String> stack = new ArrayDeque<>();
    private State state;

    protected LatinStemFilter(TokenStream input) {
        this(
            input,
            DEFAULT_MIN_NOUN_SIZE,
            DEFAULT_MIN_VERB_SIZE,
            DEFAULT_PRESERVE_ORIGINAL
        );
    }

    protected LatinStemFilter(
            TokenStream input,
            int minNounSize,
            int minVerbSize,
            boolean preserveOriginal
    ) {
        super(input);
        if (minNounSize < 1) {
            throw new IllegalArgumentException("minNounSize must be greater than zero");
        }
        if (minVerbSize < 1) {
            throw new IllegalArgumentException("minVerbSize must be greater than zero");
        }
        this.minNounSize = minNounSize;
        this.minVerbSize = minVerbSize;
        this.preserveOriginal = preserveOriginal;
        termAttr = addAttribute(CharTermAttribute.class);
        keywordAttr = addAttribute(KeywordAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        int extraIncrement = 0;
        while (true) {
            if (!stack.isEmpty()) {
                // if we have a stem left, return it
                String stem = stack.pop();
                restoreState(state);
                termAttr.copyBuffer(stem.toCharArray(), 0, stem.length());
                if (extraIncrement > 0) {
                    posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement()
                        + extraIncrement);
                }
                return true;
            }

            // get the next token
            if (!input.incrementToken()) {
                return false;
            }

            // do not stem tokens marked as keywords
            if (keywordAttr.isKeyword()) {
                return true;
            }

            state = captureState();

            // Convert all occurrences of the letters 'j' or 'v' to 'i' or 'u',
            // respectively.
            String input = termAttr.toString();
            input = input.toLowerCase(LOCALE);
            input = input.replace("j", "i");
            input = input.replace("v", "u");
            input = input.replace("k", "c");

            String noun = stemmer.stemNoun(input);
            String verb = stemmer.stemVerb(input);
            // store these for the next call
            if (verb.length() >= minVerbSize) {
                stack.push(verb);
            }
            if (noun.length() >= minNounSize) {
                stack.push(noun);
            }
            if (preserveOriginal) {
                stack.push(input);
            }

            if (stack.isEmpty()) {
                // if we pushed nothing, it means we skipped this word
                extraIncrement += posIncrAtt.getPositionIncrement();
            }
        }
    }
}
