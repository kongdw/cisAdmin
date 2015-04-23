package lab.s2jh.module.schedule.service;

import lab.s2jh.module.ad.entity.ProxyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ProxyCheckService {

    private final Logger logger = LoggerFactory.getLogger(ProxyCheckService.class);

    private boolean stopInjectFlag = false;

    private ThreadPoolTaskExecutor checkTaskExecutor;

    private AsyncProxyCheckService asyncProxyCheckService;


    public void setCheckTaskExecutor(ThreadPoolTaskExecutor checkTaskExecutor) {
        this.checkTaskExecutor = checkTaskExecutor;
    }

    public void setAsyncProxyCheckService(AsyncProxyCheckService asyncProxyCheckService) {
        this.asyncProxyCheckService = asyncProxyCheckService;
    }

    public ProxyCheckService injectProxyInfo(ProxyInfo... proxyinfos) {
        if(stopInjectFlag){
            logger.debug("Proxy inject rejected as user request.");
            return this;
        }
        for (ProxyInfo proxyInfo : proxyinfos) {
            logger.info("Injected : {}", proxyInfo);
            logger.info("CheckTaskExecutor ActiveCount/PoolSize/MaxPoolSize: {}/{}/{}",
                    checkTaskExecutor.getActiveCount(), checkTaskExecutor.getPoolSize(),
                    checkTaskExecutor.getMaxPoolSize());
            asyncProxyCheckService.startAsyncCheck(proxyInfo);
        }
        return this;
    }

    public void startCheckAsync(){
        stopInjectFlag = false;
    }

    public void startCheckSync(){
        stopInjectFlag = false;
        try {
            boolean tobeWait = true;
            while (tobeWait) {
                Thread.sleep(10000);
                logger.info("Main sync thread [{}] is waiting to complete...", Thread.currentThread().getId());
                logger.info("CheckTaskExecutor ActiveCount/PoolSize/MaxPoolSize: {}/{}/{}",
                        checkTaskExecutor.getActiveCount(), checkTaskExecutor.getPoolSize(),
                        checkTaskExecutor.getMaxPoolSize());
                if (checkTaskExecutor.getPoolSize() > 0 && checkTaskExecutor.getActiveCount() == 0) {
                    tobeWait = false;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void stopInject() {
        logger.debug("Stop check proxy signal received.");
        stopInjectFlag = true;
    }
}
