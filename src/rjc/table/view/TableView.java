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

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import rjc.table.Colors;
import rjc.table.Status;
import rjc.table.Utils;
import rjc.table.cell.CellContext;
import rjc.table.cell.CellDraw;
import rjc.table.cell.editor.CellEditorBase;
import rjc.table.data.TableData;
import rjc.table.undo.CommandSetValue;

/*************************************************************************************************/
/********************************** Base class for table views ***********************************/
/*************************************************************************************************/

public class TableView extends TableXML
{
  private AtomicBoolean    m_redrawIsRequested; // flag if redraw has been scheduled
  private boolean          m_fullRedraw;        // full view redraw (headers & body)
  private HashSet<Integer> m_columns;           // requested column indexes
  private HashSet<Integer> m_rows;              // requested row indexes
  private HashSet<Long>    m_cells;             // long = (long) column << 32 | row & 0xFFFFFFFFL

  private Status           m_status;            // status for this view

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // setup and register table view
    m_redrawIsRequested = new AtomicBoolean( false );
    m_fullRedraw = false;
    m_columns = new HashSet<>();
    m_rows = new HashSet<>();
    m_cells = new HashSet<>();

    construct( this, data );
  }

  /******************************************** reset ********************************************/
  public void reset()
  {
    // reset table view to default settings
    getColumns().reset();
    getRows().reset();
    getRows().setDefaultSize( 20 );
    getRows().setHeaderSize( 20 );
  }

  /*************************************** performRedraws ****************************************/
  private void performRedraws()
  {
    // redraw parts of table that have been requested
    m_redrawIsRequested.set( false );
    if ( m_fullRedraw )
      redrawNow(); // full redraw requested so don't need to redraw anything else
    else
    {
      // redraw requested cells that aren't covered by requested columns & rows
      for ( long hash : m_cells )
      {
        int columnIndex = (int) ( hash >> 32 );
        int rowIndex = (int) hash;
        if ( !m_columns.contains( columnIndex ) && !m_rows.contains( rowIndex ) )
          redrawCellNow( columnIndex, rowIndex );
      }

      // redraw requested columns & rows
      for ( int columnIndex : m_columns )
        redrawColumnNow( columnIndex );
      for ( int rowIndex : m_rows )
        redrawRowNow( rowIndex );
    }

    // clear requests
    m_fullRedraw = false;
    m_columns.clear();
    m_rows.clear();
    m_cells.clear();
  }

  /****************************************** schedule *******************************************/
  private void schedule()
  {
    // schedule redrawing of what has been requested
    if ( m_redrawIsRequested.compareAndSet( false, true ) )
      Platform.runLater( () -> performRedraws() );
  }

  /******************************************* redraw ********************************************/
  public void redraw()
  {
    // request redraw full visible table (headers and body)
    m_fullRedraw = true;
    schedule();
  }

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // request redraw specified table body or header cell
    m_cells.add( (long) columnIndex << 32 | rowIndex & 0xFFFFFFFFL );
    schedule();
  }

  /**************************************** redrawColumn *****************************************/
  public void redrawColumn( int columnIndex )
  {
    // request redraw visible bit of column including header
    m_columns.add( columnIndex );
    schedule();
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // request redraw visible bit of row including header
    m_rows.add( rowIndex );
    schedule();
  }

  /**************************************** getCellDrawer ****************************************/
  public CellDraw getCellDrawer()
  {
    // return new instance of class that draws table cells
    return new CellDraw();
  }

  /**************************************** getCellEditor ****************************************/
  public CellEditorBase getCellEditor( CellContext cell )
  {
    // return cell editor, or null if cell is read-only
    return null;
  }

  /****************************************** setValue *******************************************/
  public boolean setValue( int columnIndex, int rowIndex, Object newValue )
  {
    // if new value equals old value, exit with no command
    Object oldValue = getData().getValue( columnIndex, rowIndex );
    if ( Utils.equal( newValue, oldValue ) )
      return false;

    // push new command on undo-stack to update cell value
    getData().getUndoStack().push( new CommandSetValue( getData(), columnIndex, rowIndex, oldValue, newValue ) );
    return true;
  }

  /****************************************** redrawNow ******************************************/
  public void redrawNow()
  {
    // request complete redraw of table canvas (-1 and +1 to ensure canvas graphics context does a reset)
    widthChange( -1, getWidth() + 1 );
  }

  /*************************************** redrawCellNow *****************************************/
  public void redrawCellNow( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    CellDraw cell = getCellDrawer();
    if ( isVisible() && columnIndex >= HEADER && rowIndex >= HEADER )
    {
      cell.setIndex( getView(), columnIndex, rowIndex );
      cell.draw();
    }
  }

  /*************************************** redrawColumnNow ***************************************/
  public void redrawColumnNow( int columnIndex )
  {
    // redraw visible bit of column including header
    if ( isVisible() && columnIndex >= HEADER )
    {
      CellDraw cell = getCellDrawer();
      cell.view = getView();
      cell.gc = getCanvas().getGraphicsContext2D();
      cell.columnIndex = columnIndex;
      cell.columnPos = getColumns().getPositionFromIndex( columnIndex );

      // calculate which rows are visible
      int minRowPos = getRowPositionAtY( getColumnHeaderHeight() );
      int maxRowPos = getRowPositionAtY( (int) getCanvas().getHeight() );
      cell.x = getXStartFromColumnPos( cell.columnPos );
      cell.w = getColumns().getCellPixels( columnIndex );

      // redraw all body cells between min and max row positions inclusive
      int max = getData().getRowCount() - 1;
      if ( minRowPos <= max && maxRowPos >= FIRSTCELL )
      {
        if ( minRowPos < FIRSTCELL )
          minRowPos = FIRSTCELL;
        if ( maxRowPos > max )
          maxRowPos = max;

        cell.y = getYStartFromRowPos( minRowPos );
        for ( cell.rowPos = minRowPos; cell.rowPos <= maxRowPos; cell.rowPos++ )
        {
          cell.h = getYStartFromRowPos( cell.rowPos + 1 ) - cell.y;
          if ( cell.h > 0.0 )
          {
            cell.rowIndex = getRows().getIndexFromPosition( cell.rowPos );
            cell.draw();
            cell.y += cell.h;
          }
        }
      }

      // redraw column header
      cell.rowIndex = HEADER;
      cell.rowPos = HEADER;
      cell.y = 0.0;
      cell.h = getColumnHeaderHeight();
      cell.draw();
    }
  }

  /**************************************** redrawRowNow *****************************************/
  public void redrawRowNow( int rowIndex )
  {
    // redraw visible bit of row including header
    if ( isVisible() && rowIndex >= HEADER )
    {
      CellDraw cell = getCellDrawer();
      cell.view = getView();
      cell.gc = getCanvas().getGraphicsContext2D();
      cell.rowIndex = rowIndex;
      cell.rowPos = getRows().getPositionFromIndex( rowIndex );

      // calculate which columns are visible
      int minColumnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int maxColumnPos = getColumnPositionAtX( (int) getCanvas().getWidth() );
      cell.y = getYStartFromRowPos( cell.rowPos );
      cell.h = getRows().getCellPixels( rowIndex );

      // redraw all body cells between min and max column positions inclusive
      int max = getData().getColumnCount() - 1;
      if ( minColumnPos <= max && maxColumnPos >= FIRSTCELL )
      {
        if ( minColumnPos < FIRSTCELL )
          minColumnPos = FIRSTCELL;
        if ( maxColumnPos > max )
          maxColumnPos = max;

        cell.x = getXStartFromColumnPos( minColumnPos );
        for ( cell.columnPos = minColumnPos; cell.columnPos <= maxColumnPos; cell.columnPos++ )
        {
          cell.w = getXStartFromColumnPos( cell.columnPos + 1 ) - cell.x;
          if ( cell.w > 0.0 )
          {
            cell.columnIndex = getColumns().getIndexFromPosition( cell.columnPos );
            cell.draw();
            cell.x += cell.w;
          }
        }
      }

      // redraw row header
      cell.columnIndex = HEADER;
      cell.columnPos = HEADER;
      cell.x = 0.0;
      cell.w = getRowHeaderWidth();
      cell.draw();
    }
  }

  /************************************* redrawColumnsNow ****************************************/
  public void redrawColumnsNow( int minColumnPos, int maxColumnPos )
  {
    // redraw all table body columns between min and max column positions inclusive
    int max = getData().getColumnCount() - 1;
    if ( minColumnPos <= max && maxColumnPos >= FIRSTCELL )
    {
      if ( minColumnPos < FIRSTCELL )
        minColumnPos = FIRSTCELL;
      if ( maxColumnPos > max )
        maxColumnPos = max;

      for ( int pos = minColumnPos; pos <= maxColumnPos; pos++ )
        redrawColumnNow( getColumns().getIndexFromPosition( pos ) );
    }
  }

  /*************************************** redrawRowsNow *****************************************/
  public void redrawRowsNow( int minRowPos, int maxRowPos )
  {
    // redraw all table body rows between min and max row positions inclusive
    int max = getData().getRowCount() - 1;
    if ( minRowPos <= max && maxRowPos >= FIRSTCELL )
    {
      if ( minRowPos < FIRSTCELL )
        minRowPos = FIRSTCELL;
      if ( maxRowPos > max )
        maxRowPos = max;

      for ( int pos = minRowPos; pos <= maxRowPos; pos++ )
        redrawRowNow( getRows().getIndexFromPosition( pos ) );
    }
  }

  /************************************** redrawOverlayNow ***************************************/
  public void redrawOverlayNow()
  {
    // highlight focus cell with special border
    int focusColumnPos = getFocusCellProperty().getColumnPos();
    int focusRowPos = getFocusCellProperty().getRowPos();

    if ( focusColumnPos >= FIRSTCELL && focusRowPos >= FIRSTCELL )
    {
      GraphicsContext gc = getOverlay().getGraphicsContext2D();
      gc.clearRect( -1, -1, getOverlay().getWidth() + 1, getOverlay().getHeight() + 1 );

      if ( isTableFocused() )
        gc.setStroke( Colors.OVERLAY_FOCUS );
      else
        gc.setStroke( Colors.OVERLAY_FOCUS.desaturate() );

      double x = getXStartFromColumnPos( focusColumnPos );
      double y = getYStartFromRowPos( focusRowPos );
      double w = getXStartFromColumnPos( focusColumnPos + 1 ) - x;
      double h = getYStartFromRowPos( focusRowPos + 1 ) - y;

      // clip drawing to table body
      gc.save();
      gc.beginPath();
      gc.rect( getRowHeaderWidth() - 1, getColumnHeaderHeight() - 1, getCanvas().getWidth(), getCanvas().getHeight() );
      gc.clip();

      // draw special border
      gc.strokeRect( x - 0.5, y - 0.5, w, h );
      gc.strokeRect( x + 0.5, y + 0.5, w - 2, h - 2 );

      // remove clip
      gc.restore();
    }
  }

  /****************************************** getStatus ******************************************/
  public Status getStatus()
  {
    // return status associated with this view (might be null)
    return m_status;
  }

  /****************************************** setStatus ******************************************/
  public void setStatus( Status status )
  {
    // set status for this view
    m_status = status;
  }

}
