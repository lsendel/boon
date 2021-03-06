package org.boon.core.reflection;

import org.boon.Lists;
import org.boon.Maps;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldFieldsAccessor;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.core.value.LazyValueMap;
import org.boon.core.value.ValueList;
import org.boon.core.value.ValueMap;
import org.boon.core.value.ValueMapImpl;

import java.util.*;


/**
 * Created by rick on 12/26/13.
 */
public class MapObjectConversion {
    @SuppressWarnings ( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, Class<T> clazz ) {
            return fromMap( map, Reflection.newInstance ( clazz ) );

    }

    @SuppressWarnings ( "unchecked" )
    public static Object fromMap( Map<String, Object> map ) {
        String className = ( String ) map.get( "class" );
        Object newInstance = Reflection.newInstance ( className );
        return fromMap( map, newInstance );
    }

    @SuppressWarnings ( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, T newInstance ) {

        if (map instanceof ValueMap) {
            return (T) fromValueMap ( FieldAccessMode.FIELD.create (), (ValueMap) (Map) map, newInstance );
        }

        FieldsAccessor fieldsAccessor = new FieldFieldsAccessor();
        Map<String, FieldAccess> fields = fieldsAccessor.getFields( newInstance.getClass() );
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();

        /* Iterate through the fields. */
        //for ( FieldAccess field : fields ) {
        for ( Map.Entry<String, Object> entry : entrySet ) {


            FieldAccess field = fields.get( entry.getKey() );
            Object value = entry.getValue();


            if ( value instanceof Value ) {
                if ( ( ( Value ) value ).isContainer() ) {
                    value = ( ( Value ) value ).toValue();
                } else {
                    field.setFromValue( newInstance, ( Value ) value );
                    continue;
                }
            }

            if (value.getClass() == field.getType()) {
                  field.setObject( newInstance, value );
            } else if ( Typ.isBasicType ( value ) ) {

                field.setValue( newInstance, value );
            } else if ( value instanceof Value ) {
                field.setValue( newInstance, value );
            }
            /* See if it is a map<string, object>, and if it is then process it. */
            //&& Typ.getKeyType ( ( Map<?, ?> ) value ) == Typ.string
            else if ( value instanceof Map ) {
                Class <?> clazz = field.getType();
                if ( !clazz.isInterface () && !Typ.isAbstract (clazz) )  {
                    value = fromMap( ( Map<String, Object> ) value, field.getType() );

                } else {
                    value = fromMap( ( Map<String, Object> ) value );
                }
                field.setObject( newInstance, value );
            } else if ( value instanceof Collection ) {
                /*It is a collection so process it that way. */
                processCollectionFromMapUsingFields( fieldsAccessor, newInstance, field, ( Collection ) value );
            } else if ( value instanceof Map[] ) {
                /* It is an array of maps so, we need to process it as such. */
                processArrayOfMaps( newInstance, field, value );
            } else {
                field.setValue( newInstance, value );
            }

        }

        return newInstance;
    }



    @SuppressWarnings ( "unchecked" )
    public static <T> T fromValueMap(
            final FieldsAccessor fieldsAccessor,
            final Map<String, Value> map,
            final Class<T> clazz ) {

        return fromValueMap( fieldsAccessor, map, Reflection.newInstance( clazz ) );
    }



    @SuppressWarnings ( "unchecked" )
    public static <T> T fromValueMap(
            final FieldsAccessor fieldsAccessor,
            final Map<String, Value> map) {
        String className = map.get( "class" ).toString();
        Object newInstance = Reflection.newInstance ( className );
        return fromValueMap( fieldsAccessor, map, (T)newInstance );
    }

