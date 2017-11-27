package com.inovision.apitest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.HttpContentType;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseCategory;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseRun;
import com.inovision.apitest.model.TestCaseRunFilter;
import com.inovision.apitest.model.TestCaseValue;
import com.inovision.apitest.model.ValidationType;
import com.inovision.apitest.model.ValueType;

@Repository
public class TestCaseDAOImpl implements TestCaseDAO {
	
	private static final Logger LOGGER = Logger.getLogger(TestCaseDAOImpl.class);
	
	private static final String GET_TESTCASES = "select t.*, count(ti.id) instance_count from TESTCASE t LEFT OUTER JOIN TESTCASE_INSTANCE ti ON t.id = ti.TESTCASE_ID where t.TEST_CATEGORY_ID = :categoryId GROUP by t.id";
	private static final String GET_TESTCASE = "select t.*, count(ti.id) instance_count from TESTCASE t LEFT OUTER JOIN TESTCASE_INSTANCE ti ON t.id = ti.TESTCASE_ID where t.id = :id GROUP BY t.id";
	private static final String SAVE_TESTCASE = "insert into TESTCASE(NAME, DESCRIPTION, TEST_CATEGORY_ID, REST_URL, HTTP_METHOD, HTTP_DATA, CONTENT_TYPE, VALIDATE_OUTPUT, OUTPUT_TEMPLATE, ALLOW_BLANK_OUTPUT, VALIDATE_TYPE) "
			+ " values(:name, :description, :testCategoryId, :restUrl, :method, :data, :contentType, :validateOutput, :outputTemplate, :allowBlankOutput, :validateType)";
	private static final String UPDATE_TESTCASE = "update TESTCASE set NAME = :name, DESCRIPTION = :description, TEST_CATEGORY_ID = :testCategoryId, REST_URL = :restUrl, "
			+ "HTTP_METHOD = :method, HTTP_DATA = :data, CONTENT_TYPE = :contentType, VALIDATE_OUTPUT = :validateOutput, OUTPUT_TEMPLATE = :outputTemplate, ALLOW_BLANK_OUTPUT = :allowBlankOutput, VALIDATE_TYPE = :validateType where ID = :id ";
	private static final String DELETE_TESTCASE = "delete from TESTCASE where id = :id";
	
	
	private static final String GET_TESTCASEINSTANCES = "select ti.* from TESTCASE_INSTANCE ti, TESTCASE tc where tc.id = ti.TESTCASE_ID and tc.TEST_CATEGORY_ID = :categoryId";
	private static final String GET_TESTCASEINSTANCES_BY_TESTCASE = "select ti.* from TESTCASE_INSTANCE ti, TESTCASE tc where tc.id = :testcaseid";
	private static final String GET_TESTCASEINSTANCE = "select ti.* from TESTCASE_INSTANCE ti where id = :id";
	private static final String SAVE_TESTCASE_INSTANCE = "insert into TESTCASE_INSTANCE(NAME, DESCRIPTION, TESTCASE_ID, USER_ID, VALIDATE_OUTPUT, OUTPUT_TEMPLATE, ALLOW_BLANK_OUTPUT, VALIDATE_TYPE) "
			+ " values(:name, :description, :testCaseId, :userId, :validateOutput, :outputTemplate, :allowBlankOutput, :validateType)";
	private static final String UPDATE_TESTCASE_INSTANCE = "update TESTCASE_INSTANCE set NAME = :name, DESCRIPTION = :description, VALIDATE_OUTPUT = :validateOutput, OUTPUT_TEMPLATE = :outputTemplate, ALLOW_BLANK_OUTPUT = :allowBlankOutput, VALIDATE_TYPE = :validateType "
			+ " where id = :id ";
	private static final String DELETE_TESTCASE_INSTANCE = "delete from TESTCASE_INSTANCE where ID = :id";

	private static final String GET_TESTRUNS = "select tr.*, ti.id TESTCASE_INSTANCE_ID, ti.name TESTCASE_INSTANCE_NAME, ti.description, "
			+ "cat.name CATEGORY_NAME, h.id host_id, h.name host_name, h.hostname host_hostname, h.port host_port, h.securehttp host_securehttp "
			+ " from TESTCASE_RUN tr, TESTCASE_INSTANCE ti, TESTCASE tc, HOSTS h , TEST_CATEGORY cat "
			+ " where ti.id =  tr.TESTCASE_INSTANCE_ID"
			+ " and ti.TESTCASE_ID = tc.id "
			+ " and tr.host_id = h.id "
			+ " and tc.TEST_CATEGORY_ID = cat.id ";
	
