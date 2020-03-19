/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.utilities;

/**
 * A custom primitive byte collection class that does not cause auto-boxing when storing and retrieving elements.
 */
public class ByteList {

    private static final int MIN_CAPACITY_INCREMENT = 12;

    /**
     * Static empty byte array. Prevents the need to create multiple copies when the list is emptied.
     */
    public static final byte [] EMPTY = new byte [0];

    public transient byte [] mByteArray;
    private int mCount = 0;
    // This variable will keep reference of any modifications to the array. This allows us to catch concurrent modifications.
    private int mModificationCount = 0;

    /**
     * Create a new instance pre-allocated with the data inside bytes.
     * @param bytes
     */
    public ByteList(byte [] bytes) {
        throwIfNull(bytes, "bytes [] == null");
        mByteArray = new byte [bytes.length];
        System.arraycopy(bytes, 0, mByteArray, 0, bytes.length);
        mCount = mByteArray.length;
    }

    /**
     * Utility method for throwing an exception if obj is null.
     *
     * @param obj Object to check for nullness
     * @param message Exception message
     */
    final static void throwIfNull(Object obj, String message) {
        if(obj == null) throw new NullPointerException(message);
    }

    /**
     * Creates a new instance of ByteList, setting the initial capacity using the specified size.
     *
     * @param initialCapacity   The initial capacity the ByteList should be created using.
     */
    public ByteList(int initialCapacity) {
        mByteArray = new byte [initialCapacity];
        mCount = 0;
    }

    /**
     * Creates a new instance of ByteList, with an empty internal array.
     */
    public ByteList() {
        // At the expense of a little extra memory, initialize the array to be the MIN_CAPACITY_INCREMENT.
        mByteArray = EMPTY;
        mCount = 0;
    }

    /**
     * Returns if this {@code ByteList} contains no elements. This implementation
     * tests, whether {@code size} returns 0.
     *
     * @return {@code true} if this {@code ByteList} has no elements, {@code false}
     *         otherwise.
     *
     * @see #size
     */
    public boolean isEmpty() {
        return mCount == 0;
    }

    /**
     * Returns the number of elements in this {@code ByteList}.
     *
     * @return total element count
     */
    public int size() {
        return mCount;
    }

    /**
     * Utility method for copying all elements from this list to data
     *
     * @param data Dest array
     */
    public void copyTo(byte data []) {
        // pass zero and the count of data to copy(byte [], int, int)
        copyTo(data, 0, mCount);
    }

    /**
     * Utility method for copying elements into dest.
     *
     * @param dest Destination array
     * @param start Start index
     * @param length Number of elements to copy.
     */
    public void copyTo(byte dest [], int start, int length) {
        throwIfNull(dest, "data [] cannot be null!");
        if(start + length > mCount) {
            throw new IllegalArgumentException("start exceeds the length of byte data");
        }
        if(length > dest.length) {
            throw new IllegalArgumentException("The data to be copied exceeds the bounds of the buffer.");
        }
        System.arraycopy(mByteArray, start, dest, 0, length);
    }

    /**
     * Sums the entire byte list with not attention paid to carries.
     * @param start     The starting point inside of the list of bytes to being cal
     * @param length    The number of bytes to add.
     *
     * @return  Summation of the entire byte list with no attention paid to carries.
     */
    public byte calculateSumNoCarry(int start, int length) {
        if(start >= mCount) {
            throw new IllegalArgumentException("start = " + start + " exceeds the length of byte data = " + mCount );
        }
        if(start + length > mCount) {
            throw new IllegalArgumentException("The range of bytes = " + (start + length)+ " exceeds the length of byte data = " + mCount);
        }
        byte combinedValue = 0x00;
        for (int i = 0; i < length; i++) {
            combinedValue += mByteArray[i];
        }
        return combinedValue;
    }

