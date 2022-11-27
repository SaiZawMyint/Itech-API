package com.itech.api.pkg.toots.resolver;

import java.util.Map;

import com.google.gson.Gson;

public class Resolver {
	private Map<String, Object> data;

	public Resolver resolve(Map<String, Object> resolver) {
		this.data = resolver;
		return this;
	}

	public Object get() {
		return this.data;
	}

	public String toJson() {
		return new Gson().toJson(this.data);
	}

}
