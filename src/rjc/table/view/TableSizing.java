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

import java.util.HashMap;
import java.util.Map;

/*************************************************************************************************/
/****************** Table header + cell column and row sizing including hiding *******************/
/*************************************************************************************************/

public class TableSizing extends TablePosition
{
  private int                   m_columnDefaultWidth    = 100;
  private int                   m_rowDefaultHeight      = 20;
  private int                   m_columnMinimumWidth    = 40;
  private int                   m_rowMinimumHeight      = 17;
  private int                   m_rowHeaderWidth        = 30;
  private int                   m_columnHeaderHeight    = 20;

  // all columns have default widths, and rows default heights, except those in these maps, -ve means hidden
  private Map<Integer, Integer> m_columnWidthExceptions = new HashMap<Integer, Integer>();
  private Map<Integer, Integer> m_rowHeightExceptions   = new HashMap<Integer, Integer>();

  private int                   m_bodyWidthCached       = INVALID;                        // body cells total width (excludes header)
  private int                   m_bodyHeightCached      = INVALID;                        // body cells total height (excludes header)

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
      m_bodyWidthCached = calculateBodyWidth();

    return m_bodyWidthCached;
  }

  /************************************** getBodyHeight ******************************************/
  public int getBodyHeight()
  {
    // return height in pixels of all the visible table body rows only
    if ( m_bodyHeightCached == INVALID )
      m_bodyHeightCached = calculateBodyHeight();

    return m_bodyHeightCached;
  }

  /************************************ calculateBodyWidth ***************************************/
  private int calculateBodyWidth()
  {
    // calculate width of table body cell columns
    int exceptionsCount = 0;
    int columnCount = m_data.getColumnCount();
    int bodyWidth = 0;

    for ( int columnIndex : m_columnWidthExceptions.keySet() )
    {
      if ( columnIndex < columnCount )
      {
        exceptionsCount++;
        int width = m_columnWidthExceptions.get( columnIndex );
        if ( width > 0 )
          bodyWidth += width;
      }
      else
        m_columnWidthExceptions.remove( columnIndex );
    }

    return bodyWidth + ( columnCount - exceptionsCount ) * m_columnDefaultWidth;
  }

  /************************************ calculateBodyHeight **************************************/
  private int calculateBodyHeight()
  {
    // calculate height of table body cell rows
    int exceptionsCount = 0;
    int rowCount = m_data.getRowCount();
    int bodyHeight = 0;

    for ( int rowIndex : m_rowHeightExceptions.keySet() )
    {
      if ( rowIndex < rowCount )
      {
        exceptionsCount++;
        int height = m_rowHeightExceptions.get( rowIndex );
        if ( height > 0 )
          bodyHeight += height;
      }
      else
        m_rowHeightExceptions.remove( rowIndex );
    }

    return bodyHeight + ( rowCount - exceptionsCount ) * m_rowDefaultHeight;
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

  /************************************ setColumnDefaultWidth ************************************/
  public void setColumnDefaultWidth( int width )
  {
    m_columnDefaultWidth = width;
    m_bodyWidthCached = INVALID;
  }

  /************************************* setRowDefaultHeight *************************************/
  public void setRowDefaultHeight( int height )
  {
    m_rowDefaultHeight = height;
    m_bodyHeightCached = INVALID;
  }

  /************************************* getColumnIndexWidth *************************************/
  public int getColumnIndexWidth( int columnIndex )
  {
    // return width for column index
    if ( columnIndex == HEADER )
      return getRowHeaderWidth();

    int width = m_columnWidthExceptions.getOrDefault( columnIndex, m_columnDefaultWidth );
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
        m_columnWidthExceptions.remove( columnIndex );
      else
        m_columnWidthExceptions.put( columnIndex, newWidth );
    }
  }

  /************************************** getRowIndexHeight **************************************/
  public int getRowIndexHeight( int rowIndex )
  {
    // return height for row index
    if ( rowIndex == HEADER )
      return getColumnHeaderHeight();

    int height = m_rowHeightExceptions.getOrDefault( rowIndex, m_rowDefaultHeight );
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
        m_rowHeightExceptions.remove( rowIndex );
      else
        m_rowHeightExceptions.put( rowIndex, newHeight );
    }
  }

  /**************************************** hideRowIndex *****************************************/
  public void hideRowIndex( int rowIndex )
  {
    // if row not already hidden, set height exception and update body height
    int oldHeight = m_rowHeightExceptions.getOrDefault( rowIndex, m_rowDefaultHeight );
    if ( oldHeight > 0 )
    {
      m_rowHeightExceptions.put( rowIndex, -oldHeight );
      m_bodyHeightCached = m_bodyHeightCached - oldHeight;
    }
  }

  /**************************************** showRowIndex *****************************************/
  public void showRowIndex( int rowIndex )
  {
    // if row hidden, update height exception and update body height
    int oldHeight = m_rowHeightExceptions.getOrDefault( rowIndex, m_rowDefaultHeight );
    if ( oldHeight < 0 )
    {
      if ( oldHeight == -m_rowDefaultHeight )
        m_rowHeightExceptions.remove( rowIndex );
      else
        m_rowHeightExceptions.put( rowIndex, -oldHeight );

      m_bodyHeightCached = m_bodyHeightCached - oldHeight;
    }
  }

  /*********************************** getColumnPositionAtX **************************************/
  public int getColumnPositionAtX( int x )
  {
    // adjust x for horizontal offset due to scroll bar and row header
    x += getXOffset() - getRowHeaderWidth();

    // if left of table body return Integer.MIN_VALUE
    if ( x < 0 )
      return Integer.MIN_VALUE;

    // if right of table body return Integer.MAX_VALUE
    if ( x > m_view.getBodyWidth() )
      return Integer.MAX_VALUE;

    // determine column position
    for ( int pos = 0; pos < m_data.getColumnCount(); pos++ )
    {
      int width = getColumnIndexWidth( getColumnIndexFromPosition( pos ) );
      if ( x <= width )
        return pos;
      x -= width;
    }

    throw new ArithmeticException( "Shouldn't be able to get here " + x + " " + m_view.getBodyWidth() );
  }

  /************************************* getRowPositionAtY ***************************************/
  public int getRowPositionAtY( int y )
  {
    // adjust y for vertical offset due to scroll bar and column header
    y += getYOffset() - getColumnHeaderHeight();

    // if above table body return Integer.MIN_VALUE
    if ( y < 0 )
      return Integer.MIN_VALUE;

    // if below table body return Integer.MAX_VALUE
    if ( y > m_view.getBodyHeight() )
      return Integer.MAX_VALUE;

    // determine row position
    for ( int pos = 0; pos < m_data.getRowCount(); pos++ )
    {
      int height = getRowIndexHeight( getRowIndexFromPosition( pos ) );
      if ( y <= height )
        return pos;
      y -= height;
    }

    throw new ArithmeticException( "Shouldn't be able to get here " + y + " " + m_view.getBodyHeight() );
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
