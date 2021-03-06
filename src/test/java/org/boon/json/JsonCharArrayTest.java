package org.boon.json;

import org.boon.IO;
import org.boon.json.implementation.JsonParserCharArray;
import org.junit.Test;

import java.util.Map;

import static org.boon.Str.lines;


/**
 * Created by rick on 12/12/13.
 */
public class JsonCharArrayTest extends JsonParserBaseTest {


    public JsonParser parser () {
        return new JsonParserCharArray ();
    }

    @Test
    public void testNest () {


        String nest = IO.read ( "files/nest.json" );

        this.jsonParser.parse ( Map.class, nest );


    }

    @Test
    public void noNest () {


        String json = IO.read ( "files/nonest.json" );

        this.jsonParser.parse ( Map.class, json );


    }


    @Test
    public void classic() {

            Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                    lines (

                            "{ \"nums\": [12, 12345678, 999.999, 123456789.99],\n " +
                                    "    \"nums2\": [12, 12345678, 999.999, 123456789.99],\n" +
                                    "    \"nums3\": [12, 12345678, 999.999, 123456789.99]\n" +
                                    "}"
                    )
            );

        }

}
