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

import rjc.table.signal.ISignal;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/*************************** Observable table cell position reference ****************************/
/*************************************************************************************************/

public class CellPosition implements ISignal
{
  private int m_columnPos = TableAxis.INVALID;;
  private int m_rowPos    = TableAxis.INVALID;;

  /**************************************** constructor ******************************************/
  public CellPosition()
  {
    // construct
  }

  /**************************************** constructor ******************************************/
  public CellPosition( int columnPos, int rowPos )
  {
    // construct and set position
    m_columnPos = columnPos;
    m_rowPos = rowPos;
  }

  /***************************************** setPosition *****************************************/
  public void setPosition( int columnPos, int rowPos )
  {
    // if position is change, fire listeners
    if ( columnPos != m_columnPos || rowPos != m_rowPos )
    {
      m_columnPos = columnPos;
      m_rowPos = rowPos;
      signal();
    }
  }

  /***************************************** setPosition *****************************************/
  public void setPosition( CellPosition pos )
  {
    // set to position
    setPosition( pos.getColumnPos(), pos.getRowPos() );
  }

  /**************************************** setColumnPos *****************************************/
  public void setColumnPos( int position )
  {
    // if position is change, fire listeners
    if ( position != m_columnPos )
    {
      m_columnPos = position;
      signal();
    }
  }

  /****************************************** setRowPos ******************************************/
  public void setRowPos( int position )
  {
    // if position is change, fire listeners
    if ( position != m_rowPos )
    {
      m_rowPos = position;
      signal();
    }
  }

  /**************************************** getColumnPos *****************************************/
  public int getColumnPos()
  {
    // return column position
    return m_columnPos;
  }

  /****************************************** getRowPos ******************************************/
  public int getRowPos()
  {
    // return row position
    return m_rowPos;
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // return as string
    return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "[" + m_columnPos
        + " " + m_rowPos + "]";
  }

}
