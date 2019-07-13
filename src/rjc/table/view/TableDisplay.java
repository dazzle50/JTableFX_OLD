/**************************************************************************
 *  Copyright (C) 2019 by Richard Crook                                   *
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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.canvas.Canvas;
import rjc.table.data.TableData;

/*************************************************************************************************/
/************************ Table view display with canvas and scroll bars *************************/
/*************************************************************************************************/

public class TableDisplay extends TableParent
{
  protected TableView                 m_view;                                           // shortcut to table view
  protected TableData                 m_data;                                           // shortcut to table data

  protected TableAxis                 m_columns;                                        // axis for vertical columns
  protected TableAxis                 m_rows;                                           // axis for horizontal rows

  protected TableScrollBar            m_vScrollBar;                                     // vertical scroll bar
  protected TableScrollBar            m_hScrollBar;                                     // horizontal scroll bar
  protected Canvas                    m_canvas;                                         // canvas for table column & row headers and body cells

  // observable double for table-view zoom
  final private ReadOnlyDoubleWrapper m_zoomProperty = new ReadOnlyDoubleWrapper( 1.0 );

  // column & row index starts at 0 for table body, index of -1 is for axis header
  final static public int             INVALID        = TableAxis.INVALID;
  final static public int             HEADER         = TableAxis.HEADER;
  final static public int             FIRSTCELL      = TableAxis.FIRSTCELL;
  final static public int             BEFORE         = TableAxis.BEFORE;
  final static public int             AFTER          = TableAxis.AFTER;

  /******************************************* setZoom *******************************************/
  public void setZoom( double zoom )
  {
    // check zoom scale is valid
    if ( zoom < 0.01 || zoom >= 100.0 )
      throw new IllegalArgumentException( "Zoom scale not valid " + zoom );

    // set zoom scale for table-view and both axis
    m_columns.setZoom( zoom );
    m_rows.setZoom( zoom );
    m_zoomProperty.set( zoom );
  }

  /******************************************* getZoom *******************************************/
  public double getZoom()
  {
    // return current zoom scale
    return m_zoomProperty.get();
  }

  /*************************************** getZoomProperty ***************************************/
  public ReadOnlyDoubleProperty getZoomProperty()
  {
    // return zoom property
    return m_zoomProperty.getReadOnlyProperty();
  }

  /******************************************* resize ********************************************/
  @Override
  public void resize( double width, double height )
  {
    // do nothing if no change in size
    if ( (int) width == getWidth() && (int) height == getHeight() )
      return;

    // only resize if width && height less than max integer (which happens on first pass)
    if ( width < Integer.MAX_VALUE && height < Integer.MAX_VALUE )
    {
      // resize parent and re-layout canvas and scroll bars
      super.resize( width, height );
      layoutDisplay();
    }
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
    if ( newW > oldW && isVisible() && oldW < getTableWidth() )
    {
      // clear background (+0.5 needed so anti-aliasing doesn't impact previous column)
      m_canvas.getGraphicsContext2D().clearRect( oldW + 0.5, 0.0, newW, getHeight() );

      // calculate which columns need to be redrawn
      int minColumnPos = getColumnPositionAtX( oldW );
      if ( minColumnPos <= HEADER )
        minColumnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int maxColumnPos = getColumnPositionAtX( newW );
      m_view.redrawColumns( minColumnPos, maxColumnPos );

      // check if row header needs to be redrawn
      if ( maxColumnPos == HEADER
          || ( oldW < getRowHeaderWidth() && getXStartFromColumnPos( minColumnPos ) >= getRowHeaderWidth() ) )
        m_view.redrawColumn( HEADER );

      // draw table overlay
      m_view.redrawOverlay();
    }
  }

  /**************************************** heightChange *****************************************/
  public void heightChange( int oldH, int newH )
  {
    // only need to draw if new height is larger than old height
    if ( newH > oldH && isVisible() && oldH < getTableHeight() )
    {
      // clear background
      m_canvas.getGraphicsContext2D().clearRect( 0.0, oldH + 0.5, getWidth(), newH );

      // calculate which rows need to be redrawn, and redraw them
      int minRowPos = getRowPositionAtY( oldH );
      if ( minRowPos <= HEADER )
        minRowPos = getRowPositionAtY( getColumnHeaderHeight() );
      int maxRowPos = getRowPositionAtY( newH );
      m_view.redrawRows( minRowPos, maxRowPos );

      // check if column header needs to be redrawn
      if ( maxRowPos == HEADER
          || ( oldH < getColumnHeaderHeight() && getYStartFromRowPos( minRowPos ) >= getColumnHeaderHeight() ) )
        m_view.redrawRow( HEADER );

      // draw table overlay
      m_view.redrawOverlay();
    }
  }

