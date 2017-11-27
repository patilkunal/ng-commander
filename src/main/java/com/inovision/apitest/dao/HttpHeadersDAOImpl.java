package com.inovision.apitest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseCategory;

@Repository
public class HttpHeadersDAOImpl implements HttpHeadersDAO {

	private static final String SELECT_HTTP_HEADER_BY_CATEGORY = "select * from http_headers where test_category_id = :testcategoryid";	

	private static final String INSERT_HTTP_HEADER = "insert into http_headers(name, value, test_category_id, testcase_id) values( :name, :value, :testcategoryid, :testcaseid)";
	private static final String UPDATE_HTTP_HEADER = "update http_headers set name = :name, value = :value where id = :id";
	private static final String SELECT_HTTP_HEADER_BY_TESTCASE = "select * from http_headers where testcase_id = :testcaseid";

	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setJbdcTemplate(NamedParameterJdbcTemplate jbdcTemplate) {
		this.jdbcTemplate = jbdcTemplate;
	}

	@Override
	public List<HttpHeader> getHeaders(TestCaseCategory tcc) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("testcategoryid", tcc.getId());
		return jdbcTemplate.query(SELECT_HTTP_HEADER_BY_CATEGORY, paramMap, new HttpHeaderRowMapper());
	}
	
	@Override
	public List<HttpHeader> saveHttpHeader(TestCase tc, List<HttpHeader> headerList) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("testcaseid", tc.getId());
		params.put("testcategoryid", -1);
		
		for(HttpHeader header: headerList) {
			if(header.getId() >= 0) {
				params.put("name", header.getName());
				params.put("value", header.getValue());
				params.put("id", header.getId());
				jdbcTemplate.update(UPDATE_HTTP_HEADER, params);				
			} else {
				params.put("name", header.getName());
				params.put("value", header.getValue());
				KeyHolder keyHolder = new GeneratedKeyHolder();
				jdbcTemplate.update(INSERT_HTTP_HEADER, new MapSqlParameterSource(params), keyHolder, new String[] {"ID"});
				header.setId(keyHolder.getKey().intValue());				
			}
		}
		return headerList;
	}
	
	@Override
	public List<HttpHeader> saveHttpHeader(TestCaseCategory tcc, List<HttpHeader> headerList) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("testcaseid",  -1);
		params.put("testcategoryid", tcc.getId());
		
		for(HttpHeader header: headerList) {
			if(header.getId() >= 0) {
				params.put("name", header.getName());
				params.put("value", header.getName());
				params.put("id", header.getId());
				jdbcTemplate.update(UPDATE_HTTP_HEADER, params);				
			} else {
				params.put("name", header.getName());
				params.put("value", header.getName());
				KeyHolder keyHolder = new GeneratedKeyHolder();
				jdbcTemplate.update(INSERT_HTTP_HEADER, new MapSqlParameterSource(params), keyHolder, new String[] {"ID"});
				header.setId(keyHolder.getKey().intValue());				
			}
		}
		return headerList;
	}
	
	private void updateHeaders(List<HttpHeader> headers) {
		Map<String, Object> params = new HashMap<String, Object>();
		for(HttpHeader header : headers) {
			params.clear();
			if(header.getId() >=0) {
				//update
				params.put("id", header.getId());
				params.put("name", header.getName());
				params.put("value", header.getValue());
				jdbcTemplate.update(UPDATE_HTTP_HEADER, params);
			} else {
				//insert
				params.put("name", header.getName());
				params.put("value", header.getValue());
				params.put("testcategoryid", header.getTestCaseCategoryId());
				params.put("testcaseid", header.getTestCaseId());
				jdbcTemplate.update(INSERT_HTTP_HEADER, params);
			}
		}
	}

	
	
	@Override
	public List<HttpHeader> getHeaders(TestCase tc) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("testcaseid", tc.getId());
		return jdbcTemplate.query(SELECT_HTTP_HEADER_BY_TESTCASE, paramMap, new HttpHeaderRowMapper());
	}
	
	@Override
	public void deleteHttpHeader(int id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		jdbcTemplate.update("delete from http_headers where id = :id", paramMap);
	}
	
	private static final class HttpHeaderRowMapper implements RowMapper<HttpHeader> {
		@Override
		public HttpHeader mapRow(ResultSet rs, int arg1) throws SQLException {
			HttpHeader header = new HttpHeader();
			header.setName(rs.getString("name"));
			header.setValue(rs.getString("value"));
			header.setTestCaseCategoryId(rs.getInt("TEST_CATEGORY_ID"));
			header.setTestCaseId(rs.getInt("TESTCASE_ID"));
			header.setId(rs.getInt("ID"));
			return header;
		}
	}
	
	
}
