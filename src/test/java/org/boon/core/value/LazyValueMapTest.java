package org.boon.core.value;

import org.boon.IO;
import org.boon.core.Value;
import org.boon.json.JsonParser;
import org.boon.json.implementation.JsonFastParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;

public class LazyValueMapTest {

    int leafCount;
    int mapCount;
    int collectionCount;
    int integerCount;
    int longCount;
    int doubleCount;
    int stringCount;
    int dateCount;
    int nullCount;
    int listCount;
    int booleanCount;

    @Before
    public void setUp() throws Exception {


        leafCount = 0;
        mapCount = 0;
        collectionCount = 0;
        integerCount = 0;
        longCount = 0;
        doubleCount = 0;
        stringCount = 0;
        dateCount = 0;
        nullCount = 0;
        listCount = 0;
        booleanCount = 0;
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void test() {

        List<String> files  = IO.listByFileExtension ( "./files/", "json" );

        for ( String file : files) {
            puts ( file );

            JsonParser parser = new JsonFastParser ();

            Object object  = parser.parseFile ( Object.class, file.toString () );


            walkObject( object );

        }

        puts ( "leaf", leafCount, "map", mapCount, "collection", collectionCount );
        puts ( "integer", integerCount, "long", longCount, "double", doubleCount, "boolean", booleanCount );
        puts ( "string", stringCount, "date", dateCount, "null", nullCount );

    }


    @Test
    public void testGetWalk() {



        List<String> files  = IO.listByFileExtension ( "./files/", "json" );

        for ( String file : files) {
            puts ( file );

            JsonParser parser = new JsonFastParser ();

            Object object  = parser.parseFile ( Object.class, file.toString () );


            walkGetObject( object );

        }

        puts ( "leaf", leafCount, "map", mapCount, "list", listCount );
        puts ( "integer", integerCount, "long", longCount, "double", doubleCount );
        puts ( "string", stringCount, "date", dateCount, "null", nullCount );

    }

    private void walkMap( Map map ) {
        mapCount++;
        Set<Map.Entry<String, Object>> entries = map.entrySet ();

        for ( Map.Entry<String, Object> entry : entries ) {
            Object object = entry.getValue ();
            walkObject ( object );
        }

    }


    private void walkGetMap( Map map ) {
        mapCount++;
        Set<Map.Entry<String, Object>> entries = map.entrySet ();

        for ( Map.Entry<String, Object> entry : entries ) {
            walkGetObject ( map.get ( entry.getKey () ) );
        }

        map.size ();

    }
    private void walkObject( Object object ) {
        leafCount++;
        if ( object instanceof Value ) {
            die ( "Found a value" );
        } else if ( object instanceof Map ) {
            walkMap ( ( Map ) object );
        } else if ( object instanceof Collection ) {
            walkCollection ( ( Collection ) object );
        } else if ( object instanceof Long ) {
            longCount++;
        } else if ( object instanceof Integer ) {
            integerCount++;
        } else if ( object instanceof Double ) {
            doubleCount++;
        } else if ( object instanceof Boolean ) {
            booleanCount++;
        } else if ( object instanceof String ) {
            stringCount++;
        } else if ( object instanceof Date ) {
            dateCount++;
        } else if ( object == null ) {
            nullCount++;
        } else {
            die ( sputs ( object, object.getClass ().getName () ) );
        }
    }

    private void walkGetObject( Object object ) {
        leafCount++;
        if ( object instanceof Value ) {
            die ( "Found a value" );
        } else if ( object instanceof Map ) {
            walkGetMap ( ( Map ) object );
        } else if ( object instanceof List ) {
            walkGetList ( ( List ) object );
        } else if ( object instanceof Long ) {
            longCount++;
        } else if ( object instanceof Integer ) {
            integerCount++;
        } else if ( object instanceof Boolean ) {
            booleanCount++;
        }  else if ( object instanceof Double ) {
            doubleCount++;
        } else if ( object instanceof String ) {
            stringCount++;
        } else if ( object instanceof Date ) {
            dateCount++;
        } else if ( object == null ) {
            nullCount++;
        } else {
            die ( sputs ( object, object.getClass ().getName () ) );
        }
    }



    private void walkGetList( List c ) {
        listCount++;
        for ( int index = 0; index < c.size (); index++ ) {
            walkGetObject ( c.get ( index ) );
        }

        c.size();
    }

    private void walkCollection( Collection c ) {
        collectionCount++;
        for ( Object o : c ) {
            walkObject ( o );
        }
    }
}
