package com.dberm22.utils;

import java.util.HashMap;

public class TwoWayHashMap<K,V> extends HashMap<K,V>
{
	private static final long serialVersionUID = 5681599067866759894L;

	public V getValue(K key)
	{
		return super.get(key);
	}
	
	public K getKey(V value)
	{
		for(K key : this.keySet())
		{
			if(this.get(key).equals(value))
			{
				return key;
			}
		}
		return null;
	}
	
	public V removeByKey(K key)
	{
		return super.remove(key);
	}
	
	public K removeByValue(V value)
	{
		for(K key : this.keySet())
		{
			if(this.get(key).equals(value))
			{
				super.remove(key);
				return key;
			}
		}
		return null;
	}

}
