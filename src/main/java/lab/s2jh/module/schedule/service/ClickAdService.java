package lab.s2jh.module.schedule.service;

import lab.s2jh.module.ad.entity.Advertising;
import lab.s2jh.module.ad.entity.ProxyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ClickAdService {

    private final Logger logger = LoggerFactory.getLogger(ClickAdService.class);

    private boolean stopInjectFlag = false;

    private ThreadPoolTaskExecutor clickTaskExecutor;

    private AsyncClickAdService asyncClickAdService;


    public void setClickTaskExecutor(ThreadPoolTaskExecutor clickTaskExecutor) {
        this.clickTaskExecutor = clickTaskExecutor;
    }

    public void setAsyncClickAdService(AsyncClickAdService asyncClickAdService) {
        this.asyncClickAdService = asyncClickAdService;
    }

    public ClickAdService injectProxyInfo(ProxyInfo proxyInfo,Advertising advertising) {
        if(stopInjectFlag){
            logger.debug("Proxy inject rejected as user request.");
            return this;
        }
            logger.info("Injected proxy: {} advertising: {}", proxyInfo,advertising);
            logger.info("CheckTaskExecutor ActiveCount/PoolSize/MaxPoolSize: {}/{}/{}",
                    clickTaskExecutor.getActiveCount(), clickTaskExecutor.getPoolSize(),
                    clickTaskExecutor.getMaxPoolSize());
            asyncClickAdService.startClickAdAsync(proxyInfo,advertising);
        return this;
    }

    public void startClickAdAsync(){
        stopInjectFlag = false;
    }

    public void startClickAdSync(){
        stopInjectFlag = false;
        try {
            boolean tobeWait = true;
            while (tobeWait) {
                Thread.sleep(10000);
                logger.info("Main sync thread [{}] is waiting to complete...", Thread.currentThread().getId());
                logger.info("CheckTaskExecutor ActiveCount/PoolSize/MaxPoolSize: {}/{}/{}",
                        clickTaskExecutor.getActiveCount(), clickTaskExecutor.getPoolSize(),
                        clickTaskExecutor.getMaxPoolSize());
                if (clickTaskExecutor.getPoolSize() > 0 && clickTaskExecutor.getActiveCount() == 0) {
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
