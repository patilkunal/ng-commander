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

import com.inovision.apitest.model.Schedule;
import com.inovision.apitest.model.TestCaseInstance;

@Repository
public class ScheduleDAOImpl implements ScheduleDAO {

	private static final String GET_SCHEDULES = "select * from schedules";
	private static final String GET_SCHEDULE = "select * from schedules where id = :id";
	private static final String SAVE_SCHEDULE = "insert into schedules(name, cron_expr, active, host_id, test_category_id) values (:name, :cronexpr, :active, :hostid, :testCategoryId)";
	private static final String UPDATE_SCHEDULE = "update schedules set name = :name, cron_expr = :cronexpr, active = :active, host_id = :hostid, test_category_id = :testCategoryId where id = :id";
	private static final String DELETE_SCHEDULE = "delete from schedules where id = :id";
	private static final String GET_SCHEDULED_TESTCASES = "select ti.* from scheduled_testcases st join TESTCASE_INSTANCE ti on ti.id = st.testcase_instance_id where st.schedule_id = :scheduleid";
	private static final String DELETE_SCHEDULE_TESTINSTANCES = "delete from scheduled_testcases where schedule_id = :scheduleid";
	private static final String INSERT_SCHEDULE_TESTINSTANCES = "insert into scheduled_testcases(schedule_id, testcase_instance_id) values( :scheduleid, :testcaseinstanceid)";
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public List<Schedule> getScheduleList() {		
		return jdbcTemplate.query(GET_SCHEDULES, new ScheduleRowMapper());
	}

	@Override
	public Schedule getSchedule(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", new Integer(id));
		params.put("scheduleid", id);
		Schedule sch = jdbcTemplate.queryForObject(GET_SCHEDULE, params, new ScheduleRowMapper());
		if(sch != null) {
			sch.setTestCaseInstances(jdbcTemplate.query(GET_SCHEDULED_TESTCASES, params, new TestCaseDAOImpl.TestCaseInstanceRowMapper()));
		}
		return sch;
	}

	@Override
	public Schedule saveSchedule(Schedule schedule) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", schedule.getName());
		params.put("cronexpr", schedule.getCronExpression());
		params.put("active", schedule.isActive() ? 1 : 0);
		params.put("hostid", schedule.getHostId());
		params.put("testCategoryId", schedule.getCategoryId());
		if(schedule.getId() > -1) {
			//update
			params.put("id", schedule.getId());
			jdbcTemplate.update(UPDATE_SCHEDULE, params);
		} else {
			//insert new
			KeyHolder keyholder = new GeneratedKeyHolder(); 
			jdbcTemplate.update(SAVE_SCHEDULE, new MapSqlParameterSource(params), keyholder, new String[] {"ID"});
			schedule.setId(keyholder.getKey().intValue());
		}
		params.clear();
		params.put("scheduleid", schedule.getId());
		jdbcTemplate.update(DELETE_SCHEDULE_TESTINSTANCES, params);
		List<TestCaseInstance> testInstances = schedule.getTestCaseInstances();
		if((testInstances != null) && (testInstances.size() > 0)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> batchValues[] = new HashMap[testInstances.size()];
			int i=0;
			for(TestCaseInstance t : testInstances) {
				Map<String, Object> map = new HashMap<String, Object>(1);
				map.put("scheduleid", schedule.getId());
				map.put("testcaseinstanceid", t.getId());
				batchValues[i++] = map;
			}
			jdbcTemplate.batchUpdate(INSERT_SCHEDULE_TESTINSTANCES, batchValues);
		}
		return schedule;
	}

	@Override
	public Schedule deleteSchedule(int id) {
		Schedule sch = getSchedule(id);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", new Integer(id));
		jdbcTemplate.update(DELETE_SCHEDULE, params);
		return sch;
	}
	
	@Override
	public void deleteSchedulesByHost(int hostid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hostid", hostid);		
		jdbcTemplate.update("delete from scheduled_testcases where schedule_id in (select id from schedules where host_id = :hostid)", params);				
		jdbcTemplate.update("delete from schedules where host_id = :hostid", params);				
	}
	
	public final class ScheduleRowMapper implements RowMapper<Schedule> {

		@Override
		public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
			Schedule s = new Schedule();
			s.setId(rs.getInt("ID"));
			s.setName(rs.getString("NAME"));
			s.setCronExpression(rs.getString("CRON_EXPR"));
			s.setActive(rs.getInt("ACTIVE") == 1);
			s.setHostId(rs.getInt("HOST_ID"));
			s.setCategoryId(rs.getInt("TEST_CATEGORY_ID"));
			return s;
		}
		
	}
	
	/*
	public final class IntegerRowMapper implements RowMapper<Integer> {
		
		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Integer(rs.getInt("TESTCASE_INSTANCE_ID"));
		}
	}
	*/
}
