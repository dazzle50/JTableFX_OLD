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
import java.util.HashMap;
import java.util.Map;

import rjc.table.support.Utils;

/*************************************************************************************************/
/****************** Table header + cell column and row sizing including hiding *******************/
/*************************************************************************************************/

public class TableSizing extends TablePosition
{
  private int                   m_columnDefaultWidth         = 100;
  private int                   m_rowDefaultHeight           = 20;
  private int                   m_columnMinimumWidth         = 40;
  private int                   m_rowMinimumHeight           = 17;
  private int                   m_rowHeaderWidth             = 40;
  private int                   m_columnHeaderHeight         = 20;

  // all columns have default widths, and rows default heights, except those in these maps, -ve means hidden
  private Map<Integer, Integer> m_columnIndexWidthExceptions = new HashMap<Integer, Integer>();
  private Map<Integer, Integer> m_rowIndexHeightExceptions   = new HashMap<Integer, Integer>();

  private ArrayList<Integer>    m_rowPosYStartCached         = new ArrayList<Integer>();
  private ArrayList<Integer>    m_columnPosXStartCached      = new ArrayList<Integer>();

  private int                   m_bodyWidthCached            = INVALID;                        // body cells total width (excludes header)
  private int                   m_bodyHeightCached           = INVALID;                        // body cells total height (excludes header)

  /*************************************** getTableWidth *****************************************/
  public int getTableWidth()
  {
    // return width in pixels of all the visible table body columns + row header
    return getBodyWidth() + m_rowHeaderWidth;
  }

  /************************************** getTableHeight *****************************************/
  public int getTableHeight()
  {
    // return height in pixels of all the visible table body rows + column header
    return getBodyHeight() + m_columnHeaderHeight;
  }

  /*************************************** getBodyWidth ******************************************/
  public int getBodyWidth()
  {
    // return width in pixels of all the visible table body columns only
    if ( m_bodyWidthCached == INVALID )
    {
      // cached width is invalid, so calculate width of table body cell columns
      int exceptionsCount = 0;
      int columnCount = m_data.getColumnCount();
      int bodyWidth = 0;

      for ( int columnIndex : m_columnIndexWidthExceptions.keySet() )
      {
        if ( columnIndex < columnCount )
        {
          exceptionsCount++;
          int width = m_columnIndexWidthExceptions.get( columnIndex );
          if ( width > 0 )
            bodyWidth += width;
        }
        else
          m_columnIndexWidthExceptions.remove( columnIndex );
      }

      m_bodyWidthCached = bodyWidth + ( columnCount - exceptionsCount ) * m_columnDefaultWidth;
    }

    return m_bodyWidthCached;
  }

  /************************************** getBodyHeight ******************************************/
  public int getBodyHeight()
  {
    // return height in pixels of all the visible table body rows only
    if ( m_bodyHeightCached == INVALID )
    {
      // cached height is invalid, so calculate height of table body cell rows
      int exceptionsCount = 0;
      int rowCount = m_data.getRowCount();
      int bodyHeight = 0;

      for ( int rowIndex : m_rowIndexHeightExceptions.keySet() )
      {
        if ( rowIndex < rowCount )
        {
          exceptionsCount++;
          int height = m_rowIndexHeightExceptions.get( rowIndex );
          if ( height > 0 )
            bodyHeight += height;
        }
        else
          m_rowIndexHeightExceptions.remove( rowIndex );
      }

      m_bodyHeightCached = bodyHeight + ( rowCount - exceptionsCount ) * m_rowDefaultHeight;
    }

    return m_bodyHeightCached;
  }

  /************************************** getRowHeaderWidth **************************************/
  public int getRowHeaderWidth()
  {
    // return table row header width
    return m_rowHeaderWidth;
  }

  /************************************** setRowHeaderWidth **************************************/
  public void setRowHeaderWidth( int width )
  {
    // set table row header width
    m_rowHeaderWidth = width;
  }

