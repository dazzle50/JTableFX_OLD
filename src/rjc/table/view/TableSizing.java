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

/*************************************************************************************************/
/****************** Table header + cell column and row sizing including hiding *******************/
/*************************************************************************************************/

public class TableSizing extends TablePosition
{
  private int              m_defaultColumnWidth = 100;
  private int              m_defaultRowHeight   = 20;
  private int              m_minimumColumnWidth = 40;
  private int              m_minimumRowHeight   = 17;
  private int              m_vHeaderWidth       = 30;
  private int              m_hHeaderHeight      = 20;

  private static final int INVALID              = -1;

  private int              m_bodyWidthCached    = INVALID; // body cells total width (excludes header)
  private int              m_bodyHeightCached   = INVALID; // body cells total height (excludes header)

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

  /************************************* calculateBodyWidth **************************************/
  private int calculateBodyWidth()
  {
    // TODO Auto-generated method stub #######################################################################
    return 500;
  }

  /************************************ calculateBodyHeight **************************************/
  private int calculateBodyHeight()
  {
    // TODO Auto-generated method stub #######################################################################
    return 400;
  }

  /*********************************** getVerticalHeaderWidth ************************************/
  public int getVerticalHeaderWidth()
  {
    // return table vertical header width
    return m_vHeaderWidth;
  }

  /********************************** getHorizontalHeaderHeight **********************************/
  public int getHorizontalHeaderHeight()
  {
    // return table horizontal header height
    return m_hHeaderHeight;
  }

}
