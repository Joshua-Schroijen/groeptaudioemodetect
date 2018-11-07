package groept.be.emodetect.helpers.fileformathelpers;

import java.io.EOFException;
import java.util.ArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import groept.be.emodetect.helpers.miscellaneous.ArrayTools;
import groept.be.emodetect.helpers.miscellaneous.BinaryTools;

/*************************************************************************
 * This class is designed to give programs an easy way to work with      *
 * PCM audio. The using program can use ArrayLists of Bytes, Shorts &    *
 * Integers to represent audio samples (depending on the bit depth of    *
 * the audio) in a transparent way. This container class will store      *
 * the data, read data from WAV files, write data to WAV files and       *
 * append data to WAV files on command in a very easy manner.            *
 *                                                                       *
 * (Note #1: Some of this class' methods return                          *
 *           ArrayList< Number >. You are expected to cast               *
 *           this type to the ArrayList generic type you are using       *
 *           to store samples when employing one of these and a more     *
 *           specific type than Number is needed.                        *
 * (Note #2: You can convert from arrays to ArrayLists and vica versa    *
 *           with our custom ArrayTools class. See its documentation     *
 *           for more information.                                       *
 * (Note #3: The bit depth and the concrete type of a sample always      *
 *           have to match. If you use a bit depth of 8 bits, you have   *
 *           to give ArrayLists of Byte to this class' methods.          *
 *           If you use a bit depth of 16 bits, you have to give         *
 *           ArrayLists of Short to this class' methods.                 *
 *           If you use a bit depth of 24 bits, you have to give         *
 *           ArrayLists of Integer to this class' methods. Failing to    *
 *           follow these rules will give you an IlegalArgumentException *
 *           when executing operations with such invalid data that might *
 *           break this described correspondence.                        *
 *                                                                       *
 * @author Joshua Schroijen <Joshua.Schroijen@student.kuleuven.be>       *
 * @version 1.0                                                          *
 * **********************************************************************/
public class WAVFile {
    public static class Information {
        private int noChannels;
        private int sampleRate;
        private int bitDepth;

        public Information( int noChannels, int sampleRate, int bitDepth ){
            this.noChannels = noChannels;
            this.sampleRate = sampleRate;
            this.bitDepth = bitDepth;
        }

        public int getNoChannel(){ return( noChannels ); }
        public int getSampleRate(){ return( sampleRate ); }
        public int bitDepth(){ return( bitDepth ); }

        public String toString(){
            String objectString =
                "# channels = " +
                noChannels +
                ", sample rate = " +
                sampleRate +
                " Hz, bit depth = " +
                bitDepth +
                " bits";

            return( objectString );
        }
    }

    public static WAVFile.Information readWAVFileInformation( String filename ) throws FileNotFoundException, IOException {
        FileInputStream inputFile = new FileInputStream( new File( filename ) );

        inputFile.skip( 22 );

        byte[] noChannelsRead = new byte[ 2 ];
        inputFile.read( noChannelsRead );
        int noChannels = ArrayTools.byteArrayToInt( noChannelsRead, ByteOrder.LITTLE_ENDIAN );
        byte[] sampleRateRead = new byte[ 4 ];
        inputFile.read( sampleRateRead );
        int sampleRate = ArrayTools.byteArrayToInt( sampleRateRead, ByteOrder.LITTLE_ENDIAN );

        inputFile.skip( 6 );

        byte[] bitDepthRead = new byte[ 2 ];
        inputFile.read( bitDepthRead );
        int bitDepth = ArrayTools.byteArrayToInt( bitDepthRead, ByteOrder.LITTLE_ENDIAN );

        return( new WAVFile.Information( noChannels, sampleRate, bitDepth ) );
    }

