package org.fisco.bcos.safekeeper;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ConfigurableApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            context = (ConfigurableApplicationContext) applicationContext;
            DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
            dataSource.getConnection().close();
        } catch (Exception e) {
            log.debug("connect mysql error, {}", e.getMessage());
            context.close();
        }
    }
}
