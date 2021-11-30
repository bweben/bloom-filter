package ch.fhnw.dist;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class BloomFilter implements Collection<String> {
    private int nElements;
    private double pErrorProbability;

    private byte[] filter;
    private int hashFunctionSize;

    private int size;

    public BloomFilter(int nElements, double pErrorProbability) {
        this.nElements = nElements;
        this.pErrorProbability = pErrorProbability;

        generateFilter();
    }

    private void generateFilter() {
        size = 0;

        int filterSize = (int) -Math.ceil((nElements * Math.log(pErrorProbability)) / Math.pow(Math.log(2), 2));
        hashFunctionSize = (int) Math.ceil((filterSize / nElements) * Math.log(2));

        filter = new byte[filterSize];
        Arrays.fill(filter, (byte) 0);
    }

    private byte[] hash(int seed, byte[] toHash) {
        return Hashing.murmur3_128(seed).hashBytes(toHash).asBytes();
    }

    private byte[] hashMultiple(String toHash) {
        byte[] hash = toHash.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < hashFunctionSize; i++) {
            hash = hash(i, hash);
        }

        return hash;
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
        byte[] hash = hashMultiple((String) o);

        for (int i = 0; i < hash.length; i++) {
            if (filter[i] == 0) {
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
        byte[] hash = hashMultiple(s);

        for (int i = 0; i < hash.length; i++) {
            filter[i] = 1;
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
        generateFilter();
    }
}
