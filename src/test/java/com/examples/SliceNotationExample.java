package com.examples;


import org.boon.*;
import org.boon.primitive.Chr;
import org.junit.Test;

import java.util.*;


import static org.boon.Exceptions.die;
import static org.boon.Arrays.*;
import static org.boon.primitive.Byt.*;
import static org.boon.primitive.Chr.*;

/**
 */
public class SliceNotationExample {


    @Test
    public void test() {
        SliceNotationExample.main();
    }

    public static void main( String... args ) {
        collectionAndBasicTypes();
        strings();

    }

    private static void strings() {

        String letters = "abcd";

        boolean worked = true;

        worked &=

                Str.idx( letters, 0 ) == 'a'
                        || Exceptions.die( "0 index is equal to a" );


        worked &=

                Str.idx( letters, -1 ) == 'd'
                        || Exceptions.die( "-1 index is equal to a" );


        worked &=

                Str.idx( letters, letters.length() - 1 ) == 'd'
                        || Exceptions.die( "another way to express what the -1 means" );


        //We can modify too
        letters = Str.idx( letters, 1, 'z' );

        worked &=

                Str.idx( letters, 1 ) == 'z'
                        || Exceptions.die( "Set the 1 index of letters to 'z'" );


        worked &= (
                Str.in( 'a', letters ) &&
                        Str.in( 'z', letters )
        ) || Exceptions.die( "'z' is in letters and 'a' is in letters" );


        letters = "abcd";

        worked &=
                Str.slc( letters, 0, 2 ).equals( "ab" )
                        || Exceptions.die( "index 0 through index 2 is equal to \"ab\"" );


        worked &=
                Str.slc( letters, 1, -1 ).equals( "bc" )
                        || Exceptions.die( "index 1 through index (length -1) is equal to \"bc\"" );


        worked &=
                Str.slcEnd( letters, -2 ).equals( "ab" )
                        || Exceptions.die( "" );


        worked &=
                Str.slcEnd( letters, 2 ).equals( "ab" )
                        || Exceptions.die( "" );

    }

    private static void collectionAndBasicTypes() {
        //Works with lists, arrays, sets, maps, sorted maps, etc.
        List<String> fruitList;
        String[] fruitArray;
        Set<String> veggiesSet;
        char[] letters;
        byte[] bytes;
        NavigableMap<Integer, String> favoritesMap;
        Map<String, Integer> map;

        // These helper methods are used to create common Java types.
        // Sets and lists have concurrent and non concurrent variants
        // Set also has sorted and non sorted variants
        // This makes safeList, listStream, set, sortedSet, safeSet, safeSortedSet
        veggiesSet = Sets.sortedSet( "salad", "broccoli", "spinach" );
        fruitList = Lists.list( "apple", "oranges", "pineapple" );
        fruitArray = array( "apple", "oranges", "pineapple" );
        letters = Chr.array( 'a', 'b', 'c' );
        bytes = array( new byte[]{ 0x1, 0x2, 0x3, 0x4 } );

        //You addObject up name / value pairs as a pseudo literal for map
        favoritesMap = Maps.sortedMap(
                2, "pineapple",
                1, "oranges",
                3, "apple"
        );


        // You addObject up name / value pairs as a pseudo literal for map
        // map, sortedMap, safeMap (thread safe concurrent), and sortedSafeMap are
        // supported.
        map = Maps.map(
                "pineapple", 2,
                "oranges", 1,
                "apple", 3
        );


        // Getting the length
        assert Sets.len( veggiesSet ) == 3;
        assert Lists.len( fruitList ) == 3;
        assert len( fruitArray ) == 3;
        assert len( letters ) == 3;
        assert len( bytes ) == 4;
        assert Maps.len( favoritesMap ) == 3;
        assert Maps.len( map ) == 3;


        //Using idx to access a value.

        assert Sets.idx( veggiesSet, "b" ).equals( "broccoli" );

        assert Lists.idx( fruitList, 1 ).equals( "oranges" );

        assert idx( fruitArray, 1 ).equals( "oranges" );

        assert idx( letters, 1 ) == 'b';

        assert idx( bytes, 1 ) == 0x2;

        assert Maps.idx( favoritesMap, 2 ).equals( "pineapple" );

        assert Maps.idx( map, "pineapple" ) == 2;


        //Negative indexes
        assert Lists.idx( fruitList, -2 ).equals( "oranges" );

        assert idx( fruitArray, -2 ).equals( "oranges" );

        assert idx( letters, -2 ) == 'b';

        assert idx( bytes, -3 ) == 0x2;
    }
}
