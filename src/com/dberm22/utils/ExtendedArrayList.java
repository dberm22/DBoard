package com.dberm22.utils;

import java.util.ArrayList;

public class ExtendedArrayList<T> extends ArrayList<T>{
	
	private static final long serialVersionUID = -6390258240982082892L;

	public ExtendedArrayList()
	{
		super();
	}
	
	public ExtendedArrayList(T[] initialSet)
	{
		super();
		
		for(T t : initialSet)
		{
			this.add(t);
		}
	}

}
