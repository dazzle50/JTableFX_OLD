/**************************************************************************
 *  Copyright (C) 2021 by Richard Crook                                   *
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

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import rjc.table.Status;
import rjc.table.data.TableData;
import rjc.table.signal.ObservableDouble;
import rjc.table.undo.UndoStack;
import rjc.table.view.TableScrollBar.Animation;
import rjc.table.view.actions.Resize;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cell.CellContext;
import rjc.table.view.cell.CellDraw;
import rjc.table.view.cell.CellStyle;
import rjc.table.view.cell.MousePosition;
import rjc.table.view.cell.ViewPosition;
import rjc.table.view.cell.editor.CellEditorBase;
import rjc.table.view.events.ContextMenu;
import rjc.table.view.events.KeyPressed;
import rjc.table.view.events.KeyTyped;
import rjc.table.view.events.MouseClicked;
import rjc.table.view.events.MouseDragged;
import rjc.table.view.events.MouseEntered;
import rjc.table.view.events.MouseExited;
import rjc.table.view.events.MouseMoved;
import rjc.table.view.events.MousePressed;
import rjc.table.view.events.MouseReleased;
import rjc.table.view.events.MouseScroll;

/*************************************************************************************************/
/********************************** Base class for table views ***********************************/
/*************************************************************************************************/

public class TableView extends TableParent
{
  private TableData          m_data;

  protected TableCanvas      m_canvas;
  protected TableScrollBar   m_verticalScrollBar;
  protected TableScrollBar   m_horizontalScrollBar;

  protected ObservableDouble m_zoom;
  protected TableAxis        m_columnsAxis;        // columns (horizontal) axis
  protected TableAxis        m_rowsAxis;           // rows (vertical) axis

  protected TableSelection   m_selection;
  protected UndoStack        m_undostack;
  protected Status           m_status;

