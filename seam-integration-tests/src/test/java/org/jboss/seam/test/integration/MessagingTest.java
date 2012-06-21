package org.jboss.seam.test.integration;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MessagingTest
    extends JUnitSeamTest
{
	@Deployment(name="MessagingTest")
    @OverProtocol("Servlet 3.0") 
    public static Archive<?> createDeployment()
    {
        return Deployments.defaultSeamDeployment();
    }
	
    @Test
    public void publishToTopic()
        throws Exception
    {
        final SimpleReference<String> messageText = new SimpleReference<String>();
        
        new FacesRequest() {
            @Override
            protected void invokeApplication()
                throws Exception 
            {
                Contexts.getApplicationContext().set("testMessage", messageText);
                invokeAction("#{testTopic.publish}");
            }
        }.run();      

        // need to delay a bit to make sure the message is delivered
        // might need 
        Thread.sleep(2000);
        
        assert messageText.getValue().equals("message for topic");
    }
    
    @Test
    public void sendToQueue()
        throws Exception
    {
        final SimpleReference<String> messageText = new SimpleReference<String>();
        
        new FacesRequest() {
            @Override
            protected void invokeApplication()
                throws Exception 
            {
                Contexts.getApplicationContext().set("testMessage", messageText);
                invokeAction("#{testQueue.send}");
            }
        }.run();      

        // need to delay a bit to make sure the message is delivered
        // might need 
        Thread.sleep(2000);
        
        assert messageText.getValue().equals("message for queue");
    }


    @Name("testTopic")
    public static class TopicBean {
        @In 
        private TopicPublisher testPublisher; 
        
        @In 
        private TopicSession topicSession; 
        
        public void publish() 
            throws JMSException 
        { 
            testPublisher.publish(topicSession.createTextMessage("message for topic")); 
        } 
    }
    
    @Name("testQueue")
    public static class QueueBean {
        @In 
        private QueueSender testSender;
        
        @In 
        private QueueSession queueSession;
        
        public void send() throws JMSException { 
            testSender.send(queueSession.createTextMessage("message for queue")); 
        } 
    }
    
    @MessageDriven(activationConfig={
        @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Topic"),
        @ActivationConfigProperty(propertyName="destination",     propertyValue="topic/seamTest")
    })
    @Name("testTopicListener")
    static public class TestTopicListener 
        implements MessageListener
    {
        @In
        private SimpleReference<String> testMessage;

        public void onMessage(Message msg)
        {
            try {
                testMessage.setValue(((TextMessage) msg).getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @MessageDriven(activationConfig={
        @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
        @ActivationConfigProperty(propertyName="destination",     propertyValue="queue/seamTest")
    })
    @Name("testQueueListener")
    static public class TestQueueListener 
        implements MessageListener
    {
        @In
        private SimpleReference<String> testMessage;

        public void onMessage(Message msg)
        {
            try {
                testMessage.setValue(((TextMessage) msg).getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    static class SimpleReference<T> {
        T value;
        public SimpleReference() {            
        }
        public SimpleReference(T value) {
            setValue(value);
        }
        public T getValue() { 
            return value; 
        }
        public void setValue(T value) {
            this.value = value;
        }
    }
}
