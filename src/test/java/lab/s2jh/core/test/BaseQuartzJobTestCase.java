package lab.s2jh.core.test;

import lab.s2jh.module.schedule.BaseQuartzJobBean;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Spring的支持数据库访问, 事务控制和依赖注入的JUnit4 集成测试基类.
 * 相比Spring原基类名字更短并保存了dataSource变量.
 *   
 * 子类需要定义applicationContext文件的位置, 如:
 * @ContextConfiguration(locations = { "/applicationContext.xml" })
 * 
 */
@ActiveProfiles("test")
@ContextConfiguration(locations = { "classpath:/context/context-profiles.xml", "classpath*:/context/spring*.xml" })
public abstract class BaseQuartzJobTestCase extends AbstractTransactionalJUnit4SpringContextTests {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void execute() {
        try {
            BaseQuartzJobBean jobBean = (BaseQuartzJobBean) getJobClass().newInstance();
            jobBean.setApplicationContext(applicationContext);
            jobBean.executeInternal(null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    abstract public Class<?> getJobClass();
}
