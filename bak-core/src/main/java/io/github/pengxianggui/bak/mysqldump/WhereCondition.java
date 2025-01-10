package io.github.pengxianggui.bak.mysqldump;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.Condition;
import io.github.pengxianggui.bak.BakException;
import io.github.pengxianggui.bak.util.SqlUtil;
import net.sf.jsqlparser.JSQLParserException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class WhereCondition {

    private String value;
    private final List<Condition> conditions;

    public WhereCondition(String value) {
        if (StrUtil.isBlank(value)) {
            this.value = ""; // 调用shell脚本传参时, null相当于没有传, 会导致报错, 这里给一个空字符串
            this.conditions = new ArrayList<>();
        } else {
            this.value = value;
            this.conditions = parse(value);
        }
    }

    private List<Condition> parse(String value) {
        try {
            return SqlUtil.parseWhereCondition(value);
        } catch (JSQLParserException e) {
            throw new BakException("where条件解析失败", e);
        }
    }

    public WhereCondition and(String condition) {
        this.conditions.addAll(parse(condition));
        this.value += " and " + condition;
        return this;
    }

    public Optional<Condition> findFirst(Predicate<Condition> predicate) {
        return this.conditions.stream().filter(predicate).findFirst();
    }

    public boolean contain(String field, String operator) {
        if (CollectionUtil.isEmpty(this.conditions)) {
            return false;
        }
        return this.conditions.stream().anyMatch(c -> StrUtil.equals(field, c.getField()) && StrUtil.equals(operator, c.getOperator()));
    }

    public boolean isEmpty() {
        return StrUtil.isBlank(this.value);
    }

    /**
     * 为了保证value、conditions一致, 将返回一个新的List。
     *
     * @return
     */
    public List<Condition> getConditions() {
        return new ArrayList<>(conditions);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
