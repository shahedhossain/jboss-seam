//$Id$
package org.jboss.seam.microcontainer;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.jboss.resource.connectionmanager.TransactionSynchronizer;
import org.jboss.tm.TxManager;
import org.jboss.tm.usertx.client.ServerVMClientUserTransaction;
import org.jboss.util.naming.NonSerializableFactory;
import org.jboss.util.naming.Util;

/**
 * A factory that bootstraps a JTA TransactionManager
 * 
 * @author Gavin King
 */
public class TransactionManagerFactory
{
   
   private static final Logger log = Logger.getLogger(TransactionManagerFactory.class);

   private Hashtable initialContextProperties;
   
   public TransactionManager getTransactionManager() throws Exception
   {
      
      log.info("starting JTA transaction manager");
      InitialContext initialContext = (initialContextProperties != null) ? new InitialContext(initialContextProperties) : new InitialContext();

      //create a TransactionManager and bind to JNDI
      TransactionManager transactionManager = TxManager.getInstance();
      TransactionSynchronizer.setTransactionManager(transactionManager);
      NonSerializableFactory.rebind( initialContext, "java:/TransactionManager", transactionManager );
      
      //create a UserTransaction and bind to JNDI
      ServerVMClientUserTransaction ut = new ServerVMClientUserTransaction(transactionManager);
      Util.createSubcontext(initialContext, "java:/comp");
      NonSerializableFactory.rebind( initialContext, "java:comp/UserTransaction", ut );
      
      return transactionManager;

   }

   public void setInitialContextProperties(Hashtable initialContextProperties)
   {
      this.initialContextProperties = initialContextProperties;
   }

}
