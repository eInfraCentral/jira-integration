package eu.catris.configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.web.client.RestTemplate;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class Config {

    private static final Logger logger = LogManager.getLogger(Config.class);

    @Value("${jira.user.email:}")
    private String jiraUser;

    @Value("${jira.user.token:}")
    private String jiraToken;

    @Value("${jms.host}")
    private String jmsHost;

    @Value("${jms.prefix:registry}")
    private String jmsPrefix;

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        if (!jiraUser.equals("") && !jiraToken.equals("")) {
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(jiraUser, jiraToken));
        }
        return restTemplate;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(jmsHost);
        connectionFactory.setConnectionIDPrefix(jmsPrefix);
        logger.info("ActiveMQConnection Factory created for {}", jmsHost);
        return connectionFactory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory
                = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory());
        factory.setPubSubDomain(true); // false is for queue
        factory.setMessageConverter(jacksonJmsMessageConverter());
        return factory;
    }
}
