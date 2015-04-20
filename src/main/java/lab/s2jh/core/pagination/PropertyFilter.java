/**
 * Copyright (c) 2012
 */
package lab.s2jh.core.pagination;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import lab.s2jh.core.util.DateUtils;
import lab.s2jh.core.util.reflection.ConvertUtils;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;

/**
 * 与具体ORM实现无关的属性过滤条件封装类, 主要记录页面中简单的搜索过滤条件. 用于页面表单传入字符串形式条件，然后转换处理为DAO层面识别的SQL条件
 * 页面表单元素示例：
 * <ul>
 * <li>search['CN_a_OR_b']</li>
 * <li>search['EQ_id']</li>
 * <li>search['CN_user.name']</li>
 * </ul>
 * <p>
 * FORM传递表单参数规则： <br/>
 * 1, 第一部分：以"search[]"作为查询参数标识 <br/>
 * 2, 第二部分：查询类型，@see #MatchType <br/>
 * 3, 第三部分：id_OR_email，category，state, user.userprofile为属性名称,一般对应于Hibernate
 * Entity对应属性,可以以_OR_分隔多个属性进行OR查询
 * </p>
 * <p>
 * 上述拼装字符串形式主要用于JSP页面form表单元素name属性值,如果是Java代码层面追加过滤条件,一般直接用构造函数:
 * PropertyFilter(final MatchType matchType, final String propertyName, final
 * Object matchValue)
 * </p>
 */
public class PropertyFilter {

    private final static Logger logger = LoggerFactory.getLogger(PropertyFilter.class);

    /** 多个属性间OR关系的分隔符. */
    public static final String OR_SEPARATOR = "_OR_";

    /** 属性匹配比较类型. */
    public enum MatchType {
        /** "name": "bk", "description": "is blank", "operator":"IS NULL OR ==''" */
        BK,

        /** "name": "nb", "description": "is not blank", "operator":"IS NOT NULL AND !=''" */
        NB,

        /** "name": "nu", "description": "is null", "operator":"IS NULL" */
        NU,

        /** "name": "nn", "description": "is not null", "operator":"IS NOT NULL" */
        NN,

        /** "name": "in", "description": "in", "operator":"IN" */
        IN,

        /** "name": "ni", "description": "not in", "operator":"NOT IN" */
        NI,

        /** "name": "ne", "description": "not equal", "operator":"<>" */
        NE,

        /** "name": "eq", "description": "equal", "operator":"=" */
        EQ,

        /** "name": "cn", "description": "contains", "operator":"LIKE %abc%" */
        CN,

        /**
         * "name": "nc", "description": "does not contain",
         * "operator":"NOT LIKE %abc%"
         */
        NC,

        /** "name": "bw", "description": "begins with", "operator":"LIKE abc%" */
        BW,

        /**
         * "name": "bn", "description": "does not begin with",
         * "operator":"NOT LIKE abc%"
         */
        BN,

        /** "name": "ew", "description": "ends with", "operator":"LIKE %abc" */
        EW,

        /**
         * "name": "en", "description": "does not end with",
         * "operator":"NOT LIKE %abc"
         */
        EN,

        /** "name": "bt", "description": "between", "operator":"BETWEEN 1 AND 2" */
        BT,

        /** "name": "lt", "description": "less", "operator":"小于" */
        LT,

        /** "name": "gt", "description": "greater", "operator":"大于" */
        GT,

        /** "name": "le", "description": "less or equal","operator":"<=" */
        LE,

        /** "name": "ge", "description": "greater or equal", "operator":">=" */
        GE,

        /** @see javax.persistence.criteria.Fetch */
        FETCH,

        /** Property Less Equal: <= */
        PLE,

        /** Property Less Than: < */
        PLT,

        ACLPREFIXS;
    }

    /** 匹配类型 */
    private MatchType matchType = null;

    /** 匹配值 */
    private Object matchValue = null;

    /**
     * 匹配属性类型
     * 限制说明：如果是多个属性则取第一个,因此需要确保_OR_定义多个属性属于同一类型
     */
    private Class propertyClass = null;

    /** 属性名称数组, 一般是单个属性,如果有_OR_则为多个 */
    private String[] propertyNames = null;
    /**
     * 集合类型子查询,如查询包含某个商品的所有订单列表,如order上面有个List集合products对象，则可以类似这样:search['EQ_products.code'] 
     * 限制说明：框架只支持当前主对象直接定义的集合对象集合查询，不支持再多层嵌套
     */
    private Class subQueryCollectionPropetyType;

    public PropertyFilter() {
    }

