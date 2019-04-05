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

import java.util.ArrayList;

import rjc.table.Utils;

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

  // directions of focus movement
  public static enum MoveDirection
  {
    LEFT, RIGHT, UP, DOWN, NONE
  }

  // list of selected areas for this table view
  private ArrayList<Selected> m_selected = new ArrayList<>();

  private Selected            m_currentSelection;            // current selection area

  /************************************** clearAllSelection **************************************/
  public void clearAllSelection()
  {
    // remove all selected areas
    m_selected.clear();
    m_currentSelection = null;
  }

  /***************************************** selectTable *****************************************/
  public void selectTable()
  {
    // select entire visible table
    clearAllSelection();
    setCurrentSelection( getVisibleFirst(), getVisibleTop(), getVisibleLast(), getVisibleBottom() );
  }

  /************************************** startNewSelection **************************************/
  public void startNewSelection()
  {
    // start new selection
    m_currentSelection = null;
    setCurrentSelection();
  }

  /************************************* setCurrentSelection *************************************/
  public void setCurrentSelection( int columnPos1, int rowPos1, int columnPos2, int rowPos2 )
  {
    // check column & row positions are zero or positive
    if ( columnPos1 < 0 || rowPos1 < 0 || columnPos2 < 0 || rowPos2 < 0 )
      throw new IllegalArgumentException(
          "Negative positions not allowed " + columnPos1 + " " + rowPos1 + " " + columnPos2 + " " + rowPos2 );

    // if no current selection, start new selection area
    if ( m_currentSelection == null )
    {
      m_currentSelection = new Selected();
      m_selected.add( m_currentSelection );
    }

    // ensure selected columns start at top of table, and selected rows start at left edge of table
    if ( rowPos2 == BELOW_TABLE )
      rowPos1 = 0;
    if ( columnPos2 == RIGHT_OF_TABLE )
      columnPos1 = 0;

    // set current selection area
    m_currentSelection.set( columnPos1, rowPos1, columnPos2, rowPos2 );
  }

  /************************************* setCurrentSelection *************************************/
  public void setCurrentSelection()
  {
    // set current selection area to rectangle between focus and select cells
    setCurrentSelection( focusColumnPos.get(), focusRowPos.get(), selectColumnPos.get(), selectRowPos.get() );
  }

  /************************************* getCurrentSelection *************************************/
  public Selected getCurrentSelection()
  {
    // return current selection area
    return m_currentSelection;
  }

  /****************************************** moveFocus ******************************************/
  public void moveFocus( MoveDirection direction )
  {
    // is selection is only on focus cell
    if ( m_selected.size() == 0 )
    {
      // move within full visible table
      int columnPos = focusColumnPos.get();
      int rowPos = focusRowPos.get();

      if ( direction == MoveDirection.RIGHT )
      {
        columnPos = getVisibleRight( columnPos );
        if ( columnPos == focusColumnPos.get() )
        {
          columnPos = getVisibleFirst();
          rowPos = getVisibleDown( rowPos );
          if ( rowPos == focusRowPos.get() )
            rowPos = getVisibleTop();
        }
      }

      if ( direction == MoveDirection.LEFT )
      {
        columnPos = getVisibleLeft( columnPos );
        if ( columnPos == focusColumnPos.get() )
        {
          columnPos = getVisibleLast();
          rowPos = getVisibleUp( rowPos );
          if ( rowPos == focusRowPos.get() )
            rowPos = getVisibleBottom();
        }
      }

      if ( direction == MoveDirection.DOWN )
      {
        rowPos = getVisibleDown( rowPos );
        if ( rowPos == focusRowPos.get() )
        {
          rowPos = getVisibleTop();
          columnPos = getVisibleRight( columnPos );
          if ( columnPos == focusColumnPos.get() )
            columnPos = getVisibleFirst();
        }
      }

      if ( direction == MoveDirection.UP )
      {
        rowPos = getVisibleUp( rowPos );
        if ( rowPos == focusRowPos.get() )
        {
          rowPos = getVisibleBottom();
          columnPos = getVisibleLeft( columnPos );
          if ( columnPos == focusColumnPos.get() )
            columnPos = getVisibleLast();
        }
      }

      setFocusPosition( columnPos, rowPos );
    }
    else
    {
      // move within selected area(s)
      Utils.trace( "Selected area(s)" );
    }

  }

  /*********************************** setSelectFocusPosition ************************************/
  protected void setSelectFocusPosition( int columnPos, int rowPos, boolean setFocus, boolean clearSelection )
  {
    // when selecting whole row or column selection, check not also setting focus
    if ( setFocus && ( columnPos == RIGHT_OF_TABLE || rowPos == BELOW_TABLE ) )
      throw new IllegalArgumentException( "Setting focus not allowed " + columnPos + " " + rowPos );

    // clear previous selections
    if ( clearSelection )
      clearAllSelection();

    // set table select & focus cell position properties
    setSelectPosition( columnPos, rowPos );
    if ( setFocus )
      setFocusPosition( columnPos, rowPos );
    setCurrentSelection();

    // ensure column and row positions are visible
    if ( columnPos < Integer.MAX_VALUE )
      columnPos = ensureColumnShown( columnPos );
    if ( rowPos < Integer.MAX_VALUE )
      rowPos = ensureRowShown( rowPos );
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

  /************************************** getSelectionCount **************************************/
  public int getSelectionCount()
  {
    // return number of selected areas
    return m_selected.size();
  }

  /************************************** getSelectionCount **************************************/
  public int getSelectionCount( int columnPos, int rowPos )
  {
    // return count of selected areas covering specified cell
    int count = 0;
    for ( Selected area : m_selected )
      if ( columnPos >= area.c1 && columnPos <= area.c2 && rowPos >= area.r1 && rowPos <= area.r2 )
        count++;

    return count;
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

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string
    return getClass().getSimpleName() + "@" + Integer.toHexString( hashCode() ) + "[selected=" + m_selected.size()
        + "]";
  }

}