  /**************************************** layoutDisplay ****************************************/
  public void layoutDisplay()
  {
    // determine which scroll-bars should be visible
    boolean isVSBvisible = getHeight() < getTableHeight();
    int visibleWidth = isVSBvisible ? getWidth() - (int) m_vScrollBar.getWidth() : getWidth();
    boolean isHSBvisible = visibleWidth < getTableWidth();
    int visibleHeight = isHSBvisible ? getHeight() - (int) m_hScrollBar.getHeight() : getHeight();
    isVSBvisible = visibleHeight < getTableHeight();
    visibleWidth = isVSBvisible ? getWidth() - (int) m_vScrollBar.getWidth() : getWidth();

    // update vertical scroll bar
    m_vScrollBar.setVisible( isVSBvisible );
    if ( isVSBvisible )
    {
      m_vScrollBar.setPrefHeight( visibleHeight );
      m_vScrollBar.relocate( getWidth() - m_vScrollBar.getWidth(), 0.0 );

      double max = getTableHeight() - visibleHeight;
      m_vScrollBar.setMax( max );
      m_vScrollBar.setVisibleAmount( max * visibleHeight / getTableHeight() );
      m_vScrollBar.setBlockIncrement( visibleHeight - getColumnHeaderHeight() );

      if ( m_vScrollBar.getValue() > max )
        m_vScrollBar.setValue( max );
    }
    else
    {
      m_vScrollBar.setValue( 0.0 );
      m_vScrollBar.setMax( 0.0 );
    }

    // update horizontal scroll bar
    m_hScrollBar.setVisible( isHSBvisible );
    if ( isHSBvisible )
    {
      m_hScrollBar.setPrefWidth( visibleWidth );
      m_hScrollBar.relocate( 0.0, getHeight() - m_hScrollBar.getHeight() );

      double max = getTableWidth() - visibleWidth;
      m_hScrollBar.setMax( max );
      m_hScrollBar.setVisibleAmount( max * visibleWidth / getTableWidth() );
      m_hScrollBar.setBlockIncrement( visibleWidth - getRowHeaderWidth() );

      if ( m_hScrollBar.getValue() > max )
        m_hScrollBar.setValue( max );
    }
    else
    {
      m_hScrollBar.setValue( 0.0 );
      m_hScrollBar.setMax( 0.0 );
    }

    // update canvas
    m_canvas.setWidth( visibleWidth );
    m_canvas.setHeight( visibleHeight );
  }

  /*************************************** getTableWidth *****************************************/
  public int getTableWidth()
  {
    // return width in pixels of all whole visible table including header
    return m_columns.getHeaderPixels() + m_columns.getBodyPixels();
  }

  /************************************** getTableHeight *****************************************/
  public int getTableHeight()
  {
    // return height in pixels of all whole visible table including header
    return m_rows.getHeaderPixels() + m_rows.getBodyPixels();
  }

  /************************************ getColumnHeaderHeight ************************************/
  public int getColumnHeaderHeight()
  {
    // return table column header height
    return m_rows.getHeaderPixels();
  }

  /************************************** getRowHeaderWidth **************************************/
  public int getRowHeaderWidth()
  {
    // return table row header width
    return m_columns.getHeaderPixels();
  }

  /*********************************** getXStartFromColumnPos ************************************/
  public int getXStartFromColumnPos( int columnPos )
  {
    // return x coordinate of cell start for specified column position
    return m_columns.getStartFromPosition( columnPos, (int) m_hScrollBar.getValue() );
  }

  /************************************* getYStartFromRowPos *************************************/
  public int getYStartFromRowPos( int rowPos )
  {
    // return y coordinate of cell start for specified row position
    return m_rows.getStartFromPosition( rowPos, (int) m_vScrollBar.getValue() );
  }

  /*********************************** getColumnPositionAtX **************************************/
  public int getColumnPositionAtX( int x )
  {
    // return column position at specified x coordinate
    return m_columns.getPositionFromCoordinate( x, (int) m_hScrollBar.getValue() );
  }

  /************************************* getRowPositionAtY ***************************************/
  public int getRowPositionAtY( int y )
  {
    // return row position at specified y coordinate
    return m_rows.getPositionFromCoordinate( y, (int) m_vScrollBar.getValue() );
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

}
