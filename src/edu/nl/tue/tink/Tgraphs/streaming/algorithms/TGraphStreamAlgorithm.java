package edu.nl.tue.tink.Tgraphs.streaming.algorithms;

import edu.nl.tue.tink.Tgraphs.streaming.TGraphStream;

/**
 * @param <K> key type
 * @param <VV> vertex value type
 * @param <EV> edge value type
 * @param <T> the return type
 */
public interface TGraphStreamAlgorithm<K, VV, EV, N, T>
{
	public T run(TGraphStream<K, VV, EV, N> input) throws Exception;
}