    /**
     * Adds newByte at the end of this list.
     *
     * @param newByte   The newByte to be added at the end of this list.
     * @return          Always returns true.
     */
    public boolean add(byte newByte){
        byte [] a = mByteArray;
        int count = mCount;
        // If the current count equals the size of the array, we want to increase its size.
        if(count == a.length ) {
            // Create a temporary array
            byte [] tempArray = new byte[count +
                    (count < (MIN_CAPACITY_INCREMENT / 2) ?
                            MIN_CAPACITY_INCREMENT : count >> 1)];
            // Copy any data in the old list to the temporary array.
            System.arraycopy(a, 0, tempArray, 0, count);
            a = mByteArray = tempArray;
        }
        a[count] = newByte;
        mCount = count + 1;
        mModificationCount++;
        return true;
    }

    /**
     * Adds the array of bytes at the end of this list.
     *
     * @param bytes   The index to add the bytes at inside of this list.
     * @return        Always returns true.
     */
    public boolean addAll(byte [] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes [] == null");
        }
        byte [] a = mByteArray;
        int newDataSize = bytes.length;

        int oldSize = mCount;

        int newSize = newDataSize + oldSize;
        // If the array is not big enough to add the bytes into the array, adjust the size.
        if(newSize > mByteArray.length){
            // Create a temporary array
            byte [] tempArray = new byte[getNewArraySize(newSize - 1)]; // Grows by 50%
            // Copy any data in the old list to the temporary array.
            System.arraycopy(a, 0, tempArray, 0, oldSize);
            mByteArray = a = tempArray;
        }
        // Copy in the new data to the temporary array.
        System.arraycopy(bytes, 0, a, oldSize, newDataSize);
        mCount = newSize;
        mModificationCount++;
        return true;
    }

    /**
     * Adds the array of bytes at index to this list.
     *
     * @param index     The index to add the bytes at inside of this list.
     * @param bytes     The array of bytes to be added to this list at index.
     * @return          Always returns true.
     */
    public boolean addAll(int index, byte [] bytes) {
        if(!mCheckIndex(index)) {
            throw new ArrayIndexOutOfBoundsException("Invalid index, array size is: " + mCount + " index is: " + index);
        }
        if (bytes == null) {
            throw new NullPointerException("bytes [] == null");
        }
        // Copy the old data into into temporary variables.
        byte [] old = mByteArray;
        int newBufferSize = bytes.length;
        int oldSize = mCount;
        int newSize = newBufferSize + oldSize;
        // If the nexsize is less than the size of the array, just add the new data to into the array.
        if(newSize < mByteArray.length) {
            // Move the current data at index after the position of the where the new data will end.
            System.arraycopy(old, index, old, index + newBufferSize, oldSize - index );
        }
        else {
            byte [] temp = new byte [getNewArraySize(newSize - 1)];
            // Copy the old array data from the start of the array all the way until we reach index. If zero, this call does nothing.
            System.arraycopy(old, 0, temp, 0, index);
            // Copy the old array data after index, into the temporary buffer.
            System.arraycopy(old, index, temp, index + newBufferSize, oldSize - index);
            old = mByteArray = temp;
        }
        // Copy the new data into the old array at the specified index.
        System.arraycopy(bytes, 0, old, index, newBufferSize);
        // Update the mCount variable.
        mCount = newBufferSize;
        // Update the modification count.
        mModificationCount++;
        return true;
    }

    /**
     * Adds length of values from bytes starting at {@code 0} index.
     *
     * @param bytes Source array
     * @param length Number of bytes
     *
     * @return Always returns true.
     */
    public boolean addSubsetOfBytes(byte [] bytes, int length) {
        if (bytes == null) {
            throw new NullPointerException("bytes [] == null");
        }
        if(!mCheckSize(bytes, length)) {
            throw new ArrayIndexOutOfBoundsException("length = " + length + "exceeds bytes [] length = " + bytes.length);
        }
        final int oldSize = mCount;
        final int newSize = length + oldSize;

        if(newSize > mByteArray.length) {
            byte [] tempArray = new byte[getNewArraySize(newSize - 1)]; // Grows by 50%
            // Copy any data in the old list to the temporary array.
            System.arraycopy(mByteArray, 0, tempArray, 0, oldSize);
            mByteArray = tempArray;
        }
        System.arraycopy(bytes, 0, mByteArray, oldSize, length);
        mCount = newSize;
        mModificationCount++;
        return true;
    }

    /**
     * Returns the byte currently at index.
     *
     * @param index     The index to return a byte from this list.
     * @return          The byte located at index.
     */
    public byte get(int index) {
        // Perform bounds checking.
        if(!mCheckIndex(index)){
            throw new ArrayIndexOutOfBoundsException("Invalid index, array size is: " + mCount + " index is: " + index);
        }
        return mByteArray[index];
    }

    /**
     * Returns the last byte in the list.
     *  Note: this isn't safe to call if the contents of this list is empty.
     * @return  The last byte in the list. Equivalent to calling {@link #get(int) get(size() - 1)}.
     */
    public byte getLast() {
        return mByteArray[mCount - 1];
    }

    /**
     * Replaces the byte at index with the provided byte, and returns the byte the was already at index.
     *
     * @param newByte  The new byte to replace the current byte at index.
     * @param index    The index to add the new byte.
     *
     * @return         Returns the byte that was previously at index.
     */
    public byte set(byte newByte, int index) {
        if(!mCheckIndex(index)){
            throw new ArrayIndexOutOfBoundsException("Invalid index, array size is: " + mCount + " index is: " + index);
        }
        // Copy the byte that is current at 'position'.
        byte oldByte = mByteArray[index];
        // Store the b at position.
        mByteArray[index] = newByte;
        return oldByte;
    }

    /**
     * Removes the byte at index.
     *
     * @param index     The location on the array to remove the byte.
     *
     * @return          Always returns true.
     */
    public boolean remove(int index) {
        if(!mCheckIndex(index)) {
            throw new ArrayIndexOutOfBoundsException("Invalid index, array size is: " + mCount + " index is: " + index);
        }
        byte [] a = mByteArray;
        /// Shift over all of the elements
        System.arraycopy(a, index + 1, a, index, (--mCount - index));
        mModificationCount++;
        return true;
    }

    /**
     * Removes the bytes between start (inclusive) and end (exclusive) from this list.
     *
     * @param start     The start position to begin removing the bytes. (Inclusive)
     * @param end       The end position of which bytes should be removed. (Exclusive)
     *
     * @return          Always return true.
     */
    public boolean removeRange(int start, int end, boolean fillZeros) {
        if(start == end){
            return false;
        }
        if(!mCheckRange(start, end)) {
            throw new ArrayIndexOutOfBoundsException("The range cannot exceed the array bounds, array size is: " + mCount);
        }
        int size = mCount;
        byte [] a = mByteArray;
        // Moved the data starting at end and place it at start.
        System.arraycopy(a, end, a, start, size - end);
        // The the array zeros starting from the new end all the way to the old end.
        int removeAmount = (end - start);
        if(fillZeros) {
            for (int i = size - removeAmount; i < size; i++) {
                mByteArray[i] = 0;
            }
        }

        mCount = size - removeAmount;
        return true;
    }

    /**
     * Resets the list count back to zero. This will not actually clear the internal data set.
     * Since this list wraps around primitive array, we do not need to loop through the data set
     * and manually set the indexes to null. We can cheat and get a performance boost at the expense of having a couple dozen bytes remaining in heap.
     *
     * @return      Always returns true.
     */
    public boolean clear() {
        for(int i = 0; i < mCount; i++) {
            mByteArray[i] = 0;
        }
        mCount = 0;
        return true;
    }

    /**
     * Resets the internal data set and the list count back to zero.
     * Be wary, this slightly less performant than @see ByteList#clear().
     * This has further peformance implications when calling andy of the @see ByteList#add()*
     * methods because it will require the array to grown to an acommadating size.
     *
     * @param resize    Indicates if you'd like this array to be resized to its initial size of zero.
     */
    public boolean clear(boolean resize) {
        if(mCount == 0) {
            return true;
        }
        clear();
        if(resize) {
            mByteArray = EMPTY;
        }
        return true;
    }

    /**
     * Indicates if the byte is contained inside of this list.
     *
     * @param b     The byte to find inside of this list.
     *
     * @return      Returns true if the byte is found inside of this list othewise, false is returned.
     */
    public boolean contains(byte b) {
        for(int i = 0; i < mCount; i++ ){
            // We found the first index of this byte, return true.
            if( b == mByteArray[i]){
                return true;
            }
        }
        return false;
    }

    /**
     * Looks for the first index of byteToFind inside of this list.
     *
     * @param byteToFind     The byte value to check this list for.
     *
     * @return      The index of where byteToFind was found. If the byte is not found inside of the list, -1 is returned.
     */
    public int indexOf(byte byteToFind) {
        for(int i = 0; i < mCount; i++ ){
            // We found the first index of this byte, return true.
            if(byteToFind == mByteArray[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds all matching values inside of this list and removes them from the list.
     * @param value The value to find inside of the list.
     */
    public void findAllValuesAndRemove(byte value) {
        for(int i = 0; i < mCount; i++ ) {
            if(value == mByteArray[i]){
                // We need to decrement i, so we can catch the item(s) moved to the left...
                // i.e, if two duplicate values are side, by side. This will decrement will make sure we catch the second.
                remove(i--);
            }
        }
    }

    /**
     * Returns the next index of byteToFind after the specified index.
     *
     * @param byteToFind         The byte value to check this list for.
     * @param index     The starting index to begin the search for byteToFind.
     *
     * @return          The index of where byteToFind was found. If the byte is not found inside of the list, -1 is returned.
     */
    public int indexOfAfter(byte byteToFind, int index) {
        // Bounds checking.
        if(!mCheckIndex(index)) {
            throw new ArrayIndexOutOfBoundsException("Invalid index, array size is: " + mCount + " index is: " + index);
        }
        for(int i = index; i < mCount; i++ ){
            // We found the first index of this byte, return true.
            if( byteToFind == mByteArray[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last occurrence of byteToFind inside of this list.
     *
     * @param byteToFind    The byte value to check this list for.
     * @return              The last index of where byteToFind was found. If the byte is not found inside of the list, -1 is returned.
     */
    public int lastIndexOf(byte byteToFind) {
        for(int i = mCount; i >= 0; --i) {
            // We found the first index of this byte, return true.
            if( byteToFind == mByteArray[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper method that returns the new size an array of initialSize should be.
     *
     * @param initialSize   The current size of the array.
     *
     * @return  The new size the array should be.
     */
    private int getNewArraySize(int initialSize) {
        if(initialSize < (MIN_CAPACITY_INCREMENT / 2)){
            return initialSize + MIN_CAPACITY_INCREMENT;
        }
        else {
            return initialSize + (initialSize >> 1);
        }
    }

    /**
     * Helper method for checking the index against the arrays bounds.
     *
     * @param index     The index to check against the arrays bounds.
     * @return          Returns true if the index within the arrays bounds.
     */
    private boolean mCheckIndex(int index) {
        // Make sure that index never exceeds the bounds of the array.
        return !(index < 0 || index >= mCount);
    }

    private final static boolean mCheckIndex(byte [] array, int index) {
        // Make sure that index never exceeds the bounds of the array.
        return !(index < 0 || index >= array.length);
    }

    private final static boolean mCheckSize(byte [] array, int size) {
        // Make sure that index never exceeds the bounds of the array.
        return !(size < 0 || size > array.length);
    }

    /**
     * Helper method for checking that range of values is valid in respect to the arrays upper and lower bound.
     *
     * @param start     The start index of the range.(Inclusive)
     * @param end       The end index of the range. (Exclusive)
     * @return          Returns true if the range within the arrays bounds.
     */
    private boolean mCheckRange(int start, int end) {
        // Make sure that start and end are within the bounds of the array. Also make sure start never exceeds end.
        return !(start < 0 || start >= mCount || end > mCount || start > end);
    }

    @Override
    public String toString() {
        if(mCount == 0){
            return "[]";
        }
        StringBuilder builder = new StringBuilder("[");
        for(int i = 0; i < mCount; i++) {
            if(i == mCount - 1) {
                builder.append(mByteArray[i]);
            }
            else {
                builder.append(mByteArray[i]).append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Compares this instance with the object.
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ByteList)){
            return false;
        }
        ByteList byteList = (ByteList)o;

        if(byteList.mCount != mCount) {
            return false;
        }
        for(int i = 0; i < mCount; i++) {
            if(mByteArray[i] != byteList.mByteArray[i]){
                return false;
            }
        }
        return true;
    }
}