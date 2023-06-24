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
import rjc.table.undo.UndoStack;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cell.CellDrawer;
import rjc.table.view.cell.CellSelection;
import rjc.table.view.cell.MousePosition;
import rjc.table.view.cell.ViewPosition;

/*************************************************************************************************/
/********************************** Base class for table views ***********************************/
/*************************************************************************************************/

public class TableView extends TableViewParent
{
  private TableData        m_data;

  private TableCanvas      m_canvas;
  private TableScrollBar   m_verticalScrollBar;
  private TableScrollBar   m_horizontalScrollBar;

  private ObservableDouble m_zoom;               // zoom factor for table view
  private TableAxis        m_columnsAxis;        // columns (horizontal) axis
  private TableAxis        m_rowsAxis;           // rows (vertical) axis

  private CellDrawer       m_drawer;
  private CellSelection    m_selection;
  private UndoStack        m_undostack;

  protected ViewPosition   m_focusCell;
  protected ViewPosition   m_selectCell;
  protected MousePosition  m_mouseCell;

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // check parameters and setup table-view
    if ( data == null )
      throw new NullPointerException( "TableData must not be null" );
    m_data = data;

    // construct the table-axis
    m_columnsAxis = new TableAxis( m_data.columnCountProperty() );
    m_rowsAxis = new TableAxis( m_data.rowCountProperty() );
    m_rowsAxis.setDefaultSize( 20 );
    m_rowsAxis.setHeaderSize( 20 );

    // handle zoom
    m_zoom = new ObservableDouble( 1.0 );
    m_columnsAxis.setZoomProperty( m_zoom.getReadOnly() );
    m_rowsAxis.setZoomProperty( m_zoom.getReadOnly() );

    // assemble the table-view components
    m_canvas = new TableCanvas( this );
    m_horizontalScrollBar = new TableScrollBar( m_columnsAxis, Orientation.HORIZONTAL );
    m_verticalScrollBar = new TableScrollBar( m_rowsAxis, Orientation.VERTICAL );
    getChildren().addAll( m_canvas, m_horizontalScrollBar, m_verticalScrollBar );
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

  /*************************************** getCellDrawer *****************************************/
  public CellDrawer getCellDrawer()
  {
    // return class responsible for drawing the cells on canvas
    if ( m_drawer == null )
      m_drawer = new CellDrawer( this );
    return m_drawer;
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
    return m_undostack;
  }

  /****************************************** getStartX ******************************************/
  public int getStartX( int columnIndex )
  {
    // return x coordinate of cell start for specified column position
    return getColumnsAxis().getStartPixel( columnIndex, (int) getHorizontalScrollBar().getValue() );
  }

  /****************************************** getStartY ******************************************/
  public int getStartY( int rowIndex )
  {
    // return y coordinate of cell start for specified row position
    return getRowsAxis().getStartPixel( rowIndex, (int) getVerticalScrollBar().getValue() );
  }

  /************************************** getColumnIndex *****************************************/
  public int getColumnIndex( int xCoordinate )
  {
    // return column position at specified x coordinate
    return getColumnsAxis().getIndexFromCoordinate( xCoordinate, (int) getHorizontalScrollBar().getValue() );
  }

  /**************************************** getRowIndex ******************************************/
  public int getRowIndex( int yCoordinate )
  {
    // return row position at specified y coordinate
    return getRowsAxis().getIndexFromCoordinate( yCoordinate, (int) getVerticalScrollBar().getValue() );
  }

}
