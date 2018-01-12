package edu.nl.tue.tink.Tgraphs.algorithms;

import edu.nl.tue.tink.Tgraphs.Tgraph;

/**
 * @param <K> key type
 * @param <VV> vertex value type
 * @param <EV> edge value type
 * @param <T> the return type
 */
public interface TGraphAlgorithm<K, VV, EV, N, T> {

    public T run(Tgraph<K, VV, EV, N> input) throws Exception;
}
