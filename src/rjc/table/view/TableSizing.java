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

import rjc.table.Utils;

/*************************************************************************************************/
/****************** Table header + cell column and row sizing including hiding *******************/
/*************************************************************************************************/

public class TableSizing extends TablePosition
{
  private int                   m_columnDefaultWidth         = 100;
  private int                   m_rowDefaultHeight           = 20;
  private int                   m_columnMinimumWidth         = 30;
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
      if ( newWidth == m_columnDefaultWidth )
        m_columnIndexWidthExceptions.remove( columnIndex );
      else
        m_columnIndexWidthExceptions.put( columnIndex, newWidth );

      int size = m_columnPosXStartCached.size();
      if ( size > 0 )
      {
        m_bodyWidthCached = m_bodyWidthCached - oldWidth + newWidth;
        int pos = getColumnPositionFromIndex( columnIndex );
        if ( pos < size )
          m_columnPosXStartCached.subList( pos, size ).clear();
      }
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
      if ( newHeight == m_rowDefaultHeight )
        m_rowIndexHeightExceptions.remove( rowIndex );
      else
        m_rowIndexHeightExceptions.put( rowIndex, newHeight );

      int size = m_rowPosYStartCached.size();
      if ( size > 0 )
      {
        m_bodyHeightCached = m_bodyHeightCached - oldHeight + newHeight;
        int pos = getRowPositionFromIndex( rowIndex );
        if ( pos < size && size > 0 )
          m_rowPosYStartCached.subList( pos, size ).clear();
      }
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
    // adjust x for horizontal offset due to scroll bar and row header
    x += getXOffset() - getRowHeaderWidth();

    // if left of table body return LEFT
    if ( x < 0 )
      return LEFT;

    // if right of table body return RIGHT
    if ( x >= getBodyWidth() )
      return RIGHT;

    // make sure x start cache is populated by calling getColumnPositionXStart
    getColumnPositionXStart( m_data.getColumnCount() - 1 );

    // find columnPos by binary search of cache
    int startPos = 0;
    int endPos = m_columnPosXStartCached.size();
    while ( startPos != endPos )
    {
      int columnPos = ( endPos + startPos ) / 2;
      if ( m_columnPosXStartCached.get( columnPos ) <= x )
        startPos = columnPos + 1;
      else
        endPos = columnPos;
    }

