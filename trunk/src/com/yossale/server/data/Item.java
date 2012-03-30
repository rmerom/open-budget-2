package com.yossale.server.data;

public class Item {
	String num;
	double per;

	public Item(String num, double per) {
	  super();
	  this.num = num;
	  this.per = per;
  }
	public String getNum() {
  	return num;
  }
	public void setNum(String num) {
  	this.num = num;
  }
	public double getPer() {
  	return per;
  }
	public void setPer(double per) {
  	this.per = per;
  }
}
