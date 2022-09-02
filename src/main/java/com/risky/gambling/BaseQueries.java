package com.risky.gambling;

import java.util.ArrayList;

import com.risky.gambling.interfaces.IBaseQuery;
import com.risky.gambling.utils.ComparisonOperator;
import com.risky.gambling.utils.DataMapping;
import com.risky.gambling.utils.JoinCondition;

public class BaseQueries implements IBaseQuery
{
    private String table;
    private String[] columns;
    
    public BaseQueries(String table, String[] columns) {
        this.table = table;
        this.columns = columns;
    }

    //get columns
    public String[] getColumns(ArrayList<DataMapping> data)
    {
        try {
            String[] columns = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                columns[i] = data.get(i).key;
            }
            return columns;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
    }

    //get values
    public String[] getValues(ArrayList<DataMapping> data)
    {
        try {
            String[] values = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                values[i] = data.get(i).value;
            }
            return values;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
    }
    
    //get
    public String get(String[] selects, ArrayList<ComparisonOperator> conditions, ArrayList<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit)
    {
        try {
            //select - if select all columns
            if(selects == null){
                selects = columns;
            }

            //condition
            String conditionStr = " where ";
            if(conditions != null){
                for(ComparisonOperator condition : conditions) {
                    //string: '091828828', 'ad12'
                    //number: 123, 0
                    String formatConditionStr = " %s %s %s '%s' %s ";
                    if(condition.value.matches("^[0-9]*$")){
                        if(condition.value.startsWith("0") && condition.value.length() > 1) break;
                        formatConditionStr = " %s %s %s %s %s ";
                    }
                    conditionStr += String.format(formatConditionStr, condition.prefix, condition.key, condition.operator, condition.value, condition.suffix);
                }
            }

            //join
            String joinStr = "";
            if(joins != null){
                for (JoinCondition join : joins) {
                    joinStr += String.format(" %s %s on %s %s %s ", join.typeJoin, join.table, join.onEquation.key, join.onEquation.operator, join.onEquation.value);
                }
            }

            //query
            String query = "select " + String.join(",", selects) + " from " + this.table + " " 
                                     + (joins != null ? joinStr + " " : "") 
                                     + (conditions != null ? conditionStr + " " : "");

            //groupby
            if(groupBys != null){
                query += " group by " + String.join(",", groupBys);
            }

            //orderby
            if(orderBy != null){
                query += " order by " + orderBy;
            }

            //limit
            if(limit != null){
                query += " limit " + String.join(",", limit);
            }
            System.out.println("query:"+query);

            return query;

        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
    }

    //create
    public String create(ArrayList<DataMapping> data)
    {
        try {
            String query = "insert into " + this.table;
            String keyStr = "(" + String.join(",", this.getColumns(data)) + ")";   
            String valueStr = "";
            String[] values = this.getValues(data);
            for(int i = 0; i < values.length; i++) {
                if(values[i].matches("^[0-9]*$")){
                    if(values[i].startsWith("0") && values[i].length() > 1){
                        valueStr += "'"+values[i]+"'";
                    }
                    valueStr += values[i];
                } else {
                    valueStr += "'"+values[i]+"'";
                }
                if(i < values.length - 1){
                    valueStr += ",";
                }
            }
            query += keyStr + " values(" + valueStr + ")";
            System.out.println("query"+query);
            return query;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
    }

    //update
    public String update(ArrayList<DataMapping> data, ArrayList<ComparisonOperator> conditions)
    {
        try {
            String query = "update " + this.table + " set ";
            String updateStr = "";
            //data
            for (int i = 0; i < data.size(); i++) {
                if(data.get(i).value.matches("^[0-9]*$")){
                    if(data.get(i).value.startsWith("0") && data.get(i).value.length() > 1){
                        updateStr += data.get(i).key + " = '" + data.get(i).value + "'";
                    }
                    updateStr += data.get(i).key + " = " + data.get(i).value;
                } else {
                    updateStr += data.get(i).key + " = '" + data.get(i).value + "'";
                }
                if(i < data.size() - 1){
                    updateStr += ", ";
                }
            }
            query += updateStr;
            //condition
            String conditionStr = " where ";
            if(conditions.size() > 0){
                for (ComparisonOperator condition : conditions) {
                    if(condition.value.matches("^[0-9]*$")){
                        if(condition.value.startsWith("0") && condition.value.length() > 1){
                            conditionStr += String.format(" %s %s %s '%s' %s ", condition.prefix, condition.key, condition.operator, condition.value, condition.suffix);
                        }
                        conditionStr += String.format(" %s %s %s %s %s ", condition.prefix, condition.key, condition.operator, condition.value, condition.suffix);
                    } else {
                        conditionStr += String.format(" %s %s %s '%s' %s ", condition.prefix, condition.key, condition.operator, condition.value, condition.suffix);
                    }
                }
            }

            query += (conditions.size() > 0 ? conditionStr : "");
            return query;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
    }

    //delete
    public String delete(ArrayList<ComparisonOperator> conditions)
    {
        try {
            String conditionStr = " where ";
            for (ComparisonOperator condition : conditions) {
                if(condition.value.matches("^[0-9]*$")){
                    if(condition.value.startsWith("0") && condition.value.length() > 1){
                        conditionStr += String.format(" %s %s %s '%s' %s ", condition.prefix, condition.key, condition.operator, condition.value, condition.suffix);
                    }
                    conditionStr += String.format(" %s %s %s %s %s ", condition.prefix, condition.key, condition.operator, condition.value, condition.suffix);
                } else {
                    conditionStr += String.format(" %s %s %s '%s' %s ", condition.prefix, condition.key, condition.operator, condition.value, condition.suffix);
                }
            }
            String query = "delete from " + this.table + conditionStr;
            return query;
        } catch (Exception e) {
            String err = e.getMessage();
            return null;
        }
    }

    //get
    public String getTable() {
        return table;
    }

}