    return startPos - 1;
  }

  /************************************* getRowPositionAtY ***************************************/
  public int getRowPositionAtY( int y )
  {
    // adjust y for vertical offset due to scroll bar and column header
    y += getYOffset() - getColumnHeaderHeight();

    // if above table body return ABOVE
    if ( y < 0 )
      return ABOVE;

    // if below table body return BELOW
    if ( y >= getBodyHeight() )
      return BELOW;

    // make sure y start cache is populated by calling getRowPositionYStart
    getRowPositionYStart( m_data.getRowCount() - 1 );

    // find rowPos by binary search of cache
    int startPos = 0;
    int endPos = m_rowPosYStartCached.size();
    while ( startPos != endPos )
    {
      int rowPos = ( endPos + startPos ) / 2;
      if ( m_rowPosYStartCached.get( rowPos ) <= y )
        startPos = rowPos + 1;
      else
        endPos = rowPos;
    }

    return startPos - 1;
  }

  /*********************************** getColumnPositionXStart ***********************************/
  public int getColumnPositionXStart( int columnPos )
  {
    // return column pos start x
    try
    {
      if ( columnPos < 0 )
        return -getXOffset() + getRowHeaderWidth();
      if ( columnPos >= m_data.getColumnCount() )
        return getBodyWidth() - getXOffset() + getRowHeaderWidth();
      return m_columnPosXStartCached.get( columnPos ) - getXOffset() + getRowHeaderWidth();
    }
    catch ( IndexOutOfBoundsException exception )
    {
      // cache does not yet have value so build cache
      int count = m_data.getColumnCount();
      m_columnPosXStartCached.ensureCapacity( count + 1 );

      // column zero always starts at adjusted zero x
      if ( m_columnPosXStartCached.isEmpty() )
        m_columnPosXStartCached.add( 0 );

      int size = m_columnPosXStartCached.size();
      int x = m_columnPosXStartCached.get( size - 1 );
      count = Math.min( columnPos + 100, count );
      for ( int pos = size - 1; pos < count; pos++ )
      {
        x += getColumnIndexWidth( getColumnIndexFromPosition( pos ) );
        m_columnPosXStartCached.add( x );
      }

      return m_columnPosXStartCached.get( columnPos ) - getXOffset() + getRowHeaderWidth();
    }

  }

  /************************************ getRowPositionYStart *************************************/
  public int getRowPositionYStart( int rowPos )
  {
    // return row pos start y
    try
    {
      if ( rowPos < 0 )
        return -getYOffset() + getColumnHeaderHeight();
      if ( rowPos >= m_data.getRowCount() )
        return getBodyHeight() - getYOffset() + getColumnHeaderHeight();
      return m_rowPosYStartCached.get( rowPos ) - getYOffset() + getColumnHeaderHeight();
    }
    catch ( IndexOutOfBoundsException exception )
    {
      // cache does not yet have value so build cache
      int count = m_data.getRowCount();
      m_rowPosYStartCached.ensureCapacity( count + 1 );

      // row zero always starts at adjusted zero y
      if ( m_rowPosYStartCached.isEmpty() )
        m_rowPosYStartCached.add( 0 );

      int size = m_rowPosYStartCached.size();
      int y = m_rowPosYStartCached.get( size - 1 );
      count = Math.min( rowPos + 100, count );
      for ( int pos = size - 1; pos < count; pos++ )
      {
        y += getRowIndexHeight( getRowIndexFromPosition( pos ) );
        m_rowPosYStartCached.add( y );
      }

      return m_rowPosYStartCached.get( rowPos ) - getYOffset() + getColumnHeaderHeight();
    }
  }

  /************************************ getColumnIndexXStart *************************************/
  public int getColumnIndexXStart( int columnIndex )
  {
    // return column index start x
    if ( columnIndex == HEADER )
      return 0;

    int columnPos = getColumnPositionFromIndex( columnIndex );
    return getColumnPositionXStart( columnPos );
  }

  /************************************** getRowIndexYStart **************************************/
  public int getRowIndexYStart( int rowIndex )
  {
    // return row index start y
    if ( rowIndex == HEADER )
      return 0;

    int rowPos = getRowPositionFromIndex( rowIndex );
    return getRowPositionYStart( rowPos );
  }

  /**************************************** getVisibleUp *****************************************/
  public int getVisibleUp( int rowPos )
  {
    // return non-hidden row above, or if none, the top
    int above = rowPos - 1;
    while ( isRowPositionHidden( above ) )
      above--;

    if ( above >= 0 )
      return above;

    return getVisibleTop();
  }

  /*************************************** getVisibleDown ****************************************/
  public int getVisibleDown( int rowPos )
  {
    // return non-hidden row below, or if none, the bottom
    int below = rowPos + 1;
    while ( isRowPositionHidden( below ) )
      below++;

    if ( below < m_data.getRowCount() )
      return below;

    return getVisibleBottom();
  }

  /**************************************** getVisibleTop ****************************************/
  public int getVisibleTop()
  {
    // return top non-hidden row position
    return getVisibleDown( HEADER );
  }

  /************************************** getVisibleBottom ***************************************/
  public int getVisibleBottom()
  {
    // return bottom non-hidden row position
    return getVisibleUp( m_data.getRowCount() );
  }

  /*************************************** getVisibleLeft ****************************************/
  public int getVisibleLeft( int columnPos )
  {
    // return non-hidden column-position to left, or if none, the first
    int left = columnPos - 1;
    while ( isColumnPositionHidden( left ) )
      left--;

    if ( left >= 0 )
      return left;

    return getVisibleFirst();
  }

  /*************************************** getVisibleRight ***************************************/
  public int getVisibleRight( int columnPos )
  {
    // return non-hidden column-position to right, or if none, the last
    int right = columnPos + 1;
    while ( isColumnPositionHidden( right ) )
      right++;

    if ( right < m_data.getColumnCount() )
      return right;

    return getVisibleLast();
  }

  /*************************************** getVisibleFirst ***************************************/
  public int getVisibleFirst()
  {
    // return first non-hidden column position
    return getVisibleRight( HEADER );
  }

  /*************************************** getVisibleLast ****************************************/
  public int getVisibleLast()
  {
    // return last non-hidden column position
    return getVisibleLeft( m_data.getColumnCount() );
  }

  /************************************** ensureColumnShown **************************************/
  public int ensureColumnShown( int columnPos )
  {
    // if column is hidden, return next non-hidden column, and scroll view so visible
    if ( columnPos < 0 )
      columnPos = getVisibleFirst();
    else if ( columnPos >= m_data.getColumnCount() || isColumnPositionHidden( columnPos ) )
      columnPos = getVisibleRight( columnPos );

    // determine if any horizontal scrolling needed
    int leftEdge = getColumnPositionXStart( columnPos ) - getRowHeaderWidth();
    if ( leftEdge < 0 )
      animateToXOffset( getXOffset() + leftEdge );
    else
    {
      int rightEdge = leftEdge + getColumnIndexWidth( getColumnIndexFromPosition( columnPos ) ) + getRowHeaderWidth();
      if ( rightEdge > getCanvasWidth() )
        animateToXOffset( getXOffset() + rightEdge - getCanvasWidth() );
    }

    return columnPos;
  }

  /*************************************** ensureRowShown ****************************************/
  public int ensureRowShown( int rowPos )
  {
    // if row is hidden, return next non-hidden row, and scroll view so visible
    if ( rowPos < 0 )
      rowPos = getVisibleTop();
    else if ( rowPos >= m_data.getRowCount() || isRowPositionHidden( rowPos ) )
      rowPos = getVisibleDown( rowPos );

    // determine if any vertical scrolling needed
    int topEdge = getRowPositionYStart( rowPos ) - getColumnHeaderHeight();
    if ( topEdge < 0 )
      animateToYOffset( getYOffset() + topEdge );
    else
    {
      int bottomEdge = topEdge + getRowIndexHeight( getRowIndexFromPosition( rowPos ) ) + getColumnHeaderHeight();
      if ( bottomEdge > getCanvasHeight() )
        animateToYOffset( getYOffset() + bottomEdge - getCanvasHeight() );
    }

    return rowPos;
  }

  /*********************************** isColumnPositionHidden ************************************/
  public boolean isColumnPositionHidden( int columnPos )
  {
    // return if column position is hidden (out of bound columns are not hidden)
    return getColumnIndexWidth( getColumnIndexFromPosition( columnPos ) ) <= 0;
  }

  /************************************ isRowPositionHidden **************************************/
  public boolean isRowPositionHidden( int rowPos )
  {
    // return if row position is hidden (out of bound rows are not hidden)
    return getRowIndexHeight( getRowIndexFromPosition( rowPos ) ) <= 0;
  }

  /************************************** autofitRowHeight ***************************************/
  public void autofitRowHeight( int rowPos )
  {
    // autofit column width to avoid text ellipsis
    Utils.trace( "DOUBLE CLICK to autofit row position " + rowPos );
  }

  /************************************* autofitColumnWidth **************************************/
  public void autofitColumnWidth( int columnPos )
  {
    // autofit row height to avoid text ellipsis
    Utils.trace( "DOUBLE CLICK to autofit column position " + columnPos );
  }

}
