package com.risky.gambling.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.risky.gambling.mappers.TodoMapper;
import com.risky.gambling.models.Todo;
import com.risky.gambling.utils.ComparisonOperator;
import com.risky.gambling.utils.DataMapping;
import com.risky.gambling.utils.JoinCondition;

@Repository
public class TodoDAO extends JdbcDaoSupport {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Todo todo;
    
    public TodoDAO(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public List<Todo> get(ArrayList<DataMapping> data,ArrayList<ComparisonOperator> conditions, ArrayList<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit) 
    {
        try {
            String sql = todo.get(null, conditions, joins, groupBys, orderBy, limit);
            List<Todo> todoList = this.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<Todo>(Todo.class));
            return todoList;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
	}

    public Number create(ArrayList<DataMapping> data)
    {   
        try {
            // String table = todo.getTable();
            // Map<String, String> map = new HashMap<String, String>();
            // for (DataMapping dataMapping : data) {
            //     map.put(dataMapping.key, dataMapping.value);
            // }
            // Number createdId = new SimpleJdbcInsert(this.getJdbcTemplate()).withTableName(table).usingGeneratedKeyColumns("id").executeAndReturnKey(map);
            String sql = todo.create(data);
            int created = this.getJdbcTemplate().update(sql);
            return created;//createdId;
        } catch (Exception e) {
            String err = e.getMessage();
            return 0;
        }
    }

    public TodoMapper getById(ArrayList<ComparisonOperator> conditions)
    {
        try {
            String sql = todo.get(null, conditions, null, null, null, null);
            Todo todo = this.getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Todo>(Todo.class));
            TodoMapper todoMapper = mapper.map(todo, TodoMapper.class);
            return todoMapper;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
    }

    public boolean update(ArrayList<DataMapping> data, ArrayList<ComparisonOperator> conditions)
    {
        try {
            String sql = todo.update(data, conditions);
            int updated = this.getJdbcTemplate().update(sql);
            System.out.println("updated:"+updated);
            if(updated > 0){
                return true;
            }
            return false;
        } catch (Exception e) {
            String err = e.getMessage();
            return false;
        }
    }

    public boolean delete(ArrayList<ComparisonOperator> conditions)
    {
        try {
            String sql = todo.delete(conditions);
            int deleted = this.getJdbcTemplate().update(sql);
            if(deleted > 0){
                return true;
            }
            return false;
        } catch (Exception e) {
            String err = e.getMessage();
            return false;
        }
    }

}
