package edu.rice.rubis.beans;

import java.rmi.RemoteException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;
import java.io.Serializable;
import javax.transaction.UserTransaction;

/**
 * This is a stateless session bean used when a user buy an item.
 *  
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.1
 */

public class SB_StoreBuyNowBean implements SessionBean 
{
  protected SessionContext sessionContext;
  protected Context initialContext = null;

  /**
   * Create a buyNow and update the item.
   *
   * @param itemId id of the item related to the comment
   * @param userId id of the buyer
   * @param qty quantity of items
   * @since 1.1
   */
  public void createBuyNow(Integer itemId, Integer userId, int qty) throws RemoteException
  {
    // Try to find the Item corresponding to the Item ID
    ItemLocal item;
    try 
    {
      ItemLocalHome itemHome = (ItemLocalHome)initialContext.lookup("java:comp/env/ejb/Item");
      item = itemHome.findByPrimaryKey(itemId);
      item.setQuantity(item.getQuantity() - qty);
      if (item.getQuantity() == 0)
        item.setEndDate(TimeManagement.currentDateToString());
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot update Item: " +e+"<br>");
    }
    // Try to find the User corresponding to the User ID
    UserLocal user;
    try 
    {
      UserLocalHome userHome = (UserLocalHome)initialContext.lookup("java:comp/env/ejb/User");
      user = userHome.findByPrimaryKey(userId);
    } 
    catch (Exception e)
    {
      throw new RemoteException("Cannot find User: " +e+"<br>");
    }
    try
    {
      BuyNowLocalHome bHome = (BuyNowLocalHome)initialContext.lookup("java:comp/env/ejb/BuyNow");
      BuyNowLocal b = bHome.create(user, item, qty);   
    }
    catch (Exception e)
    {
      throw new RemoteException("Error while storing the buyNow (got exception: " +e+")<br>");
    }
		
  }



  // ======================== EJB related methods ============================

  /**
   * This method is empty for a stateless session bean
   */
  public void ejbCreate() throws CreateException, RemoteException
  {
  }

  /** This method is empty for a stateless session bean */
  public void ejbActivate() throws RemoteException {}
  /** This method is empty for a stateless session bean */
  public void ejbPassivate() throws RemoteException {}
  /** This method is empty for a stateless session bean */
  public void ejbRemove() throws RemoteException {}


  /** 
   * Sets the associated session context. The container calls this method 
   * after the instance creation. This method is called with no transaction context. 
   * We also retrieve the Home interfaces of all RUBiS's beans.
   *
   * @param sessionContext - A SessionContext interface for the instance. 
   * @exception RemoteException - Thrown if the instance could not perform the function 
   *            requested by the container because of a system-level error. 
   */
  public void setSessionContext(SessionContext sessionContext) throws RemoteException
  {
    this.sessionContext = sessionContext;
    
    try
    {
      initialContext = new InitialContext(); 
    }
    catch (Exception e) 
    {
      throw new RemoteException("Cannot get JNDI InitialContext");
    }
  }

}
