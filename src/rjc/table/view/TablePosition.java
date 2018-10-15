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

import javafx.beans.property.SimpleIntegerProperty;

/*************************************************************************************************/
/********************** Table column and row index to position arrangement ***********************/
/*************************************************************************************************/

public class TablePosition extends TableDisplay
{
  // arrays mapping from position to index
  private ArrayList<Integer>         m_columnIndexes  = new ArrayList<Integer>();
  private ArrayList<Integer>         m_rowIndexes     = new ArrayList<Integer>();

  // properties holding mouse, edit focus, and select cell positions
  public final SimpleIntegerProperty mouseColumnPos   = new SimpleIntegerProperty( INVALID );
  public final SimpleIntegerProperty mouseRowPos      = new SimpleIntegerProperty( INVALID );

  public final SimpleIntegerProperty mouseColumnIndex = new SimpleIntegerProperty( INVALID );
  public final SimpleIntegerProperty mouseRowIndex    = new SimpleIntegerProperty( INVALID );

  public final SimpleIntegerProperty focusColumnPos   = new SimpleIntegerProperty( INVALID );
  public final SimpleIntegerProperty focusRowPos      = new SimpleIntegerProperty( INVALID );

  public final SimpleIntegerProperty selectColumnPos  = new SimpleIntegerProperty( INVALID );
  public final SimpleIntegerProperty selectRowPos     = new SimpleIntegerProperty( INVALID );

  /**************************************** constructor ******************************************/
  public TablePosition()
  {
    super();

    // keep mouse column index property updated
    mouseColumnPos.addListener( ( observable, oldColumn, newColumn ) -> mouseColumnIndex
        .set( getColumnIndexFromPosition( newColumn.intValue() ) ) );

    // keep mouse row index property updated
    mouseRowPos.addListener(
        ( observable, oldRow, newRow ) -> mouseRowIndex.set( getRowIndexFromPosition( newRow.intValue() ) ) );
  }

  /********************************* getColumnIndexFromPosition **********************************/
  public int getColumnIndexFromPosition( int columnPos )
  {
    // return column index from position (faster)
    try
    {
      return m_columnIndexes.get( columnPos );
    }
    catch ( IndexOutOfBoundsException exception )
    {
      // check position is within column count
      int count = m_data.getColumnCount();
      if ( columnPos < 0 || columnPos >= count )
        return INVALID;

      // expand mapping to cover column count
      for ( int column = m_columnIndexes.size(); column < count; column++ )
        m_columnIndexes.add( column );

      return m_columnIndexes.get( columnPos );
    }
  }

  /********************************* getColumnPositionFromIndex **********************************/
  public int getColumnPositionFromIndex( int columnIndex )
  {
    // return column position from index (slower)
    return m_columnIndexes.indexOf( columnIndex );
  }

  /*********************************** getRowIndexFromPosition ***********************************/
  public int getRowIndexFromPosition( int rowPos )
  {
    // return column index from position (faster)
    try
    {
      return m_rowIndexes.get( rowPos );
    }
    catch ( IndexOutOfBoundsException exception )
    {
      // check position is within row count
      int count = m_data.getRowCount();
      if ( rowPos < 0 || rowPos >= count )
        return INVALID;

      // expand mapping to cover row count
      for ( int row = m_rowIndexes.size(); row < count; row++ )
        m_rowIndexes.add( row );

      return m_rowIndexes.get( rowPos );
    }
  }

  /*********************************** getRowPositionFromIndex ***********************************/
  public int getRowPositionFromIndex( int rowIndex )
  {
    // return column position from index (slower)
    return m_rowIndexes.indexOf( rowIndex );
  }

  /*************************************** resetPositions ****************************************/
  public void resetPositions()
  {
    // reset column and row position to index mapping
    m_columnIndexes.clear();
    m_rowIndexes.clear();
  }

}
