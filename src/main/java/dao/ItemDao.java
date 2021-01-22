package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import logic.Item;

@Repository   //@Component + 영속객체. model 담당 객체 
public class ItemDao {
	private NamedParameterJdbcTemplate template;
	private RowMapper<Item> mapper = new BeanPropertyRowMapper<Item>(Item.class);
	private Map<String,Object> param = new HashMap<>();
	@Autowired
	public void setDataSource(DataSource dataSource) {
		//dataSource : db 연결 객체
		template = new NamedParameterJdbcTemplate(dataSource);
	}
	public List<Item> list() {
		return template.query("select * from item", mapper);
	}
	public Item selectOne(Integer id) {
		param.clear();
		param.put("id", id);
		return template.queryForObject("select * from item where id=:id",
				    param, mapper);
	}
	public void insert(Item item) {
		param.clear();
		//id : 등록된 id중 최대값
		int id=template.queryForObject("select ifnull(max(id),0) from item",
				param, Integer.class);
		item.setId(++id + "");
		String sql = "insert into item (id,name,price,description,pictureUrl )"
				+ " values (:id,:name,:price,:description,:pictureUrl )";
		//prop : item 객체의 프로퍼티를 이용하여 sql의 파라미터값을 설정.
		SqlParameterSource prop = new BeanPropertySqlParameterSource(item);
		template.update(sql, prop);
	}
	public void update(Item item) {
		String sql="update item set name=:name, price=:price, "
				+ " description=:description, pictureUrl=:pictureUrl "
				+ " where id=:id";
		SqlParameterSource prop = new BeanPropertySqlParameterSource(item);
		template.update(sql,  prop);
		
	}
	public void delete(Integer id) {
		param.clear();
		param.put("id", id);
		String sql = " delete from item where id=:id";
		template.update(sql, param);
		
	}
}