	private static final String GET_TESTCASE_VALUES = "select tv.* from TESTCASE_VALUES tv where tv.TESTCASE_INSTANCE_ID = :testCaseInstanceId";
	private static final String SAVE_TESTCASE_VALUES = "insert into TESTCASE_VALUES(TESTCASE_INSTANCE_ID, KEY_NAME, KEY_VALUE, VALUE_TYPE) "
			+ " values(:testCaseInstanceId, :keyname, :keyvalue, :valuetype)";
	private static final String DELETE_TESTCASE_VALUES = "delete from TESTCASE_VALUES where TESTCASE_INSTANCE_ID = :testCaseInstanceId";
	
	private static final String SAVE_TEST_CASE_RUN = "insert into TESTCASE_RUN(TESTCASE_INSTANCE_ID, HOST_ID, SUCCESS, RUN_DATE, ERROR, RESULT, CONTENT_TYPE, RETURN_CODE) "
			+ " values(:testCaseInstanceId, :hostid, :success, :runDate, :error, :result, :contentType, :returnCode)";
	
	private static final String UPDATE_TEST_CASE_RUN = "update TESTCASE_RUN set RUN_OUTPUT = :output, SUCCESS = :success where id = :id";
	
//	private static final String GET_TEST_CASE_RUN_LIST_BY_CATEGORY = "select tc.name, tr.* from testcase_run tr, testcase_instance ti, testcase t, test_category tc"
//														+ "where tr.testcase_instance_id = ti.id"
//														+ "and ti.testcase_id = t.id"
//														+ "and t.test_category_id = tc.id"
//														+ "and tc.id = :testCategoryId;";
	
	private static final String GET_TEST_CASE_RUN_LIST_BY_CATEGORY = GET_TESTRUNS + " and cat.id = :testCategoryId ";
	
	private static final String GET_TEST_CASE_RUN_BY_ID = GET_TESTRUNS + " and tr.id = :id";
	
	private static final String DELETE_TEST_CASE_RUN_BY_INSTANCE_ID = "delete from testcase_run where testcase_instance_id = :testcaseinstanceid";
	private static final String DELETE_TEST_CASE_VALUES_BY_INSTANCE_ID = "delete from testcase_values where testcase_instance_id = :testcaseinstanceid";

	
	private NamedParameterJdbcTemplate jdbcTemplate;
	private HttpHeadersDAO httpHeaderDAO;
	
	@Autowired
	public void setJbdcTemplate(NamedParameterJdbcTemplate jbdcTemplate) {
		this.jdbcTemplate = jbdcTemplate;
	}
	
	@Autowired
	public void setHttpHeaderDAO(HttpHeadersDAO httpHeaderDAO) {
		this.httpHeaderDAO = httpHeaderDAO;
	}
	
	public List<TestCase> getTestCases(int categoryId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("categoryId", categoryId);
		
		return jdbcTemplate.query(GET_TESTCASES, params, new TestCaseRowMapper());
	}

	public TestCase getTestCase(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		TestCase tc = jdbcTemplate.queryForObject(GET_TESTCASE, params, new TestCaseRowMapper());
		List<HttpHeader> headers = httpHeaderDAO.getHeaders(tc);
		headers.addAll(httpHeaderDAO.getHeaders(new TestCaseCategory(tc.getTestCategoryId())));
		tc.setHttpHeaders(headers);
		return tc;
	}
	