    @SuppressWarnings ( "unchecked" )
    public static <T> T fromValueMap( final FieldsAccessor fieldsAccessor,
                                      final Map<String, Value> amap,
                                      final T newInstance ) {


        ValueMap map =  (ValueMap) (Map) amap;


        Map<String, FieldAccess> fields = fieldsAccessor.getFields( newInstance.getClass() );
        Map.Entry<String, Object>[] entries;
        int size;

        if (!map.hydrated()) {
            size = map.len();
            entries = map.items();
        } else {
            size = map.size();
            entries = ( Map.Entry<String, Object>[] ) map.entrySet ().toArray( new Map.Entry[size] );
        }

        /* guard. */
        if ( size==0 || entries == null) {
            return newInstance;
        }


        for ( int index = 0; index < size; index++ ) {
            Map.Entry<String, Object> entry = entries[ index ];

            String key = entry.getKey();
            FieldAccess field = fields.get( key );
            Object ovalue = entry.getValue();


            if (ovalue instanceof Value) {
                Value value = (Value) ovalue;

                if ( value.isContainer() ) {
                    Object objValue;

                    objValue = value.toValue();
                    if ( objValue instanceof Map ) {



                        Class <?> clazz = field.getType();
                        if ( !clazz.isInterface () && !Typ.isAbstract (clazz) )  {
                            objValue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) objValue, field.getType() );

                        } else {

                            objValue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) objValue );
                        }
                        field.setObject( newInstance, objValue );
                    } else if ( objValue instanceof Collection ) {
                        handleCollectionOfValues(fieldsAccessor, newInstance, field,
                                ( Collection<Value> ) objValue );
                    }

                } else {
                    field.setFromValue( newInstance, value );
                }

            } else {

                if ( ovalue instanceof Map ) {



                    Class <?> clazz = field.getType();
                    if ( !clazz.isInterface () && !Typ.isAbstract (clazz) )  {
                        ovalue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) ovalue, field.getType() );

                    } else {

                        ovalue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) ovalue );
                    }
                    field.setObject( newInstance, ovalue );
                } else if ( ovalue instanceof Collection ) {
                    handleCollectionOfValues(fieldsAccessor, newInstance, field,
                            ( Collection<Value> ) ovalue );
                } else {
                    field.setValue( newInstance, ovalue );
                }

            }

        }

        return newInstance;
    }



    @SuppressWarnings ( "unchecked" )
    public static <T> T fromValueMap( final FieldsAccessor fieldsAccessor,
                                      final ValueMapImpl  map,
                                      final T newInstance ) {




        Map<String, FieldAccess> fields = fieldsAccessor.getFields( newInstance.getClass() );
        Map.Entry<String, Value>[] entries;
        int size;

        if (!map.hydrated()) {
            size = map.len();
            entries = map.items();
        } else {
            size = map.size();
            entries = ( Map.Entry<String, Value>[] ) map.entrySet ().toArray( new Map.Entry[size] );
        }

        /* guard. */
        if ( size==0 || entries == null) {
            return newInstance;
        }


        for ( int index = 0; index < size; index++ ) {
            Map.Entry<String, Value> entry = entries[ index ];

            String key = entry.getKey();
            FieldAccess field = fields.get( key );
            Value value  = entry.getValue();



                if ( value.isContainer() ) {
                    Object objValue;

                    objValue = value.toValue();
                    if ( objValue instanceof Map ) {

                        Class <?> clazz = field.getType();
                        if ( !clazz.isInterface () && !Typ.isAbstract (clazz) )  {
                            objValue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) objValue, field.getType() );

                        } else {

                            objValue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) objValue );
                        }
                        field.setObject( newInstance, objValue );
                    } else if ( objValue instanceof Collection ) {
                        handleCollectionOfValues(fieldsAccessor, newInstance, field,
                                ( Collection<Value> ) objValue );
                    }

                } else {
                    field.setFromValue( newInstance, value );
                }
        }

        return newInstance;
    }
    private static void processCollectionFromMapUsingFields(
                    final FieldsAccessor fieldsAccessor, final Object newInstance,
                                                  final FieldAccess field,
                                                  final Collection<?> collection ) {

        final Class<?> componentType = Reflection.getComponentType ( collection );
        /** See if we have a collection of maps because if we do, then we have some
         * recursive processing to do.
         */
        if ( Typ.isMap( componentType ) ) {
            handleCollectionOfMaps( newInstance, field,
                    ( Collection<Map<String, Object>> ) collection );
        } else if ( Typ.isValue( componentType ) ) {
            handleCollectionOfValues( fieldsAccessor, newInstance, field,
                    ( Collection<Value> ) collection );

        } else {

            /* It might be a collection of regular types. */

            /*If it is a compatiable type just inject it. */
            if ( field.getType().isInterface() &&
                    Typ.implementsInterface( collection.getClass(), field.getType() ) ) {

                field.setValue( newInstance, collection );

            } else {
                /* The type was not compatible so create a new collection that is. */
                Collection<Object> newCollection =
                        Reflection.createCollection ( field.getType (), collection.size () );

                newCollection.addAll( collection );
                field.setValue( newInstance, newCollection );

            }

        }

    }

    private static void processArrayOfMaps( Object newInstance, FieldAccess field, Object value ) {
        Map<String, Object>[] maps = ( Map<String, Object>[] ) value;
        List<Map<String, Object>> list = Lists.list ( maps );
        handleCollectionOfMaps( newInstance, field,
                list );

    }

    @SuppressWarnings ( "unchecked" )
    private static void handleCollectionOfMaps( Object newInstance,
                                                FieldAccess field, Collection<Map<String, Object>> collectionOfMaps ) {

        Collection<Object> newCollection = Reflection.createCollection ( field.getType (), collectionOfMaps.size () );


        Class<?> componentClass = field.getComponentClass();

        if ( componentClass != null ) {


            for ( Map<String, Object> mapComponent : collectionOfMaps ) {

                newCollection.add( fromMap( mapComponent, componentClass ) );

            }
            field.setObject( newInstance, newCollection );

        }

    }

    @SuppressWarnings ( "unchecked" )
    private static void handleCollectionOfValues( FieldsAccessor fieldsAccessor, Object newInstance,
                                                  FieldAccess field, Collection<Value> acollectionOfValues ) {

        Collection collectionOfValues = acollectionOfValues;

        if (collectionOfValues instanceof ValueList) {
            collectionOfValues = ((ValueList)collectionOfValues).list();
        }

        Collection<Object> newCollection = Reflection.createCollection ( field.getType (), collectionOfValues.size () );


        Class<?> componentClass = field.getComponentClass();

        if ( componentClass != null ) {


            for ( Value value : (List<Value>) collectionOfValues ) {

                if ( value.isContainer() ) {
                    Object oValue = value.toValue();
                    if ( oValue instanceof Map ) {
                        newCollection.add( fromValueMap( fieldsAccessor, ( Map ) oValue, componentClass ) );
                    }
                } else {
                    newCollection.add( Conversions.coerce ( componentClass, value.toValue () ) );
                }


            }
            field.setObject( newInstance, newCollection );

        }

    }

    public static Map<String, Object> toMap( final Object object ) {

        if ( object == null ) {
            return null;
        }

        Map<String, Object> map = new LinkedHashMap<>();


        class FieldToEntryConverter implements
                Conversions.Converter<Maps.Entry<String, Object>, FieldAccess> {
            @Override
            public Maps.Entry<String, Object> convert( FieldAccess from ) {
                if ( from.isReadOnly() ) {
                    return null;
                }
                Maps.Entry<String, Object> entry = new Maps.EntryImpl<>( from.getName(),
                        from.getValue( object ) );
                return entry;
            }
        }

        final Map<String, FieldAccess> fieldMap = Reflection.getAllAccessorFields ( object.getClass () );
        List<FieldAccess> fields = new ArrayList( fieldMap.values() );


        Collections.reverse( fields ); // make super classes fields first that
        // their update get overriden by
        // subclass fields with the same name

        List<Maps.Entry<String, Object>> entries = Conversions.mapFilterNulls(
                new FieldToEntryConverter(), new ArrayList( fields ) );

        map.put( "class", object.getClass().getName() );

        for ( Maps.Entry<String, Object> entry : entries ) {
            Object value = entry.value();
            if ( value == null ) {
                continue;
            }
            if ( Typ.isBasicType( value ) ) {
                map.put( entry.key(), entry.value() );
            } else if ( Reflection.isArray ( value )
                    && Typ.isBasicType( value.getClass().getComponentType() ) ) {
                map.put( entry.key(), entry.value() );
            } else if ( Reflection.isArray ( value ) ) {
                int length = Reflection.arrayLength ( value );
                List<Map<String, Object>> list = new ArrayList<>( length );
                for ( int index = 0; index < length; index++ ) {
                    Object item = Reflection.idx ( value, index );
                    list.add( toMap( item ) );
                }
                map.put( entry.key(), list );
            } else if ( value instanceof Collection ) {
                Collection<?> collection = ( Collection<?> ) value;
                Class<?> componentType = Reflection.getComponentType ( collection, fieldMap.get ( entry.key () ) );
                if ( Typ.isBasicType( componentType ) ) {
                    map.put( entry.key(), value );
                } else {
                    List<Map<String, Object>> list = new ArrayList<>(
                            collection.size() );
                    for ( Object item : collection ) {
                        if ( item != null ) {
                            list.add( toMap( item ) );
                        } else {

                        }
                    }
                    map.put( entry.key(), list );
                }
            } else if ( value instanceof Map ) {

            } else {
                map.put( entry.key(), toMap( value ) );
            }
        }
        return map;
    }

    public static <T> List<T> convertListOfMapsToObjects( FieldsAccessor fieldsAccessor, Class<T> componentType, List<Object> list ) {
        List<Object> newList = new ArrayList<> ( list.size () );
        for (Object obj : list) {
            Map map = obj instanceof Map ? (Map)obj  : (Map)( (Value) obj).toValue();
            if ( map instanceof ValueMapImpl ) {
                newList.add ( fromValueMap(fieldsAccessor, ( Map<String, Value> ) map, componentType ) );
            } else {
                newList.add ( fromMap ( map, componentType ) );
            }
        }
        return (List<T>) newList;
    }
}
