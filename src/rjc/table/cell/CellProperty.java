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

package rjc.table.cell;

import javafx.beans.property.SimpleObjectProperty;
import rjc.table.cell.CellProperty.CellPosition;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/*************************** Observable table cell position reference ****************************/
/*************************************************************************************************/

public class CellProperty extends SimpleObjectProperty<CellPosition>
{
  public class CellPosition
  {
    protected int m_columnPos = TableAxis.INVALID;;
    protected int m_rowPos    = TableAxis.INVALID;;

    public int getColumnPos()
    {
      return m_columnPos;
    }

    public int getRowPos()
    {
      return m_rowPos;
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "["
          + m_columnPos + " " + m_rowPos + "]";
    }

  }

  private CellPosition m_oldPosition; // old cell position to avoid creating new when setting position

  /**************************************** constructor ******************************************/
  public CellProperty()
  {
    // call super and setup old and current default positions
    super();
    m_oldPosition = new CellPosition();
    set( new CellPosition() );
  }

  /**************************************** constructor ******************************************/
  public CellProperty( int columnPos, int rowPos )
  {
    // call super and set position
    super();
    setPosition( columnPos, rowPos );
  }

  /***************************************** setPosition *****************************************/
  public void setPosition( int columnPos, int rowPos )
  {
    // if position is change, use oldPosition instead of creating new
    if ( get().m_columnPos != columnPos || get().m_rowPos != rowPos )
    {
      CellPosition temp = get();
      m_oldPosition.m_columnPos = columnPos;
      m_oldPosition.m_rowPos = rowPos;
      set( m_oldPosition );
      m_oldPosition = temp;
    }
  }

  /**************************************** setColumnPos *****************************************/
  public void setColumnPos( int position )
  {
    // set column position
    setPosition( position, getRowPos() );
  }

  /****************************************** setRowPos ******************************************/
  public void setRowPos( int position )
  {
    // set row position
    setPosition( getColumnPos(), position );
  }

  /**************************************** getColumnPos *****************************************/
  public int getColumnPos()
  {
    // return column position
    return get().m_columnPos;
  }

  /****************************************** getRowPos ******************************************/
  public int getRowPos()
  {
    // return row position
    return get().m_rowPos;
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // return as string
    return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "["
        + getColumnPos() + " " + getRowPos() + "]";
  }

}