  /************************************ getColumnHeaderHeight ************************************/
  public int getColumnHeaderHeight()
  {
    // return table column header height
    return m_columnHeaderHeight;
  }

  /************************************ setColumnHeaderHeight ************************************/
  public void setColumnHeaderHeight( int height )
  {
    // set table column header height
    m_columnHeaderHeight = height;
  }

  /************************************* getRowMinimumHeight *************************************/
  public int getRowMinimumHeight()
  {
    // return table row minimum height
    return m_rowMinimumHeight;
  }

  /************************************* setRowMinimumHeight *************************************/
  public void setRowMinimumHeight( int height )
  {
    // set table row minimum height, checking exceptions first
    if ( height > m_rowMinimumHeight )
    {
      var it = m_rowIndexHeightExceptions.entrySet().iterator();
      while ( it.hasNext() )
      {
        var entry = it.next();
        if ( entry.getValue() > 0 && entry.getValue() < height )
          entry.setValue( height );
        if ( entry.getValue() < 0 && entry.getValue() > -height )
          entry.setValue( -height );
      }
      m_bodyHeightCached = INVALID;
      m_rowPosYStartCached.clear();
    }

    m_rowMinimumHeight = height;
  }

  /************************************ getColumnMinimumWidth ************************************/
  public int getColumnMinimumWidth()
  {
    // return table column minimum width
    return m_columnMinimumWidth;
  }

  /************************************ setColumnMinimumWidth ************************************/
  public void setColumnMinimumWidth( int width )
  {
    // set table column minimum width, checking exceptions first
    if ( width > m_columnMinimumWidth )
    {
      var it = m_columnIndexWidthExceptions.entrySet().iterator();
      while ( it.hasNext() )
      {
        var entry = it.next();
        if ( entry.getValue() > 0 && entry.getValue() < width )
          entry.setValue( width );
        if ( entry.getValue() < 0 && entry.getValue() > -width )
          entry.setValue( -width );
      }
      m_bodyWidthCached = INVALID;
      m_columnPosXStartCached.clear();
    }

    m_columnMinimumWidth = width;
  }

  /************************************ setColumnDefaultWidth ************************************/
  public void setColumnDefaultWidth( int width )
  {
    // set default column width
    m_columnDefaultWidth = width;
    m_bodyWidthCached = INVALID;
    m_columnPosXStartCached.clear();

    // if new width is less than minimum, also set minimum width to same
    if ( width < m_columnMinimumWidth )
      setColumnMinimumWidth( width );
  }

  /************************************* setRowDefaultHeight *************************************/
  public void setRowDefaultHeight( int height )
  {
    // set default row height
    m_rowDefaultHeight = height;
    m_bodyHeightCached = INVALID;
    m_rowPosYStartCached.clear();

    // if new height is less than minimum, also set minimum height to same
    if ( height < m_rowMinimumHeight )
      setRowMinimumHeight( height );
  }

  /************************************* getColumnIndexWidth *************************************/
  public int getColumnIndexWidth( int columnIndex )
  {
    // return width for column index
    if ( columnIndex == HEADER )
      return getRowHeaderWidth();

    int width = m_columnIndexWidthExceptions.getOrDefault( columnIndex, m_columnDefaultWidth );
    if ( width < 0 )
      return 0; // -ve means column hidden, so return zero

    return width;
  }

  /*********************************** setColumnIndexWidth ***************************************/
  public void setColumnIndexWidth( int columnIndex, int newWidth )
  {
    // width should not be below minimum
    if ( newWidth < m_columnMinimumWidth )
      newWidth = m_columnMinimumWidth;

    // if width is changed, update body width and update width exception
    int oldWidth = getColumnIndexWidth( columnIndex );
    if ( newWidth != oldWidth )
    {
      m_bodyWidthCached = m_bodyWidthCached - oldWidth + newWidth;
      if ( newWidth == m_columnDefaultWidth )
        m_columnIndexWidthExceptions.remove( columnIndex );
      else
        m_columnIndexWidthExceptions.put( columnIndex, newWidth );

      int size = m_columnPosXStartCached.size();
      int pos = getColumnPositionFromIndex( columnIndex );
      if ( pos < size )
        m_columnPosXStartCached.subList( pos, size ).clear();
    }
  }

