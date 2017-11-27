package com.inovision.apitest.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonComparator implements ResultComparator {
	
	protected List<String> errors = new ArrayList<String>(5);
	protected List<String> warnings = new ArrayList<String>(5);
	protected List<String> excludeList = new ArrayList<String>(5);
	protected boolean allowNullForArrays = false;
	
	private Stack<String> stack = new Stack<String>();
	
	public void setExcludeList(List<String> list) {
		this.excludeList = list;
	}
	
	public void setAllowNullForArrays(boolean allowNullForArrays) {
		this.allowNullForArrays = allowNullForArrays;
	}
	
	public boolean compare(String lhsJson, String rhsJson) {
		errors.clear();
		warnings.clear();
		stack.clear();
		boolean isEqual = true;
		
		if(StringUtils.isEmpty(lhsJson) || StringUtils.isEmpty(rhsJson)) {
			warnings.add("One of the JSON String is null");			
			return false;
		}
		
		JsonParser parser = new JsonParser();
		
		JsonElement lhsRoot = parser.parse(lhsJson);
		JsonElement rhsRoot = parser.parse(rhsJson);

		if(isEqual = isSameType(lhsRoot, rhsRoot)) {
			stack.push("root");
			if(lhsRoot.isJsonArray() && rhsRoot.isJsonArray()) {
				isEqual = compare(lhsRoot.getAsJsonArray(), rhsRoot.getAsJsonArray());				
			} else if(lhsRoot.isJsonObject() && rhsRoot.isJsonObject()) {
				isEqual = compare(lhsRoot.getAsJsonObject(), rhsRoot.getAsJsonObject());
			} 
		} else {
			errors.add("Root JSON Objects are not same");
		}
		
		return isEqual;
		
	}
	
	//we assume that Json Array will have similar structure objects/values
	public boolean compare(JsonArray lhsArr, JsonArray rhsArr) {
		boolean isEqual = true;
		Iterator<JsonElement> lhsItr = lhsArr.iterator();
		Iterator<JsonElement> rhsItr = rhsArr.iterator();
		int count = 0 ;
		String name = stack.peek();
		while(isEqual && lhsItr.hasNext() && rhsItr.hasNext()) {
			stack.pop();
			stack.push(String.format(name + "[%d]",  count++));
			JsonElement lhsEle = lhsItr.next();
			JsonElement rhsEle = rhsItr.next();
			if(isEqual = isSameType(lhsEle, rhsEle)) {
				if(lhsEle != null && lhsEle.isJsonObject() && rhsEle != null && rhsEle.isJsonObject()) {
					isEqual = compare(lhsEle.getAsJsonObject(), rhsEle.getAsJsonObject());
				} else if(lhsEle != null && lhsEle.isJsonArray() && rhsEle != null && rhsEle.isJsonArray()) {
					isEqual = compare(lhsEle.getAsJsonArray(), rhsEle.getAsJsonArray());
				}
			}
		}
		return isEqual;
	}
	
	public boolean compare(JsonObject lhsObject, JsonObject rhsObject) {
		//we assume true
		boolean isEqual = true;
		
		//Compare just keys
		Set<Entry<String, JsonElement>> lhsSet = lhsObject.entrySet();
		Set<Entry<String, JsonElement>> rhsSet = rhsObject.entrySet();
		
		Iterator<Entry<String, JsonElement>> lhsKeyIterator = lhsSet.iterator();
		Iterator<Entry<String, JsonElement>> rhsKeyIterator = rhsSet.iterator();
		
		//TODO: collect distinct keys from LHS and RHS and use the super set to iterate
		Set<String> masterKeyList = new TreeSet<String>();
		
		while(lhsKeyIterator.hasNext()) {
			masterKeyList.add(lhsKeyIterator.next().getKey());
		}
		while(rhsKeyIterator.hasNext()) {
			masterKeyList.add(rhsKeyIterator.next().getKey());
		}
		
		//Iterate over each value and compare them (only if they are object or arrays)			
		Iterator<String> keyiterator = masterKeyList.iterator();//lhsKeys.iterator(); 
		while(keyiterator.hasNext() && isEqual) {
			String key = keyiterator.next();
			if(excludeList.contains(key)) {
				//skip nodes which are on exclude list
				continue;
			}
			stack.push(key);
			JsonElement ele1 = lhsObject.get(key);
			JsonElement ele2 = rhsObject.get(key);
			
			if(isSameType(ele1, ele2)) {
				if(ele1 != null && ele1.isJsonArray() && ele2 != null && ele2.isJsonArray()) {
					isEqual = compare(ele1.getAsJsonArray(), ele2.getAsJsonArray());
				} else if(ele1 != null && ele1.isJsonObject() && ele2!= null && ele2.isJsonObject()) {
					isEqual = compare(ele1.getAsJsonObject(), ele2.getAsJsonObject());
				} 
			} else {
				errors.add("Objects are not of same type at " + getXPath() + " with values [" + ele1 + "] and [" + ele2 + "]");
				//errors.add("Objects are not of same type : " + ele1 + " and " + ele2);
				isEqual = false;
			}
			stack.pop();
		}
		
		return isEqual;
	}
	
	private String getXPath() {
		StringBuilder buf = new StringBuilder();
		if(!stack.isEmpty()) {
			Iterator<String> itr = stack.iterator();
			buf.append(itr.next());
			while(itr.hasNext()) {
				buf.append(".");
				buf.append(itr.next());
			}
		}
		
		return buf.toString();
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public List<String> getWarnings() {
		return warnings;
	}
	
	private boolean isSameType(JsonElement ele1, JsonElement ele2) {
		//'xyz': null --> is JsonNull
		//having no xyz element means null
		
//		boolean same = ( ((ele1 == null) && (ele2 == null)) ||	//Both null means same			
//			(ele1 != null && ele1.isJsonNull() && (ele2 == null)) || //JsonNull is same as null element
//			(ele2 != null && ele2.isJsonNull() && (ele1 == null)); //JsonNull is same as null element
		
		boolean same = isNull(ele1) && isNull(ele2);
		
//		if(!same) {
//		    same = ( 
//		    		(ele1 != null && ele1.isJsonObject() && (isNull(ele2) || ele2.isJsonObject())) || //Object value and a null value is same
//		    		(ele2 != null && ele2.isJsonObject() && (isNull(ele1) || ele1.isJsonObject())) || //Object value and a null value is same
//		    		(ele1 != null && ele1.isJsonPrimitive() && (isNull(ele2) || ele2.isJsonPrimitive())) || //both needs to be primitive type to be same 
//		    		(ele1 != null && ele1.isJsonNull() && (isNull(ele2) || ele2.isJsonObject() || ele2.isJsonArray() || ele2.isJsonPrimitive())) || //JsonNull and JsonNull or object or array are same
//		    		(ele1 != null && ele1.isJsonArray() && (isNull(ele2) || ele2.isJsonArray())) //JsonArray and JsonArray or JsonNull are same 
//		    		); 	
//		}

		if(!same) {
		    same = ( 
		    		(isObject(ele1) && isObject(ele2)) || //Object value and a null value is same
		    		(isPrimitive(ele1) && isPrimitive(ele2)) || //both needs to be primitive type to be same 
		    		(isArray(ele1) && isArray(ele2)) //JsonArray and JsonArray or JsonNull are same 
		    		); 	
		}
		
		return same;
	}
	
	private boolean isObject(JsonElement ele) {
		return (ele != null && (ele.isJsonObject() || ele.isJsonNull()));
	}

	private boolean isPrimitive(JsonElement ele) {
		return (ele != null && (ele.isJsonPrimitive() || ele.isJsonNull()));
	}

	//Strictly speaking it should empty or non-empty array, but never null
	private boolean isArray(JsonElement ele) {
		return (ele != null && (ele.isJsonArray() || (allowNullForArrays && ele.isJsonNull()) ));
	}
	
	private boolean isNull(JsonElement ele) {
		return (ele == null) || ((ele != null) && ele.isJsonNull());
	}
	
	public static void main(String[] args) {
		JsonComparator comp = new JsonComparator();

		String jsonstring = "{name:'Kunal', age:23}";
		String jsonstring2 = "{name:'Kunal2', \n \n \n age:23 \n\n}";
		
		System.out.println("Same Object compare Expected true : " + comp.compare(jsonstring, jsonstring));
		System.out.println("Object compare Expected true : " + comp.compare(jsonstring, jsonstring2));
		
		jsonstring = "{name: 'Kunal'}";
		jsonstring2 = "{name: 'Kunal 213213'}";
		System.out.println("Object compare Expected true : " + comp.compare(jsonstring, jsonstring2));
		
		jsonstring = "[{name: 'Kunal'}, {name: 'Kunal1'}, {name: 'Kunal2'}, {name: 'Kunal3'} ]";
		jsonstring2 = "[{name: 'Kunal4'}, {name: 'Kunal5'}, {name: 'Kunal6'}, {name: 'Kunal7'} ]";
		System.out.println("Array Compare Expected true : " + comp.compare(jsonstring, jsonstring2));
		
		jsonstring = "[{name: 'Kunal'}, {name: 'Kunal1'}, {name: 'Kunal2'}, {name: 'Kunal3'} ]";
		jsonstring2 = "[{name: 'Kunal4'}, {name: 'Kunal5'} ]";
		System.out.println("Diff size Array Compare Expected true : " + comp.compare(jsonstring, jsonstring2));
		
		jsonstring = "{ \"took\": 8, \"timed_out\": false, \"_shards\": {  \"total\": 3,  \"successful\": 3,  \"failed\": 0 }, \"hits\": {  \"total\": 158606,  \"max_score\": 1.0  }}";
		jsonstring2 = "{ \"took\": 10, \"timed_out\": true, \"_shards\": {  \"total\": 5,  \"successful\": 100,  \"failed\": 10 }, \"hits\": {  \"total\": 158,  \"max_score\": 1.0  }}";
		System.out.println("Object1 Compare Expected true : " + comp.compare(jsonstring, jsonstring2));

		jsonstring = "{ \"timed_out\": 8, \"took\": false, \"_shards\": {  \"total\": 3,  \"successful\": 3,  \"failed\": 0 }, \"hits\": {  \"total\": 158606,  \"max_score\": 1.0  }}";
		jsonstring2 = "{ \"took\": 10, \"timed_out\": true, \"_shards\": {  \"total\": 5,  \"successful\": 100,  \"failed\": 10 }, \"hits\": {  \"total\": 158,  \"max_score\": 1.0  }}";
		System.out.println("Object2 Compare ( change order props) Expected true : " + comp.compare(jsonstring, jsonstring2));
		
		jsonstring = "{ \"timed_out\": false, \"_shards\": {  \"total\": 3,  \"successful\": 3,  \"failed\": 0 }, \"hits\": {  \"total\": 158606,  \"max_score\": 1.0  }}";
		jsonstring2 = "{ \"took\": 10, \"timed_out\": true, \"_shards\": {  \"total\": 5,  \"successful\": 100,  \"failed\": 10 }, \"hits\": {  \"total\": 158,  \"max_score\": 1.0  }}";
		System.out.println("Object3 Compare Expected false : " + comp.compare(jsonstring, jsonstring2));
		printList("Error", comp.getErrors());

		jsonstring = "{\"took\": 8,\"timed_out\": false,\"_shards\": {\"total\": 3,\"successful\": 3,\"failed\": 0},\"hits\": {\"total\": 158606,\"max_score\": 1.0,\"hits\": [{\"_index\": \"tv_2015-02-16_04-09-20\",\"_type\": \"events\",\"_id\": \"EP000000260104_20150221_0200_14870\",\"_score\": 1.0,\"_source\": {\"program\": {\"ratings\": {\"rating\": [{\"area\": \"USA Parental Rating\"}],\"score\" : 1.55}},\"TMSId\": \"EP000000260104\"}}]}}";
		jsonstring2 = "{\"took\": 4,\"timed_out\": true,\"_shards\": {\"total\": 13,\"successful\": 13,\"failed\": 1000},\"hits\": {\"total\": 1586,\"max_score\": 2.0,\"hits\": [{\"_type\": \"even\",\"_score\": 2.0,\"_index\": \"tv_201\",\"_source\": {\"program\": {\"ratings\": {\"rating\": [{\"area\": \"USA \"}],\"score\" : 1.55}},\"TMSId\": \"EP000000260104\"},\"_id\": \"EP000000260104_20150221_0200_14870\"}]}}";
		System.out.println("Object4 Compare (shuffle props and change values)  Expected true : " + comp.compare(jsonstring, jsonstring2));
		
		jsonstring = "{\"one\" : 1, arr:[] }"; 
		jsonstring2 = "{\"one\" : 1, arr:[1,2,3] }"; 
		System.out.println("Array empty and non-empty  Expected true : " + comp.compare(jsonstring, jsonstring2));

		jsonstring = "{\"one\" : 1, arr:[] }"; 
		jsonstring2 = "{\"one\" : 1, arr: null }"; 
		System.out.println("Array empty and null  Expected false : " + comp.compare(jsonstring, jsonstring2));
		printList("Error", comp.getErrors());
		
		comp.setAllowNullForArrays(true);
		System.out.println("Array empty and null  Expected true : " + comp.compare(jsonstring, jsonstring2));
				
		
		jsonstring = "[{name: 'Kunal'}, {name: 'Kunal1'}, {name: 'Kunal2'}, {name: 'Kunal3'} ]";
		jsonstring2 = "{name: 'Kunal 213213'}";
		System.out.println("Array and Object Compare Expected false : " + comp.compare(jsonstring, jsonstring2));
		printList("Error", comp.getErrors());
		System.out.println("Array and Object 2 Compare Expected false : " + comp.compare(jsonstring2, jsonstring));
		printList("Error", comp.getErrors());
		

		jsonstring = "{\"a\": \"b\", \"program\": [{\"value\": \"Death for a Stolen Horse\",\"type\": \"full\",\"lang\": \"en\",\"size\": \"150\"}, {\"value\": \"Death for a Stolen Horse\",\"type\": \"full\",\"lang\": \"en\",\"size\": \"150\"}]}";
		jsonstring2 = "{\"a\": \"b\", \"program\": [{\"value\": \"Death for a Stolen Horse\",\"type\": \"full\",\"lang\": \"en\",\"size\": \"150\"}, {\"value\": \"Death for a Stolen Horse\",\"lang\": \"en\",\"size\": \"150\"}]}";
		System.out.println("Missing element in array Expected false : " + comp.compare(jsonstring, jsonstring2));
		printList("Error", comp.getErrors());

	}

	private static void printList(String type, List<String> list) {
		System.out.print("| +----- "); System.out.print(type);
		if(list.size() > 5 ) 
			System.out.println(" (Printing 5/" + list.size() + ")");
		else 
			System.out.println(" ");
		int i = 0;
		for(String err : list) {
			System.out.print("| | ");
			System.out.println(err);
			i++;
			if(i > 5) break;
		}
		System.out.println("| +--------------------");
	}
	
}
