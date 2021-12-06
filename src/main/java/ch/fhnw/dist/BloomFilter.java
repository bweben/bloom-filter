package ch.fhnw.dist;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

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

    private void initializeFilter() {
        size = 0;

        int filterSize = (int) -Math.ceil((nElements * Math.log(pErrorProbability)) / Math.pow(Math.log(2), 2));
        this.hashFunctionSize = (int) Math.ceil((filterSize / (double)nElements) * Math.log(2));

        filter = new boolean[filterSize];
        Arrays.fill(filter, false);
    }

    private long hash(int seed, String toHash) {
        return Math.abs(Hashing.murmur3_128(seed).hashString(toHash, StandardCharsets.UTF_8).asLong());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

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

    @Override
    public boolean add(String s) {
        ++size;

        for (int i = 0; i < hashFunctionSize; i++) {
            int hash = (int) (hash(i, s) % filter.length);
            filter[hash] = true;
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("bloom filter cannot remove element");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

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