  /************************************** getRowIndexHeight **************************************/
  public int getRowIndexHeight( int rowIndex )
  {
    // return height for row index
    if ( rowIndex == HEADER )
      return getColumnHeaderHeight();

    int height = m_rowIndexHeightExceptions.getOrDefault( rowIndex, m_rowDefaultHeight );
    if ( height < 0 )
      return 0; // -ve means row hidden, so return zero

    return height;
  }

  /************************************** setRowIndexHeight **************************************/
  public void setRowIndexHeight( int rowIndex, int newHeight )
  {
    // height should not be below minimum
    if ( newHeight < m_rowMinimumHeight )
      newHeight = m_rowMinimumHeight;

    // if height is changed, update body height and update height exception
    int oldHeight = getRowIndexHeight( rowIndex );
    if ( newHeight != oldHeight )
    {
      m_bodyHeightCached = m_bodyHeightCached - oldHeight + newHeight;
      if ( newHeight == m_rowDefaultHeight )
        m_rowIndexHeightExceptions.remove( rowIndex );
      else
        m_rowIndexHeightExceptions.put( rowIndex, newHeight );

      int size = m_rowPosYStartCached.size();
      int pos = getRowPositionFromIndex( rowIndex );
      if ( pos < size )
        m_rowPosYStartCached.subList( pos, size ).clear();
    }
  }

  /**************************************** hideRowIndex *****************************************/
  public void hideRowIndex( int rowIndex )
  {
    // if row not already hidden, set height exception and update body height
    int oldHeight = m_rowIndexHeightExceptions.getOrDefault( rowIndex, m_rowDefaultHeight );
    if ( oldHeight > 0 )
    {
      m_rowIndexHeightExceptions.put( rowIndex, -oldHeight );
      m_bodyHeightCached = m_bodyHeightCached - oldHeight;

      int size = m_rowPosYStartCached.size();
      int pos = getRowPositionFromIndex( rowIndex );
      if ( pos < size )
        m_rowPosYStartCached.subList( pos, size ).clear();
    }
  }

  /**************************************** showRowIndex *****************************************/
  public void showRowIndex( int rowIndex )
  {
    // if row hidden, update height exception and update body height
    int oldHeight = m_rowIndexHeightExceptions.getOrDefault( rowIndex, m_rowDefaultHeight );
    if ( oldHeight < 0 )
    {
      if ( oldHeight == -m_rowDefaultHeight )
        m_rowIndexHeightExceptions.remove( rowIndex );
      else
        m_rowIndexHeightExceptions.put( rowIndex, -oldHeight );

      m_bodyHeightCached = m_bodyHeightCached - oldHeight;

      int size = m_rowPosYStartCached.size();
      int pos = getRowPositionFromIndex( rowIndex );
      if ( pos < size )
        m_rowPosYStartCached.subList( pos, size ).clear();
    }
  }

