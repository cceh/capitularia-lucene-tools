package de.uni_koeln.capitularia.lucene_tools;

import java.util.Map;
import java.util.Set;

/**
 * Latin stemmer using an algorithm by Schinke et al.
 * <p>
 * See: Schinke R, Greengrass M, Robertson AM and Willett P (1996)
 *      <i>A stemming algorithm for Latin text databases.</i>
 *      Journal of Documentation, 52: 172-187.
 * <p>
 * See: https://snowballstem.org/otherapps/schinke/
 */
public class LatinStemmer {
    /**
     * List of words from which the enclitic suffix '-que' should not be removed.
     */
    private Set<String> figure4 = Set.of("""
        atque quoque neque itaque absque apsque abusque adaeque adusque
        denique deque susque oblique peraeque plenisque quandoque quisque
        quaeque cuiusque cuique quemque quamque quaque quique
        quorumque quarumque quibusque quosque quasque quotusquisque
        quousque ubique undique usque uterque utique utroque utribique
        torque coque concoque contorque detorque decoque excoque extorque
        obtorque optorque retorque recoque attorque incoque intorque
        praetorque
        """.split("\\s+"));

    /** The list of suffixes to remove from nouns / adjectives. */
    private String[] figure6a = """
        ibus ius ae am as em es ia
        is   nt  os ud um us a  e
        i    o   u
        """.split("\\s+");

    /** The list of suffixes to remove or replace from verbs. */
    private String[] figure6b = """
        iuntur beris erunt untur iunt mini ntur stis
        bor    ero   mur   mus   ris  sti  tis  tur
        unt    bo    ns    nt    ri   m    r    s
        t
        """.split("\\s+");

    /** Verb suffixes to transform rather than remove. */
    private Map<String, String> figure6b2 = Map.of(
        "iuntur", "i",
        "erunt",  "i",
        "untur",  "i",
        "iunt",   "i",
        "unt",    "i",
        "beris",  "bi",
        "bor",    "bi",
        "bo",     "bi",
        "ero",    "eri"
    );

    private String cutSuffix(String input, int len) {
        return input.substring(0, input.length() - len);
    }

    /**
     * Stems a latin word with the noun algorithm.
     * <p>
     * Expects a lowercased latin word with all occurrences of the letters 'j' or 'v'
     * converted to to 'i' or 'u' respectively.
     *
     * @param input the word to stem
     * @return the noun stem or the original input
     */
    String stemNoun(String input) {
        /* 3. If the word ends in '-que' then
                if the word is on the list shown in Figure 4, then
                    write the original word to both the noun-based and verb-based
                    stem dictionaries and go to 8.
                else remove '-que'
        */
        if (input.endsWith("que")) {
            if (figure4.contains(input)) {
                return input;
            }
            input = cutSuffix(input, 3);
        }

        /* Match the end of the word against the suffix list show in Figure 6(a),
           removing the longest matching suffix, (if any). */

        for (String suffix : figure6a) {
            if (input.endsWith(suffix)) {
                input = cutSuffix(input, suffix.length());
                break;
            }
        }
        return input;
    }

    /**
     * Stems a latin word with the verb algorithm.
     * <p>
     * Expects a lowercased latin word with all occurrences of the letters 'j' or 'v'
     * converted to to 'i' or 'u' respectively.
     *
     * @param input the word to stem
     * @return the verb stem or the original input
     */
    String stemVerb(String input) {
        /* 3. If the word ends in '-que' then
                if the word is on the list shown in Figure 4, then
                    write the original word to both the noun-based and verb-based
                    stem dictionaries and go to 8.
                else remove '-que'
        */
        if (input.endsWith("que")) {
            if (figure4.contains(input)) {
                return input;
            }
            input = cutSuffix(input, 3);
        }

        /*  Match the end of the word against the suffix list show in Figure 6(b),
            identifying the longest matching suffix, (if any).

            If any of the following suffixes are found then convert them as shown:

                '-iuntur', '-erunt', '-untur', '-iunt', and '-unt' to '-i';
                '-beris', '-bor', and '-bo'                        to '-bi';
                '-ero'                                             to '-eri'

            else remove the suffix in the normal way.
        */

        for (String suffix : figure6b) {
            if (input.endsWith(suffix)) {
                input = cutSuffix(input, suffix.length());
                String repl = figure6b2.get(suffix);
                if (repl != null) {
                    input = input + repl;
                }
                break;
            }
        }

        return input;
    }
}
