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
  protected TableView        m_view;                      // shortcut to table view
  protected TableData        m_data;                      // shortcut to table data

  protected TableScrollBar   m_vScrollBar;                // vertical scroll bar
  protected TableScrollBar   m_hScrollBar;                // horizontal scroll bar
  protected TableCanvas      m_canvas;                    // table canvas

  protected static final int INVALID = -2;                // when int value is invalid
  protected static final int HEADER  = -1;                // when column or row refers to headers

  protected static final int LEFT    = Integer.MIN_VALUE; // column index or position left of table body
  protected static final int RIGHT   = Integer.MAX_VALUE; // column index or position right of table body
  protected static final int ABOVE   = Integer.MIN_VALUE; // row index or position above table body
  protected static final int BELOW   = Integer.MAX_VALUE; // row index or position below table body

  private static double      MAXSIZE = 999999;            // max valid size for table

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

  /*************************************** isTableFocused ****************************************/
  public boolean isTableFocused()
  {
    // return if table canvas has focus
    return m_canvas.isFocused();
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
      m_vScrollBar.setBlockIncrement( visibleHeight - m_view.getColumnHeaderHeight() );

      if ( m_vScrollBar.getValue() > max )
        m_vScrollBar.setValue( max );
    }
    else
      m_vScrollBar.setValue( 0.0 );

    // update horizontal scroll bar
    m_hScrollBar.setVisible( isHSBvisible );
    if ( isHSBvisible )
    {
      m_hScrollBar.setPrefWidth( visibleWidth );
      m_hScrollBar.relocate( 0.0, getHeight() - m_hScrollBar.getHeight() );

      double max = m_view.getTableWidth() - visibleWidth;
      m_hScrollBar.setMax( max );
      m_hScrollBar.setVisibleAmount( max * visibleWidth / m_view.getTableWidth() );
      m_hScrollBar.setBlockIncrement( visibleWidth - m_view.getRowHeaderWidth() );

      if ( m_hScrollBar.getValue() > max )
        m_hScrollBar.setValue( max );
    }
    else
      m_hScrollBar.setValue( 0.0 );

    // update canvas
    m_canvas.setWidth( visibleWidth );
    m_canvas.setHeight( visibleHeight );
  }

  /******************************************* redraw ********************************************/
  public void redraw()
  {
    // request complete redraw of table canvas (-1 and +1 to ensure canvas graphics context does a reset)
    widthChange( -1, (int) getWidth() + 1 );
  }

  /***************************************** widthChange *****************************************/
  public void widthChange( int oldW, int newW )
  {
    // only need to draw if new width is larger than old width
    if ( newW > oldW && m_view.draw.get() && oldW < m_view.getTableWidth() )
    {
      // clear background (+0.5 needed so anti-aliasing doesn't impact previous column)
      m_canvas.getGraphicsContext2D().clearRect( oldW + 0.5, 0.0, newW, getHeight() );

      // calculate which columns need to be redrawn
      int minColumnPos = m_view.getColumnPositionAtX( oldW );
      if ( minColumnPos == TableView.HEADER )
        minColumnPos = m_view.getColumnPositionAtX( m_view.getRowHeaderWidth() );
      int maxColumnPos = m_view.getColumnPositionAtX( newW );
      m_view.redrawColumns( minColumnPos, maxColumnPos );

      // check if row header needs to be redrawn
      if ( oldW < m_view.getRowHeaderWidth() )
        m_view.redrawColumn( TableView.HEADER );

      // draw table overlay
      m_view.redrawOverlay();
    }
  }

  /**************************************** heightChange *****************************************/
  public void heightChange( int oldH, int newH )
  {
    // only need to draw if new height is larger than old height
    if ( newH > oldH && m_view.draw.get() && oldH < m_view.getTableHeight() )
    {
      // clear background
      m_canvas.getGraphicsContext2D().clearRect( 0.0, oldH + 0.5, getWidth(), newH );

      // calculate which rows need to be redrawn, and redraw them
      int minRowPos = m_view.getRowPositionAtY( oldH );
      if ( minRowPos == TableView.HEADER )
        minRowPos = m_view.getRowPositionAtY( m_view.getColumnHeaderHeight() );
      int maxRowPos = m_view.getRowPositionAtY( newH );
      m_view.redrawRows( minRowPos, maxRowPos );

      // check if column header needs to be redrawn
      if ( oldH < m_view.getColumnHeaderHeight() )
        m_view.redrawRow( TableView.HEADER );

      // draw table overlay
      m_view.redrawOverlay();
    }
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

  /*************************************** getCanvasWidth ****************************************/
  public int getCanvasWidth()
  {
    // return width of canvas
    return (int) m_canvas.getWidth();
  }

  /*************************************** getCanvasHeight ***************************************/
  public int getCanvasHeight()
  {
    // return height of canvas
    return (int) m_canvas.getHeight();
  }

  /************************************** animateToXOffset ***************************************/
  public void animateToXOffset( int endValue )
  {
    // create scroll horizontal animation
    if ( endValue < m_hScrollBar.getMin() )
      endValue = (int) m_vScrollBar.getMin();
    if ( endValue > m_hScrollBar.getMax() )
      endValue = (int) m_vScrollBar.getMax();
    finishAnimation();
    animate( m_hScrollBar.valueProperty(), endValue, 200 );
  }

  /************************************** animateToYOffset ***************************************/
  public void animateToYOffset( int endValue )
  {
    // create scroll vertical animation
    if ( endValue < m_vScrollBar.getMin() )
      endValue = (int) m_vScrollBar.getMin();
    if ( endValue > m_vScrollBar.getMax() )
      endValue = (int) m_vScrollBar.getMax();
    finishAnimation();
    animate( m_vScrollBar.valueProperty(), endValue, 200 );
  }

}