    public static WAVFile getWAVFileFromFile( String filename ) throws FileNotFoundException, IOException {
        WAVFile.Information audioInformation = readWAVFileInformation( filename );

        RandomAccessFile inputFile = new RandomAccessFile( filename, "r" );
        inputFile.seek( 44 ); // Jump to data

        ArrayList< ? extends Number > readSamples = null;
        try{
            if( audioInformation.bitDepth == 8 ){
                readSamples = new ArrayList< Byte >();

                while( true ){
                    ( ( ArrayList< Byte > ) readSamples ).add( inputFile.readByte() );
                }
            } else if( audioInformation.bitDepth == 16 ){
                readSamples = new ArrayList< Short >();

                while( true ){
                    ( ( ArrayList< Short > ) readSamples ).add( BinaryTools.reverseEndianness( inputFile.readShort() ) );
                }
            } else if( audioInformation.bitDepth == 24 ){
                readSamples = new ArrayList< Integer >();

                while( true ){
                    ( ( ArrayList< Integer > ) readSamples ).add( BinaryTools.reverseEndianness( inputFile.readInt() ) );
                }
            }
        } catch( EOFException e ){
            return(
                new WAVFile(
                    ( ( short )( audioInformation.noChannels ) ),
                    audioInformation.sampleRate,
                    ( ( short )( audioInformation.bitDepth ) ),
                    readSamples )
            );
        }

        // Redundant code - should never be reached actually
        return(
            new WAVFile(
                ( ( short )( audioInformation.noChannels ) ),
                audioInformation.sampleRate,
                ( ( short )( audioInformation.bitDepth ) )
            ) );
    }

    /**************************
     * PRIVATE STATIC METHODS *
     *************************/
    /* Some validator methods for internal use
     */
    private static boolean isCorrectBitDepth( short bitDepth ){
        return( ( bitDepth == 8 ) ||
                ( bitDepth == 16 ) ||
                ( bitDepth == 24 ) );
    }

    private static boolean isCorrectStorageType( int bitDepth, Class type ){
        return( ( ( bitDepth == 8 ) && ( type == Byte.class ) ) ||
                ( ( bitDepth == 16 ) && ( type == Short.class ) ) ||
                ( ( bitDepth == 24 ) && ( type == Integer.class ) ) );
    }

    /*******************
     * PRIVATE METHODS *
     ******************/
    private void rewriteLengthFields( RandomAccessFile outputFile ) throws IOException {
        /* We store the original file pointer location so we can
           restore it later for the using programs convenience */
        long originalFilePointer = outputFile.getFilePointer();

        /* First we have to update the length field in the WAV header
           Remember, the header is 12 bytes, byte 5 to 8 contain
           a little-endian integer describing the file length excluding
           the initial "RIFF" string (i.e. it contains the length of the
           data in bytes + 36) */
        outputFile.seek( 4 );
        outputFile.writeInt( BinaryTools.reverseEndianness( ( dataLengthInBytes + 36 ) ) );

         /* Than we have to update the length field in the WAV data chunk
            To do so, we always have to skip the next 32 bytes of the file
            (4 bytes remain in the WAV header, than we have 24 irrelevant
             bytes belonging to the format chunk and than we have to skip the
             first 4 tag bytes of the WAV data chunk) */
        outputFile.skipBytes( 32 );
        outputFile.writeInt( BinaryTools.reverseEndianness( dataLengthInBytes ) );

        /* Reset the file pointer to the original location for the using
           program's convenience */
        outputFile.seek( originalFilePointer );
    }

    private void appendBinaryData( byte[] newBinaryData,
                                   RandomAccessFile outputFile ) throws IOException {
        if( newBinaryData != null ){ // There is something to append
            /* We store the original file pointer location so we can
               restore it later for the using programs convenience */
            long originalFilePointer = outputFile.getFilePointer();

            /* Go to the end of the file and actually append the PCM data
               (while first converting it to a byte array) */
            outputFile.seek( outputFile.length() );

            outputFile.write( newBinaryData );

            /* Reset the file pointer to the original location for the using
               program's convenience */
            outputFile.seek( originalFilePointer );
        }
    }

    private void appendBinaryData( ArrayList< ? extends Number > newPCMData,
                                   RandomAccessFile outputFile ) throws IOException {
        appendBinaryData( getPCMDataByteArray( newPCMData ), outputFile );
    }