	public TestCase saveTestCase(TestCase testCase) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", testCase.getName());
		params.put("description", testCase.getDescription());
		params.put("restUrl", testCase.getRestUrl());
		params.put("data", testCase.getData());
		params.put("method", testCase.getMethod().name());
		params.put("testCategoryId", testCase.getTestCategoryId());
		params.put("contentType", testCase.getResponseType().getType());
		params.put("validateOutput", testCase.isValidateOutput() ? 1 : 0);
		params.put("outputTemplate", testCase.getOutputTemplate());
		params.put("allowBlankOutput", testCase.isAllowBlankOutput() ? 1 : 0);
		params.put("validateType", testCase.getValidateType().name());
		if(testCase.getId() > -1) {
			//update 
			params.put("id", testCase.getId());
			jdbcTemplate.update(UPDATE_TESTCASE, params);
		} else {
			//save
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(SAVE_TESTCASE, new MapSqlParameterSource(params), keyHolder, new String[] {"ID"});
			testCase.setId(keyHolder.getKey().intValue());
		}
		return testCase;
	}
	
	public void deleteTestCase(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		jdbcTemplate.update("delete from scheduled_testcases where testcase_instance_id in (select id from testcase_instance where testcase_id = :id)", params);
		jdbcTemplate.update("delete from testcase_values where testcase_instance_id in (select id from testcase_instance where testcase_id = :id)", params);
		jdbcTemplate.update("delete from testcase_run where testcase_instance_id in (select id from testcase_instance where testcase_id = :id)", params);
		jdbcTemplate.update("delete from testcase_instance where testcase_id = :id", params);
		jdbcTemplate.update("delete from http_headers where testcase_id = :id", params);
		jdbcTemplate.update(DELETE_TESTCASE, params);
	}

	public List<TestCaseInstance> getTestCaseInstances(int categoryId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("categoryId", categoryId);
		
		List<TestCaseInstance> list = jdbcTemplate.query(GET_TESTCASEINSTANCES, params, new TestCaseInstanceRowMapper());
		for(TestCaseInstance ti : list) {
			ti.setTestCaseValues(getTestCaseValues(ti.getId()));
		}
		return list;
	}
	
	@Override
	public List<TestCaseInstance> getTestCaseInstances(TestCase tc) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("testcaseid", tc.getId());
		
		List<TestCaseInstance> list = jdbcTemplate.query(GET_TESTCASEINSTANCES_BY_TESTCASE, params, new TestCaseInstanceRowMapper());
		for(TestCaseInstance ti : list) {
			ti.setTestCaseValues(getTestCaseValues(ti.getId()));
		}
		return list;
	}	
	
	public TestCaseInstance getTestCaseInstance(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		
		List<TestCaseInstance> list = jdbcTemplate.query(GET_TESTCASEINSTANCE, params, new TestCaseInstanceRowMapper());
		if(list.size() > 0) {
			TestCaseInstance ti = list.get(0);
			ti.setTestCaseValues(getTestCaseValues(ti.getId()));
			return ti;
		} else {
			return null;
		}
	}
	
	public List<TestCaseValue> getTestCaseValues(int testCaseInstanceId) {
		Map<String, Object> params2 = new HashMap<String, Object>();
		params2.put("testCaseInstanceId", testCaseInstanceId);			
		return jdbcTemplate.query(GET_TESTCASE_VALUES, params2, new TestCaseValueRowMapper());
	}
	
	@Override
	public void deleteTestCaseValues(TestCaseInstance testInstance) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("testcaseinstanceid", testInstance.getId());			
		jdbcTemplate.update(DELETE_TEST_CASE_VALUES_BY_INSTANCE_ID, paramMap);		
	}
	
	public TestCaseInstance saveTestCaseInstance(TestCaseInstance ti) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", ti.getName());
		params.put("description", ti.getDescription());
		params.put("testCaseId", ti.getTestCaseId());
		params.put("validateOutput", ti.isValidateOutput() ? 1 : 0);
		params.put("outputTemplate", ti.getOutputTemplate());
		params.put("allowBlankOutput", ti.isAllowBlankOutput() ? 1 : 0);
		params.put("validateType", ti.getValidateType().name());
		
		if(ti.getId() > -1) {
			params.put("id", ti.getId());
			jdbcTemplate.update(UPDATE_TESTCASE_INSTANCE, params);
		} else {
			params.put("userId", ti.getUserId());
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(SAVE_TESTCASE_INSTANCE, new MapSqlParameterSource(params), keyHolder, new String[] {"ID"});
			ti.setId(keyHolder.getKey().intValue());			
		}
		
		if(ti.getTestCaseValues() != null) {
			params.put("testCaseInstanceId", ti.getId());
			jdbcTemplate.update(DELETE_TESTCASE_VALUES, params);
			for(TestCaseValue tcv : ti.getTestCaseValues()) {
				params.put("keyname", tcv.getName());
				params.put("keyvalue", tcv.getValue());
				params.put("valuetype", tcv.getValueType() != null ? tcv.getValueType().name() : null);
				jdbcTemplate.update(SAVE_TESTCASE_VALUES, params);
			}
		}
		return ti;
	}
	
	public void deleteTestCaseInstance(int id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		jdbcTemplate.update(DELETE_TESTCASE_INSTANCE, paramMap);
	}
	
	public List<TestCaseRun> getTestCaseRuns(TestCaseRunFilter filter) {
		StringBuilder sql = new StringBuilder(GET_TESTRUNS);
		Map<String, Object> params = new HashMap<String, Object>();
		if(filter.getTestCaseCategoryId() > -1) {
			sql.append(" and cat.id = :categoryId");
			params.put("categoryId", filter.getTestCaseCategoryId());
		}
		if(filter.getTestCaseInstanceId() > -1) {
			sql.append(" and tc.id = :testCaseInstanceId");
			params.put("testCaseInstanceId", filter.getTestCaseInstanceId());
		}
		if((filter.getStartRunDate() != null) && (filter.getEndRunDate() != null)) {
			sql.append(" and tr.run_date between :startDate and :endDate");
			params.put("startDate", filter.getStartRunDate());
			params.put("endDate", filter.getEndRunDate());
		}
		if(filter.getHistoryCount() > 0) {
			sql.append(" and ti.user_id = :userid order by id desc");
			params.put("userid", filter.getUserId());
			String existingsql = sql.toString();
			sql.delete(0, sql.length());
			sql.append("select top :count * from (").append(existingsql).append(")");
			params.put("count", filter.getHistoryCount());
		}
		return jdbcTemplate.query(sql.toString(), params, new TestCaseRunMapper());
	}
	
	@Override
	public void deleteTestRun(TestCaseInstance testInstance) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("testcaseinstanceid", testInstance.getId());
		jdbcTemplate.update(DELETE_TEST_CASE_RUN_BY_INSTANCE_ID, paramMap);				
	}
	
	public TestCaseRun saveTestRun(TestCaseRun run) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("testCaseInstanceId", run.getTestCaseInstance().getId());
		params.put("hostid", run.getHost().getId());
		params.put("success", run.isSuccess() ? 1 : 0);
		params.put("runDate", run.getRunDate());
		params.put("error", run.getError());
		params.put("result", run.getResult());
		params.put("returnCode", run.getReturnCode());
		params.put("contentType", run.getContentType() != null ? run.getContentType().getType() : "");
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(SAVE_TEST_CASE_RUN, new MapSqlParameterSource(params), keyHolder, new String[] {"ID"});
		run.setId(keyHolder.getKey().intValue());
		return run;
	}
	
	@Override
	public void updateTestRun(TestCaseRun run) {
		LOGGER.info("Updating test case run " + run);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", run.getId());
		params.put("success", run.isSuccess() ? 1 : 0);
		jdbcTemplate.update(UPDATE_TEST_CASE_RUN, params);		
	}
	
	public TestCaseRun getTestRun(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);		
		return jdbcTemplate.queryForObject(GET_TEST_CASE_RUN_BY_ID, params, new TestCaseRunMapper());
	}
	
	public List<TestCaseRun> getTestRunList() {		
		return jdbcTemplate.query(GET_TESTRUNS, new TestCaseRunMapper());
	}
	
	@Override
	public List<TestCaseRun> getTestRunList(TestCaseCategory category) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("testCategoryId", category.getId());		
		return jdbcTemplate.query(GET_TEST_CASE_RUN_LIST_BY_CATEGORY, params, new TestCaseRunMapper());
	}
	
	@Override
	public void deleteTestRunByHost(int hostid) {
		LOGGER.info("Deleting hosts by id " + hostid);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hostid", hostid);		
		jdbcTemplate.update("delete from testcase_run where host_id = :hostid", params);				
	}
	
	private static final class TestCaseRowMapper implements RowMapper<TestCase> {
		
		public TestCase mapRow(ResultSet rs, int arg1) throws SQLException {
			TestCase t = new TestCase();
			t.setId(rs.getInt("ID"));
			t.setTestCategoryId(rs.getInt("TEST_CATEGORY_ID"));
			t.setName(rs.getString("name"));
			t.setDescription(rs.getString("description"));
			t.setRestUrl(rs.getString("REST_URL"));
			t.setData(rs.getString("http_data"));
			t.setMethod(HttpMethod.valueOf(rs.getString("http_method")));
			t.setInstanceCount(rs.getInt("instance_count"));
			t.setResponseType(HttpContentType.fromType(rs.getString("content_type")));
			t.setValidateOutput(rs.getInt("validate_output") == 1);
			t.setOutputTemplate(rs.getString("output_template"));
			t.setAllowBlankOutput(rs.getInt("allow_blank_output") == 1);
			if(rs.getString("validate_type") != null) {
				t.setValidateType(ValidationType.valueOf(rs.getString("validate_type")));
			}
			return t;
		}
	}

	public static final class TestCaseInstanceRowMapper implements RowMapper<TestCaseInstance> {
		
		public TestCaseInstance mapRow(ResultSet rs, int arg1) throws SQLException {
			TestCaseInstance t = new TestCaseInstance();
			t.setId(rs.getInt("ID"));
			t.setTestCaseId(rs.getInt("TESTCASE_ID"));
			t.setName(rs.getString("name"));
			t.setDescription(rs.getString("description"));
			t.setValidateOutput(rs.getInt("validate_output") == 1);
			t.setOutputTemplate(rs.getString("output_template"));
			t.setAllowBlankOutput(rs.getInt("allow_blank_output") == 1);
			if(rs.getString("validate_type") != null) {
				t.setValidateType(ValidationType.valueOf(rs.getString("validate_type")));
			}
			return t;
		}
	}

	private static final class TestCaseValueRowMapper implements RowMapper<TestCaseValue> {
		
		public TestCaseValue mapRow(ResultSet rs, int arg1) throws SQLException {
			TestCaseValue t = new TestCaseValue();
			t.setId(rs.getInt("ID"));
			t.setTestCaseInstanceId(rs.getInt("TESTCASE_INSTANCE_ID"));
			t.setName(rs.getString("KEY_NAME"));
			t.setValue(rs.getString("KEY_VALUE"));
			t.setValueType(ValueType.valueOf(rs.getString("VALUE_TYPE")));
			return t;
		}
	}
	
	/*
	 * 	private static final String GET_TESTRUNS = "select tr.*, ti.id TESTCASE_INSTANCE_ID, ti.name TESTCASE_INSTANCE_NAME, ti.description, "
			+ "cat.name CATEGORY_NAME, h.id host_id, h.name host_name, h.hostname host_hostname, h.port host_port, h.securehttp host_securehttp "
			+ " from TESTCASE_RUN tr, TESTCASE_INSTANCE ti, TESTCASE tc, HOSTS h , TEST_CATEGORY cat "
			+ " where ti.id =  tr.TESTCASE_INSTANCE_ID"
			+ " and ti.TESTCASE_ID = tc.id "
			+ " and tr.host_id = h.id "
			+ " and tc.TEST_CATEGORY_ID = cat.id ";

	 */
	private static final class TestCaseRunMapper implements RowMapper<TestCaseRun> {
		
		public TestCaseRun mapRow(ResultSet rs, int rowNum) throws SQLException {
			TestCaseRun run = new TestCaseRun();
			run.setId(rs.getInt("id"));
			TestCaseInstance ti = new TestCaseInstance();
			ti.setId(rs.getInt("TESTCASE_INSTANCE_ID"));
			ti.setName(rs.getString("TESTCASE_INSTANCE_NAME"));
			ti.setDescription(rs.getString("description"));
			Host h = new Host();
			h.setHostname(rs.getString("host_hostname"));
			h.setId(rs.getInt("host_id"));
			h.setName(rs.getString("host_name"));
			h.setPort(rs.getInt("host_port"));
			h.setSecureHttp(rs.getInt("host_securehttp") == 1);
			run.setHost(h);
			run.setTestCaseInstance(ti);
			run.setSuccess(rs.getInt("SUCCESS") == 1);
			run.setRunDate(rs.getTimestamp("RUN_DATE"));
			run.setError(rs.getString("ERROR"));
			run.setContentType(HttpContentType.fromType(rs.getString("CONTENT_TYPE")));
			run.setResult(rs.getString("RESULT"));
			run.setReturnCode(rs.getInt("RETURN_CODE"));
			return run;
		}
	}


}
