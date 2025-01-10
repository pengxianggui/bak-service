package io.github.pengxianggui.bak.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.Condition;
import io.github.pengxianggui.bak.mysqldump.WhereCondition;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SqlUtil {

    /**
     * 是否是合法的where条件
     *
     * @param whereCondition SQL WHERE 条件(不含where关键词)
     * @param allowBlank     是否允许为空
     * @return
     */
    public static boolean isLegal(String whereCondition, boolean allowBlank) {
        if (StrUtil.isBlank(whereCondition) && allowBlank) {
            return true;
        }

        try {
            parseWhereCondition(whereCondition);
            return true;
        } catch (JSQLParserException e) {
            return false;
        }
    }

    /**
     * 解析where 条件。注意: 不支持复杂的嵌套条件, 并且解析后的结构化数据，各条件项之间的关系(or 还是 and)不支持!
     *
     * @param whereCondition SQL WHERE 条件,  例如: type = '2' AND age > 23 OR name != 'John'
     * @return 解析后的条件列表
     */
    public static List<Condition> parseWhereCondition(String whereCondition) throws JSQLParserException {
        List<Condition> result = new ArrayList<>();
        // 构建虚拟的 SELECT 语句，便于解析 WHERE 子句
        String sql;
        if (StrUtil.isBlank(whereCondition)) {
            sql = "SELECT * FROM xxx";
        } else {
            sql = "SELECT * FROM xxx WHERE " + whereCondition;
        }
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        parseExpression(plainSelect.getWhere(), result);
        return result;
    }

    /**
     * 递归解析表达式，并提取字段、操作符和值。
     *
     * @param expression 当前表达式
     * @param conditions 存储解析结果的 List
     */
    private static void parseExpression(Expression expression, List<Condition> conditions) throws JSQLParserException {
        if (expression instanceof Parenthesis) { // 处理括号中的表达式
            parseExpression(((Parenthesis) expression).getExpression(), conditions);
        } else if (expression instanceof AndExpression || expression instanceof OrExpression) {
            // 递归解析 AND 或 OR 表达式的左侧和右侧
            BinaryExpression binaryExpr = (BinaryExpression) expression;
            parseExpression(binaryExpr.getLeftExpression(), conditions);
            parseExpression(binaryExpr.getRightExpression(), conditions);
        } else if (expression instanceof BinaryExpression) {
            // 处理简单条件
            BinaryExpression binaryExpr = (BinaryExpression) expression;
            Expression leftExpression = binaryExpr.getLeftExpression();
            String field;
            if (leftExpression instanceof Column) {
                field = ((Column) binaryExpr.getLeftExpression()).getColumnName();
            } else {
                field = leftExpression.toString(); // 规避 1=1这样的条件
            }
            String operator = binaryExpr.getStringExpression();
            String value = binaryExpr.getRightExpression().toString();
            Condition condition = new Condition(field, operator, value);
            condition.setPlaceHolder(Boolean.FALSE);
            conditions.add(condition);
        } else if (expression instanceof IsNullExpression) {
            IsNullExpression isNullExpr = (IsNullExpression) expression;
            String field = isNullExpr.getLeftExpression().toString();
            String operator = isNullExpr.isNot() ? "is not" : "is";
            Condition condition = new Condition();
            condition.setField(field);
            condition.setOperator(operator);
            condition.setValue("null");
            condition.setPlaceHolder(Boolean.FALSE);
            conditions.add(condition);
        } else {
            throw new JSQLParserException("where 条件解析失败: " + expression.toString());
        }
    }

    public static void main(String[] args) {
        // 示例 WHERE 条件
        WhereCondition condition = new WhereCondition("id = 2 AND (age > 3 OR name != 'John')");
        for (Condition c : condition.getConditions()) {
            System.out.println("Field: " + c.getField() + "\t operator:" + c.getOperator() + "\t value:" + c.getValue());
        }

        System.out.println(condition);
        System.out.println("================================================");
        condition.and("sex=1");
        for (Condition c : condition.getConditions()) {
            System.out.println("Field: " + c.getField() + "\t operator:" + c.getOperator() + "\t value:" + c.getValue());
        }
        System.out.println(condition);
    }
}