    /* This helper method will convert the PCMData ArrayList into a byte array
     */
    private byte[] getPCMDataByteArray( ArrayList< ? extends Number > PCMData ){
        if( PCMData != null ){
            if( !PCMData.isEmpty() ){
                if( PCMData.get( 0 ) instanceof Byte ){
                    return( ArrayTools.byteArrayListToByteArray( ( ArrayList< Byte > )( PCMData ) ) );
                } else if ( PCMData.get( 0 ) instanceof Short ){
                    return( ArrayTools.shortArrayListToByteArray( ( ArrayList< Short > )( PCMData ), ByteOrder.LITTLE_ENDIAN ) );
                } else if ( PCMData.get( 0 ) instanceof Integer ){
                    return( ArrayTools.integerArrayListToByteArray( ( ArrayList< Integer > )( PCMData ), ByteOrder.LITTLE_ENDIAN ) );
                } else {
                    throw new IllegalArgumentException( "Queried to convert ArrayList of illegal data type (for WAVFile class) to byte array!" );
                }
            } else {
                throw( new IllegalArgumentException( "WAV file contains empty ArrayList, which is illegal!" ) );
            }
        } else {
            /* If there is no data to append, we can return an empty byte array.
               When an I/O or buffer write function gets this empty byte array
               as a parameter, it will safely just write nothing to the file. */
            return( new byte[ 0 ] );
        }
    }

    private byte[] getWAVHeader(){
        ByteBuffer wavHeader = ByteBuffer.allocate( 12 ).order( ByteOrder.LITTLE_ENDIAN );

        /* The first field in a WAVE file header is the 4-byte
         * string "RIFF" */
        wavHeader.put( new String( "RIFF" ).getBytes() );

        /* The second field in a WAVE file header is a 4-byte
         * number representing the total file size of all data
         * after this field */
        wavHeader.putInt( dataLengthInBytes + 36 );

        /* The last field in a WAVE file header is the 4-byte
         * string "WAVE" */
        wavHeader.put( new String( "WAVE" ).getBytes() );

        return( wavHeader.array() );
    }

    private byte[] getWAVFormatChunk(){
        /* The total length in bytes of a WAV format chunk is 24 bytes
         * */
        ByteBuffer wavFormatChunk = ByteBuffer.allocate( 24 ).order( ByteOrder.LITTLE_ENDIAN );

        /* This 4-byte string indicates the start of the format chunk
         * in the file */
        wavFormatChunk.put( new String( "fmt " ).getBytes() );

        /* Immediately after the fmt string, we have a 4-byte field
         * specifying the size of the format chunk excluding these first
         * 2 fields. For our purposes, this will always be 16. */
        wavFormatChunk.putInt( 16 );

        /* This short codes the 2-byte format field and should always contain 1
         * to indicate PCM audio */
        wavFormatChunk.putShort( ( short )( 1 ) );

        /* The next 2-byte field of the WAV format chunk indicates the number
         * of channels passed to us in the noChannels parameter */
        wavFormatChunk.putShort( noChannels );

        /* The following 4-byte field of the format chunk indicates the
         * sample rate ( in Hz ) */
        wavFormatChunk.putInt( sampleRate );

        /* This 4-byte field is a convenience field holding the amount of bytes
         * in this file holding one second of audio */
        wavFormatChunk.putInt( noChannels * ( bitDepth / 8 ) * sampleRate );

        /* The 2-byte penultimate field of the WAV format chunk is a convenience field
         * that holds the total size in bytes of one audio frame ( the collection
         * of all audio samples for all channels for one instance in time ) */
        wavFormatChunk.putShort( ( short )( noChannels * ( bitDepth / 8 ) ) );

        /* The last, 2 byte-field of the format chunk indicates the bit depth
         * (i.e. 8/16/32 bits per sample) */
        wavFormatChunk.putShort( bitDepth );

        return( wavFormatChunk.array() );
    }

    private byte[] getWAVDataChunkHead(){
        ByteBuffer wavDataChunkHead = ByteBuffer.allocate( 8 ).order( ByteOrder.LITTLE_ENDIAN );

        /* The first field in the data chunk is just the 4-byte
         * string "data" */
        wavDataChunkHead.put( new String( "data" ).getBytes() );

        /* The second field in the data chunk is a 4-byte field
         * containing the size of the PCMData that will follow it
         * in bytes. The 2 in the following expression comes from
         * the fact that a short has a length of 2 bytes on the
         * Java platform of course. */
        wavDataChunkHead.putInt( dataLengthInBytes );

        return( wavDataChunkHead.array() );
    }

    private byte[] getWAVDataChunkBody(){
        return( getPCMDataByteArray( PCMData ) );
    }

