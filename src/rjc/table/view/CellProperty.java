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

package rjc.table.view;

import javafx.beans.property.SimpleObjectProperty;
import rjc.table.view.CellProperty.CellPosition;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/*************************** Observable table cell position reference ****************************/
/*************************************************************************************************/

public class CellProperty extends SimpleObjectProperty<CellPosition>
{
  public class CellPosition
  {
    protected int columnPos;
    protected int rowPos;

    public int getColumnPos()
    {
      return columnPos;
    }

    public int getRowPos()
    {
      return rowPos;
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "[" + columnPos
          + " " + rowPos + "]";
    }

  }

  private CellPosition m_oldPosition; // old cell position for when ??????

  /**************************************** constructor ******************************************/
  public CellProperty()
  {
    // call super
    super();

    // setup old position
    m_oldPosition = new CellPosition();
    m_oldPosition.columnPos = TableAxis.INVALID;
    m_oldPosition.rowPos = TableAxis.INVALID;

    // setup current position
    CellPosition position = new CellPosition();
    position.columnPos = TableAxis.INVALID;
    position.rowPos = TableAxis.INVALID;
    set( position );
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
    if ( get().columnPos != columnPos || get().rowPos != rowPos )
    {
      CellPosition temp = get();
      m_oldPosition.columnPos = columnPos;
      m_oldPosition.rowPos = rowPos;
      set( m_oldPosition );
      m_oldPosition = temp;
    }
  }

  /**************************************** setColumnPos *****************************************/
  public void setColumnPos( int position )
  {
    // set column position
    setPosition( position, get().rowPos );
  }

  /****************************************** setRowPos ******************************************/
  public void setRowPos( int position )
  {
    // set row position
    setPosition( get().columnPos, position );
  }

  /**************************************** getColumnPos *****************************************/
  public int getColumnPos()
  {
    // return column position
    return get().columnPos;
  }

  /****************************************** getRowPos ******************************************/
  public int getRowPos()
  {
    // return row position
    return get().rowPos;
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // return as string
    return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "["
        + get().columnPos + " " + get().rowPos + "]";
  }

}
