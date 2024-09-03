Capitularia Lucene Tools
========================

This project contains:

- a stemmer for the Latin language,
- a filter that converts roman numerals into arabic ones, and
- a value source that correctly sorts strings with numbers.

Latin Stem Filter
-----------------

Usage example in :code:`conf/schema.xml`:

.. code-block:: xml

    <fieldType name="text_la_stem" class="solr.TextField" positionIncrementGap="100">
        <analyzer>
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.LowerCaseFilterFactory"/>
            <filter class="de.uni_koeln.capitularia.lucene_tools.LatinStemFilterFactory"
                    preserveOriginal="true" minNounSize="3" minVerbSize="3"/>
        </analyzer>
    </fieldType>

The stemmer uses an algorithm by Schinke et al.

See:

    Schinke R, Greengrass M, Robertson AM and Willett P (1996)
    :title:`A stemming algorithm for Latin text databases.`
    Journal of Documentation, 52: 172-187.

    https://snowballstem.org/otherapps/schinke/


Roman Numerals Filter
---------------------

The filter will convert roman :code:`XLII` to arabic :code:`42`.

Usage example in :code:`conf/schema.xml`:

.. code-block:: xml

    <fieldType name="text_la_stem" class="solr.TextField" positionIncrementGap="100">
        <analyzer>
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.LowerCaseFilterFactory"/>
            <filter class="de.uni_koeln.capitularia.lucene_tools.RomanNumeralsFilterFactory"
                    preserveOriginal="true"/>
        </analyzer>
    </fieldType>


Sorting Value Source
--------------------

The value source generates strings that sort correctly when used as keys, like this:

#. paris-bn-lat-4638
#. paris-bn-lat-10528

instead of alphabetically, like this:

#. paris-bn-lat-10528
#. paris-bn-lat-4638

Usage example in :code:`conf/solrconfig.xml`:

.. code-block:: xml

    <config>
        <valueSourceParser
            name="strnumsort"
            class="de.uni_koeln.capitularia.lucene_tools.StringNumberSortValueSourceParser"
        />
        ...
    </config>

In the query set the :code:`sort` parameter to: :code:`strnumsort(my_alphanum_id) asc`
