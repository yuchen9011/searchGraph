package com.processing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

import com.commom.RelTypes;

public class ScoreWithStruct {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// BitSet bitSet = new BitSet();
		String DB_PATH = "sub_graph_test/graph.db";
		GraphDatabaseService graphDb = new GraphDatabaseFactory()
				.newEmbeddedDatabase(DB_PATH);
		try {
			
			String output = "";
			Node node = graphDb.getNodeById(132);
			TraversalDescription FRIENDS_TRAVERSAL = Traversal.description()
					.breadthFirst().relationships(RelTypes.KNOWS)
					;
			for (Path path : FRIENDS_TRAVERSAL
					.evaluator(Evaluators.fromDepth(1))
					.evaluator(Evaluators.toDepth(2)).traverse(node)) {
				output += path + "\n";
				System.out.println(path.endNode().getId() == 63789);
				System.out.println(path.length());
			}
			
//			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			graphDb.shutdown();
			System.out.println("图数据库关闭");
		}
	}

}
