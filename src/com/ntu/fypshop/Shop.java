package com.ntu.fypshop;

public class Shop {
	final public String address;
	final public String name;
	final public String lat;
	final public String lng;
	final public String distance;

	public Shop(String add, String nm, String lt, String lg, String dt)
	{
		address = add;
		name = nm;
		lat = lt;
		lng = lg;
		distance = dt;
	}
}