    /*************************
     * PUBLIC STATIC METHODS *
     ************************/
    public static ArrayList< Short > generateSineWaveSamples( short noChannels,
                                                              int sampleRate,
                                                              int secondsToGenerate,
                                                              int sineWaveFrequency ){
        int noFramesToGenerate = ( sampleRate * secondsToGenerate );
        double angularFrequency = ( 2 * Math.PI * ( ( double )( sineWaveFrequency ) / ( double ) ( sampleRate ) ) );
        ArrayList< Short > samples = new ArrayList<>();

        for( int frame = 0; frame < noFramesToGenerate; ++frame ){
            short currentNewSample = ( short )( Math.round( 32767 * Math.sin ( ( angularFrequency * frame ) ) ) );

            for ( int channel = 0; channel < noChannels; ++channel ){
                samples.add( currentNewSample );
            }
        }

        return( samples );
    }

    public static WAVFile generateSineWaveWAVFile( short noChannels,
                                                   int sampleRate,
                                                   int secondsToGenerate,
                                                   int sineWaveFrequency ){
        return( new WAVFile( noChannels,
                sampleRate,
                ( short )( 16 ),
                generateSineWaveSamples( noChannels,
                        sampleRate,
                        secondsToGenerate,
                        sineWaveFrequency ) ) );
    }

    /******************
     * PRIVATE FIELDS *
     *****************/
    /* These fields will be accessible to the outside
     */
    private ArrayList< ? extends Number > PCMData;

    private short noChannels;
    private int sampleRate;
    private short bitDepth;

    /* This is a field for mostly internal use
     */
    private int dataLengthInBytes;

    /****************
     * CONSTRUCTORS *
     ***************/
    /* This constructor produces a WAVFile with no data
     * in it yet.
     *
     * @param noChannels The number of channels of audio in the file
     * @param sampleRate The sample rate of the audio in Hz
     * @param bitDepth   The bit depth of the audio (either 8, 16, 24)
     * @throws IllegalArgumentException if an invalid bit depth is passed
     */
    public WAVFile( short noChannels,
                    int sampleRate,
                    short bitDepth ){
        this.noChannels = noChannels;
        this.sampleRate = sampleRate;
        this.dataLengthInBytes = 0;

        if( !isCorrectBitDepth( bitDepth ) ){
            throw( new IllegalArgumentException( "Attempt to build WAV-file with invalid bit depth" ) );
        } else {
            this.bitDepth = bitDepth;
        }
    }

    /* This constructor produces a WAVFile with initial data
     *
     * @param noChannels The number of channels of audio in the file
     * @param sampleRate The sample rate of the audio in Hz
     * @param bitDepth   The bit depth of the audio (either 8, 16, 24)
     * @param PCMData    An ArrayList containing the desired initial audio
     *                   sample data
     * @throws IllegalArgumentException if an invalid bit depth or a
     *         sample ArrayList containing a type incompatible with the
     *         bit depth is passed
     */
    public WAVFile( short noChannels,
                    int sampleRate,
                    short bitDepth,
                    ArrayList< ? extends Number > PCMData ) {
        this( noChannels, sampleRate, bitDepth );

        if( PCMData != null ){
            if( !PCMData.isEmpty() ){
                if( !isCorrectStorageType( bitDepth, PCMData.get( 0 ).getClass() ) ){
                    throw( new IllegalArgumentException( "Cannot instantiate with data WAV file because ArrayList contains objects of type incompatible with bit depth" ) );
                } else {
                    this.PCMData = PCMData;
                    this.dataLengthInBytes = ( PCMData.size() * ( bitDepth / 8 ) );
                }
            } else {
                throw( new IllegalArgumentException( "Cannot instantiate with data WAV file with empty ArrayList" ) );
            }
        } else {
            throw( new IllegalArgumentException( "Cannot instantiate with data WAV file with null" ) );
        }
    }

    /******************
     * PUBLIC METHODS *
     *****************/

    /* Returns the contained PCM audio samples as an ArrayList
     *
     * @return The contained PCM audio samples as an ArrayList
     */
    public ArrayList< ? extends Number > getPCMData(){
        return( PCMData );
    }

