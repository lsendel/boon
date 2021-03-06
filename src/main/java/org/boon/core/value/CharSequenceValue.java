package org.boon.core.value;

import org.boon.core.Conversions;
import org.boon.core.Dates;
import org.boon.Exceptions;
import org.boon.core.Type;
import org.boon.core.Value;
import org.boon.core.reflection.FastStringUtils;
import org.boon.json.JsonException;
import org.boon.json.implementation.JsonStringDecoder;
import org.boon.primitive.CharScanner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.primitive.CharScanner.*;

public class CharSequenceValue implements Value, CharSequence {

    private char[] buffer;
    private boolean checkDate;
    private boolean chopped;
    private int startIndex;
    private int endIndex;
    private Type type;
    private boolean decodeStrings;
    private Object value;



    public CharSequenceValue() {

    }

    public CharSequenceValue( boolean chop, Type type, int startIndex, int endIndex, char[] buffer ) {
        this.type = type;


        try {
            if ( chop ) {

                this.buffer = Arrays.copyOfRange ( buffer, startIndex, endIndex );
                this.startIndex = 0;
                this.endIndex = this.buffer.length;
                chopped = true;
            } else {
                this.startIndex = startIndex;
                this.endIndex = endIndex;
                this.buffer = buffer;
            }
        } catch ( Exception ex ) {
            puts ( "exception", ex, "start", startIndex, "end", endIndex );
            Exceptions.handle ( ex );

        }
    }


    public CharSequenceValue( boolean chop, Type type, int startIndex, int endIndex, char[] buffer,
                              boolean encoded, boolean checkDate ) {
        this.type = type;
        this.checkDate = checkDate;

        try {
            if ( chop ) {


                this.buffer = Arrays.copyOfRange ( buffer, startIndex, endIndex );
                this.startIndex = 0;
                this.endIndex = this.buffer.length;
            } else {
                this.startIndex = startIndex;
                this.endIndex = endIndex;
                this.buffer = buffer;
            }

            this.decodeStrings = encoded;

        } catch ( Exception ex ) {
            Exceptions.handle ( ex );
        }

    }


    public String toString () {
        if ( startIndex == 0 && endIndex == buffer.length ) {
            return FastStringUtils.noCopyStringFromChars ( buffer );
        } else {
            return new String ( buffer, startIndex, ( endIndex - startIndex ) );
        }
    }


    @Override
    public final Object toValue () {
        return value != null ? value : (value = doToValue ()) ;
    }

    @Override
    public <T extends Enum> T toEnum( Class<T> cls ) {

        switch ( type ) {
            case STRING:
                return Conversions.toEnum ( cls, stringValue () );
            case INTEGER:
                return Conversions.toEnum( cls, intValue() );
            case NULL:
                return null;
        }
        die( "toEnum " + cls + " value was " + stringValue() );
        return null;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    private final Object doToValue () {

        switch ( type ) {
            case DOUBLE:
                return doubleValue ();
            case INTEGER:

                int sign = 1;
                boolean negative = false;
                if ( buffer[ startIndex ] == '-' ) {
                    startIndex++;
                    sign = -1;
                    negative = true;

                }


                if ( isInteger ( buffer, startIndex, endIndex - startIndex, negative ) ) {
                    return intValue () * sign;
                } else {
                    return longValue () * sign;
                }
            case STRING:
                if ( checkDate ) {
                    if ( Dates.isISO8601QuickCheck ( buffer, startIndex, endIndex ) ) {
                        return Dates.fromISO8601DateLoose ( buffer, startIndex, endIndex );
                    }
                }
                return stringValue ();
        }
        die ();
        return null;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Value ) ) return false;

        CharSequenceValue value1 = ( CharSequenceValue ) o;

        if ( endIndex != value1.endIndex ) return false;
        if ( startIndex != value1.startIndex ) return false;
        if ( !Arrays.equals ( buffer, value1.buffer ) ) return false;
        if ( type != value1.type ) return false;
        if ( value != null ? !value.equals ( value1.value ) : value1.value != null ) return false;

