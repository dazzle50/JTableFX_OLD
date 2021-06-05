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

package rjc.table.view;

import java.util.ArrayList;
import java.util.HashSet;

import rjc.table.signal.ISignal;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cell.CellPosition;

/*************************************************************************************************/
/************************************* Table area selection **************************************/
/*************************************************************************************************/

public class TableSelection implements ISignal
{
  // class represents one selected rectangle
  private class Selected
  {
    public int c1; // smallest column position
    public int r1; // smallest row position
    public int c2; // largest column position
    public int r2; // largest row position

    public void set( int columnPos1, int rowPos1, int columnPos2, int rowPos2 )
    {
      // set private variables in correct order
      c1 = Math.min( columnPos1, columnPos2 );
      c1 = c1 < FIRSTCELL ? FIRSTCELL : c1;
      c2 = Math.max( columnPos1, columnPos2 );
      r1 = Math.min( rowPos1, rowPos2 );
      r1 = r1 < FIRSTCELL ? FIRSTCELL : r1;
      r2 = Math.max( rowPos1, rowPos2 );
    }

    public boolean isCellSelected( int columnPos, int rowPos )
    {
      // return true is specified position is selected
      return columnPos >= c1 && columnPos <= c2 && rowPos >= r1 && rowPos <= r2;
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "[c1=" + c1
          + " r1=" + r1 + " c2=" + c2 + " r2=" + r2 + "]";
    }
  }

