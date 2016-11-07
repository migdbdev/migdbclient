package org.migdb.migdbclient.models.queryconverter;

import java.util.LinkedHashMap;

public class FindDocument {
	
	private String collectionName;
	private LinkedHashMap<String, Object> limitPair;
	private MatchCondition matchPairs;
	private LinkedHashMap<String, Object> sortPair;
	private int limit;
	private boolean count;
	
	public FindDocument(String collectionName) {
		this.collectionName = collectionName;
		this.limitPair = new LinkedHashMap<String, Object>();
		this.matchPairs = new MatchCondition();
		this.sortPair = new LinkedHashMap<String, Object>();
		this.count = false;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public LinkedHashMap<String, Object> getLimitPair() {
		return limitPair;
	}

	public void setLimitPair(LinkedHashMap<String, Object> limitPair) {
		this.limitPair = limitPair;
	}

	public MatchCondition getMatchPairs() {
		return matchPairs;
	}

	public void setMatchPairs(MatchCondition matchPairs) {
		this.matchPairs = matchPairs;
	}

	public LinkedHashMap<String, Object> getSortPair() {
		return sortPair;
	}

	public void setSortPair(LinkedHashMap<String, Object> sortPair) {
		this.sortPair = sortPair;
	}
	
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isCount() {
		return count;
	}

	public void setCount(boolean count) {
		this.count = count;
	}

	@Override
	public String toString() {
		String str = "db."+collectionName+".find( \n\t"+matchPairs;
		if(!limitPair.isEmpty()) {
			str += ", \n\t"+limitPair.toString().replace("=", ": ").replace("{", "{ ")
					.replace("}", " }");
		}
		str += "\n)";
		if(!sortPair.isEmpty()) {
			str += ".sort( "+sortPair.toString().replace("=",": ").replace("{","{ ")
					.replace("}"," }")+" )";
		}
		if(count) {
			str += ".count()";
		}
		if(limit != 0) {
			str += ".limit("+limit+")";
		}
		return str;
	}

	

}
