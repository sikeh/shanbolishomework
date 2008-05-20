/*
 *                             Sun Public License
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License").  You may not use this file except in compliance with
 * the License.  A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the SLAMD Distributed Load Generation Engine.
 * The Initial Developer of the Original Code is Neil A. Wilson.
 * Portions created by Neil A. Wilson are Copyright (C) 2004-2005.
 * Some preexisting portions Copyright (C) 2002-2005 Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):  Neil A. Wilson
 */
package com.sun.snoop;



/**
 * This class defines an exception that may be thrown if a problem occurs while
 * attempting to decode snoop data.
 *
 *
 * @author   Neil A. Wilson
 */
public class SnoopException
       extends Exception
{
  // The parent exception that triggered this snoop exception.
  Exception parentException;



  /**
   * Creates a new snoop exception with the provided message.
   *
   * @param  message  The message explaining the reason for this exception.
   */
  public SnoopException(String message)
  {
    super(message);

    this.parentException = null;
  }



  /**
   * Creates a new snoop exception with the provided message.
   *
   * @param  message          The message explaining the reason for this
   *                          exception.
   * @param  parentException  The parent exception that triggered this snoop
   *                          exception.
   */
  public SnoopException(String message, Exception parentException)
  {
    super(message);

    this.parentException = parentException;
  }



  /**
   * Retrieves the parent exception that triggered this snoop exception.
   *
   * @return  The parent exception that triggered this snoop exception, or
   *          <CODE>null</CODE> if no parent exception is available.
   */
  public Exception getParentException()
  {
    return parentException;
  }
}
