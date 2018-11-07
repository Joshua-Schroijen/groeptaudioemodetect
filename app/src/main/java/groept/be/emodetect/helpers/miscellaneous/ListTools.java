package groept.be.emodetect.helpers.miscellaneous;

import java.util.List;

public class ListTools {
    public static <T>
    T[] getFromTo( List<T> list, T[] destinationArray, int startIndex, int endIndex ){
        for( int listIndex = startIndex; listIndex <= endIndex; ++listIndex ){
            int arrayIndex = listIndex - startIndex;
            destinationArray[ arrayIndex ] = list.get( listIndex );
        }

        return( destinationArray );
    }
}
