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
  private int                   m_defaultColumnWidth    = 100;
  private int                   m_defaultRowHeight      = 20;
  private int                   m_minimumColumnWidth    = 40;
  private int                   m_minimumRowHeight      = 17;
  private int                   m_vHeaderWidth          = 30;
  private int                   m_hHeaderHeight         = 20;

  // all columns have default widths, and rows default heights, except those in these maps, -ve means hidden
  private Map<Integer, Integer> m_columnWidthExceptions = new HashMap<Integer, Integer>();
  private Map<Integer, Integer> m_rowHeightExceptions   = new HashMap<Integer, Integer>();

  private int                   m_bodyWidthCached       = INVALID;                        // body cells total width (excludes header)
  private int                   m_bodyHeightCached      = INVALID;                        // body cells total height (excludes header)

  /*************************************** getTableWidth *****************************************/
  public int getTableWidth()
  {
    // return width in pixels of all the visible table columns + vertical header
    if ( m_bodyWidthCached == INVALID )
      m_bodyWidthCached = calculateBodyWidth();

    return m_bodyWidthCached + m_vHeaderWidth;
  }

  /************************************** getTableHeight *****************************************/
  public int getTableHeight()
  {
    // return height in pixels of all the visible table rows + horizontal header
    if ( m_bodyHeightCached == INVALID )
      m_bodyHeightCached = calculateBodyHeight();

    return m_bodyHeightCached + m_hHeaderHeight;
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

    return bodyWidth + ( columnCount - exceptionsCount ) * m_defaultColumnWidth;
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

    return bodyHeight + ( rowCount - exceptionsCount ) * m_defaultRowHeight;
  }

  /*********************************** getVerticalHeaderWidth ************************************/
  public int getVerticalHeaderWidth()
  {
    // return table vertical header width
    return m_vHeaderWidth;
  }

  /*********************************** setVerticalHeaderWidth ************************************/
  public void setVerticalHeaderWidth( int width )
  {
    // set vertical header width
    m_vHeaderWidth = width;
  }

  /********************************** getHorizontalHeaderHeight **********************************/
  public int getHorizontalHeaderHeight()
  {
    // return table horizontal header height
    return m_hHeaderHeight;
  }

  /********************************** setHorizontalHeaderHeight **********************************/
  public void setHorizontalHeaderHeight( int height )
  {
    // set horizontal header height
    m_hHeaderHeight = height;
  }

  /************************************ setDefaultColumnWidth ************************************/
  public void setDefaultColumnWidth( int width )
  {
    m_defaultColumnWidth = width;
    m_bodyWidthCached = INVALID;
  }

  /************************************* setDefaultRowHeight *************************************/
  public void setDefaultRowHeight( int height )
  {
    m_defaultRowHeight = height;
    m_bodyHeightCached = INVALID;
  }

  /************************************* getColumnIndexWidth *************************************/
  public int getColumnIndexWidth( int columnIndex )
  {
    // return width for column index
    if ( columnIndex < 0 || columnIndex >= m_data.getColumnCount() )
      return INVALID;

    int width = m_columnWidthExceptions.getOrDefault( columnIndex, m_defaultColumnWidth );
    if ( width < 0 )
      return 0; // -ve means column hidden, so return zero

    return width;
  }

  /*********************************** setColumnIndexWidth ***************************************/
  public void setColumnIndexWidth( int columnIndex, int newWidth )
  {
    // width should not be below minimum
    if ( newWidth < m_minimumColumnWidth )
      newWidth = m_minimumColumnWidth;

    // if width is changed, update body width and update width exception
    int oldWidth = getColumnIndexWidth( columnIndex );
    if ( newWidth != oldWidth )
    {
      m_bodyWidthCached = m_bodyWidthCached - oldWidth + newWidth;
      if ( newWidth == m_defaultColumnWidth )
        m_columnWidthExceptions.remove( columnIndex );
      else
        m_columnWidthExceptions.put( columnIndex, newWidth );
    }
  }

  /************************************** getRowIndexHeight **************************************/
  public int getRowIndexHeight( int rowIndex )
  {
    // return height for row index
    if ( rowIndex < 0 || rowIndex >= m_data.getRowCount() )
      return INVALID;

    int height = m_rowHeightExceptions.getOrDefault( rowIndex, m_defaultRowHeight );
    if ( height < 0 )
      return 0; // -ve means row hidden, so return zero

    return height;
  }

  /************************************** setRowIndexHeight **************************************/
  public void setRowIndexHeight( int rowIndex, int newHeight )
  {
    // height should not be below minimum
    if ( newHeight < m_minimumRowHeight )
      newHeight = m_minimumRowHeight;

    // if height is changed, update body height and update height exception
    int oldHeight = getRowIndexHeight( rowIndex );
    if ( newHeight != oldHeight )
    {
      m_bodyHeightCached = m_bodyHeightCached - oldHeight + newHeight;
      if ( newHeight == m_defaultRowHeight )
        m_rowHeightExceptions.remove( rowIndex );
      else
        m_rowHeightExceptions.put( rowIndex, newHeight );
    }
  }

  /******************************************* hideRow *******************************************/
  public void hideRow( int rowIndex )
  {
    // if row not already hidden, set height exception and update body height
    int oldHeight = m_rowHeightExceptions.getOrDefault( rowIndex, m_defaultRowHeight );
    if ( oldHeight > 0 )
    {
      m_rowHeightExceptions.put( rowIndex, -oldHeight );
      m_bodyHeightCached = m_bodyHeightCached - oldHeight;
    }
  }

  /******************************************* showRow *******************************************/
  public void showRow( int rowIndex )
  {
    // if row hidden, update height exception and update body height
    int oldHeight = m_rowHeightExceptions.getOrDefault( rowIndex, m_defaultRowHeight );
    if ( oldHeight < 0 )
    {
      if ( oldHeight == -m_defaultRowHeight )
        m_rowHeightExceptions.remove( rowIndex );
      else
        m_rowHeightExceptions.put( rowIndex, -oldHeight );

      m_bodyHeightCached = m_bodyHeightCached - oldHeight;
    }
  }

}
