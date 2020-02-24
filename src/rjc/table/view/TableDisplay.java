/**************************************************************************
 *  Copyright (C) 2020 by Richard Crook                                   *
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
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.FontSmoothingType;
import rjc.table.data.TableData;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/************************ Table view display with canvas and scroll bars *************************/
/*************************************************************************************************/

public class TableDisplay extends TableParent
{
  private TableView                   m_view;                                           // shortcut to table view
  private TableData                   m_data;                                           // shortcut to table data

  private TableAxis                   m_columns;                                        // axis for vertical columns
  private TableAxis                   m_rows;                                           // axis for horizontal rows

  private TableScrollBar              m_vScrollBar;                                     // vertical scroll bar
  private TableScrollBar              m_hScrollBar;                                     // horizontal scroll bar
  private Canvas                      m_canvas;                                         // canvas for table column & row headers and body cells

  // observable double for table-view zoom
  final private ReadOnlyDoubleWrapper m_zoomProperty = new ReadOnlyDoubleWrapper( 1.0 );

  // column & row index starts at 0 for table body, index of -1 is for axis header
  final static public int             INVALID        = TableAxis.INVALID;
  final static public int             HEADER         = TableAxis.HEADER;
  final static public int             FIRSTCELL      = TableAxis.FIRSTCELL;
  final static public int             BEFORE         = TableAxis.BEFORE;
  final static public int             AFTER          = TableAxis.AFTER;

  /***************************************** construct *******************************************/
  protected void construct( TableView view, TableData data )
  {
    // register associated table view and data
    m_view = view;
    m_data = data;
    data.register( m_view );

    // create table canvas, axis and scroll bars
    m_canvas = new Canvas();
    m_columns = new TableAxis( data.getColumnCountProperty() );
    m_rows = new TableAxis( data.getRowCountProperty() );
    m_hScrollBar = new TableScrollBar( m_columns, Orientation.HORIZONTAL );
    m_vScrollBar = new TableScrollBar( m_rows, Orientation.VERTICAL );

    // when canvas size changes draw new areas
    m_canvas.widthProperty()
        .addListener( ( observable, oldW, newW ) -> widthChange( oldW.intValue(), newW.intValue() ) );
    m_canvas.heightProperty()
        .addListener( ( observable, oldH, newH ) -> heightChange( oldH.intValue(), newH.intValue() ) );

    // redraw table when focus changes
    m_canvas.focusedProperty().addListener( ( observable, oldF, newF ) -> redraw() );

    // redraw table when focus changes or becomes visible
    visibleProperty().addListener( ( observable, oldV, newV ) -> redraw() );

    // react to mouse events
    setOnMouseExited( event -> view.mouseExited( event ) );
    setOnMouseEntered( event -> view.mouseEntered( event ) );
    setOnMouseMoved( event -> view.mouseMoved( event ) );
    setOnMouseDragged( event -> view.mouseDragged( event ) );
    setOnMouseReleased( event -> view.mouseReleased( event ) );
    setOnMousePressed( event -> view.mousePressed( event ) );
    setOnMouseClicked( event -> view.mouseClicked( event ) );
    setOnScroll( event -> view.mouseScroll( event ) );

    // react to keyboard events
    setOnKeyPressed( event -> view.keyPressed( event ) );
    setOnKeyTyped( event -> view.keyTyped( event ) );

    // react to scroll bar position value changes
    m_hScrollBar.valueProperty().addListener( ( observable, oldV, newV ) -> view.tableScrolled() );
    m_vScrollBar.valueProperty().addListener( ( observable, oldV, newV ) -> view.tableScrolled() );

    // add canvas and scroll bars to parent displayed children
    add( m_canvas );
    add( m_vScrollBar );
    add( m_hScrollBar );

    // setup graphics context & reset view
    m_canvas.getGraphicsContext2D().setFontSmoothingType( FontSmoothingType.LCD );
    view.reset();
  }

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
      getView().redrawColumns( minColumnPos, maxColumnPos );

      // check if row header needs to be redrawn
      if ( maxColumnPos == HEADER
          || ( oldW < getRowHeaderWidth() && getXStartFromColumnPos( minColumnPos ) >= getRowHeaderWidth() ) )
        getView().redrawColumn( HEADER );

      // draw table overlay
      getView().redrawOverlay();
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
      getView().redrawRows( minRowPos, maxRowPos );

      // check if column header needs to be redrawn
      if ( maxRowPos == HEADER
          || ( oldH < getColumnHeaderHeight() && getYStartFromRowPos( minRowPos ) >= getColumnHeaderHeight() ) )
        getView().redrawRow( HEADER );

      // draw table overlay
      getView().redrawOverlay();
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

  /******************************************* getView *******************************************/
  public TableView getView()
  {
    // return the table view
    return m_view;
  }

  /******************************************* getData *******************************************/
  public TableData getData()
  {
    // return the view's data source
    return m_data;
  }

  /****************************************** getColumns *****************************************/
  public TableAxis getColumns()
  {
    // return the view's columns axis
    return m_columns;
  }

  /******************************************* getRows *******************************************/
  public TableAxis getRows()
  {
    // return the view's rows axis
    return m_rows;
  }

  /****************************************** getCanvas ******************************************/
  public Canvas getCanvas()
  {
    // return canvas where table headers and body are drawn
    return m_canvas;
  }

  /************************************ getHorizontalScrollBar ***********************************/
  public TableScrollBar getHorizontalScrollBar()
  {
    // return horizontal scroll bar
    return m_hScrollBar;
  }

  /************************************ getVerticalScrollBar *************************************/
  public TableScrollBar getVerticalScrollBar()
  {
    // return vertical scroll bar
    return m_vScrollBar;
  }

  /*************************************** getTableWidth *****************************************/
  public int getTableWidth()
  {
    // return width in pixels of all whole visible table including header
    return getColumns().getHeaderPixels() + getColumns().getBodyPixels();
  }

  /************************************** getTableHeight *****************************************/
  public int getTableHeight()
  {
    // return height in pixels of all whole visible table including header
    return getRows().getHeaderPixels() + getRows().getBodyPixels();
  }

  /************************************ getColumnHeaderHeight ************************************/
  public int getColumnHeaderHeight()
  {
    // return table column header height
    return getRows().getHeaderPixels();
  }

  /************************************** getRowHeaderWidth **************************************/
  public int getRowHeaderWidth()
  {
    // return table row header width
    return getColumns().getHeaderPixels();
  }

  /*********************************** getXStartFromColumnPos ************************************/
  public int getXStartFromColumnPos( int columnPos )
  {
    // return x coordinate of cell start for specified column position
    return getColumns().getStartFromPosition( columnPos, (int) m_hScrollBar.getValue() );
  }

  /************************************* getYStartFromRowPos *************************************/
  public int getYStartFromRowPos( int rowPos )
  {
    // return y coordinate of cell start for specified row position
    return getRows().getStartFromPosition( rowPos, (int) m_vScrollBar.getValue() );
  }

  /*********************************** getColumnPositionAtX **************************************/
  public int getColumnPositionAtX( int x )
  {
    // return column position at specified x coordinate
    return getColumns().getPositionFromCoordinate( x, (int) m_hScrollBar.getValue() );
  }

  /************************************* getRowPositionAtY ***************************************/
  public int getRowPositionAtY( int y )
  {
    // return row position at specified y coordinate
    return getRows().getPositionFromCoordinate( y, (int) m_vScrollBar.getValue() );
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