    /* Sets the contained PCM audio samples to the new samples
     * contained in newPCMData
     *
     * @param newPCMData The ArrayList containing the new PCM audio
     *                   samples
     * @throws IllegalArgumentException When the ArrayList contains a
     *                                  type that is incompatible
     *                                  with the bit depth or is empty
     */
    public void setPCMData( ArrayList< ? extends Number > newPCMData ){
        if( newPCMData != null ){
            if( !newPCMData.isEmpty() ){
                if( !isCorrectStorageType( bitDepth, newPCMData.get( 0 ).getClass() ) ){
                    throw( new IllegalArgumentException( "Cannot set WAV file data because new data's type is incompatible with bit depth" ) );
                } else {
                    PCMData = newPCMData;
                    dataLengthInBytes = ( newPCMData.size() * ( bitDepth / 8 ) );
                }
            } else {
                throw( new IllegalArgumentException( "Cannot set WAV file data to empty ArrayList" ) );
            }
        } else {
            this.PCMData = newPCMData;
            this.dataLengthInBytes = 0;
        }
    }

    /* Sets the contained PCM audio samples to the new samples
     * contained in newPCMData and writes out all the data
     * this WAVFile contains to the file specified in outputFile
     *
     * @param newPCMData The ArrayList containing the new PCM audio
     *                   samples
     * @param outputFile An initialized FileOutputStream pointing to
     *                   the desired output file
     * @throws IllegalArgumentException When the ArrayList contains a
     *                                  type that is incompatible
     *                                  with the bit depth or is empty
     * @throws IOException When outputFile is not a properly initialized
     *                     FileOutputStrema object
     */
    public void setPCMData( ArrayList< Number > newPCMData,
                            RandomAccessFile outputFile ) throws IOException {
        setPCMData( newPCMData );
        writeEverythingToFile( outputFile );
    }

    public void appendData( ArrayList< ? extends Number > newPCMData ){
        if( newPCMData != null ){
            if( !newPCMData.isEmpty() ){
                if( !isCorrectStorageType( bitDepth, newPCMData.get( 0 ).getClass() ) ){
                    throw( new IllegalArgumentException( "Cannot append data to WAV file because data type doesn't correspond to bit depth" ) );
                } else{
                    switch( bitDepth ){
                        case( 8 ):
                            if( PCMData == null ){
                                PCMData = new ArrayList< Byte >();
                            }
                            ( ( ArrayList< Byte > )( PCMData ) ).addAll( ( ArrayList< Byte > )( newPCMData ) );
                            break;

                        case( 16 ):
                            if( PCMData == null ){
                                PCMData = new ArrayList< Short >();
                            }
                            ( ( ArrayList< Short > )( PCMData ) ).addAll( ( ArrayList< Short > )( newPCMData ) );
                            break;

                        case( 24 ):
                            if( PCMData == null ){
                                PCMData = new ArrayList< Integer >();
                            }
                            ( ( ArrayList< Integer > )( PCMData ) ).addAll( ( ArrayList< Integer > )( newPCMData ) );
                            break;
                    }

                    dataLengthInBytes += ( newPCMData.size() * ( bitDepth / 8 ) );
                }
            }
        }
    }

    public void appendData( ArrayList< ? extends Number > newPCMData,
                            RandomAccessFile outputFile ) throws IOException {
        if( newPCMData != null ){ // Only now is there actually something to append to a file
            appendData( newPCMData );

            /* Checking the ArrayList newPCMData for proper content
               to append is done in the writeless appendData function! */
            appendBinaryData( newPCMData, outputFile );

            rewriteLengthFields( outputFile );
        }
    }

    public void writeHeadersToFile( RandomAccessFile outputFile ) throws IOException {
        long originalFilePointer = outputFile.getFilePointer();

        outputFile.seek( 0 );

        outputFile.write( getWAVHeader() );
        outputFile.write( getWAVFormatChunk() );
        outputFile.write( getWAVDataChunkHead() );

        outputFile.seek( originalFilePointer );
    }

    public void writeEverythingToFile( RandomAccessFile outputFile ) throws IOException {
        long originalFilePointer = outputFile.getFilePointer();

        outputFile.write( getWAVHeader() );
        outputFile.write( getWAVFormatChunk() );
        outputFile.write( getWAVDataChunkHead() );
        outputFile.write( getWAVDataChunkBody() );

        outputFile.seek( originalFilePointer );
    }
}
