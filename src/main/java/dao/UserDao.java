package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import logic.Item;
import logic.User;

@Repository
public class UserDao {
	private NamedParameterJdbcTemplate template;
	private RowMapper<User> mapper = new BeanPropertyRowMapper<User>(User.class);
	private Map<String, Object> param = new HashMap<>();

	@Autowired
	public void setDataSource(DataSource dataSource) {
		template = new NamedParameterJdbcTemplate(dataSource);
	}

	public void insert(User user) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "insert into useraccount "
	+ "(userid, username, password,birthday, phoneno, postcode,address,email)"
	+ " values (:userid,:username,:password,:birthday,:phoneno,:postcode,:address,:email)";
		template.update(sql, param);
	}

	public User selectUserOne(String userid) {
		param.clear();
		param.put("userid", userid);
		return template.queryForObject
			("select * from useraccount where userid=:userid", param, mapper);
	}

	public void update(User user) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql="update useraccount set username=:username, birthday=:birthday, phoneno=:phoneno, "
				+ "postcode=:postcode, address=:address, email=:email where userid=:userid";
		template.update(sql,param);		
	}

	public void delete(String userid) {
		param.clear();
		param.put("userid", userid);
		template.update("delete from useraccount where userid=:userid",param);		
	}

	public List<User> list() {
		return template.query("select * from useraccount", mapper);
	}

	public List<User> list(String[] idchks) {
		String ids = "";
		for(int i=0;i < idchks.length;i++) {
			ids += "'" + idchks[i] + ((i==idchks.length-1)?"'":"',");
		}
		String sql="select * from useraccount where userid in ("+ ids + ")";
		return template.query(sql,mapper);
	}

	public String search(User user) {
		String sql =null;
		if(user.getUserid() == null) //idsearch 인경우
		    sql ="select concat(substr(userid,1,char_length(userid)-2),'**')"
	    		+ " from useraccount "
	    		+ "where email=:email and phoneno=:phoneno"; 
		else //pwsearch 인경우
		    sql ="select concat('**',substr(password,3,char_length(password)-2))"
    		+ " from useraccount "
    		+ " where userid=:userid and email=:email and phoneno=:phoneno"; 
		System.out.println(sql);
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		return template.queryForObject(sql, param,String.class);
	}
	
	public void passwordUpdate(String userid, String pass) {
		param.clear();
		param.put("userid", userid);
		param.put("password", pass);
		String sql = "update useraccount set password=:password "
				+ " where userid=:userid";
		template.update(sql, param);
	}
}
