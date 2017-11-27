package com.inovision.apitest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.inovision.apitest.model.Host;

@Repository
public class HostDAOImpl implements HostDAO {

	private static final Logger LOGGER = Logger.getLogger(HostDAOImpl.class);
	
	private static final String GET_HOST_BY_ID = "select * from hosts where id = :id";
	private static final String GET_HOSTS = "select * from hosts order by id ";
	private static final String GET_HOSTS_BY_CATEGORY = "select * from hosts where test_category_id = :testCategoryId order by id ";
	private static final String SAVE_HOST = "insert into HOSTS(HOSTNAME, TEST_CATEGORY_ID, PORT, NAME, SECUREHTTP ) "
			+ " values(:hostname, :testCategoryId, :port, :name, :securehttp)";
	private static final String UPDATE_HOST = "update HOSTS set HOSTNAME = :hostname, TEST_CATEGORY_ID = :testCategoryId, PORT = :port, NAME = :name, SECUREHTTP = :securehttp "
			+ " where id = :id ";
	private static final String DELETE_HOST = "delete from hosts where id = :id";

	
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public void setJbdcTemplate(NamedParameterJdbcTemplate jbdcTemplate) {
		this.jdbcTemplate = jbdcTemplate;
	}
	
	public List<Host> getHosts() {
		return jdbcTemplate.query(GET_HOSTS, new HostRowMapper());
	}

	public List<Host> getHosts(int testCategoryId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("testCategoryId", testCategoryId);
		return jdbcTemplate.query(GET_HOSTS_BY_CATEGORY, params, new HostRowMapper());
	}
	
	public Host getHost(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		List<Host> list = jdbcTemplate.query(GET_HOST_BY_ID, params, new HostRowMapper());
		if(list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public Host saveHost(Host host) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hostname", host.getHostname());
		params.put("testCategoryId", host.getTestCategoryId());
		params.put("port", host.getPort());
		params.put("name", host.getName());
		params.put("securehttp", host.isSecureHttp() ? 1 : 0);
		if(host.getId() > -1) {
			params.put("id", host.getId());
			LOGGER.debug("Updating " + host);
			jdbcTemplate.update(UPDATE_HOST, params);			
		} else {
			LOGGER.debug("Saving " + host);
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(SAVE_HOST, new MapSqlParameterSource(params), keyHolder, new String[] {"ID"});
			host.setId(keyHolder.getKey().intValue());			
		}
		return host;
	}
	
	public void deleteHost(int id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		jdbcTemplate.update(DELETE_HOST, paramMap);
	}
	
	private static final class HostRowMapper implements RowMapper<Host> {
		
		public Host mapRow(ResultSet rs, int rowNum) throws SQLException {
			Host host = new Host();
			host.setId(rs.getInt("ID"));
			host.setTestCategoryId(rs.getInt("test_category_id"));
			host.setHostname(rs.getString("hostname"));
			host.setPort(rs.getInt("port"));
			host.setName(rs.getString("name"));
			host.setSecureHttp(rs.getInt("securehttp") == 1);
			return host;
		}
	}
	
	
}
