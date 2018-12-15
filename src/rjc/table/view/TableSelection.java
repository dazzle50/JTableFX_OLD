/**************************************************************************
 *  Copyright (C) 2018 by Richard Crook                                   *
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

import java.util.ArrayList;

/*************************************************************************************************/
/************************************* Table area selection **************************************/
/*************************************************************************************************/

public class TableSelection extends TableSizing
{
  // structure that contains one selected area
  public class Selected
  {
    public int c1; // smallest column position
    public int r1; // smallest row position
    public int c2; // largest column position
    public int r2; // largest row position

    public void set( int columnPos1, int rowPos1, int columnPos2, int rowPos2 )
    {
      // set private variables in correct order
      c1 = Math.min( columnPos1, columnPos2 );
      c2 = Math.max( columnPos1, columnPos2 );
      r1 = Math.min( rowPos1, rowPos2 );
      r2 = Math.max( rowPos1, rowPos2 );
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "@" + Integer.toHexString( hashCode() ) + "[c1=" + c1 + " r1=" + r1 + " c2="
          + c2 + " r2=" + r2 + "]";
    }
  }

  // list of selected areas for this table view
  private ArrayList<Selected> m_selected = new ArrayList<>();

  /************************************** clearAllSelection **************************************/
  public void clearAllSelection()
  {
    // remove all selected areas
    m_selected.clear();
  }

  /*************************************** selectionCount ****************************************/
  public int selectionCount()
  {
    // return number of selected areas
    return m_selected.size();
  }

  /*************************************** isCellSelected ****************************************/
  public boolean isCellSelected( int columnPos, int rowPos )
  {
    // return true if specified cell is in a selected area
    for ( Selected area : m_selected )
      if ( columnPos >= area.c1 && columnPos <= area.c2 && rowPos >= area.r1 && rowPos <= area.r2 )
        return true;

    return false;
  }

  /************************************** isColumnSelected ***************************************/
  public boolean isColumnSelected( int columnPos )
  {
    // return true if all visible cells in specified column are selected
    int top = getVisibleTop();
    int bottom = getVisibleBottom();
    for ( int rowPos = top; rowPos <= bottom; rowPos++ )
      rows: if ( !isRowPositionHidden( rowPos ) )
      {
        for ( Selected area : m_selected )
          if ( columnPos >= area.c1 && columnPos <= area.c2 && rowPos >= area.r1 && rowPos <= area.r2 )
          {
            rowPos = area.r2;
            break rows;
          }
        return false;
      }

    return true;
  }

  /**************************************** isRowSelected ****************************************/
  public boolean isRowSelected( int rowPos )
  {
    // return true if all visible cells in specified row are selected
    int left = getVisibleFirst();
    int right = getVisibleLast();
    for ( int columnPos = left; columnPos <= right; columnPos++ )
      columns: if ( !isColumnPositionHidden( columnPos ) )
      {
        for ( Selected area : m_selected )
          if ( columnPos >= area.c1 && columnPos <= area.c2 && rowPos >= area.r1 && rowPos <= area.r2 )
          {
            columnPos = area.c2;
            break columns;
          }
        return false;
      }

    return true;
  }

  /*************************************** hasRowSelection ***************************************/
  public boolean hasRowSelection( int rowPos )
  {
    // return true if specified row has any selection
    for ( Selected area : m_selected )
      if ( rowPos >= area.r1 && rowPos <= area.r2 )
        return true;

    return false;
  }

  /************************************* hasColumnSelection **************************************/
  public boolean hasColumnSelection( int columnPos )
  {
    // return true if specified column has any selection
    for ( Selected area : m_selected )
      if ( columnPos >= area.c1 && columnPos <= area.c2 )
        return true;

    return false;
  }

  /*************************************** startSelection ****************************************/
  public Selected startSelection()
  {
    // add a new selection to this tables list of selected areas
    Selected selected = new Selected();
    m_selected.add( selected );
    return selected;
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string
    return getClass().getSimpleName() + "@" + Integer.toHexString( hashCode() ) + "[selected=" + m_selected.size()
        + "]";
  }

}
