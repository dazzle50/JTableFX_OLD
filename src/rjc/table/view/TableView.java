/**************************************************************************
 *  Copyright (C) 2023 by Richard Crook                                   *
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
import rjc.table.data.TableData;
import rjc.table.signal.ObservableDouble;
import rjc.table.signal.ObservablePosition;
import rjc.table.undo.UndoStack;
import rjc.table.view.TableScrollBar.Animation;
import rjc.table.view.action.Resize;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cell.CellContext;
import rjc.table.view.cell.CellDrawer;
import rjc.table.view.cell.CellSelection;
import rjc.table.view.cell.MousePosition;
import rjc.table.view.cell.ViewPosition;
import rjc.table.view.cell.editor.CellEditorBase;
import rjc.table.view.cursor.Cursors;
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

public class TableView extends TableViewParent
{
  private TableData        m_data;
  private String           m_name;

  private TableCanvas      m_canvas;
  private TableScrollBar   m_verticalScrollBar;
  private TableScrollBar   m_horizontalScrollBar;

  private ObservableDouble m_zoom;               // zoom factor for table view
  private TableAxis        m_columnsAxis;        // columns (horizontal) axis
  private TableAxis        m_rowsAxis;           // rows (vertical) axis

  private CellDrawer       m_drawer;
  private CellSelection    m_selection;
  private UndoStack        m_undostack;

  private ViewPosition     m_focusCell;
  private ViewPosition     m_selectCell;
  private MousePosition    m_mouseCell;

  /**************************************** constructor ******************************************/
  public TableView( TableData data, String name )
  {
    // set view name and construct the view
    m_name = name;
    constructView( data );
  }

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // construct the view
    constructView( data );
  }

  /**************************************** constructView ****************************************/
  private void constructView( TableData data )
  {
    // check parameters and setup table-view
    if ( data == null )
      throw new NullPointerException( "TableData must not be null" );
    m_data = data;

    // construct the table-axis
    m_columnsAxis = new TableAxis( m_data.columnCountProperty() );
    m_rowsAxis = new TableAxis( m_data.rowCountProperty() );
    reset();

    // handle zoom
    m_zoom = new ObservableDouble( 1.0 );
    m_columnsAxis.setZoomProperty( m_zoom.getReadOnly() );
    m_rowsAxis.setZoomProperty( m_zoom.getReadOnly() );

    // assemble the table-view components
    m_canvas = new TableCanvas( this );
    m_horizontalScrollBar = new TableScrollBar( m_columnsAxis, Orientation.HORIZONTAL );
    m_verticalScrollBar = new TableScrollBar( m_rowsAxis, Orientation.VERTICAL );
    getChildren().addAll( m_canvas, m_canvas.getOverlay(), m_horizontalScrollBar, m_verticalScrollBar );

    // add event handlers
    addEventHandlers();
  }

  /************************************** addEventHandlers ***************************************/
  protected void addEventHandlers()
  {
    // create the observable positions for focus, select and mouse
    m_selection = new CellSelection( this );
    m_focusCell = new ViewPosition( this );
    m_focusCell.addListener( ( sender, msg ) -> redraw() );

    m_selectCell = new ViewPosition( this );
    m_selectCell.addLaterListener( ( sender, msg ) ->
    {
      getSelection().update();
      getCanvas().redrawOverlay();
      if ( getCursor() != Cursors.SELECTING_CELLS && getCursor() != Cursors.SELECTING_COLS
          && getCursor() != Cursors.SELECTING_ROWS )
        scrollTo( m_selectCell );
    } );

    // when selection changes redraw the vertical & horizontal headers
    m_selection.addLaterListener( ( sender, msg ) ->
    {
      getCanvas().redrawColumn( TableAxis.HEADER );
      getCanvas().redrawRow( TableAxis.HEADER );
    } );

    // react to zoom values changes
    m_zoom.addListener( ( sender, msg ) ->
    {
      layoutDisplay();
      tableScrolled();
    } );

    m_mouseCell = new MousePosition( this );
    m_mouseCell.addListener( ( sender, msg ) -> checkSelectPosition() );

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

    // react to losing & gaining focus and visibility
    focusedProperty().addListener( ( observable, oldFocus, newFocus ) -> redraw() );
    visibleProperty().addListener( ( observable, oldVisibility, newVisibility ) -> redraw() );

    // react to scroll bar position value changes
    m_horizontalScrollBar.valueProperty().addListener( ( observable, oldValue, newValue ) -> tableScrolled() );
    m_verticalScrollBar.valueProperty().addListener( ( observable, oldValue, newValue ) -> tableScrolled() );

    // set mouse position cell to invalid if mouse is over scroll-bar
    m_horizontalScrollBar.setOnMouseEntered( ( event ) -> m_mouseCell.setInvalid() );
    m_verticalScrollBar.setOnMouseEntered( ( event ) -> m_mouseCell.setInvalid() );
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

  /******************************************** reset ********************************************/
  public void reset()
  {
    // reset table view to default settings
    getColumnsAxis().reset();
    getRowsAxis().reset();
    getRowsAxis().setDefaultSize( 20 );
    getRowsAxis().setHeaderSize( 20 );
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
    var sb = getVerticalScrollBar();
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

    // update canvas size (table + blank excess space)
    m_canvas.resize( visibleWidth, visibleHeight );
  }

  /**************************************** tableScrolled ****************************************/
  private void tableScrolled()
  {
    // handle any actions needed due to view being modified usually scrolled
    redraw();
    getMouseCell().checkXY();
    // CellEditorBase.endEditing();

    // if column/row resize in progress, no need to do anything more
    if ( Resize.inProgress() )
      return;

    // check selected cell position
    checkSelectPosition();
  }

  /************************************* checkSelectPosition *************************************/
  private void checkSelectPosition()
  {
    // if not selecting then nothing to do and just return
    if ( getCursor() != Cursors.SELECTING_CELLS && getCursor() != Cursors.SELECTING_COLS
        && getCursor() != Cursors.SELECTING_ROWS )
      return;

    int column = getCursor() == Cursors.SELECTING_ROWS ? m_selectCell.getColumn() : m_mouseCell.getColumn();
    int row = getCursor() == Cursors.SELECTING_COLS ? m_selectCell.getRow() : m_mouseCell.getRow();

    // if animating to start or end means drag is in progress, so update select cell position
    var animation = getHorizontalScrollBar().getAnimation();
    if ( animation == Animation.TO_START )
    {
      column = getColumnIndex( getHeaderWidth() );
      column = getColumnsAxis().getNextVisible( column );
    }
    if ( animation == Animation.TO_END )
    {
      column = getColumnIndex( (int) getCanvas().getWidth() );
      column = getColumnsAxis().getPreviousVisible( column );
    }

    animation = getVerticalScrollBar().getAnimation();
    if ( animation == Animation.TO_START )
    {
      row = getRowIndex( getHeaderHeight() );
      row = getRowsAxis().getNextVisible( row );
    }
    if ( animation == Animation.TO_END )
    {
      row = getRowIndex( (int) getCanvas().getHeight() );
      row = getRowsAxis().getPreviousVisible( row );
    }

    // update select cell position
    m_selectCell.setPosition( column, row );
  }

  /****************************************** getData ********************************************/
  public TableData getData()
  {
    // return data model for table-view
    return m_data;
  }

  /****************************************** getName ********************************************/
  public String getName()
  {
    // return name of table-view
    return m_name;
  }

  /****************************************** setName ********************************************/
  public void setName( String name )
  {
    // set name of table-view
    m_name = name;
  }

  /***************************************** getCanvas *******************************************/
  public TableCanvas getCanvas()
  {
    // return canvas (shows table headers & body cells + BLANK excess space) for table-view
    return m_canvas;
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

  /*************************************** getCellDrawer *****************************************/
  public CellDrawer getCellDrawer()
  {
    // return class responsible for drawing the cells on canvas
    if ( m_drawer == null )
      m_drawer = new CellDrawer();
    return m_drawer;
  }

  /**************************************** getCellEditor ****************************************/
  public CellEditorBase getCellEditor( CellContext cell )
  {
    // return cell editor control (or null if cell is read-only)
    return null;
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

  /*************************************** getTableHeight ****************************************/
  public int getTableHeight()
  {
    // return height in pixels of all whole table including header (with zoom but no scrolling) - often larger than canvas
    return getRowsAxis().getTotalPixels();
  }

  /**************************************** getTableWidth ****************************************/
  public int getTableWidth()
  {
    // return width in pixels of all whole table including header (with zoom but no scrolling) - often larger than canvas
    return getColumnsAxis().getTotalPixels();
  }

  /*************************************** getHeaderHeight ***************************************/
  public int getHeaderHeight()
  {
    // return header height in pixels (taking zoom into account)
    return getRowsAxis().getHeaderPixels();
  }

  /*************************************** getHeaderWidth ****************************************/
  public int getHeaderWidth()
  {
    // return header width in pixels (taking zoom into account)
    return getColumnsAxis().getHeaderPixels();
  }

  /****************************************** getZoom ********************************************/
  public ObservableDouble getZoom()
  {
    // return observable zoom factor (1.0 is normal 100% size) for table-view
    return m_zoom;
  }

  /**************************************** getSelection *****************************************/
  public CellSelection getSelection()
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

  /*************************************** getColumnStartX ***************************************/
  public int getColumnStartX( int columnIndex )
  {
    // return x coordinate of cell start for specified column position
    return getColumnsAxis().getStartPixel( columnIndex, (int) getHorizontalScrollBar().getValue() );
  }

  /**************************************** getRowStartY *****************************************/
  public int getRowStartY( int rowIndex )
  {
    // return y coordinate of cell start for specified row position
    return getRowsAxis().getStartPixel( rowIndex, (int) getVerticalScrollBar().getValue() );
  }

  /*************************************** getColumnIndex ****************************************/
  public int getColumnIndex( int xCoordinate )
  {
    // return column position at specified x coordinate
    return getColumnsAxis().getIndexFromCoordinate( xCoordinate, (int) getHorizontalScrollBar().getValue() );
  }

  /***************************************** getRowIndex *****************************************/
  public int getRowIndex( int yCoordinate )
  {
    // return row position at specified y coordinate
    return getRowsAxis().getIndexFromCoordinate( yCoordinate, (int) getVerticalScrollBar().getValue() );
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

  /****************************************** scrollTo *******************************************/
  private void scrollTo( ObservablePosition position )
  {
    // scroll view if necessary to show specified position
    int column = position.getColumn();
    if ( column >= TableAxis.FIRSTCELL && column < getColumnsAxis().getCount() )
      getHorizontalScrollBar().scrollToShowIndex( column );

    int row = position.getRow();
    if ( row >= TableAxis.FIRSTCELL && row < getRowsAxis().getCount() )
      getVerticalScrollBar().scrollToShowIndex( row );
  }
}
