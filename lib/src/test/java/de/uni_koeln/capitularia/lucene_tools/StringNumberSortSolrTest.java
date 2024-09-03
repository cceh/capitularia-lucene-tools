package de.uni_koeln.capitularia.lucene_tools;

import org.apache.solr.SolrTestCaseJ4;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test of {@link StringNumberSortValueSource}
 */
public class StringNumberSortSolrTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void beforeClass() throws Exception {
        // System.setProperty("solr.directoryFactory", "solr.ByteBuffersDirectoryFactory");
        initCore("solrconfig.xml", "schema.xml", getFile("configsets/collection1").getParent());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        assertU(delQ("*:*"));
        assertU(commit());
    }

    @Test
    public void test() {
        assertU(adoc("id", "paris-bn-lat-1"));
        assertU(adoc("id", "paris-bn-lat-11"));
        assertU(adoc("id", "paris-bn-lat-111"));
        assertU(adoc("id", "paris-bn-lat-1111"));
        assertU(adoc("id", "paris-bn-lat-2"));
        assertU(adoc("id", "paris-bn-lat-22"));
        assertU(adoc("id", "paris-bn-lat-222"));
        assertU(adoc("id", "paris-bn-lat-2222"));
        assertU(commit());

        assertQ(
            req("fl", "*,score", "q", "*:*", "sort", "strnumsort(id) asc"),
            "//*[@numFound='8']",
            "//result/doc[1]/str[@name='id'][.='paris-bn-lat-1']",
            "//result/doc[2]/str[@name='id'][.='paris-bn-lat-2']",
            "//result/doc[3]/str[@name='id'][.='paris-bn-lat-11']",
            "//result/doc[4]/str[@name='id'][.='paris-bn-lat-22']",
            "//result/doc[5]/str[@name='id'][.='paris-bn-lat-111']",
            "//result/doc[6]/str[@name='id'][.='paris-bn-lat-222']",
            "//result/doc[7]/str[@name='id'][.='paris-bn-lat-1111']",
            "//result/doc[8]/str[@name='id'][.='paris-bn-lat-2222']"
        );
    }
}
