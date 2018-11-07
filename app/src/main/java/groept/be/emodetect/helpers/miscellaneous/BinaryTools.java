package groept.be.emodetect.helpers.miscellaneous;

public class BinaryTools{
    public static byte shortGetMSB( short shortVariable ){
        return( ( byte ) ( ( shortVariable & 0xFF00 ) >> 8 ) );
    }

    public static byte shortGetLSB( short shortVariable ){
        return( ( byte ) ( shortVariable & 0x00FF ) );
    }

    public static byte intGetMSB( int intVariable ){
        return( ( byte ) ( ( intVariable & 0xFF000000 ) >> 24 ) );
    }

    public static byte intGetMSH( int intVariable ){
        return( ( byte ) ( ( intVariable & 0xFF0000 ) >> 16 ) );
    }

    public static byte intGetMSL( int intVariable ){
        return( ( byte ) ( ( intVariable & 0xFF00 ) >> 8 ) );
    }

    public static byte intGetLSB( int intVariable ){
        return( ( byte ) ( intVariable & 0xFF ) );
    }

    public static short reverseEndianness( short shortVariable ){
        short resultShort = 0;
        byte lowByte = shortGetLSB( shortVariable );
        byte highByte = shortGetMSB( shortVariable );

        resultShort += highByte;
        resultShort += ( short )( lowByte << 8 );

        return( resultShort );
    };

    public static int reverseEndianness( int intVariable ){
        int resultInt = 0;
        byte lowByte = intGetLSB( intVariable );
        byte middleLowByte = intGetMSL( intVariable );
        byte middleHighByte = intGetMSH( intVariable );
        byte highByte = intGetMSB( intVariable );

        resultInt += highByte;
        resultInt += ( int )( middleHighByte << 8 );
        resultInt += ( int )( middleLowByte << 16 );
        resultInt += ( int )( lowByte << 24 );

        return( resultInt );
    }
}