  /*********************************** getColumnPositionAtX **************************************/
  public int getColumnPositionAtX( int x )
  {
    // determine if row header
    if ( x >= 0 && x < getRowHeaderWidth() )
      return HEADER;

    // adjust x for horizontal offset due to scroll bar and row header
    x += getXOffset() - getRowHeaderWidth();

    // if left of table body return LEFT
    if ( x < 0 )
      return LEFT;

    // if right of table body return RIGHT
    if ( x >= m_view.getBodyWidth() )
      return RIGHT;

    // column zero always starts at adjusted zero x
    if ( m_columnPosXStartCached.isEmpty() )
      m_columnPosXStartCached.add( 0 );

    // if x past cache end then add new cached values until column position found
    int size = m_columnPosXStartCached.size();
    int end = m_columnPosXStartCached.get( size - 1 );
    if ( x >= end )
      for ( int columnPos = size - 1; columnPos < m_data.getColumnCount(); columnPos++ )
      {
        end += getColumnIndexWidth( getColumnIndexFromPosition( columnPos ) );
        m_columnPosXStartCached.add( end );
        if ( x < end )
          return columnPos;
      }

    // x in cache so find by binary searching
    int startPos = 0;
    int endPos = size - 1;
    int columnPos = ( endPos + startPos ) / 2;
    while ( x < m_columnPosXStartCached.get( columnPos ) || x >= m_columnPosXStartCached.get( columnPos + 1 ) )
    {
      if ( x < m_columnPosXStartCached.get( columnPos ) )
        endPos = columnPos;
      else
        startPos = columnPos;

      columnPos = ( endPos + startPos ) / 2;
    }

    return columnPos;
  }

  /************************************* getRowPositionAtY ***************************************/
  public int getRowPositionAtY( int y )
  {
    // determine if column header
    if ( y >= 0 && y < getColumnHeaderHeight() )
      return HEADER;

    // adjust y for vertical offset due to scroll bar and column header
    y += getYOffset() - getColumnHeaderHeight();

    // if above table body return ABOVE
    if ( y < 0 )
      return ABOVE;

    // if below table body return BELOW
    if ( y >= m_view.getBodyHeight() )
      return BELOW;

    // row zero always starts at adjusted zero y
    if ( m_rowPosYStartCached.isEmpty() )
      m_rowPosYStartCached.add( 0 );

    // if y past cache end then add new cached values until row position found
    int size = m_rowPosYStartCached.size();
    int end = m_rowPosYStartCached.get( size - 1 );
    if ( y >= end )
      for ( int rowPos = size - 1; rowPos < m_data.getRowCount(); rowPos++ )
      {
        end += getRowIndexHeight( getRowIndexFromPosition( rowPos ) );
        m_rowPosYStartCached.add( end );
        if ( y < end )
          return rowPos;
      }

    // y in cache so find by binary searching
    int startPos = 0;
    int endPos = size - 1;
    int rowPos = ( endPos + startPos ) / 2;
    while ( y < m_rowPosYStartCached.get( rowPos ) || y >= m_rowPosYStartCached.get( rowPos + 1 ) )
    {
      if ( y < m_rowPosYStartCached.get( rowPos ) )
        endPos = rowPos;
      else
        startPos = rowPos;

      rowPos = ( endPos + startPos ) / 2;
    }

    return rowPos;
  }

  /*********************************** getColumnPositionXStart ***********************************/
  public int getColumnPositionXStart( int columnPos )
  {
    // return column pos start x
    Utils.trace( "NOT YET IMPLEMETED" );

    return INVALID;
  }

  /************************************ getRowPositionYStart *************************************/
  public int getRowPositionYStart( int rowPos )
  {
    // return row pos start y
    Utils.trace( "NOT YET IMPLEMETED" );

    return INVALID;
  }

  /************************************ getColumnIndexXStart *************************************/
  public int getColumnIndexXStart( int columnIndex )
  {
    // return column index start x
    if ( columnIndex == HEADER )
      return 0;

    int x = getRowHeaderWidth() - getXOffset();
    int columnPos = getColumnPositionFromIndex( columnIndex );
    for ( int pos = 0; pos < columnPos; pos++ )
      x += getColumnIndexWidth( getColumnIndexFromPosition( pos ) );

    return x;
  }

  /************************************** getRowIndexYStart **************************************/
  public int getRowIndexYStart( int rowIndex )
  {
    // return row index start y
    if ( rowIndex == HEADER )
      return 0;

    int y = getColumnHeaderHeight() - getYOffset();
    int rowPos = getRowPositionFromIndex( rowIndex );
    for ( int pos = 0; pos < rowPos; pos++ )
      y += getRowIndexHeight( getRowIndexFromPosition( pos ) );

    return y;
  }

}
