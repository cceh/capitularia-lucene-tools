package de.uni_koeln.capitularia.lucene_tools;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to convert roman numerals into integers.
 */
public class RomanNumerals {
    private final Pattern regexRomans;
    private final Pattern regexRoman;
    private final Map<String, Integer> mapRomans = new HashMap<>();

    /**
     * Constructor.
     */
    public RomanNumerals() {
        String[] romans  = "M    CM  D   CD  C   XC L  XL X  IX U V IU IV I".split("\\s+");
        String[] arabics = "1000 900 500 400 100 90 50 40 10 9  5 5 4  4  1".split("\\s+");
        int index = 0;
        for (String roman : romans) {
            mapRomans.put(roman, Integer.valueOf(arabics[index++]));
        }
        regexRomans = Pattern.compile(String.join("|", romans), Pattern.CASE_INSENSITIVE);
        regexRoman  = Pattern.compile("^[MDCLXUVI]+$", Pattern.CASE_INSENSITIVE);
    }

    /**
     * Test if the input string is a roman numeral.
     * @param roman the input string
     * @return true if the input string is a roman numeral
     */
    public boolean isRomanNumeral(String roman) {
        return regexRoman.matcher(roman).matches();
    }

    /**
     * Converts a roman numeral into an Integer.
     * @param roman the roman numeral
     * @return the numeral as integer
     */
    public Integer toArabic(String roman) {
        Matcher m = regexRomans.matcher(roman);
        Integer arabic = 0;
        while (m.find()) {
            arabic += mapRomans.get(m.group());
        }
        return arabic;
    }
}
