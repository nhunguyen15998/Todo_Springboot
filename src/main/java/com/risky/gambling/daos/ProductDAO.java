package com.risky.gambling.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.risky.gambling.mappers.ProductMapper;
import com.risky.gambling.models.Product;
import com.risky.gambling.utils.ComparisonOperator;
import com.risky.gambling.utils.DataMapping;
import com.risky.gambling.utils.JoinCondition;

@Repository
public class ProductDAO extends JdbcDaoSupport {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Product product;
    
    public ProductDAO(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public List<Product> get(ArrayList<DataMapping> data,ArrayList<ComparisonOperator> conditions, ArrayList<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit) 
    {
        try {
            String sql = product.get(null, conditions, joins, groupBys, orderBy, limit);
            List<Product> productList = this.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<Product>(Product.class));
            return productList;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
	}

    public Number create(ArrayList<DataMapping> data)
    {   
        try {
            // String table = product.getTable();
            // Map<String, String> map = new HashMap<String, String>();
            // for (DataMapping dataMapping : data) {
            //     map.put(dataMapping.key, dataMapping.value);
            // }
            // Number createdId = new SimpleJdbcInsert(this.getJdbcTemplate()).withTableName(table).usingGeneratedKeyColumns("id").executeAndReturnKey(map);
            String sql = product.create(data);
            int created = this.getJdbcTemplate().update(sql);
            return created;//createdId;
        } catch (Exception e) {
            String err = e.getMessage();
            return 0;
        }
    }

    public ProductMapper getById(ArrayList<ComparisonOperator> conditions)
    {
        try {
            String sql = product.get(null, conditions, null, null, null, null);
            Product product = this.getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Product>(Product.class));
            ProductMapper productMapper = mapper.map(product, ProductMapper.class);
            return productMapper;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
    }

    public boolean update(ArrayList<DataMapping> data, ArrayList<ComparisonOperator> conditions)
    {
        try {
            String sql = product.update(data, conditions);
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
            String sql = product.delete(conditions);
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
