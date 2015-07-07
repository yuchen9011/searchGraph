package com.processing;

public class Features {
	private String featureWord;
	private int termFrequency;

	public Features(String word, int termFreq) {
		this.featureWord = word;
		this.termFrequency = termFreq;
	}

	public String getFeatureWord()
	{
		return featureWord;
	}

	public void setFeatureWord(String featureWord)
	{
		this.featureWord = featureWord;
	}

	public int getTermFrequency()
	{
		return termFrequency;
	}

	public void setTermFrequency(int termFrequency)
	{
		this.termFrequency = termFrequency;
	}
	public int addCount()
	{
		termFrequency++;
		return termFrequency;
	}
}