        return true;
    }

    @Override
    public int hashCode () {
        int result = type != null ? type.hashCode () : 0;
        result = 31 * result + ( buffer != null ? Arrays.hashCode ( buffer ) : 0 );
        result = 31 * result + startIndex;
        result = 31 * result + endIndex;
        result = 31 * result + ( value != null ? value.hashCode () : 0 );
        return result;
    }


    @Override
    public final int length () {
        return buffer.length;
    }

    @Override
    public final char charAt ( int index ) {
        return buffer[ index ];
    }

    @Override
    public final CharSequence subSequence ( int start, int end ) {

        CharSequenceValue b = new CharSequenceValue ();
        b.startIndex = start;
        b.endIndex = end;
        return b;
    }


    public BigDecimal bigDecimalValue () {
        return new BigDecimal ( buffer, startIndex, endIndex - startIndex );
    }

    @Override
    public BigInteger bigIntegerValue() {
        return new BigInteger ( toString () );
    }

    public String stringValue () {
        if ( this.decodeStrings ) {
            return JsonStringDecoder.decodeForSure ( buffer, startIndex, endIndex );
        } else {
            return toString ();
        }
    }

    @Override
    public String stringValueEncoded () {
        return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
    }


    @Override
    public Date dateValue () {


        if ( type == Type.STRING ) {

            if ( Dates.isISO8601QuickCheck ( buffer, startIndex, endIndex ) ) {

                if ( Dates.isJsonDate ( buffer, startIndex, endIndex ) ) {
                    return Dates.fromJsonDate ( buffer, startIndex, endIndex );

                } else if ( Dates.isISO8601 ( buffer, startIndex, endIndex ) ) {
                    return Dates.fromISO8601 ( buffer, startIndex, endIndex );
                } else {
                    throw new JsonException ( "Unable to convert " + stringValue () + " to date " );
                }
            } else {

                throw new JsonException ( "Unable to convert " + stringValue () + " to date " );
            }
        } else {

            return new Date ( Dates.utc ( longValue () ) );
        }

    }


    @Override
    public int intValue () {
        int sign = 1;
        if ( buffer[ startIndex ] == '-' ) {
            startIndex++;
            sign = -1;

        }
        return parseInt ( buffer, startIndex, endIndex - startIndex ) * sign;
    }

    @Override
    public long longValue () {
        long sign = 1;
        if ( buffer[ startIndex ] == '-' ) {
            startIndex++;
            sign = -1;

        }
        if ( isInteger ( buffer, startIndex, endIndex - startIndex, sign < 0 ) ){
            return parseInt( buffer, startIndex, endIndex - startIndex ) * sign;
        } else {
           return parseLong( buffer, startIndex, endIndex - startIndex ) * sign;
        }
    }


    public byte byteValue () {
        return ( byte ) intValue ();
    }

    public short shortValue () {
        return ( short ) intValue ();
    }


    private static float fpowersOf10[] = {
            1.0f,
            10.0f,
            100.0f,
            1_000.0f,
            10_000.0f,
            100_000.0f,
            1_000_000.0f,
            10_000_000.0f,
            100_000_000.0f,
            1_000_000_000.0f,
    };

    @Override
    public double doubleValue () {
        return CharScanner.doubleValue ( this.buffer, startIndex, endIndex );

    }

    @Override
    public boolean booleanValue() {
        return Boolean.parseBoolean ( toString () );
    }

    @Override
    public float floatValue () {

        boolean simple = true;
        int digitsPastPoint = 0;
        boolean foundPoint = false;

        float sign;

        if ( buffer[ startIndex ] == '-' ) {
            startIndex++;
            sign = -1.0f;
        } else {
            sign = 1.0f;
        }


        int length = endIndex - startIndex;
        if ( length > 10 ) {
            return Float.parseFloat ( toString () ) * sign;
        }
        loop:
        for ( int index = startIndex; index < endIndex; index++ ) {
            char ch = buffer[ index ];
            switch ( ch ) {
                case 'e':
                    simple = false;
                    break loop;
                case 'E':
                    simple = false;
                    break loop;
                case 'F':
                    simple = false;
                    break loop;
                case 'f':
                    simple = false;
                    break loop;
                case '.':
                    foundPoint = true;
                    continue loop;
            }
            if ( foundPoint ) {
                digitsPastPoint++;
                if ( digitsPastPoint >= fpowersOf10.length ) {
                    simple = true;
                    break;
                }
            }
        }

        if ( simple ) {
            int value;

            value = parseIntIgnoreDot ( buffer, startIndex, length );
            if ( digitsPastPoint < fpowersOf10.length ) {
                float power = fpowersOf10[ digitsPastPoint ] * sign;
                return value / power;

            }


        }

        return Float.parseFloat ( toString () ) * sign;

    }

    public final void chop () {
        if ( !chopped ) {
            this.chopped = true;
            this.buffer = Arrays.copyOfRange ( buffer, startIndex, endIndex );
            this.startIndex = 0;
            this.endIndex = this.buffer.length;
        }
    }




    @Override
    public char charValue () {
        return buffer[startIndex];
    }


}