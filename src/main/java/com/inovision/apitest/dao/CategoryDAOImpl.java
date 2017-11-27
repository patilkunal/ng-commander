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

import com.inovision.apitest.model.TestCaseCategory;

@Repository
public class CategoryDAOImpl implements CategoryDAO {

	private static final String GET_CATEGORIES = "select c.*, (select count(1) from hosts h where h.test_category_id = c.id)  as hostcount, (select count(1) from testcase tc where tc.test_category_id = c.id) as testcount from TEST_CATEGORY c";
	private static final String GET_CATEGORY = "select c.*, (select count(1) from hosts h where h.test_category_id = c.id) as hostcount, (select count(1) from testcase tc where tc.test_category_id = c.id) as testcount from TEST_CATEGORY c where c.id = :id";
	private static final String DELETE_CATEGORY = "delete from TEST_CATEGORY where id = :id";
	private static final String SAVE_CATEGORY = "insert into TEST_CATEGORY(name, description) values(:name, :description)";
	private static final String UPDATE_CATEGORY = "update TEST_CATEGORY set name = :name, description = :description where id = :id";
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	private HttpHeadersDAO httpHeadersDAO;
	
	@Autowired
	public void setJbdcTemplate(NamedParameterJdbcTemplate jbdcTemplate) {
		this.jdbcTemplate = jbdcTemplate;
	}
	
	@Autowired
	public void setHttpHeadersDAO(HttpHeadersDAO httpHeadersDAO) {
		this.httpHeadersDAO = httpHeadersDAO;
	}

	public List<TestCaseCategory> getTestCategories() {
		return jdbcTemplate.query(GET_CATEGORIES, new TestCaseCategoryRowMapper());		
	}
	
	public TestCaseCategory getTestCategory(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		TestCaseCategory tcc = jdbcTemplate.queryForObject(GET_CATEGORY, params, new TestCaseCategoryRowMapper());
		tcc.setHeaders(httpHeadersDAO.getHeaders(tcc));
		return tcc;
	}
	
	public void deleteTestCategory(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		jdbcTemplate.update(DELETE_CATEGORY, params);
	}
	
	public TestCaseCategory saveTestCategory(TestCaseCategory tc) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", tc.getName());
		params.put("description", tc.getDescription());
		if(tc.getId() > -1) {
			params.put("id", tc.getId());
			jdbcTemplate.update(UPDATE_CATEGORY, params);
		} else {
			KeyHolder keyholder = new GeneratedKeyHolder(); 
			jdbcTemplate.update(SAVE_CATEGORY, new MapSqlParameterSource(params), keyholder, new String[] {"ID"});
			tc.setId(keyholder.getKey().intValue());
		}
		httpHeadersDAO.saveHttpHeader(tc, tc.getHeaders());
		return tc;
	}
	
	@Override
	public void deleteHeader(int categoryid, int headerid) {
		httpHeadersDAO.deleteHttpHeader(headerid);		
	}
	
	private static final class TestCaseCategoryRowMapper implements RowMapper<TestCaseCategory> {

		public TestCaseCategory mapRow(ResultSet rs, int arg1) throws SQLException {
			TestCaseCategory category = new TestCaseCategory();
			category.setId(rs.getInt("ID"));
			category.setDescription(rs.getString("description"));
			category.setName(rs.getString("name"));
			category.setHostCount(rs.getInt("hostcount"));
			category.setTestCount(rs.getInt("testcount"));
			return category;
		}
		
	}
	
}
