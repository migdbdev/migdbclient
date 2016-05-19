package org.migdb.migdbclient.config;

public enum TreeviewSize {

	TREEVIEWHEIGHT(200),
	TREEVIEWIDTH(150);
	
	private double size;
	
	TreeviewSize(double size) {
		this.size = size;
	}
	
	public double getSize() {
		return size;
	}
}
