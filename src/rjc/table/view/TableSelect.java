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

import java.util.HashSet;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import rjc.table.Utils;

/*************************************************************************************************/
/************************************* Table area selection **************************************/
/*************************************************************************************************/

public class TableSelect extends TableNavigate
{
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

    public boolean isSelected( int columnPos, int rowPos )
    {
      // return true is specified position is selected
      return columnPos >= c1 && columnPos <= c2 && rowPos >= r1 && rowPos <= r2;
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "@" + Integer.toHexString( hashCode() ) + "[c1=" + c1 + " r1=" + r1 + " c2="
          + c2 + " r2=" + r2 + "]";
    }
  }

  public class SelectedSet
  {
    public boolean          all; // all columns or rows selected
    public HashSet<Integer> set;
  }

  // observable list of selected areas for this table view (should always include focus cell)
  final private ReadOnlyListWrapper<Selected> m_selected = new ReadOnlyListWrapper<>(
      FXCollections.observableArrayList() );

  private Selected                            m_currentSelection;                    // current selection area

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
    // select entire table
    clearAllSelection();
    setCurrentSelection( FIRSTCELL, FIRSTCELL, AFTER, AFTER );
  }

  /************************************* setCurrentSelection *************************************/
  public void setCurrentSelection()
  {
    // set current selection area to rectangle between focus and select cells
    if ( getFocusColumnPosition() > HEADER )
      setCurrentSelection( getFocusColumnPosition(), getFocusRowPosition(), getSelectColumnPosition(),
          getSelectRowPosition() );
  }

  /************************************* setCurrentSelection *************************************/
  public void setCurrentSelection( int columnPos1, int rowPos1, int columnPos2, int rowPos2 )
  {
    // check column & row positions are zero or positive
    if ( columnPos1 <= HEADER || rowPos1 <= HEADER || columnPos2 <= HEADER || rowPos2 <= HEADER )
      throw new IllegalArgumentException(
          "Positions not valid " + columnPos1 + " " + rowPos1 + " " + columnPos2 + " " + rowPos2 );

    // if no current selection, start new selection area
    if ( m_currentSelection == null )
    {
      m_currentSelection = new Selected();
      m_selected.add( m_currentSelection );
    }

    // ensure selected columns start at top of table, and selected rows start at left edge of table
    if ( rowPos2 == AFTER )
      rowPos1 = FIRSTCELL;
    if ( columnPos2 == AFTER )
      columnPos1 = FIRSTCELL;

    // set current selection area
    m_currentSelection.set( columnPos1, rowPos1, columnPos2, rowPos2 );
  }

  /************************************ getSelectionProperty *************************************/
  public ReadOnlyListProperty<Selected> getSelectionProperty()
  {
    // return selected areas list property
    return m_selected.getReadOnlyProperty();
  }

  /************************************** getSelectionCount **************************************/
  public int getSelectionCount()
  {
    // return number of table selected areas
    return m_selected.size();
  }

  /************************************** getSelectionCount **************************************/
  public int getSelectionCount( int columnPos, int rowPos )
  {
    // return count of selected areas covering specified cell
    int count = 0;
    for ( Selected area : m_selected )
      if ( area.isSelected( columnPos, rowPos ) )
        count++;

    return count;
  }

  /*************************************** isCellSelected ****************************************/
  public boolean isCellSelected( int columnPos, int rowPos )
  {
    // return true if specified cell is in a selected area
    for ( Selected area : m_selected )
      if ( area.isSelected( columnPos, rowPos ) )
        return true;

    return false;
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

  /************************************** isColumnSelected ***************************************/
  public boolean isColumnSelected( int columnPos )
  {
    // return true if all visible cells in specified column are selected
    int top = m_rows.getFirst();
    int bottom = m_rows.getLast();
    for ( int rowPos = top; rowPos <= bottom; rowPos++ )
      rows: if ( !m_rows.isPositionHidden( rowPos ) )
      {
        for ( Selected area : m_selected )
          if ( area.isSelected( columnPos, rowPos ) )
          {
            rowPos = area.r2;
            break rows;
          }
        return false;
      }

    return true;
  }

  /************************************* getSelectedColumns **************************************/
  public SelectedSet getSelectedColumns()
  {
    // return return list of selected column positions
    SelectedSet columns = new SelectedSet();
    columns.all = false;
    columns.set = new HashSet<>();

    int first = m_columns.getFirst();
    int last = m_columns.getLast();
    int top = m_rows.getFirst();
    int bottom = m_rows.getLast();

    // loop through the selected areas
    for ( Selected area : m_selected )
    {
      // if whole table selected then set all and exit
      if ( area.c1 <= first && area.c2 >= last && area.r1 <= top && area.r2 >= bottom )
      {
        columns.all = true;
        columns.set = null;
        return columns;
      }

      // if columns selected then to list
      if ( area.r1 <= top && area.r2 >= bottom )
      {
        for ( int column = area.c1; column <= area.c2; column++ )
          columns.set.add( column );
      }
    }

    Utils.trace( columns.all, columns.set );
    return columns;
  }

  /*************************************** getSelectedRows ***************************************/
  public SelectedSet getSelectedRows()
  {
    // return return list of selected row positions
    SelectedSet rows = new SelectedSet();
    rows.all = false;
    rows.set = new HashSet<>();

    int first = m_columns.getFirst();
    int last = m_columns.getLast();
    int top = m_rows.getFirst();
    int bottom = m_rows.getLast();

    // loop through the selected areas
    for ( Selected area : m_selected )
    {
      // if whole table selected then set all and exit
      if ( area.c1 <= first && area.c2 >= last && area.r1 <= top && area.r2 >= bottom )
      {
        rows.all = true;
        rows.set = null;
        return rows;
      }

      // if rows selected then to list
      if ( area.c1 <= first && area.c2 >= last )
      {
        for ( int row = area.r1; row <= area.r2; row++ )
          rows.set.add( row );
      }
    }

    Utils.trace( rows.all, rows.set );
    return rows;
  }

  /**************************************** isRowSelected ****************************************/
  public boolean isRowSelected( int rowPos )
  {
    // return true if all visible cells in specified row are selected
    int left = m_columns.getFirst();
    int right = m_columns.getLast();
    for ( int columnPos = left; columnPos <= right; columnPos++ )
      columns: if ( !m_columns.isPositionHidden( columnPos ) )
      {
        for ( Selected area : m_selected )
          if ( area.isSelected( columnPos, rowPos ) )
          {
            columnPos = area.c2;
            break columns;
          }
        return false;
      }

    return true;
  }

  /************************************** startNewSelection **************************************/
  public void startNewSelection()
  {
    // start new selection
    m_currentSelection = null;
    setCurrentSelection();
  }

  /*********************************** setSelectFocusPosition ************************************/
  protected void setSelectFocusPosition( int columnPos, int rowPos, boolean setFocus, boolean clearSelection,
      boolean scroll )
  {
    // clear previous selections
    if ( clearSelection )
      clearAllSelection();

    // check column & row positions are valid
    if ( columnPos < FIRSTCELL )
      columnPos = FIRSTCELL;
    if ( rowPos < FIRSTCELL )
      rowPos = FIRSTCELL;

    // set table select & focus cell positions
    setSelectColumnPosition( columnPos );
    setSelectRowPosition( rowPos );
    if ( setFocus || getFocusColumnPosition() < FIRSTCELL || getFocusRowPosition() < FIRSTCELL )
    {
      setFocusColumnPosition( columnPos );
      setFocusRowPosition( rowPos );
    }
    setCurrentSelection();

    // scroll table if necessary to show cell position
    if ( scroll )
    {
      if ( columnPos < AFTER )
        m_hScrollBar.scrollToPos( columnPos );
      if ( rowPos < AFTER )
        m_vScrollBar.scrollToPos( rowPos );
    }
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