  protected ViewPosition     m_focusCell;
  protected ViewPosition     m_selectCell;
  protected MousePosition    m_mouseCell;

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // check parameters and setup table-view
    if ( data == null )
      throw new NullPointerException( "TableData must not be null" );
    m_data = data;
    m_data.register( this );
    construct();
  }

  /***************************************** construct *******************************************/
  public void construct()
  {
    // construct the table-view
    m_zoom = new ObservableDouble( 1.0 );
    m_columnsAxis = new TableAxis( m_data.getColumnCountProperty() );
    m_rowsAxis = new TableAxis( m_data.getRowCountProperty() );

    m_canvas = new TableCanvas( this );
    m_horizontalScrollBar = new TableScrollBar( m_columnsAxis, Orientation.HORIZONTAL );
    m_verticalScrollBar = new TableScrollBar( m_rowsAxis, Orientation.VERTICAL );
    getChildren().addAll( m_canvas, m_canvas.m_overlay, m_horizontalScrollBar, m_verticalScrollBar );

    m_selection = new TableSelection( this );
    m_status = new Status();

    m_focusCell = new ViewPosition( this );
    m_selectCell = new ViewPosition( this );
    m_mouseCell = new MousePosition( this );

    // reset table view to default settings
    reset();

    // react to mouse events
    setOnMouseMoved( new MouseMoved() );
    setOnMouseClicked( new MouseClicked() );
    setOnMousePressed( new MousePressed() );
    setOnMouseReleased( new MouseReleased() );
    setOnMouseExited( new MouseExited() );
    setOnMouseEntered( new MouseEntered() );
    setOnMouseDragged( new MouseDragged() );
    setOnScroll( new MouseScroll() );
    setOnContextMenuRequested( new ContextMenu() );

    // react to keyboard events
    setOnKeyPressed( new KeyPressed() );
    setOnKeyTyped( new KeyTyped() );

    // react to focus & select cell movement
    m_focusCell.addListener( x ->
    {
      getSelection().update();
      redraw();
      scrollTo( m_focusCell.getColumnPos(), m_focusCell.getRowPos() );
    } );
    m_selectCell.addListener( x ->
    {
      getSelection().update();
      redraw();
      scrollTo( m_selectCell.getColumnPos(), m_selectCell.getRowPos() );
    } );

    // react to scroll bar position value changes
    m_horizontalScrollBar.valueProperty().addListener( ( observable, oldV, newV ) -> tableScrolled() );
    m_verticalScrollBar.valueProperty().addListener( ( observable, oldV, newV ) -> tableScrolled() );
  }

  /******************************************** reset ********************************************/
  public void reset()
  {
    // reset table view to default settings
    getColumnsAxis().reset();
    getRowsAxis().reset();
    getRowsAxis().setDefaultSize( 20 );
    getRowsAxis().setHeaderSize( 20 );
  }

  /****************************************** getData ********************************************/
  public TableData getData()
  {
    // return data model for table-view
    return m_data;
  }

  /***************************************** getCanvas *******************************************/
  public TableCanvas getCanvas()
  {
    // return canvas (shows table headers & body cells + BLANK excess space) for table-view
    return m_canvas;
  }

  /*********************************** getHorizontalScrollBar ************************************/
  public TableScrollBar getHorizontalScrollBar()
  {
    // return horizontal scroll bar (will not be visible if not needed) for table-view
    return m_horizontalScrollBar;
  }

  /************************************ getVerticalScrollBar *************************************/
  public TableScrollBar getVerticalScrollBar()
  {
    // return vertical scroll bar (will not be visible if not needed) for table-view
    return m_verticalScrollBar;
  }

  /*************************************** getColumnsAxis ****************************************/
  public TableAxis getColumnsAxis()
  {
    // return horizontal axis for column widths and mapping of index to position for table-view
    return m_columnsAxis;
  }

  /**************************************** getRowsAxis ******************************************/
  public TableAxis getRowsAxis()
  {
    // return vertical axis for row heights and mapping of index to position for table-view
    return m_rowsAxis;
  }

  /****************************************** getZoom ********************************************/
  public ObservableDouble getZoom()
  {
    // return observable zoom factor (1.0 is normal 100% size) for table-view
    return m_zoom;
  }

  /*************************************** getSelection ******************************************/
  public TableSelection getSelection()
  {
    // return selection model for table-view
    return m_selection;
  }

  /***************************************** getUndoStack ****************************************/
  public UndoStack getUndoStack()
  {
    // return undo-stack for table-view (create if necessary)
    if ( m_undostack == null )
      m_undostack = new UndoStack();
    return m_undostack;
  }

  /***************************************** setUndoStack ****************************************/
  public void setUndoStack( UndoStack undostack )
  {
    // set the undo-stack for table-view
    m_undostack = undostack;
  }

  /***************************************** getStatus *******************************************/
  public Status getStatus()
  {
    // return status object for table-view
    return m_status;
  }

  /***************************************** setStatus *******************************************/
  public void setStatus( Status status )
  {
    // set status object for table-view
    m_status = status;
  }

  /**************************************** getFocusCell *****************************************/
  public ViewPosition getFocusCell()
  {
    // return observable focus cell position on table-view
    return m_focusCell;
  }

  /**************************************** getSelectCell ****************************************/
  public ViewPosition getSelectCell()
  {
    // return observable select cell position on table-view
    return m_selectCell;
  }

  /**************************************** getMouseCell *****************************************/
  public MousePosition getMouseCell()
  {
    // return observable mouse cell position on table-view
    return m_mouseCell;
  }

  /**************************************** getCellDrawer ****************************************/
  public CellDraw getCellDrawer()
  {
    // return new instance of class that draws table cells (override to use different drawer)
    return new CellDraw();
  }

  /**************************************** getCellEditor ****************************************/
  public CellEditorBase getCellEditor( CellContext cell )
  {
    // return cell editor control (or null if cell is read-only)
    return null;
  }

  /***************************************** openEditor ******************************************/
  public void openEditor( Object value )
  {
    // generate cell context from cell drawer
    CellStyle cell = getCellDrawer();
    cell.setPosition( this, getFocusCell() );
    CellEditorBase editor = getCellEditor( cell );

    // open editor if provided and valid value
    if ( editor != null && editor.isValueValid( value ) )
      editor.open( value, cell );
  }

  /******************************************* redraw ********************************************/
  public void redraw()
  {
    // request redraw of full visible table (headers and body)
    getCanvas().redraw();
  }

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // request redraw of specified table body or header cell
    getCanvas().redrawCell( columnIndex, rowIndex );
  }

  /**************************************** redrawColumn *****************************************/
  public void redrawColumn( int columnIndex )
  {
    // request redraw of visible part of column including header
    getCanvas().redrawColumn( columnIndex );
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // request redraw of visible part of row including header
    getCanvas().redrawRow( rowIndex );
  }

  /*********************************** getXStartFromColumnPos ************************************/
  public int getXStartFromColumnPos( int columnPos )
  {
    // return x coordinate of cell start for specified column position
    return getColumnsAxis().getStartFromPosition( columnPos, (int) getHorizontalScrollBar().getValue() );
  }

  /************************************* getYStartFromRowPos *************************************/
  public int getYStartFromRowPos( int rowPos )
  {
    // return y coordinate of cell start for specified row position
    return getRowsAxis().getStartFromPosition( rowPos, (int) getVerticalScrollBar().getValue() );
  }

  /*********************************** getColumnPositionAtX **************************************/
  public int getColumnPositionAtX( int x )
  {
    // return column position at specified x coordinate
    return getColumnsAxis().getPositionFromCoordinate( x, (int) getHorizontalScrollBar().getValue() );
  }

  /************************************* getRowPositionAtY ***************************************/
  public int getRowPositionAtY( int y )
  {
    // return row position at specified y coordinate
    return getRowsAxis().getPositionFromCoordinate( y, (int) getVerticalScrollBar().getValue() );
  }

  /************************************** getTableHeight *****************************************/
  public int getTableHeight()
  {
    // return height in pixels of all whole table including header (with zoom but no scrolling) - probably larger than canvas
    return getRowsAxis().getHeaderPixels() + getRowsAxis().getBodyPixels();
  }

  /*************************************** getTableWidth *****************************************/
  public int getTableWidth()
  {
    // return width in pixels of all whole non-scrolled table including header (with zoom but no scrolling) - probably larger than canvas
    return getColumnsAxis().getHeaderPixels() + getColumnsAxis().getBodyPixels();
  }

  /************************************* getHeaderHeight *****************************************/
  public int getHeaderHeight()
  {
    // return header height in pixels (taking zoom into account)
    return getRowsAxis().getHeaderPixels();
  }

  /************************************** getHeaderWidth *****************************************/
  public int getHeaderWidth()
  {
    // return header width in pixels (taking zoom into account)
    return getColumnsAxis().getHeaderPixels();
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

  /**************************************** layoutDisplay ****************************************/
  public void layoutDisplay()
  {
    // determine which scroll-bars should be visible
    int tableH = getTableHeight();
    int tableW = getTableWidth();
    int scrollbarSize = (int) getVerticalScrollBar().getWidth();

    boolean isVSBvisible = getHeight() < tableH;
    int visibleWidth = isVSBvisible ? getWidth() - scrollbarSize : getWidth();
    boolean isHSBvisible = visibleWidth < tableW;
    int visibleHeight = isHSBvisible ? getHeight() - scrollbarSize : getHeight();
    isVSBvisible = visibleHeight < tableH;
    visibleWidth = isVSBvisible ? getWidth() - scrollbarSize : getWidth();

    // update vertical scroll bar
    ScrollBar sb = getVerticalScrollBar();
    sb.setVisible( isVSBvisible );
    if ( isVSBvisible )
    {
      sb.setPrefHeight( visibleHeight );
      sb.relocate( getWidth() - scrollbarSize, 0.0 );

      double max = tableH - visibleHeight;
      sb.setMax( max );
      sb.setVisibleAmount( max * visibleHeight / tableH );
      sb.setBlockIncrement( visibleHeight - getRowsAxis().getHeaderPixels() );

      if ( sb.getValue() > max )
        sb.setValue( max );
    }
    else
    {
      sb.setValue( 0.0 );
      sb.setMax( 0.0 );
    }

    // update horizontal scroll bar
    sb = getHorizontalScrollBar();
    sb.setVisible( isHSBvisible );
    if ( isHSBvisible )
    {
      sb.setPrefWidth( visibleWidth );
      sb.relocate( 0.0, getHeight() - scrollbarSize );

      double max = tableW - visibleWidth;
      sb.setMax( max );
      sb.setVisibleAmount( max * visibleWidth / tableW );
      sb.setBlockIncrement( visibleWidth - getColumnsAxis().getHeaderPixels() );

      if ( sb.getValue() > max )
        sb.setValue( max );
    }
    else
    {
      sb.setValue( 0.0 );
      sb.setMax( 0.0 );
    }

    // update canvas & overlay size (table + blank excess space)
    getCanvas().resize( visibleWidth, visibleHeight );
  }

  /****************************************** scrollTo *******************************************/
  public void scrollTo( int columnPos, int rowPos )
  {
    // scroll view if necessary to show specified position
    if ( columnPos >= TableAxis.FIRSTCELL && columnPos < TableAxis.AFTER )
      getHorizontalScrollBar().scrollToPos( columnPos );
    if ( rowPos >= TableAxis.FIRSTCELL && rowPos < TableAxis.AFTER )
      getVerticalScrollBar().scrollToPos( rowPos );
  }

  /**************************************** tableScrolled ****************************************/
  private void tableScrolled()
  {
    // handle any actions needed due to the table scrolled
    redraw();
    getMouseCell().checkXY();
    CellEditorBase.endEditing();

    // if resize in progress, no need to do anything more
    if ( Resize.inProgress() )
      return;

    // if animating to start or end means drag is in progress, so update select cell position
    var animation = getHorizontalScrollBar().getAnimation();
    if ( animation == Animation.TO_START )
    {
      int columnPos = getColumnPositionAtX( getHeaderWidth() );
      columnPos = getColumnsAxis().getNext( columnPos );
      getSelectCell().setColumnPos( columnPos );
    }
    if ( animation == Animation.TO_END )
    {
      int columnPos = getColumnPositionAtX( (int) getCanvas().getWidth() );
      columnPos = getColumnsAxis().getPrevious( columnPos );
      getSelectCell().setColumnPos( columnPos );
    }

    animation = getVerticalScrollBar().getAnimation();
    if ( animation == Animation.TO_START )
    {
      int rowPos = getRowPositionAtY( getHeaderHeight() );
      rowPos = getRowsAxis().getNext( rowPos );
      getSelectCell().setRowPos( rowPos );
    }
    if ( animation == Animation.TO_END )
    {
      int rowPos = getRowPositionAtY( (int) getCanvas().getHeight() );
      rowPos = getRowsAxis().getPrevious( rowPos );
      getSelectCell().setRowPos( rowPos );
    }

    /**
    TODO
    // if column or row reordering, ensure reorder mark placed correctly
    if ( m_reorder.getOrientation() == Orientation.HORIZONTAL )
      m_reorder.setPlacement( m_x );
    if ( m_reorder.getOrientation() == Orientation.VERTICAL )
      m_reorder.setPlacement( m_y );
    
    // if column or row resizing, update
    if ( m_resize.getOrientation() == Orientation.HORIZONTAL )
      m_resize.resize( m_x );
    if ( m_resize.getOrientation() == Orientation.VERTICAL )
      m_resize.resize( m_y );
    **/
  }

}
