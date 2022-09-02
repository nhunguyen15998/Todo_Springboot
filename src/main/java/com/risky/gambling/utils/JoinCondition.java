package com.risky.gambling.utils;

public class JoinCondition 
{
    public String typeJoin;
    public String table;
    public ComparisonOperator onEquation;

    public static JoinCondition comparisonOperator;

    public static JoinCondition getInstance(String typeJoin, String table, ComparisonOperator onEquation)
    {
        if(comparisonOperator == null){
            JoinCondition item = new JoinCondition();
            item.typeJoin = typeJoin;
            item.table = table;
            item.onEquation = onEquation;
            return item;
        }
        return comparisonOperator;
    }
    
}
