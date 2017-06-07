package au.gov.dhs.nlp;

import org.kitesdk.morphline.api.Record;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class NLPSentimentAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Record x = new org.kitesdk.morphline.api.Record();
		System.out.println(x.getFields());
		
		
		NLPSentimentAnalysis  analysis = new NLPSentimentAnalysis();
		int result = analysis.findSentiment("機能が強化され マックでも使える自動面付けツール　なんと今だけ");
		System.out.println("Sentiment: " + result);
	}
	
	private StanfordCoreNLP pipeline;
	
	public NLPSentimentAnalysis() {
		pipeline = new StanfordCoreNLP("sentiment-pipeline.properties");
	}
	
	public int findSentiment(String tweet) {

		int mainSentiment = 0;
		if (tweet != null && tweet.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(tweet);
			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence
						.get(SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}
			}
		}
		return mainSentiment;
	}
	
}
