package com.inovision.apitest.util;

import com.inovision.apitest.model.ValidationType;

public class ResultComparatorFactory {

	public static ResultComparator getResultComparator(ValidationType type) {
		if(ValidationType.JSON.equals(type)) {
			return new JsonComparator();
		} else if(ValidationType.REGEX.equals(type)){
			return new RegexResultComparator();
		} else {
			return null;
		}
	}
}
