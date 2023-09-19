/**************************************************************************
 *  Copyright (C) 2023 by Richard Crook                                   *
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

import java.util.ArrayList;

import rjc.table.signal.ISignal;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/******************************** Table view selected cells/areas ********************************/
/*************************************************************************************************/

public class CellSelection implements ISignal
{
  // class represents one selected rectangle
  private class SelectedArea
  {
    public int c1; // smallest column index
    public int r1; // smallest row index
    public int c2; // largest column index
    public int r2; // largest row index

    public void set( int columnIndex1, int rowIndex1, int columnIndex2, int rowIndex2 )
    {
      // set private variables in correct order
      c1 = Math.min( columnIndex1, columnIndex2 );
      c1 = c1 < FIRSTCELL ? FIRSTCELL : c1;
      c2 = Math.max( columnIndex1, columnIndex2 );
      r1 = Math.min( rowIndex1, rowIndex2 );
      r1 = r1 < FIRSTCELL ? FIRSTCELL : r1;
      r2 = Math.max( rowIndex1, rowIndex2 );
    }

    public boolean isCellSelected( int columnIndex, int rowIndex )
    {
      // return true is specified position is selected
      return columnIndex >= c1 && columnIndex <= c2 && rowIndex >= r1 && rowIndex <= r2;
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "[c1=" + c1
          + " r1=" + r1 + " c2=" + c2 + " r2=" + r2 + "]";
    }
  }

  final static private int        FIRSTCELL = TableAxis.FIRSTCELL;
  final static private int        AFTER     = TableAxis.AFTER;

  private TableView               m_view;
  private ArrayList<SelectedArea> m_selected;

  /***************************************** constructor *****************************************/
  public CellSelection( TableView view )
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

  /******************************************** start ********************************************/
  public void start()
  {
    // start selecting new area based on focus & select cells
    ViewPosition focus = m_view.getFocusCell();
    ViewPosition select = m_view.getSelectCell();
    selectArea( focus.getColumn(), focus.getRow(), select.getColumn(), select.getRow() );
  }

  /******************************************* update ********************************************/
  public void update()
  {
    // update last selected area
    ViewPosition focus = m_view.getFocusCell();
    ViewPosition select = m_view.getSelectCell();
    if ( m_selected.isEmpty() )
      start();
    SelectedArea last = m_selected.get( m_selected.size() - 1 );

    // if selecting columns or rows, then start selecting from first cell
    int focusColumn = select.isColumnAfter() ? FIRSTCELL : focus.getColumn();
    int focusRow = select.isRowAfter() ? FIRSTCELL : focus.getRow();
    last.set( focusColumn, focusRow, select.getColumn(), select.getRow() );
    signal( m_selected.size() );
  }

  /*************************************** isCellSelected ****************************************/
  public boolean isCellSelected( int columnIndex, int rowIndex )
  {
    // TODO Auto-generated method stub
    return false;
  }

  /************************************** isColumnSelected ***************************************/
  public boolean isColumnSelected( int column )
  {
    // TODO Auto-generated method stub
    return false;
  }

  /**************************************** isRowSelected ****************************************/
  public boolean isRowSelected( int row )
  {
    // TODO Auto-generated method stub
    return false;
  }

  /************************************* hasColumnSelection **************************************/
  public boolean hasColumnSelection( int columnIndex )
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*************************************** hasRowSelection ***************************************/
  public boolean hasRowSelection( int rowIndex )
  {
    // TODO Auto-generated method stub
    return false;
  }

  /***************************************** selectArea ******************************************/
  public void selectArea( int columnIndex1, int rowIndex1, int columnIndex2, int rowIndex2 )
  {
    // add new selected area to table selected
    SelectedArea newArea = new SelectedArea();
    newArea.set( columnIndex1, rowIndex1, columnIndex2, rowIndex2 );
    m_selected.add( newArea );
    signal( m_selected.size() );
  }

  /****************************************** selectAll ******************************************/
  public void selectAll()
  {
    // select entire table
    m_selected.clear(); // direct clear to avoid clear signal before add signal
    selectArea( FIRSTCELL, FIRSTCELL, AFTER, AFTER );
    m_view.getFocusCell().setPosition( FIRSTCELL, FIRSTCELL );
    m_view.getSelectCell().setPosition( AFTER, AFTER );
  }

  /****************************************** getAreas *******************************************/
  public ArrayList<int[]> getAreas()
  {
    // return list of selected areas
    int maxColumn = m_view.getColumnsAxis().getCount() - 1;
    int maxRow = m_view.getRowsAxis().getCount() - 1;
    var areas = new ArrayList<int[]>();

    // construct the list
    for ( SelectedArea selected : m_selected )
    {
      int[] area = { selected.c1, selected.r1, Math.min( selected.c2, maxColumn ), Math.min( selected.r2, maxRow ) };
      areas.add( area );
    }

    return areas;
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