  // class represents set of selected column or row positions, or all
  public static class SelectedSet
  {
    public boolean          all = false;          // all columns or rows selected
    public HashSet<Integer> set = new HashSet<>();

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "[" + all + " "
          + set + "]";
    }
  }

  final static private int    FIRSTCELL = TableAxis.FIRSTCELL;
  final static private int    AFTER     = TableAxis.AFTER;

  private boolean             m_show    = true;               // if false all 'is' and 'has' methods return false
  private TableView           m_view;
  private ArrayList<Selected> m_selected;

  /***************************************** constructor *****************************************/
  public TableSelection( TableView view )
  {
    // construct selection model
    m_view = view;
    m_selected = new ArrayList<>();
  }

  /******************************************** clear ********************************************/
  public void clear()
  {
    // remove all selected areas
    if ( !m_selected.isEmpty() )
    {
      m_selected.clear();
      signal( m_selected.size() ); // signal selection model has been cleared
    }
  }

  /******************************************* getShow *******************************************/
  public boolean getShow()
  {
    // get show status
    return m_show;
  }

  /******************************************* setShow *******************************************/
  public void setShow( boolean show )
  {
    // set show flag - if false all 'is' and 'has' methods will return false
    m_show = show;
  }

  /****************************************** getCount *******************************************/
  public int getCount()
  {
    // return number of selected areas on table-view
    return m_selected.size();
  }

  /****************************************** getCount *******************************************/
  public int getCount( int columnPos, int rowPos )
  {
    // return count of selected areas covering specified cell
    int count = 0;
    for ( Selected area : m_selected )
      if ( area.isCellSelected( columnPos, rowPos ) )
        count++;

    return count;
  }

  /***************************************** getSelected *****************************************/
  public int[] getSelected( int num )
  {
    // returns array of 4 integers representing selected area
    int[] list = new int[4];
    list[0] = m_selected.get( num ).c1;
    list[1] = m_selected.get( num ).r1;
    list[2] = m_selected.get( num ).c2;
    list[3] = m_selected.get( num ).r2;
    return list;
  }

  /*************************************** isCellSelected ****************************************/
  public boolean isCellSelected( int columnPos, int rowPos )
  {
    // return true if specified cell is in a selected area
    if ( m_show )
      for ( Selected area : m_selected )
        if ( area.isCellSelected( columnPos, rowPos ) )
          return true;

    return false;
  }

  /*************************************** hasRowSelection ***************************************/
  public boolean hasRowSelection( int rowPos )
  {
    // return true if specified row has any selection
    if ( m_show )
      for ( Selected area : m_selected )
        if ( rowPos >= area.r1 && rowPos <= area.r2 )
          return true;

    return false;
  }

  /************************************* hasColumnSelection **************************************/
  public boolean hasColumnSelection( int columnPos )
  {
    // return true if specified column has any selection
    if ( m_show )
      for ( Selected area : m_selected )
        if ( columnPos >= area.c1 && columnPos <= area.c2 )
          return true;

    return false;
  }

  /************************************** isColumnSelected ***************************************/
  public boolean isColumnSelected( int columnPos )
  {
    // if show disabled return false
    if ( !m_show )
      return false;

    // return true if all visible cells in specified column are selected
    TableAxis axis = m_view.getRowsAxis();
    int top = axis.getFirst();
    int bottom = axis.getLast();

    for ( int rowPos = top; rowPos <= bottom; rowPos++ )
      rows: if ( !axis.isPositionHidden( rowPos ) )
      {
        for ( Selected area : m_selected )
          if ( area.isCellSelected( columnPos, rowPos ) )
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

    int first = m_view.getColumnsAxis().getFirst();
    int last = m_view.getColumnsAxis().getLast();
    int top = m_view.getRowsAxis().getFirst();
    int bottom = m_view.getRowsAxis().getLast();

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

      // if columns selected then add to list
      if ( area.r1 <= top && area.r2 >= bottom )
      {
        for ( int column = area.c1; column <= area.c2; column++ )
          columns.set.add( column );
      }
    }

    return columns;
  }

  /*************************************** getSelectedRows ***************************************/
  public SelectedSet getSelectedRows()
  {
    // return return list of selected row positions
    SelectedSet rows = new SelectedSet();

    int first = m_view.getColumnsAxis().getFirst();
    int last = m_view.getColumnsAxis().getLast();
    int top = m_view.getRowsAxis().getFirst();
    int bottom = m_view.getRowsAxis().getLast();

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

      // if rows selected then add to list
      if ( area.c1 <= first && area.c2 >= last )
      {
        for ( int row = area.r1; row <= area.r2; row++ )
          rows.set.add( row );
      }
    }

    return rows;
  }

  /**************************************** isRowSelected ****************************************/
  public boolean isRowSelected( int rowPos )
  {
    // if show disabled return false
    if ( !m_show )
      return false;

    // return true if all visible cells in specified row are selected
    TableAxis axis = m_view.getColumnsAxis();

    int left = axis.getFirst();
    int right = axis.getLast();
    for ( int columnPos = left; columnPos <= right; columnPos++ )
      columns: if ( !axis.isPositionHidden( columnPos ) )
      {
        for ( Selected area : m_selected )
          if ( area.isCellSelected( columnPos, rowPos ) )
          {
            columnPos = area.c2;
            break columns;
          }
        return false;
      }

    return true;
  }

  /***************************************** selectArea ******************************************/
  public void selectArea( int columnPos1, int rowPos1, int columnPos2, int rowPos2 )
  {
    // add new selected area to table selected
    Selected newArea = new Selected();
    newArea.set( columnPos1, rowPos1, columnPos2, rowPos2 );
    m_selected.add( newArea );
    signal( m_selected.size() );
  }

  /******************************************** start ********************************************/
  public void start()
  {
    // start selecting new area (doesn't send signal)
    CellPosition focus = m_view.getFocusCell();
    CellPosition select = m_view.getSelectCell();

    Selected newArea = new Selected();
    newArea.set( focus.getColumnPos(), focus.getRowPos(), select.getColumnPos(), select.getRowPos() );
    m_selected.add( newArea );
  }

  /******************************************* update ********************************************/
  public void update()
  {
    // update last selected area if one
    if ( m_selected.isEmpty() )
      start();

    CellPosition focus = m_view.getFocusCell();
    CellPosition select = m_view.getSelectCell();
    Selected last = m_selected.get( m_selected.size() - 1 );

    // if selecting columns or rows, then start selecting from first cell
    int focusColumn = select.isColumnAfter() ? FIRSTCELL : focus.getColumnPos();
    int focusRow = select.isRowAfter() ? FIRSTCELL : focus.getRowPos();
    last.set( focusColumn, focusRow, select.getColumnPos(), select.getRowPos() );
    signal( m_selected.size() );
  }

  /****************************************** selectAll ******************************************/
  public void selectAll()
  {
    // select entire table
    m_selected.clear(); // direct clear to avoid clear signal before add signal
    selectArea( FIRSTCELL, FIRSTCELL, AFTER, AFTER );
    m_view.getSelectCell().setPosition( TableAxis.AFTER, TableAxis.AFTER );
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string
    String text = getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) );
    return text + "[selected=" + m_selected + "]";
  }

}
