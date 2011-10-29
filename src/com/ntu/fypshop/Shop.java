package com.ntu.fypshop;

public class Shop {
	final public String address;
	final public String name;
	final public String lat;
	final public String lng;
	public String distance;

	public Shop(String add, String nm, String lt, String lg)
	{
		address = add;
		name = nm;
		lat = lt;
		lng = lg;
	}
	public Shop(String add, String nm, String lt, String lg, String dt)
	{
		address = add;
		name = nm;
		lat = lt;
		lng = lg;
		distance = dt;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getLat()
	{
		return lat;
	}
	
	public String getLng()
	{
		return lng;
	}
	
	public String getDist()
	{
		return distance;
	}
}
