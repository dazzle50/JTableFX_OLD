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

import rjc.table.data.TableData;

/*************************************************************************************************/
/************************ Table view display with canvas and scroll bars *************************/
/*************************************************************************************************/

public class TableDisplay extends TableParent
{
  protected TableView        m_view;           // shortcut to table view
  protected TableData        m_data;           // shortcut to table data

  protected TableScrollBar   m_vScrollBar;     // vertical scroll bar
  protected TableScrollBar   m_hScrollBar;     // horizontal scroll bar
  protected TableCanvas      m_canvas;         // table canvas

  protected static final int INVALID = -2;
  protected static final int HEADER  = -1;

  private static double      MAXSIZE = 999999; // max valid size for table

  /******************************************* resize ********************************************/
  @Override
  public void resize( double width, double height )
  {
    // do nothing if no change in size
    if ( (int) width == getWidth() && (int) height == getHeight() )
      return;

    // do nothing if size is larger than max allowed
    if ( width > MAXSIZE || height > MAXSIZE )
      return;

    // resize parent and re-layout canvas and scroll bars
    super.resize( width, height );
    layoutDisplay();
  }

  /**************************************** requestFocus *****************************************/
  @Override
  public void requestFocus()
  {
    // setting focus on table should set focus on canvas
    m_canvas.requestFocus();
  }

  /**************************************** layoutDisplay ****************************************/
  public void layoutDisplay()
  {
    // determine which scroll-bars should be visible
    boolean isVSBvisible = getHeight() < m_view.getTableHeight();
    int visibleWidth = isVSBvisible ? getWidth() - (int) m_vScrollBar.getWidth() : getWidth();
    boolean isHSBvisible = visibleWidth < m_view.getTableWidth();
    int visibleHeight = isHSBvisible ? getHeight() - (int) m_hScrollBar.getHeight() : getHeight();
    isVSBvisible = visibleHeight < m_view.getTableHeight();
    visibleWidth = isVSBvisible ? getWidth() - (int) m_vScrollBar.getWidth() : getWidth();

    // update vertical scroll bar
    m_vScrollBar.setVisible( isVSBvisible );
    if ( isVSBvisible )
    {
      m_vScrollBar.setPrefHeight( visibleHeight );
      m_vScrollBar.relocate( getWidth() - m_vScrollBar.getWidth(), 0.0 );

      double max = m_view.getTableHeight() - visibleHeight;
      m_vScrollBar.setMax( max );
      m_vScrollBar.setVisibleAmount( max * visibleHeight / m_view.getTableHeight() );
      m_vScrollBar.setBlockIncrement( visibleHeight - m_view.getHorizontalHeaderHeight() );
    }

    // update horizontal scroll bar
    m_hScrollBar.setVisible( isHSBvisible );
    if ( isHSBvisible )
    {
      m_hScrollBar.setPrefWidth( visibleWidth );
      m_hScrollBar.relocate( 0.0, getHeight() - m_hScrollBar.getHeight() );

      double max = m_view.getTableWidth() - visibleWidth;
      m_hScrollBar.setMax( max );
      m_hScrollBar.setVisibleAmount( max * visibleWidth / m_view.getTableWidth() );
      m_hScrollBar.setBlockIncrement( visibleWidth - m_view.getVerticalHeaderWidth() );
    }

    // update canvas
    m_canvas.setWidth( visibleWidth );
    m_canvas.setHeight( visibleHeight );
  }

  /******************************************* redraw ********************************************/
  public void redraw()
  {
    // request complete redraw of table canvas
    m_canvas.redraw();
  }

  /************************************** getColumnPosAtX ****************************************/
  public int getColumnPosAtX( int x )
  {
    // TODO ##################################
    return 0;
  }

  /**************************************** getRowPosAtY *****************************************/
  public int getRowPosAtY( int y )
  {
    // TODO ##################################
    return 0;
  }

  /***************************************** getXOffset ******************************************/
  public int getXOffset()
  {
    // return table horizontal offset due to scroll bar
    return (int) m_hScrollBar.getValue();
  }

  /***************************************** getYOffset ******************************************/
  public int getYOffset()
  {
    // return table vertical offset due to scroll bar
    return (int) m_vScrollBar.getValue();
  }

}
