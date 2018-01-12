package edu.nl.tue.tink.Tgraphs.streaming;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.NullValue;

import edu.nl.tue.tink.Tgraphs.streaming.algorithms.TGraphStreamAlgorithm;

/**
 * 
 * @author iluvex
 * @creation Jan 3, 2018
 *
 */
public class TGraphStream<K, VV, EV, N>
{
	protected final StreamExecutionEnvironment context;
	protected final DataStream<Vertex<K, VV>> vertices;
	protected final DataStream<Edge<K, Tuple3<EV, N, N>>> edges;
	protected final int maxVertices;
	protected final int maxEdges;

	/*
	 * Constructor for stream temporal graph
	 */
	public TGraphStream(StreamExecutionEnvironment context, DataStream<Vertex<K, VV>> vertices,
			DataStream<Edge<K, Tuple3<EV, N, N>>> edges, int maxVertices, int maxEdges)
	{
		this.context = context;
		this.vertices = vertices;
		this.edges = edges;
		this.maxEdges = maxEdges;
		this.maxVertices = maxVertices;
	}

	/**
     * @param algorithm the algorithm to run on the Graph
     * @param <T> the return type
     * @return the result of the graph algorithm
     * @throws Exception
     */
    public <T> T run(TGraphStreamAlgorithm<K, VV, EV,N, T> algorithm) throws Exception {
        return algorithm.run(this);
    }
}
