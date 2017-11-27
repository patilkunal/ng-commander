package com.inovision.apitest.dao;

import java.util.Arrays;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.inovision.apitest.dao.ScheduleDAOImpl;
import com.inovision.apitest.dao.ScheduleDAOImpl.ScheduleRowMapper;
import com.inovision.apitest.dao.TestCaseDAOImpl.TestCaseInstanceRowMapper;
import com.inovision.apitest.model.Schedule;

public class TestScheduleDAOImpl extends TestCase {

	public void testGetScheduleList() {
		//fail("Not yet implemented");
	}

	public void testGetSchedule() {
		ScheduleDAOImpl dao = new ScheduleDAOImpl();
		JdbcOperations classicJdbcTemplate = EasyMock.createMock(JdbcOperations.class);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(classicJdbcTemplate);		
		dao.setJdbcTemplate(jdbcTemplate);
		Schedule sch = new Schedule();
		sch.setId(100);
		sch.setActive(true);
		sch.setCategoryId(1);
		sch.setCronExpression("* *");
		sch.setHostId(1);
		sch.setName("Test");
		EasyMock.expect(classicJdbcTemplate.query(EasyMock.anyObject(PreparedStatementCreator.class), EasyMock.anyObject(ScheduleRowMapper.class))).andReturn(Arrays.asList(sch));
		EasyMock.expect(classicJdbcTemplate.query(EasyMock.anyObject(PreparedStatementCreator.class), EasyMock.anyObject(TestCaseInstanceRowMapper.class))).andReturn(null);
		
		EasyMock.replay(classicJdbcTemplate);		
		Schedule returnsch = dao.getSchedule(100);
		assertNotNull(returnsch);
		assertTrue(sch.equals(returnsch));
		EasyMock.verify(classicJdbcTemplate);		
	}

	public void testGetScheduleNull() {
		ScheduleDAOImpl dao = new ScheduleDAOImpl();
		JdbcOperations classicJdbcTemplate = EasyMock.createMock(JdbcOperations.class);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(classicJdbcTemplate);		
		dao.setJdbcTemplate(jdbcTemplate);
		Schedule sch = null;
		EasyMock.expect(classicJdbcTemplate.query(EasyMock.anyObject(PreparedStatementCreator.class), EasyMock.anyObject(ScheduleRowMapper.class))).andReturn(Arrays.asList(sch));		
		
		EasyMock.replay(classicJdbcTemplate);		
		Schedule returnsch = dao.getSchedule(100);
		assertNull(returnsch);		
		EasyMock.verify(classicJdbcTemplate);		
	}
	
	public void testSaveSchedule() {
		//fail("Not yet implemented");
	}

	public void testDeleteSchedule() {
		//fail("Not yet implemented");
	}

}
