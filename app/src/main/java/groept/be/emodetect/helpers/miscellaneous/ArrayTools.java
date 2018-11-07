package groept.be.emodetect.helpers.miscellaneous;

import java.util.ArrayList;
import java.util.Arrays;

import java.nio.ByteOrder;

public class ArrayTools{
    public static byte[] byteArrayListToByteArray( ArrayList< Byte > byteArrayList ){
        byte[] byteArray = new byte[ byteArrayList.size() ];

        for( int index = 0; index < byteArray.length; ++index ){
            byteArray[ index ] = byteArrayList.get( index );
        }

        return( byteArray );
    }

    public static ArrayList< Byte > byteArrayToByteArrayList( byte[] byteArray ){
        ArrayList< Byte > byteArrayList = new ArrayList< Byte >();

        for( int index = 0; index < byteArray.length; ++index ){
            byteArrayList.add( byteArray[ index ] );
        }

        return( byteArrayList );
    }

    public static byte[] shortArrayToByteArray( short[] shortArray, ByteOrder byteOrder ){
        byte[] newByteArray = new byte[ ( 2 * shortArray.length ) ];

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < shortArray.length; ++index ){
                newByteArray[ ( 2 * index ) ]         = BinaryTools.shortGetMSB( shortArray[ index ] );
                newByteArray[ ( ( 2 * index ) + 1 ) ] = BinaryTools.shortGetLSB( shortArray[ index ] );
            }
        } else { // Little-endian
            for( int index = 0; index < shortArray.length; ++index ){
                newByteArray[ ( 2 * index ) ]         = BinaryTools.shortGetLSB( shortArray[ index ] );
                newByteArray[ ( ( 2 * index ) + 1 ) ] = BinaryTools.shortGetMSB( shortArray[ index ] );
            }
        }

        return( newByteArray );
    }

    public static byte[] shortArrayListToByteArray( ArrayList< Short > shortArrayList, ByteOrder byteOrder ){
        byte[] newByteArray = new byte[ ( 2 * shortArrayList.size() ) ];

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < shortArrayList.size(); ++index ){
                newByteArray[ ( 2 * index ) ]         = BinaryTools.shortGetMSB( shortArrayList.get( index ) );
                newByteArray[ ( ( 2 * index ) + 1 ) ] = BinaryTools.shortGetLSB( shortArrayList.get( index ) );
            }
        } else { // Little-endian
            for( int index = 0; index < shortArrayList.size(); ++index ){
                newByteArray[ ( 2 * index ) ]         = BinaryTools.shortGetLSB( shortArrayList.get( index ) );
                newByteArray[ ( ( 2 * index ) + 1 ) ] = BinaryTools.shortGetMSB( shortArrayList.get( index ) );
            }
        }

        return( newByteArray );
    }

    public static ArrayList< Byte > shortArrayToByteArrayList( short[] shortArray, ByteOrder byteOrder ){
        ArrayList< Byte > newByteArrayList = new ArrayList< Byte >();

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < shortArray.length; ++index ){
                newByteArrayList.add( BinaryTools.shortGetMSB( shortArray[ index ] ) );
                newByteArrayList.add( BinaryTools.shortGetLSB( shortArray[ index ] ) );
            }
        } else { // LITTLE_ENDIAN
            for( int index = 0; index < shortArray.length; ++index ){
                newByteArrayList.add( BinaryTools.shortGetLSB( shortArray[ index ] ) );
                newByteArrayList.add( BinaryTools.shortGetMSB( shortArray[ index ] ) );
            }
        }

        return( newByteArrayList );
    }

    public static ArrayList< Byte > shortArrayListToByteArrayList( ArrayList< Short > shortArrayList, ByteOrder byteOrder ){
        ArrayList< Byte > newByteArrayList = new ArrayList< Byte >();

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < shortArrayList.size(); ++index ){
                newByteArrayList.add( BinaryTools.shortGetMSB( shortArrayList.get ( index ) ) );
                newByteArrayList.add( BinaryTools.shortGetLSB( shortArrayList.get ( index ) ) );
            }
        } else { // LITTLE_ENDIAN
            for( int index = 0; index < shortArrayList.size(); ++index ){
                newByteArrayList.add( BinaryTools.shortGetLSB( shortArrayList.get ( index ) ) );
                newByteArrayList.add( BinaryTools.shortGetMSB( shortArrayList.get ( index ) ) );
            }
        }

        return( newByteArrayList );
    }

    public static byte[] intArrayToByteArray( int[] intArray, ByteOrder byteOrder ){
        byte[] newByteArray = new byte[ ( 4 * intArray.length ) ];

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < intArray.length; ++index ){
                newByteArray[ ( 4 * index ) ]         = BinaryTools.intGetMSB( intArray[ index ] );
                newByteArray[ ( ( 4 * index ) + 1 ) ] = BinaryTools.intGetMSH( intArray[ index ] );
                newByteArray[ ( ( 4 * index ) + 2 ) ] = BinaryTools.intGetMSL( intArray[ index ] );
                newByteArray[ ( ( 4 * index ) + 3 ) ] = BinaryTools.intGetLSB( intArray[ index ] );
            }
        } else { // Little-endian
            for( int index = 0; index < intArray.length; ++index ){
                newByteArray[ ( 4 * index ) ]         = BinaryTools.intGetLSB( intArray[ index ] );
                newByteArray[ ( ( 4 * index ) + 1 ) ] = BinaryTools.intGetMSL( intArray[ index ] );
                newByteArray[ ( ( 4 * index ) + 2 ) ] = BinaryTools.intGetMSH( intArray[ index ] );
                newByteArray[ ( ( 4 * index ) + 3 ) ] = BinaryTools.intGetMSB( intArray[ index ] );
            }
        }

        return( newByteArray );
    }

    public static byte[] integerArrayListToByteArray( ArrayList< Integer > integerArrayList, ByteOrder byteOrder ){
        byte[] newByteArray = new byte[ ( 4 * integerArrayList.size() ) ];

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < integerArrayList.size(); ++index ){
                newByteArray[ ( 4 * index ) ]         = BinaryTools.intGetMSB( integerArrayList.get( index ) );
                newByteArray[ ( ( 4 * index ) + 1 ) ] = BinaryTools.intGetMSH( integerArrayList.get( index ) );
                newByteArray[ ( ( 4 * index ) + 2 ) ] = BinaryTools.intGetMSL( integerArrayList.get( index ) );
                newByteArray[ ( ( 4 * index ) + 3 ) ] = BinaryTools.intGetLSB( integerArrayList.get( index ) );
            }
        } else { // Little-endian
            for( int index = 0; index < integerArrayList.size(); ++index ){
                newByteArray[ ( 4 * index ) ]         = BinaryTools.intGetLSB( integerArrayList.get( index ) );
                newByteArray[ ( ( 4 * index ) + 1 ) ] = BinaryTools.intGetMSL( integerArrayList.get( index ) );
                newByteArray[ ( ( 4 * index ) + 2 ) ] = BinaryTools.intGetMSH( integerArrayList.get( index ) );
                newByteArray[ ( ( 4 * index ) + 3 ) ] = BinaryTools.intGetMSB( integerArrayList.get( index ) );
            }
        }

        return( newByteArray );
    }

    public static ArrayList< Byte > intArrayToByteArrayList( int[] intArray, ByteOrder byteOrder ){
        ArrayList< Byte > newByteArrayList = new ArrayList< Byte >();

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < intArray.length; ++index ){
                newByteArrayList.add( BinaryTools.intGetMSB( intArray[ index ] ) );
                newByteArrayList.add( BinaryTools.intGetMSH( intArray[ index ] ) );
                newByteArrayList.add( BinaryTools.intGetMSL( intArray[ index ] ) );
                newByteArrayList.add( BinaryTools.intGetLSB( intArray[ index ] ) );
            }
        } else { // LITTLE_ENDIAN
            for( int index = 0; index < intArray.length; ++index ){
                newByteArrayList.add( BinaryTools.intGetLSB( intArray[ index ] ) );
                newByteArrayList.add( BinaryTools.intGetMSL( intArray[ index ] ) );
                newByteArrayList.add( BinaryTools.intGetMSH( intArray[ index ] ) );
                newByteArrayList.add( BinaryTools.intGetMSB( intArray[ index ] ) );
            }
        }

        return( newByteArrayList );
    }

    public static ArrayList< Byte > integerArrayListToByteArrayList( ArrayList< Integer > integerArrayList, ByteOrder byteOrder ){
        ArrayList< Byte > newByteArrayList = new ArrayList< Byte >();

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < integerArrayList.size(); ++index ){
                newByteArrayList.add( BinaryTools.intGetMSB( integerArrayList.get( index ) ) );
                newByteArrayList.add( BinaryTools.intGetMSH( integerArrayList.get( index ) ) );
                newByteArrayList.add( BinaryTools.intGetMSL( integerArrayList.get( index ) ) );
                newByteArrayList.add( BinaryTools.intGetLSB( integerArrayList.get( index ) ) );
            }
        } else { // LITTLE_ENDIAN
            for( int index = 0; index < integerArrayList.size(); ++index ){
                newByteArrayList.add( BinaryTools.intGetLSB( integerArrayList.get( index ) ) );
                newByteArrayList.add( BinaryTools.intGetMSL( integerArrayList.get( index ) ) );
                newByteArrayList.add( BinaryTools.intGetMSH( integerArrayList.get( index ) ) );
                newByteArrayList.add( BinaryTools.intGetMSB( integerArrayList.get( index ) ) );
            }
        }

        return( newByteArrayList );
    }

    public static short[] shortArrayListToShortArray( ArrayList< Short > shortArrayList ){
        short[] shortArray = new short[ shortArrayList.size() ];

        for( int index = 0; index < shortArray.length; ++index ){
            shortArray[ index ] = shortArrayList.get( index );
        }

        return( shortArray );
    }

    public static ArrayList< Short > shortArrayToShortArrayList( short[] shortArray ){
        ArrayList< Short > shortArrayList = new ArrayList< Short >();

        for( int index = 0; index < shortArray.length; ++index ){
            shortArrayList.add( shortArray [ index ] );
        }

        return( shortArrayList );
    }

    public static ArrayList< Short > byteArrayToShortArrayList( byte[] byteArray, ByteOrder byteOrder ){
        ArrayList< Short > shorts = new ArrayList< Short >();

        for( int index = 0; index < byteArray.length; index += 2 ){
            if( index != ( byteArray.length - 1 ) ) {
                byte[] currentByteArray = Arrays.copyOfRange( byteArray, index, ( index + 2 ) );
                Short currentShort = new Short( byteArrayToShort( currentByteArray, byteOrder ) );
                shorts.add( currentShort );
            } else {
                Short currentShort = new Short( byteArray[ index ] );
                shorts.add( currentShort );
            }
        }

        return( shorts );
    }

    public static ArrayList< Integer > byteArrayToIntegerArrayList( byte[] byteArray, ByteOrder byteOrder ){
        ArrayList< Integer > integers = new ArrayList< Integer >();

        for( int index = 0; index < byteArray.length; index += 4 ){
            if( index < byteArray.length - 3 ) {
                byte[] currentByteArray = Arrays.copyOfRange( byteArray, index, ( index + 4 ) );
                Integer currentInteger = new Integer( byteArrayToInt( currentByteArray, byteOrder ) );
                integers.add( currentInteger );
            } else {
                byte[] currentByteArray = Arrays.copyOfRange( byteArray, index, byteArray.length );
                Integer currentInteger = new Integer( byteArrayToInt( currentByteArray, byteOrder ) );
                integers.add( currentInteger );
            }
        }

        return( integers );
    }

    public static int[] integerArrayListToIntArray( ArrayList< Integer > integerArrayList ){
        int[] intArray = new int[ integerArrayList.size() ];

        for( int index = 0; index < intArray.length; ++index ){
            intArray[ index ] = integerArrayList.get( index );
        }

        return( intArray );
    }

    public static ArrayList< Integer > intArrayToIntegerArrayList( int[] intArray ){
        ArrayList< Integer > integerArrayList = new ArrayList< Integer >();

        for( int index = 0; index < intArray.length; ++index ){
            integerArrayList.add( intArray [ index ] );
        }

        return( integerArrayList );
    }

    public static byte[] byteObjectArrayToByteArray( Byte[] byteObjectArray ){
        byte[] primitiveByteArray = new byte[ byteObjectArray.length ];

        for( int index = 0; index < byteObjectArray.length; ++index ){
            primitiveByteArray[ index ] = byteObjectArray[ index ];
        }

        return( primitiveByteArray );
    }

    public static Byte[] byteArrayToByteObjectArray( byte[] primitiveByteArray ){
        Byte[] byteObjectArray = new Byte[ primitiveByteArray.length ];

        for( int index = 0; index < primitiveByteArray.length; ++index ){
            byteObjectArray[ index ] = primitiveByteArray [ index ];
        }

        return( byteObjectArray );
    }

    public static short[] shortObjectArrayToShortArray( Short[] shortObjectArray ){
        short[] primitiveShortArray = new short[ shortObjectArray.length ];

        for( int index = 0; index < shortObjectArray.length; ++index ){
            primitiveShortArray[ index ] = shortObjectArray[ index ];
        }

        return( primitiveShortArray );
    }

    public static Short[] shortArrayToShortObjectArray( short[] primitiveShortArray ){
        Short[] shortObjectArray = new Short[ primitiveShortArray.length ];

        for( int index = 0; index < primitiveShortArray.length; ++index ){
            shortObjectArray[ index ] = primitiveShortArray[ index ];
        }

        return( shortObjectArray );
    }

    public static int[] integerObjectArrayToIntegerArray( Integer[] integerObjectArray ){
        int[] primitiveIntegerArray = new int[ integerObjectArray.length ];

        for( int index = 0; index < integerObjectArray.length; ++index ){
            primitiveIntegerArray[ index ] = integerObjectArray[ index ];
        }

        return( primitiveIntegerArray );
    }

    public static Integer[] integerArrayToIntegerObjectArray( int[] primitiveIntegerArray ){
        Integer[] integerObjectArray = new Integer[ primitiveIntegerArray.length ];

        for( int index = 0; index < primitiveIntegerArray.length; ++index ){
            integerObjectArray[ index ] = primitiveIntegerArray[ index ];
        }

        return( integerObjectArray );
    }

    public static short byteArrayToShort( byte[] byteArray, ByteOrder byteOrder ){
        short theShort = 0;

        if( ( byteArray.length > 2 ) ||
            ( byteArray.length == 0 ) ){
            throw new IllegalArgumentException( "Byte array has length that disqualifies it from being converted to short!" );
        }

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < byteArray.length; ++index ){
                short currentByte = ( byteArray[ index ] < 0 ) ? ( ( short )( byteArray[ index ] + 256 ) ) : byteArray[ index ];
                theShort += ( currentByte << ( ( ( byteArray.length - 1 ) * 8 ) - ( index * 8 ) ) );
            }
        } else if( byteOrder == ByteOrder.LITTLE_ENDIAN ){
            for( int index = 0; index < byteArray.length; ++index ){
                short currentByte = ( byteArray[ index ] < 0 ) ? ( ( short )( byteArray[ index ] + 256 ) ) : byteArray[ index ];
                theShort += ( currentByte << ( index * 8 ) );
            }
        }

        return( theShort );
    }

    public static int byteArrayToInt( byte[] byteArray, ByteOrder byteOrder ){
        int theInteger = 0;

        if( ( byteArray.length > 4 ) ||
                ( byteArray.length == 0 ) ){
            throw new IllegalArgumentException( "Byte array has length that disqualifies it from being converted to integer!" );
        }

        if( byteOrder == ByteOrder.BIG_ENDIAN ){
            for( int index = 0; index < byteArray.length; ++index ){
                int currentByte = ( byteArray[ index ] < 0 ) ? ( byteArray[ index ] + 256 ) : byteArray[ index ];
                theInteger += ( currentByte << ( ( ( byteArray.length - 1 ) * 8 ) - ( index * 8 ) ) );
            }
        } else if( byteOrder == ByteOrder.LITTLE_ENDIAN ){
            for( int index = 0; index < byteArray.length; ++index ){
                int currentByte = ( byteArray[ index ] < 0 ) ? ( byteArray[ index ] + 256 ) : byteArray[ index ];
                theInteger += ( currentByte << ( index * 8 ) );
            }
        }

        return( theInteger );
    }
}