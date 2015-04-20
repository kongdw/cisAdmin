package com.github.loafer.mybatis.pagination;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.github.loafer.mybatis.pagination.dialect.Dialect;
import com.github.loafer.mybatis.pagination.helper.DialectHelper;
import com.github.loafer.mybatis.pagination.helper.SqlHelper;
import com.github.loafer.mybatis.pagination.util.PatternMatchUtils;

/**
 * Date Created  2014-2-17
 *
 * @author loafer[zjh527@163.com]
 * @version 2.0
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PaginationInterceptor implements Interceptor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ThreadLocal<Sort> PAGINATION_ORDERBY = new ThreadLocal<Sort>();

    private static final ThreadLocal<Integer> PAGINATION_TOTAL = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    private Dialect dialect;
    private String pagingSqlIdRegex;

    public static void setPaginationOrderby(Sort sort) {
        PAGINATION_ORDERBY.set(sort);
    }

    /**
     * Get Pagination total
     *
     * @return
     */
    public static int getPaginationTotal() {
        int count = PAGINATION_TOTAL.get();
        PaginationInterceptor.clean();
        return count;
    }

    public static void clean() {
        PAGINATION_TOTAL.remove();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");

        int offset = rowBounds.getOffset();
        int limit = rowBounds.getLimit();

        boolean intercept = PatternMatchUtils.simpleMatch(pagingSqlIdRegex, mappedStatement.getId());
        if (intercept && dialect.supportsLimit()
                && (offset != RowBounds.NO_ROW_OFFSET || limit != RowBounds.NO_ROW_LIMIT)) {

            BoundSql boundSql = statementHandler.getBoundSql();
            Object parameterObject = boundSql.getParameterObject();
            Connection connection = (Connection) invocation.getArgs()[0];
            int count = SqlHelper.getCount(mappedStatement, connection, parameterObject, dialect);
            PAGINATION_TOTAL.set(count);

            String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");

            Sort sort = PAGINATION_ORDERBY.get();
            if (sort != null) {
                Iterator<Order> orders = sort.iterator();
                if (orders.hasNext()) {
                    Order order = orders.next();
                    originalSql = StringUtils.substringBefore(StringUtils.substringBefore(
                            StringUtils.substringBefore(originalSql, " order "), "\norder "), "\torder ");
                    originalSql = originalSql + " order by " + order.getProperty() + " " + order.getDirection();
                }
            }
            metaStatementHandler.setValue("delegate.boundSql.sql", dialect.getLimitString(originalSql, offset, limit));
            metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
            metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
            logger.info("limit sql: {}", boundSql.getSql());
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String dialectClass = properties.getProperty("dialectClass");
        if (StringUtils.isBlank(dialectClass)) {
            Dialect.Type databaseType = null;
            try {
                databaseType = Dialect.Type.valueOf(properties.getProperty("dialect").toUpperCase());
            } catch (Exception e) {
            }

            if (null == databaseType) {
                throw new RuntimeException(
                        "Plug-in [PaginationInterceptor] the dialect of the attribute value is invalid! Valid values for:"
                                + getDialectTypeValidValues());
            }
            dialect = DialectHelper.getDialect(databaseType);
        } else {
            try {
                dialect = (Dialect) Class.forName(dialectClass).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Plug-in [PaginationInterceptor] cannot create dialect instance by dialectClass: "
                                + dialectClass);
            }
        }

        pagingSqlIdRegex = properties.getProperty("stmtIdRegex", "*.selectPaging");
    }

    private String getDialectTypeValidValues() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Dialect.Type.values().length; i++) {
            sb.append(Dialect.Type.values()[i].name()).append(",");
        }
        return sb.toString();
    }
}
