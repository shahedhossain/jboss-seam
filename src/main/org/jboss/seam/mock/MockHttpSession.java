/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.jboss.seam.Session;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class MockHttpSession extends Session implements HttpSession
{
   
   private Map<String, Object> attributes = new HashMap<String, Object>();
   private boolean isInvalid;
   private ExternalContext externalContext;
   
   public MockHttpSession(ExternalContext externalContext)
   {
      this.externalContext = externalContext;
   }
   
   public boolean isInvalid()
   {
      return isInvalid;
   }

   public long getCreationTime()
   {
      //TODO
      return 0;
   }

   public String getId()
   {
      //TODO
      return null;
   }

   public long getLastAccessedTime()
   {
      //TODO
      return 0;
   }

   public void setMaxInactiveInterval(int arg0)
   {
      //TODO

   }

   public int getMaxInactiveInterval()
   {
      //TODO
      return 0;
   }
   
   @SuppressWarnings("deprecation")
   public HttpSessionContext getSessionContext()
   {
      //TODO
      return null;
   }

   public Object getAttribute(String att)
   {
      return attributes.get(att);
   }

   public Object getValue(String att)
   {
      return getAttribute(att);
   }

   public Enumeration getAttributeNames()
   {
      return new IteratorEnumeration( attributes.keySet().iterator() );
   }

   public String[] getValueNames()
   {
      return attributes.keySet().toArray( new String[0] );
   }

   public void setAttribute(String att, Object value)
   {
      attributes.put(att, value);
   }

   public void putValue(String att, Object value)
   {
      setAttribute(att, value);
   }

   public void removeAttribute(String att)
   {
      attributes.remove(att);
   }

   public void removeValue(String att)
   {
      removeAttribute(att);
   }

   public void invalidate()
   {
      attributes.clear();
      isInvalid = true;
   }

   public boolean isNew()
   {
      return false;
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
   }

   public ServletContext getServletContext()
   {
      // FIXME getServletContext
      return null;
   }

   @Override
   public ExternalContext getExternalContext()
   {
      return externalContext;
   }

   public void setExternalContext(ExternalContext externalContext)
   {
      this.externalContext = externalContext;
   }

}
