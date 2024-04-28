package com.commander4j.network;

public class ControlCode
{
	private String token;
	private String unicode;
	
	public void setToken(String val)
	{
		this.token = val;
	}
	
	public void setUnicode(String val)
	{
		this.unicode = val;
	}
	
	public String getToken()
	{
		return this.token;
	}
	
	public String getUnicode()
	{
		return this.unicode;
	}
	
	public ControlCode(String token,String unicode)
	{
		setToken(token);
		setUnicode(unicode);
	}
	
}
