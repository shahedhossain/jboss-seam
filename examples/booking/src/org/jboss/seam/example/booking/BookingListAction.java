//$Id$
package org.jboss.seam.example.booking;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.core.FacesMessages;

@Stateful
@Scope(SESSION)
@Name("bookingList")
@LoggedIn
@TransactionAttribute(REQUIRES_NEW)
public class BookingListAction implements BookingList, Serializable
{
   
   @PersistenceContext
   private EntityManager em;
   
   @In
   private User user;
   
   @DataModel
   private List<Booking> bookings;
   @DataModelSelection
   private Booking booking;
   
   @Factory("bookings")
   public void find()
   {
      bookings = em.createQuery("from Booking b where b.user.username = :username order by b.checkinDate")
            .setParameter("username", user.getUsername())
            .getResultList();
   }
   
   public String cancel()
   {
      Booking cancelled = em.find(Booking.class, booking.getId());
      if (cancelled!=null) em.remove( cancelled );
      refresh();
      FacesMessages.instance().add("Booking cancelled for confirmation number #{bookings.rowData.id}");
      return "main";
   }
   
   public void refresh()
   {
      booking = null;
      find();
   }
   
   @Destroy @Remove
   public void destroy() {}
   
}
