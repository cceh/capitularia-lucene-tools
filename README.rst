Capitularia Lucene Tools
========================

This project contains:

- a stemmer for the Latin language,
- a filter that converts roman numerals into arabic ones, and
- a value source that correctly sorts strings with numbers.

The Latin stemmer uses an algorithm by Schinke et al.

.. seealso::

    Schinke R, Greengrass M, Robertson AM and Willett P (1996)
    :title:`A stemming algorithm for Latin text databases.`
    Journal of Documentation, 52: 172-187.

    https://snowballstem.org/otherapps/schinke/

The filter will convert roman "XLII" to arabic "42".

The value source generates a string that can be used as a key to sort strings correctly
like this:

#. paris-bn-lat-4638
#. paris-bn-lat-10528

instead of alphabetically, like this:

#. paris-bn-lat-10528
#. paris-bn-lat-4638
