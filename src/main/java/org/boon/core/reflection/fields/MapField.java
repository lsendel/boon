package org.boon.core.reflection.fields;

import org.boon.core.Type;
import org.boon.core.Value;
import org.boon.core.Conversions;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Map;

import static org.boon.Exceptions.die;

public class MapField implements FieldAccess {

    private final String name;

    public  MapField( String name ) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Object getValue( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return map.get( name );
        }
        return die( Object.class, "Object must be a map but was a " + obj.getClass().getName() );
    }

    @Override
    public final void setValue( Object obj, Object value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final void setFromValue( Object obj, Value value ) {
        setValue( obj, value.toValue() );
    }

    @Override
    public final boolean getBoolean( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return Conversions.toBoolean( map.get( name ) );
        }
        return die( Boolean.class, "Object must be a map" );
    }

    @Override
    public final void setBoolean( Object obj, boolean value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final int getInt( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return Conversions.toInt( map.get( name ) );
        }
        die( "Object must be a map" );
        return -1;
    }

    @Override
    public final void setInt( Object obj, int value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final short getShort( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return Conversions.toShort( map.get( name ) );
        }
        die( "Object must be a map" );
        return -1;
    }

    @Override
    public final void setShort( Object obj, short value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final char getChar( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return Conversions.toChar( map.get( name ) );
        }
        die( "Object must be a map" );
        return 0;
    }

    @Override
    public final void setChar( Object obj, char value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final long getLong( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return Conversions.toLong( map.get( name ) );
        }
        die( "Object must be a map" );
        return -1;
    }

    @Override
    public final void setLong( Object obj, long value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final double getDouble( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return Conversions.toDouble( map.get( name ) );
        }
        die( "Object must be a map" );
        return Double.NaN;
    }

    @Override
    public final void setDouble( Object obj, double value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final float getFloat( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return Conversions.toFloat( map.get( name ) );
        }
        die( "Object must be a map" );
        return Float.NaN;
    }

    @Override
    public final void setFloat( Object obj, float value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final byte getByte( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return Conversions.toByte( map.get( name ) );
        }
        die( "Object must be a map" );
        return Byte.MAX_VALUE;
    }

    @Override
    public final void setByte( Object obj, byte value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );

    }

    @Override
    public final Object getObject( Object obj ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            return map.get( name );
        }
        die( "Object must be a map" );
        return -1;
    }

    @Override
    public final void setObject( Object obj, Object value ) {
        if ( obj instanceof Map ) {
            Map map = ( Map ) obj;
            map.put( name, value );
        }
        die( "Object must be a map" );
    }

    @Override
    public final Type typeEnum () {
        return Type.OBJECT;
    }


    @Override
    public final boolean isPrimitive () {
        return false;
    }


    @Override
    public final Field getField() {
        return die( Field.class, "Unsupported operation" );

    }

    @Override
    public final boolean include () {
        return false;
    }

    @Override
    public final boolean ignore () {
        return false;
    }

    @Override
    public final ParameterizedType getParameterizedType() {
        return null;
    }

    @Override
    public final Class<?> getComponentClass() {
        return null;
    }

    @Override
    public final boolean hasAnnotation ( String annotationName ) {
        return false;
    }

    @Override
    public final Map<String, Object> getAnnotationData ( String annotationName ) {
        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean isViewActive( String activeView ) {
        return true;
    }

    @Override
    public final boolean isFinal() {
        return false;
    }

    @Override
    public final boolean isStatic() {
        return false;
    }

    @Override
    public final boolean isVolatile() {
        return false;
    }

    @Override
    public final boolean isQualified() {
        return false;
    }

    @Override
    public final boolean isReadOnly() {
        return false;
    }

    @Override
    public final Class<?> getType() {
        return Object.class;
    }
}
