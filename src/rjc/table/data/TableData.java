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

package rjc.table.data;

import rjc.table.signal.ISignal;
import rjc.table.signal.ObservableInteger;
import rjc.table.signal.ObservableInteger.ReadOnlyInteger;

/*************************************************************************************************/
/************** Table data source, column & row counts, signals to announce changes **************/
/*************************************************************************************************/

public class TableData implements ISignal
{
  // observable integers for table body column & row counts
  private ObservableInteger m_columnCount = new ObservableInteger( 3 );
  private ObservableInteger m_rowCount    = new ObservableInteger( 10 );

  public enum Signal
  {
    CELL_VALUE_CHANGED, ROW_VALUES_CHANGED, COLUMN_VALUES_CHANGED, TABLE_VALUES_CHANGED
  }

  // column & row index starts at 0 for table body, index of -1 is for header
  final static public int HEADER = -1;

  /*************************************** getColumnCount ****************************************/
  final public int getColumnCount()
  {
    // return number of columns in table body
    return m_columnCount.get();
  }

  /*************************************** setColumnCount ****************************************/
  final public void setColumnCount( int columnCount )
  {
    // set number of columns in table body
    m_columnCount.set( columnCount );
  }

  /**************************************** getRowCount ******************************************/
  final public int getRowCount()
  {
    // return number of rows in table body
    return m_rowCount.get();
  }

  /**************************************** setRowCount ******************************************/
  final public void setRowCount( int rowCount )
  {
    // set number of rows in table body
    m_rowCount.set( rowCount );
  }

  /************************************* columnCountProperty *************************************/
  final public ReadOnlyInteger columnCountProperty()
  {
    // return read-only property for column count
    return m_columnCount.getReadOnly();
  }

  /************************************** rowCountProperty ***************************************/
  final public ReadOnlyInteger rowCountProperty()
  {
    // return read-only property for row count
    return m_rowCount.getReadOnly();
  }

  /****************************************** getValue *******************************************/
  public Object getValue( int columnIndex, int rowIndex )
  {
    // return header corner cell value
    if ( columnIndex == HEADER && rowIndex == HEADER )
      return "-";

    // return row value for specified row index
    if ( columnIndex == HEADER )
      return "R" + rowIndex;

    // return column value for specified column index
    if ( rowIndex == HEADER )
      return "C" + columnIndex;

    // return cell value for specified cell index
    return "{" + columnIndex + "," + rowIndex + "}";
  }

  /****************************************** setValue *******************************************/
  public boolean setValue( int columnIndex, int rowIndex, Object newValue )
  {
    // returns true if cell value successfully set for specified cell index
    return false;
  }

  /************************************** signalCellChanged **************************************/
  final public void signalCellChanged( int column, int row )
  {
    // signal that a table cell value has changed (usually to trigger cell redraw)
    signal( Signal.CELL_VALUE_CHANGED, column, row );
  }

  /************************************* signalColumnChanged *************************************/
  final public void signalColumnChanged( int column )
  {
    // signal that table column values have changed (usually to trigger column redraw)
    signal( Signal.COLUMN_VALUES_CHANGED, column );
  }

  /*************************************** signalRowChanged **************************************/
  final public void signalRowChanged( int row )
  {
    // signal that table row values have changed (usually to trigger row redraw)
    signal( Signal.ROW_VALUES_CHANGED, row );
  }

  /************************************** signalTableChanged *************************************/
  final public void signalTableChanged()
  {
    // signal that table values have changed (usually to trigger table redraw)
    signal( Signal.TABLE_VALUES_CHANGED );
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // return as string
    return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "[m_columnCount="
        + m_columnCount + " m_rowCount=" + m_rowCount + "]";
  }
}