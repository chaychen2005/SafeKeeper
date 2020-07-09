package org.fisco.bcos.safekeeper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Slf4j
@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ConfigurableApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            context = (ConfigurableApplicationContext)applicationContext;
            DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
            dataSource.getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
            context.close();
        }
    }
}
