package lab.s2jh.core.test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

import javax.persistence.Column;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模拟实体对象实例构造帮助类
 */
public class MockEntityUtils {

    private final static Logger logger = LoggerFactory.getLogger(MockEntityUtils.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <X> X buildMockObject(Class<X> clazz) {
        X x = null;
        try {
            x = clazz.newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                String mn = method.getName();
                if (mn.startsWith("set")) {
                    Class[] parameters = method.getParameterTypes();
                    if (parameters.length == 1) {
                        Method getMethod = MethodUtils.getAccessibleMethod(clazz, "get" + mn.substring(3), null);
                        if (getMethod != null) {
                            if (getMethod.getName().equals("getId")) {
                                continue;
                            }
                            Object value = null;
                            Class parameter = parameters[0];
                            if (parameter.isAssignableFrom(String.class)) {
                                Column column = getMethod.getAnnotation(Column.class);
                                int columnLength = 10;
                                if (column != null && column.length() < columnLength) {
                                    columnLength = column.length();
                                }
                                Size size = getMethod.getAnnotation(Size.class);
                                if (size != null && size.max() < columnLength) {
                                    columnLength = size.max();
                                }
                                value = RandomStringUtils.randomAlphabetic(columnLength);
                            } else if (parameter.isAssignableFrom(Date.class)) {
                                value = new Date();
                            } else if (parameter.isAssignableFrom(BigDecimal.class)) {
                                value = new BigDecimal(10 + new Random().nextDouble() * 1000);
                            } else if (parameter.isAssignableFrom(Integer.class)) {
                                value = new Random().nextInt();
                            } else if (parameter.isAssignableFrom(Boolean.class)) {
                                value = new Random().nextBoolean();
                            } else if (parameter.isEnum()) {
                                Method m = parameter.getDeclaredMethod("values", null);
                                Object[] result = (Object[]) m.invoke(parameter.getEnumConstants()[0], null);
                                value = result[new Random().nextInt(result.length)];
                            }
                            if (value != null) {
                                MethodUtils.invokeMethod(x, mn, value);
                                logger.trace("{}={}", method.getName(), value);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return x;
    }

    public static class TestVO {
        private String str;
        private Date dt;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public Date getDt() {
            return dt;
        }

        public void setDt(Date dt) {
            this.dt = dt;
        }
    }

    public static void main(String[] args) {
        TestVO testVO = MockEntityUtils.buildMockObject(TestVO.class);
        System.out
                .println("Mock Entity: " + ReflectionToStringBuilder.toString(testVO, ToStringStyle.MULTI_LINE_STYLE));
    }
}
