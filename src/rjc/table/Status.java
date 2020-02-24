/**************************************************************************
 *  Copyright (C) 2020 by Richard Crook                                   *
 *  https://github.com/dazzle50/JTableFX                                  *
 *                                                                        *
 *  This program is free software: you can redistribute it and/or modify  *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  This program is distributed in the hope that it will be useful,       *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with this program.  If not, see http://www.gnu.org/licenses/    *
 **************************************************************************/

package rjc.table;

/*************************************************************************************************/
/******************* Generic status with severity and associated text message ********************/
/*************************************************************************************************/

public class Status
{
  private Level  m_severity; // severity of status
  private String m_msg;      // text message for status

  public static enum Level // status types
  {
    NORMAL, WARNING, ERROR, FATAL
  }

  private static final String STYLE_ERROR  = "-fx-text-fill: red;";
  private static final String STYLE_NORMAL = "-fx-text-fill: black;";

  /**************************************** constructor ******************************************/
  public Status()
  {
    // create empty status
    m_severity = Level.NORMAL;
    m_msg = null;
  }

  /**************************************** constructor ******************************************/
  public Status( Level severity, String msg )
  {
    // create specified status
    m_severity = severity;
    m_msg = msg;
  }

  /***************************************** getSeverity *****************************************/
  public Level getSeverity()
  {
    // return status severity leve;
    return m_severity;
  }

  /***************************************** getMessage ******************************************/
  public String getMessage()
  {
    // return status text message
    return m_msg;
  }

  /****************************************** getStyle *******************************************/
  public String getStyle()
  {
    // return suitable style css for current severity
    if ( m_severity == Level.ERROR || m_severity == Level.FATAL )
      return Status.STYLE_ERROR;

    return Status.STYLE_NORMAL;
  }

  /******************************************* equals ********************************************/
  @Override
  public boolean equals( Object other )
  {
    // return true if other object represents same status
    if ( other != null && other instanceof Status )
    {
      Status status = (Status) other;
      return m_severity == status.m_severity && Utils.equal( m_msg, status.m_msg );
    }

    return false;
  }

  /****************************************** hashCode ******************************************/
  @Override
  public int hashCode()
  {
    // status hash code combines severity and message
    int hash = m_severity == null ? 0 : m_severity.hashCode();
    hash += m_msg == null ? 0 : 17 * m_msg.hashCode();
    return hash;
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // return as string
    return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "[" + m_severity
        + " '" + m_msg + "']";
  }
}
