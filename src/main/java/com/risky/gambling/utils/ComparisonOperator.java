package com.risky.gambling.utils;

public class ComparisonOperator 
{
    public String key;
    public String value;
    public String operator;
    public String prefix;
    public String suffix;

    public static ComparisonOperator comparisonOperator;

    public static ComparisonOperator getInstance(String prefix, String key, String operator, String value, String suffix)
    {
        if(comparisonOperator == null){
            ComparisonOperator item = new ComparisonOperator();
            item.prefix = prefix;
            item.key = key;
            item.operator = operator;
            item.value = value;
            item.suffix = suffix;
            return item;
        }
        return comparisonOperator;
    }
}
