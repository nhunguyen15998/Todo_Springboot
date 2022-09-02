package com.risky.gambling.interfaces;

import java.util.ArrayList;

import com.risky.gambling.utils.ComparisonOperator;
import com.risky.gambling.utils.DataMapping;
import com.risky.gambling.utils.JoinCondition;

public interface IBaseQuery {
    public String[] getColumns(ArrayList<DataMapping> data);
    public String[] getValues(ArrayList<DataMapping> data);

    public String get(String[] selects, ArrayList<ComparisonOperator> conditions, ArrayList<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit);
    public String create(ArrayList<DataMapping> data);
    public String update(ArrayList<DataMapping> data, ArrayList<ComparisonOperator> conditions);
    public String delete(ArrayList<ComparisonOperator> conditions);
}
