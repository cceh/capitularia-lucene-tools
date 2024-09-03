package de.uni_koeln.capitularia.lucene_tools;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.lucene.queries.function.valuesource.LiteralValueSource;
import org.junit.Test;

public class StringNumberSortValueSourceTest {
    void test(String input, String expected) throws IOException {
        StringNumberSortValueSource s = new StringNumberSortValueSource(new LiteralValueSource(input));
        assertEquals(expected, s.getValues(null, null).strVal(0));
    }

    @Test
    public void tests() throws IOException {
        test("",      "");

        test("1",     "11");
        test("20",    "220");
        test("300",   "3300");
        test("4000",  "44000");

        test("A",     "A");
        test("A1",    "A11");
        test("A2",    "A12");
        test("A10",   "A210");
        test("A11",   "A211");
        test("A100",  "A3100");
        test("A1000", "A41000");

        test("1B",    "11B");
        test("20B",   "220B");

        test("A1B2C3D",  "A11B12C13D");
        test("A1B3C3D",  "A11B13C13D");
        test("A1B20C3D", "A11B220C13D");
    }
}
