/**************************************************************************
 *  Copyright (C) 2021 by Richard Crook                                   *
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

package rjc.table.view.cell;

import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/****************************** Observable table-view cell position ******************************/
/*************************************************************************************************/

public class ViewPosition extends CellPosition
{
  private TableAxis m_columnsAxis; // associated view columns axis
  private TableAxis m_rowsAxis;    // associated view rows axis

  /**************************************** constructor ******************************************/
  public ViewPosition( TableView view )
  {
    // construct
    super();
    m_columnsAxis = view.getColumnsAxis();
    m_rowsAxis = view.getRowsAxis();
  }

  /***************************************** moveRight *******************************************/
  public void moveRight()
  {
    // move position right one visible column
    int newPos = m_columnsAxis.getNext( getColumnPos() );
    if ( m_rowsAxis.isVisible( getRowPos() ) )
      setColumnPos( newPos );
    else
      setPosition( newPos, m_rowsAxis.getNext( getRowPos() ) );
  }

  /*************************************** moveRightEdge *****************************************/
  public void moveRightEdge()
  {
    // move position to right-most visible column
    int newPos = m_columnsAxis.getLast();
    if ( m_rowsAxis.isVisible( getRowPos() ) )
      setColumnPos( newPos );
    else
      setPosition( newPos, m_rowsAxis.getNext( getRowPos() ) );
  }

  /***************************************** moveLeft ********************************************/
  public void moveLeft()
  {
    // move position left one visible column
    int newPos = m_columnsAxis.getPrevious( getColumnPos() );
    if ( m_rowsAxis.isVisible( getRowPos() ) )
      setColumnPos( newPos );
    else
      setPosition( newPos, m_rowsAxis.getNext( getRowPos() ) );
  }

  /**************************************** moveLeftEdge *****************************************/
  public void moveLeftEdge()
  {
    // move position to left-most visible column
    int newPos = m_columnsAxis.getFirst();
    if ( m_rowsAxis.isVisible( getRowPos() ) )
      setColumnPos( newPos );
    else
      setPosition( newPos, m_rowsAxis.getNext( getRowPos() ) );
  }

  /******************************************* moveUp ********************************************/
  public void moveUp()
  {
    // move position up one visible row
    int newPos = m_rowsAxis.getPrevious( getRowPos() );
    if ( m_columnsAxis.isVisible( getColumnPos() ) )
      setRowPos( newPos );
    else
      setPosition( m_columnsAxis.getNext( getColumnPos() ), newPos );
  }

  /****************************************** moveTop ********************************************/
  public void moveTop()
  {
    // move position to top-most visible row
    int newPos = m_rowsAxis.getFirst();
    if ( m_columnsAxis.isVisible( getColumnPos() ) )
      setRowPos( newPos );
    else
      setPosition( m_columnsAxis.getNext( getColumnPos() ), newPos );
  }

  /****************************************** moveDown *******************************************/
  public void moveDown()
  {
    // move position down one visible row
    int newPos = m_rowsAxis.getNext( getRowPos() );
    if ( m_columnsAxis.isVisible( getColumnPos() ) )
      setRowPos( newPos );
    else
      setPosition( m_columnsAxis.getNext( getColumnPos() ), newPos );
  }

  /**************************************** moveBottom *******************************************/
  public void moveBottom()
  {
    // move position to bottom visible row
    int newPos = m_rowsAxis.getLast();
    if ( m_columnsAxis.isVisible( getColumnPos() ) )
      setRowPos( newPos );
    else
      setPosition( m_columnsAxis.getNext( getColumnPos() ), newPos );
  }

  /****************************************** isVisible ******************************************/
  public boolean isVisible()
  {
    // return true if position is visible
    return m_columnsAxis.isVisible( getColumnPos() ) && m_rowsAxis.isVisible( getRowPos() );
  }

}
