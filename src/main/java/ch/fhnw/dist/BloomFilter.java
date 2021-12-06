package ch.fhnw.dist;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation of a Bloom Filter for Strings implementing Collection<String>.
 */
public class BloomFilter implements Collection<String> {
    private final int nElements;
    private final double pErrorProbability;

    private boolean[] filter;
    private int hashFunctionSize;

    private int size;

    public BloomFilter(int nElements, double pErrorProbability) {
        this.nElements = nElements;
        this.pErrorProbability = pErrorProbability;

        initializeFilter();
    }

    /**
     * Create new filter and calculate hash function size
     */
    private void initializeFilter() {
        size = 0;

        int filterSize = (int) -Math.ceil((nElements * Math.log(pErrorProbability)) / Math.pow(Math.log(2), 2));
        this.hashFunctionSize = (int) Math.ceil((filterSize / (double)nElements) * Math.log(2));

        filter = new boolean[filterSize];
    }

    /**
     * Calculate the hash of a given string with a seed
     * @param seed
     * @param toHash
     * @return
     */
    private long hash(int seed, String toHash) {
        return Math.abs(Hashing.murmur3_128(seed).hashString(toHash, StandardCharsets.UTF_8).asLong());
    }

    /**
     * Amount of elements included in the collection
     * @return
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Check if collection is empty
     * @return
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Is the element included in the collection, can return true even though the element is not in the collection
     * Cannot return false even though the element is in the collection
     * @param o
     * @return
     */
    @Override
    public boolean contains(Object o) {
        if(!(o instanceof String)) {
            return false;
        }

        for (int i = 0; i < hashFunctionSize; i++) {
            int hash = (int) (hash(i, (String)o) % filter.length);
            if(!filter[hash]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<String> iterator() {
        throw new UnsupportedOperationException("bloom filter cannot iterate over elements");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("bloom filter cannot return array");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("bloom filter cannot return array");
    }

    /**
     * Add a string to the collection
     * @param s
     * @return
     */
    @Override
    public boolean add(String s) {
        ++size;

        for (int i = 0; i < hashFunctionSize; i++) {
            int hash = (int) (hash(i, s) % filter.length);
            filter[hash] = true;
        }

        return true;
    }

    /**
     * Cannot remove element from collection as bloom filter cannot remove elements except by clearing the filter
     * or with a special version of the filter
     * @param o
     * @return
     */
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("bloom filter cannot remove element");
    }

    /**
     * Check if the collection contains all elements of the given collection
     * @param c
     * @return
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    /**
     * Add all elements of the given collection to the collection
     * @param c
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends String> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("bloom filter cannot remove elements");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("bloom filter cannot remove elements");
    }

    /**
     * Remove every element and recreate the filter
     */
    @Override
    public void clear() {
        initializeFilter();
    }

    public int getNumberOfElements() {
        return nElements;
    }

    public double getAcceptedErrorProbability() {
        return pErrorProbability;
    }

    public int getHashFunctionSize() {
        return hashFunctionSize;
    }

    public int getFilterSize() {
        return this.filter.length;
    }

}
