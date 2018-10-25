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

import java.util.HashSet;
import java.util.Set;

/*************************************************************************************************/
/******************************** Table cell/row/column selection ********************************/
/*************************************************************************************************/

public class TableSelection extends TableSizing
{
  // long HASH = (long)columnPos << 32 | rowPos & 0xFFFFFFFFL;
  // int column = (int)(HASH >> 32);
  // int row = (int)HASH;
  private Set<Long>    m_selectedCells   = new HashSet<>();
  private Set<Integer> m_selectedRows    = new HashSet<>();
  private Set<Integer> m_selectedColumns = new HashSet<>();

  private static int clamp( int val, int min, int max )
  {
    return Math.max( min, Math.min( max, val ) );
  }

  /*************************************** clearSelection ****************************************/
  public void clearSelection()
  {
    // clear selection from all cells
    m_selectedCells.clear();
    m_selectedRows.clear();
    m_selectedColumns.clear();
  }

  /*************************************** isCellSelected ****************************************/
  public boolean isCellSelected( int columnPos, int rowPos )
  {
    // return true if specified row is selected
    if ( m_selectedRows.contains( rowPos ) )
      return true;

    // return true if specified column-position is selected
    if ( m_selectedColumns.contains( columnPos ) )
      return true;

    // return true if specified body cell is selected
    return m_selectedCells.contains( (long) columnPos << 32 | rowPos & 0xFFFFFFFFL );
  }

  /************************************** isColumnSelected ***************************************/
  public boolean isColumnSelected( int columnPos )
  {
    // return true if specified column is selected
    if ( m_selectedColumns.contains( columnPos ) )
      return true;

    // return false if any visible row cell in column is not selected
    int rows = m_data.getRowCount();
    for ( int rowPos = 0; rowPos < rows; rowPos++ )
      if ( !isRowPositionHidden( rowPos ) && !isCellSelected( columnPos, rowPos ) )
        return false;

    return true;
  }

  /**************************************** isRowSelected ****************************************/
  public boolean isRowSelected( int rowPos )
  {
    // return true if specified row is selected
    if ( m_selectedRows.contains( rowPos ) )
      return true;

    // return false if any visible column cell in row is not selected
    int columns = m_data.getColumnCount();
    for ( int columnPos = 0; columnPos < columns; columnPos++ )
      if ( !isColumnPositionHidden( columnPos ) && !isCellSelected( columnPos, columnPos ) )
        return false;

    return true;
  }

  /******************************************* select ********************************************/
  public void select( int columnPos, int rowPos, boolean selected )
  {
    // set whether specified body cell is selected
    if ( selected )
    {
      // only need to record if not already selected via row/column selection
      if ( !m_selectedColumns.contains( columnPos ) && !m_selectedRows.contains( rowPos ) )
        m_selectedCells.add( (long) columnPos << 32 | rowPos & 0xFFFFFFFFL );
    }
    else
      m_selectedCells.remove( (long) columnPos << 32 | rowPos & 0xFFFFFFFFL );
  }

  /**************************************** selectColumn *****************************************/
  public void selectColumn( int columnPos, boolean selected )
  {
    // set whether specified table column is selected
    if ( selected )
      m_selectedColumns.add( columnPos );
    else
      m_selectedColumns.remove( columnPos );
  }

  /****************************************** selectRow ******************************************/
  public void selectRow( int rowPos, boolean selected )
  {
    // set whether specified table row is selected
    if ( selected )
      m_selectedRows.add( rowPos );
    else
      m_selectedRows.remove( rowPos );
  }

  /******************************************* select ********************************************/
  public void select( int columnPos1, int rowPos1, int columnPos2, int rowPos2, boolean selected )
  {
    // ensure column and row positions are within bounds
    columnPos1 = clamp( columnPos1, 0, m_data.getColumnCount() - 1 );
    columnPos2 = clamp( columnPos2, 0, m_data.getColumnCount() - 1 );
    rowPos1 = clamp( rowPos1, 0, m_data.getRowCount() - 1 );
    rowPos2 = clamp( rowPos2, 0, m_data.getRowCount() - 1 );

    // determine min & max positions
    int c1 = Math.min( columnPos1, columnPos2 );
    int c2 = Math.max( columnPos1, columnPos2 );
    int r1 = Math.min( rowPos1, rowPos2 );
    int r2 = Math.max( rowPos1, rowPos2 );

    // set whether specified table region is selected
    for ( int column = c1; column <= c2; column++ )
      for ( int row = r1; row <= r2; row++ )
        select( column, row, selected );
  }

  /***************************************** selectRows ******************************************/
  public void selectRows( int row1, int row2, boolean selected )
  {
    // ensure row positions are within bounds
    row1 = clamp( row1, 0, m_data.getRowCount() - 1 );
    row2 = clamp( row2, 0, m_data.getRowCount() - 1 );

    // determine min & max positions
    int r1 = Math.min( row1, row2 );
    int r2 = Math.max( row1, row2 );

    // set whether specified row positions are selected
    for ( int row = r1; row <= r2; row++ )
      selectRow( row, selected );
  }

  /**************************************** selectColumns ****************************************/
  public void selectColumns( int columnPos1, int columnPos2, boolean selected )
  {
    // ensure column positions are within bounds
    columnPos1 = clamp( columnPos1, 0, m_data.getColumnCount() - 1 );
    columnPos2 = clamp( columnPos2, 0, m_data.getColumnCount() - 1 );

    // determine min & max positions
    int c1 = Math.min( columnPos1, columnPos2 );
    int c2 = Math.max( columnPos1, columnPos2 );

    // set whether specified column positions are selected
    for ( int column = c1; column <= c2; column++ )
      selectColumn( column, selected );
  }

  /************************************ doesRowHaveSelection *************************************/
  public boolean doesRowHaveSelection( int rowPos )
  {
    // return true if any column selected
    if ( !m_selectedColumns.isEmpty() )
      return true;

    // return true if specified row-position is selected
    if ( m_selectedRows.contains( rowPos ) )
      return true;

    // return true if any selected body cells on specified row
    for ( long hash : m_selectedCells )
      if ( (int) hash == rowPos )
        return true;

    return false;
  }

  /*********************************** doesColumnHaveSelection ***********************************/
  public boolean doesColumnHaveSelection( int columnPos )
  {
    // return true if any row selected
    if ( !m_selectedRows.isEmpty() )
      return true;

    // return true if specified column-position is selected
    if ( m_selectedColumns.contains( columnPos ) )
      return true;

    // return true if any selected body cells on specified column
    for ( long hash : m_selectedCells )
      if ( (int) ( hash >> 32 ) == columnPos )
        return true;

    return false;
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string
    return getClass().getSimpleName() + "@" + Integer.toHexString( hashCode() ) + "[cols=" + m_selectedColumns.size()
        + " rows=" + m_selectedRows.size() + " cells=" + m_selectedCells.size() + "]";
  }

}
