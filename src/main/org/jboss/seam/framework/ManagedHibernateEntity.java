package org.jboss.seam.framework;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.hibernate.Session;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.Reflections;

/**
 * Manager component for a Hibernate entity instance. Allows
 * auto-fetching of contextual entities. The identifier
 * is determined by evaluating an EL expression and then
 * using JSF type conversion if necessary.
 * 
 * @author Gavin King
 *
 */
public class ManagedHibernateEntity
{
   private Session session;
   private Serializable id;
   private String entityClassName;
   private Class entityClass;
   private String idClass;
   private Object instance;
   private String idConverterId;
   private Converter idConverter;
   
   public Session getSession()
   {
      return session;
   }

   public void setSession(Session session)
   {
      this.session = session;
   }

   public Serializable getId()
   {
      return id;
   }

   public void setId(Serializable id)
   {
      this.id = id;
   }
   
   public String getEntityClass()
   {
      return entityClassName;
   }

   public void setEntityClass(String entityClass)
   {
      this.entityClassName = entityClass;
   }

   @Create
   public void initEntityClass() throws Exception
   {
      entityClass = Reflections.classForName(entityClassName);
   }
   
   @Unwrap @Transactional
   public Object getInstance() throws Exception
   {
      if ( id==null || "".equals(id) )
      {
         if (instance==null)
         {
            instance = createInstance();
         }
      }
      else
      {
         if (instance==null)
         {
            //we cache the instance so that it does not "disappear"
            //after remove() is called on the instance
            //is this really a Good Idea??
            instance = loadInstance( getConvertedId() );
         }
      }
      return instance;
   }
   
   protected Object createInstance() throws Exception
   {
      return entityClass.newInstance();
   }

   protected Object loadInstance(Serializable id)
   {
      return getSession().get(entityClass, id);
   }
   
   ////////////TODO: copy/paste from ManagedEntity ///////////////////

   private Serializable getConvertedId() throws Exception
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (idConverter==null)
      {
         if (idConverterId==null)
         {
            //TODO: guess the id class using @Id
            idConverter = facesContext.getApplication().createConverter( Reflections.classForName(idClass) );
         }
         else
         {
            idConverter = facesContext.getApplication().createConverter(idConverterId); //cache the lookup
         }
      }
      
      if (idConverter==null)
      {
         return id;
      }
      else
      {
         return (Serializable) idConverter.getAsObject( 
               facesContext, 
               facesContext.getViewRoot(), 
               (String) id 
            );
      }
   }

   public String getIdConverterId()
   {
      return idConverterId;
   }

   public void setIdConverterId(String converterId)
   {
      this.idConverterId = converterId;
   }

   public Converter getIdConverter()
   {
      return idConverter;
   }

   public void setIdConverter(Converter converter)
   {
      this.idConverter = converter;
   }

   public String getIdClass()
   {
      return idClass;
   }

   public void setIdClass(String idClass)
   {
      this.idClass = idClass;
   }

}
