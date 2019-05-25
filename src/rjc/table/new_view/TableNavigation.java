/**************************************************************************
 *  Copyright (C) 2019 by Richard Crook                                   *
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

package rjc.table.new_view;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

/*************************************************************************************************/
/**************************** Table focus & select cell functionality ****************************/
/*************************************************************************************************/

public class TableNavigation extends TableDisplay
{
  // observable integers for focus, select and mouse cell positions
  final private ReadOnlyIntegerWrapper m_focusColumnPos  = new ReadOnlyIntegerWrapper( INVALID );
  final private ReadOnlyIntegerWrapper m_focusRowPos     = new ReadOnlyIntegerWrapper( INVALID );
  final private ReadOnlyIntegerWrapper m_selectColumnPos = new ReadOnlyIntegerWrapper( INVALID );
  final private ReadOnlyIntegerWrapper m_selectRowPos    = new ReadOnlyIntegerWrapper( INVALID );
  final private ReadOnlyIntegerWrapper m_mouseColumnPos  = new ReadOnlyIntegerWrapper( INVALID );
  final private ReadOnlyIntegerWrapper m_mouseRowPos     = new ReadOnlyIntegerWrapper( INVALID );

  /*********************************** getFocusColumnProperty ************************************/
  public ReadOnlyIntegerProperty getFocusColumnProperty()
  {
    // return focus cell column position property
    return m_focusColumnPos.getReadOnlyProperty();
  }

  /********************************** getSelectColumnProperty ************************************/
  public ReadOnlyIntegerProperty getSelectColumnProperty()
  {
    // return select cell column position property
    return m_selectColumnPos.getReadOnlyProperty();
  }

  /*********************************** getMouseColumnProperty ************************************/
  public ReadOnlyIntegerProperty getMouseColumnProperty()
  {
    // return mouse cell column position property
    return m_mouseColumnPos.getReadOnlyProperty();
  }

  /************************************ getFocusRowProperty **************************************/
  public ReadOnlyIntegerProperty getFocusRowProperty()
  {
    // return focus cell row position property
    return m_focusRowPos.getReadOnlyProperty();
  }

  /************************************ getSelectRowProperty *************************************/
  public ReadOnlyIntegerProperty getSelectRowProperty()
  {
    // return select cell row position property
    return m_selectRowPos.getReadOnlyProperty();
  }

  /************************************ getMouseRowProperty **************************************/
  public ReadOnlyIntegerProperty getMouseRowProperty()
  {
    // return mouse cell row position property
    return m_mouseRowPos.getReadOnlyProperty();
  }

  /************************************** getFocusColumnPos **************************************/
  public int getFocusColumnPos()
  {
    // return focus cell column position
    return m_focusColumnPos.get();
  }

  /************************************* getSelectColumnPos **************************************/
  public int getSelectColumnPos()
  {
    // return select cell column position
    return m_selectColumnPos.get();
  }

  /************************************** getMouseColumnPos **************************************/
  public int getMouseColumnPos()
  {
    // return mouse cell column position
    return m_mouseColumnPos.get();
  }

  /*************************************** getFocusRowPos ****************************************/
  public int getFocusRowPos()
  {
    // return focus cell row position
    return m_focusRowPos.get();
  }

  /*************************************** getSelectRowPos ***************************************/
  public int getSelectRowPos()
  {
    // return select cell row position
    return m_selectRowPos.get();
  }

  /*************************************** getMouseRowPos ****************************************/
  public int getMouseRowPos()
  {
    // return mouse cell row position
    return m_mouseRowPos.get();
  }

  /************************************** setFocusColumnPos **************************************/
  public void setFocusColumnPos( int position )
  {
    // set focus cell column position
    m_focusColumnPos.set( position );
  }

  /*************************************** setFocusRowPos ****************************************/
  public void setFocusRowPos( int position )
  {
    // set focus cell row position
    m_focusRowPos.set( position );
  }

  /************************************** setSelectColumnPos *************************************/
  public void setSelectColumnPos( int position )
  {
    // set select cell column position
    m_selectColumnPos.set( position );
  }

  /*************************************** setSelectRowPos ***************************************/
  public void setSelectRowPos( int position )
  {
    // set select cell row position
    m_selectRowPos.set( position );
  }

  /*************************************** setMouseColumnPos *************************************/
  public void checkMouseColumnPos( int x )
  {
    // set mouse cell column position from x coordinate
    // TODO
  }

  /**************************************** setMouseRowPos ***************************************/
  public void checkMouseRowPos( int y )
  {
    // set mouse cell row position from y coordinate
    // TODO
  }

}
