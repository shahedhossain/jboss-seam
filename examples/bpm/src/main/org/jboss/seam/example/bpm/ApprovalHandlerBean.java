// $Id$
package org.jboss.seam.example.bpm;

import javax.ejb.Stateless;
import javax.ejb.Interceptor;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;

/**
 * Implementation of ApprovalHandlerBean.
 *
 * @author Steve Ebersole
 */
@Stateless
@Name( "approvalHandler" )
@Interceptor( SeamInterceptor.class )
public class ApprovalHandlerBean implements ApprovalHandler
{
   @PersistenceContext
   private EntityManager manager;

   @In("username")
   private String username; // a BusinessProcessContext scoped attribute

   @Begin
   @StartTask( taskIdName = "taskId", taskName = "task" )
   public String beginApproval()
   {
      return "assigned";
   }

   @End
   @CompleteTask( name = "task", transitionMap = { "approved=>approve" } )
   public String approve()
   {
      User user = findUser();
//      user.setState( State.APPROVED );
      manager.merge( user );
      return "approved";
   }

   @End
   @CompleteTask( name = "task", transitionMap = { "denied=>deny" } )
   public String deny()
   {
      User user = findUser();
//      user.setState( State.DENIED );
      manager.merge( user );
      return "denied";
   }

   private User findUser()
   {
      return ( User ) manager
              .createQuery( "from User as u where u.username = :username" )
              .setParameter( "username", username )
              .getSingleResult();
   }
}