    /**
     * @param filterName
     *            比较属性字符串,含待比较的比较类型、属性值类型及属性列表.
     * @param values
     *            待比较的值.
     */
    public PropertyFilter(Class<?> entityClass, String filterName, String... values) {

        String matchTypeCode = StringUtils.substringBefore(filterName, "_");

        try {
            matchType = Enum.valueOf(MatchType.class, matchTypeCode);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("filter名称" + filterName + "没有按规则编写,无法得到属性比较类型.", e);
        }

        String propertyNameStr = StringUtils.substringAfter(filterName, "_");
        Assert.isTrue(StringUtils.isNotBlank(propertyNameStr), "filter名称" + filterName + "没有按规则编写,无法得到属性名称.");
        propertyNames = StringUtils.splitByWholeSeparator(propertyNameStr, PropertyFilter.OR_SEPARATOR);
        try {
            if (propertyNameStr.indexOf("count(") > -1) {
                propertyClass = Integer.class;
            } else if (propertyNameStr.indexOf("(") > -1) {
                propertyClass = BigDecimal.class;
            } else {
                Method method = null;
                String[] namesSplits = StringUtils.split(propertyNames[0], ".");
                if (namesSplits.length == 1) {
                    method = MethodUtils.getAccessibleMethod(entityClass, "get" + StringUtils.capitalize(propertyNames[0]));
                } else {
                    Class<?> retClass = entityClass;
                    for (String nameSplit : namesSplits) {
                        method = MethodUtils.getAccessibleMethod(retClass, "get" + StringUtils.capitalize(nameSplit));
                        retClass = method.getReturnType();
                        if (Collection.class.isAssignableFrom(retClass)) {
                            Type genericReturnType = method.getGenericReturnType();
                            if (genericReturnType instanceof ParameterizedType) {
                                retClass = (Class<?>) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                                subQueryCollectionPropetyType = retClass;
                            }
                        }
                    }
                }
                propertyClass = method.getReturnType();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("无效对象属性定义：" + entityClass + ":" + propertyNames[0], e);
        }

        if (values.length == 1) {
            if (matchType.equals(MatchType.IN) || matchType.equals(MatchType.NI)) {
                String value = values[0];
                values = value.split(",");
            } else if (propertyClass.equals(Date.class) || propertyClass.equals(DateTime.class)) {
                String value = values[0].trim();
                value = value.replace("～", "~");
                if (value.indexOf(" ") > -1) {
                    values = StringUtils.split(value, "~");
                    if (matchType.equals(MatchType.BT)) {
                        values[0] = values[0].trim();
                        values[1] = values[1].trim();
                    } else {
                        values = new String[] { values[0].trim() };
                    }
                }
            }
        }

        if (values.length == 1) {
            this.matchValue = parseMatchValueByClassType(propertyClass, values[0]);
        } else {
            Object[] matchValues = new Object[values.length];
            for (int i = 0; i < values.length; i++) {
                matchValues[i] = parseMatchValueByClassType(propertyClass, values[i]);
            }
            this.matchValue = matchValues;
        }
    }

    private Object parseMatchValueByClassType(Class propertyClass, String value) {
        if ("NULL".equalsIgnoreCase(value)) {
            return value;
        }
        if (Enum.class.isAssignableFrom(propertyClass)) {
            return Enum.valueOf(propertyClass, value);
        } else if (propertyClass.equals(Boolean.class) || matchType.equals(MatchType.NN) || matchType.equals(MatchType.NU)) {
            return new Boolean(BooleanUtils.toBoolean(value));
        } else if (propertyClass.equals(Date.class)) {
            return DateUtils.parseMultiFormatDate((String) value);
        } else {
            return ConvertUtils.convertStringToObject(value, propertyClass);
        }
    }

    /**
     * Java程序层直接构造过滤器对象, 如filters.add(new PropertyFilter(MatchType.EQ, "code",
     * code));
     * 
     * @param matchType
     * @param propertyName
     * @param matchValue
     */
    public PropertyFilter(final MatchType matchType, final String propertyName, final Object matchValue) {
        this.matchType = matchType;
        this.propertyNames = StringUtils.splitByWholeSeparator(propertyName, PropertyFilter.OR_SEPARATOR);
        this.matchValue = matchValue;
    }

    /**
     * Java程序层直接构造过滤器对象, 如filters.add(new PropertyFilter(MatchType.LIKE, new
     * String[]{"code","name"}, value));
     * 
     * @param matchType
     * @param propertyName
     * @param matchValue
     */
    public PropertyFilter(final MatchType matchType, final String[] propertyNames, final Object matchValue) {
        this.matchType = matchType;
        this.propertyNames = propertyNames;
        this.matchValue = matchValue;
    }

    /**
     * 从HttpRequest中创建PropertyFilter列表
     * PropertyFilter命名规则为Filter属性前缀_比较类型属性类型_属性名.
     */
    public static List<PropertyFilter> buildFiltersFromHttpRequest(Class<?> entityClass, ServletRequest request) {

        List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();

        // 从request中获取含属性前缀名的参数,构造去除前缀名后的参数Map.
        Map<String, String[]> filterParamMap = getParametersStartingWith(request, "search['", "']");

        // 分析参数Map,构造PropertyFilter列表
        for (Map.Entry<String, String[]> entry : filterParamMap.entrySet()) {
            String filterName = entry.getKey();
            String[] values = entry.getValue();
            if (values == null || values.length == 0) {
                continue;
            }

            if (values.length == 1) {
                String value = values[0];
                // 如果value值为空,则忽略此filter.
                if (StringUtils.isNotBlank(value)) {
                    PropertyFilter filter = new PropertyFilter(entityClass, filterName, value);
                    filterList.add(filter);
                }
            } else {
                String[] valuesArr = values;
                // 如果value值为空,则忽略此filter.
                if (valuesArr.length > 0) {
                    Set<String> valueSet = new HashSet<String>();
                    for (String value : valuesArr) {
                        if (StringUtils.isNotBlank(value)) {
                            valueSet.add(value);
                        }
                    }
                    if (valueSet.size() > 0) {
                        String[] realValues = new String[valueSet.size()];
                        int cnt = 0;
                        for (String v : valueSet) {
                            realValues[cnt++] = v;
                        }
                        PropertyFilter filter = new PropertyFilter(entityClass, filterName, realValues);
                        filterList.add(filter);
                    }

                }
            }

        }
        return filterList;
    }

    public static Pageable buildPageableFromHttpRequest(HttpServletRequest request) {
        return buildPageableFromHttpRequest(request, null);
    }

    public static Pageable buildPageableFromHttpRequest(HttpServletRequest request, Sort sort) {
        String rows = StringUtils.isBlank(request.getParameter("rows")) ? "20" : request.getParameter("rows");
        if (Integer.valueOf(rows) < 0) {
            return null;
        }
        String page = StringUtils.isBlank(request.getParameter("page")) ? "1" : request.getParameter("page");
        if (sort == null) {
            sort = buildSortFromHttpRequest(request);
        }
        return new PageRequest(Integer.valueOf(page) - 1, Integer.valueOf(rows), sort);
    }

    public static Sort buildSortFromHttpRequest(HttpServletRequest request) {
        String sidx = StringUtils.isBlank(request.getParameter("sidx")) ? "id" : request.getParameter("sidx");
        Direction sord = "desc".equalsIgnoreCase(request.getParameter("sord")) ? Direction.DESC : Direction.ASC;
        Sort sort = null;

        //按照逗号切分支持多属性排序
        for (String sidxItem : sidx.split(",")) {
            if (StringUtils.isNotBlank(sidxItem)) {
                //再按空格切分获取排序属性和排序方向
                String[] sidxItemWithOrder = sidxItem.trim().split(" ");
                String sortname = sidxItemWithOrder[0];
                //如果查询属性包含_OR_则取第一个作为排序属性
                //因此在写OR多属性查询时注意把排序属性写在最前面
                if (sortname.indexOf(OR_SEPARATOR) > -1) {
                    sortname = StringUtils.substringBefore(sortname, OR_SEPARATOR);
                }
                //如果单个属性没有跟随排序方向，则取Grid组件传入的sord参数定义
                if (sidxItemWithOrder.length == 1) {
                    if (sort == null) {
                        //初始化排序对象
                        sort = new Sort(sord, sortname);
                    } else {
                        //and追加多个排序
                        sort = sort.and(new Sort(sord, sortname));
                    }
                } else {
                    //排序属性后面空格跟随排序方向定义
                    String sortorder = sidxItemWithOrder[1];
                    if (sort == null) {
                        sort = new Sort("desc".equalsIgnoreCase(sortorder) ? Direction.DESC : Direction.ASC, sortname);
                    } else {
                        sort = sort.and(new Sort("desc".equalsIgnoreCase(sortorder) ? Direction.DESC : Direction.ASC, sortname));
                    }
                }
            }
        }
        return sort;
    }

    /**
     * 获取比较方式.
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * 获取比较值.
     */
    public Object getMatchValue() {
        return matchValue;
    }

    /**
     * 获取比较属性名称列表.
     */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * 获取唯一的比较属性名称.
     */
    public String getPropertyName() {
        Assert.isTrue(propertyNames.length == 1, "There are not only one property in this filter.");
        return propertyNames[0];
    }

    /**
     * 是否比较多个属性.
     */
    public boolean hasMultiProperties() {
        return (propertyNames.length > 1);
    }

    /**
     * 构造一个缺省过滤集合.
     */
    public static List<PropertyFilter> buildDefaultFilterList() {
        return new ArrayList<PropertyFilter>();
    }

    public Class getPropertyClass() {
        return propertyClass;
    }

    public Class getSubQueryCollectionPropetyType() {
        return subQueryCollectionPropetyType;
    }

    private static Map<String, String[]> getParametersStartingWith(ServletRequest request, String prefix, String suffix) {
        Assert.notNull(request, "Request must not be null");
        @SuppressWarnings("rawtypes")
        Enumeration paramNames = request.getParameterNames();
        Map<String, String[]> params = new TreeMap<String, String[]>();
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (("".equals(prefix) || paramName.startsWith(prefix)) && ("".equals(suffix) || paramName.endsWith(suffix))) {
                String unprefixed = paramName.substring(prefix.length(), paramName.length() - suffix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, new String[] { values[0] });
                }
            }
        }
        return params;
    }
}
