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

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.FontSmoothingType;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cell.CellDrawer;

/*************************************************************************************************/
/************************ Base canvas for table-views with redraw methods ************************/
/*************************************************************************************************/

public class TableCanvasDraw extends Canvas
{
  private TableView        m_view;
  private Canvas           m_overlay;

  private AtomicBoolean    m_redrawIsRequested;                      // flag if redraw has been scheduled
  private boolean          m_fullRedraw;                             // full view redraw (headers & body)
  private HashSet<Integer> m_columns;                                // requested column indexes
  private HashSet<Integer> m_rows;                                   // requested row indexes
  private HashSet<Long>    m_cells;                                  // long = (long) column << 32 | row & 0xFFFFFFFFL
  private int              m_redrawCount;                            // count to ensure canvas cleared periodically

  // column & row index starts at 0 for table body, index of -1 is for axis header
  final static public int  INVALID             = TableAxis.INVALID;
  final static public int  HEADER              = TableAxis.HEADER;
  final static public int  FIRSTCELL           = TableAxis.FIRSTCELL;

  final static private int REDRAW_COUNT_MAX    = 1000;
  final static private int REDRAW_COUNT_CELL   = 1;
  final static private int REDRAW_COUNT_COLUMN = 20;
  final static private int REDRAW_COUNT_ROW    = 5;

  /**************************************** constructor ******************************************/
  public TableCanvasDraw( TableView tableView )
  {
    // prepare main & overlay canvas
    m_view = tableView;
    m_overlay = new Canvas();
    m_redrawIsRequested = new AtomicBoolean();
    m_columns = new HashSet<>();
    m_rows = new HashSet<>();
    m_cells = new HashSet<>();

    getGraphicsContext2D().setFontSmoothingType( FontSmoothingType.LCD );
    m_overlay.getGraphicsContext2D().setFontSmoothingType( FontSmoothingType.LCD );
  }

  /******************************************* redraw ********************************************/
  public void redraw()
  {
    // request redraw full visible table (headers and body)
    if ( m_fullRedraw )
      return;
    m_fullRedraw = true;
    schedule();
  }

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // request redraw specified table body or header cell
    if ( m_fullRedraw )
      return;
    if ( m_cells.add( (long) columnIndex << 32 | rowIndex & 0xFFFFFFFFL ) )
      m_redrawCount += REDRAW_COUNT_CELL;
    schedule();
  }

  /**************************************** redrawColumn *****************************************/
  public void redrawColumn( int columnIndex )
  {
    // request redraw visible bit of column including header
    if ( m_fullRedraw )
      return;
    if ( m_columns.add( columnIndex ) )
      m_redrawCount += REDRAW_COUNT_COLUMN;
    schedule();
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // request redraw visible bit of row including header
    if ( m_fullRedraw )
      return;
    if ( m_rows.add( rowIndex ) )
      m_redrawCount += REDRAW_COUNT_ROW;
    schedule();
  }

  /****************************************** schedule *******************************************/
  private void schedule()
  {
    // schedule redrawing of what has been requested
    if ( m_redrawIsRequested.compareAndSet( false, true ) )
      Platform.runLater( () -> performRedraws() );
  }

  /*************************************** performRedraws ****************************************/
  private void performRedraws()
  {
    // redraw parts of table that have been requested
    m_redrawIsRequested.set( false );
    if ( m_fullRedraw || m_redrawCount > REDRAW_COUNT_MAX )
    {
      // full redraw requested so don't need to redraw anything else
      redrawNow();
      m_redrawCount = 0;
    }
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

  /****************************************** redrawNow ******************************************/
  public void redrawNow()
  {
    // request complete redraw of table canvas
    if ( isVisible() && getHeight() > 0.0 )
    {
      getGraphicsContext2D().clearRect( 0.0, 0.0, getWidth(), getHeight() );
      int minColumnPos = m_view.getColumnIndex( m_view.getHeaderWidth() );
      int maxColumnPos = m_view.getColumnIndex( (int) getWidth() );
      redrawColumnsNow( minColumnPos, maxColumnPos );
      redrawColumnNow( HEADER );
      redrawOverlayNow();
    }
  }

  /*************************************** redrawCellNow *****************************************/
  public void redrawCellNow( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    CellDrawer cell = m_view.getCellDrawer();
    if ( isVisible() && columnIndex >= HEADER && rowIndex >= HEADER )
    {
      cell.setIndex( m_view, columnIndex, rowIndex );
      cell.draw();
    }
  }

  /*************************************** redrawColumnNow ***************************************/
  public void redrawColumnNow( int columnIndex )
  {
    // redraw visible bit of column including header
    CellDrawer cell = m_view.getCellDrawer();
    cell.view = m_view;
    cell.gc = getGraphicsContext2D();
    cell.columnIndex = columnIndex;

    // calculate which rows are visible
    int minRow = m_view.getRowIndex( m_view.getHeaderHeight() );
    int maxRow = m_view.getRowIndex( (int) m_view.getCanvas().getHeight() );
    cell.x = m_view.getColumnStartX( columnIndex );
    cell.w = m_view.getColumnsAxis().getIndexPixels( columnIndex );
    if ( cell.w == 0.0 )
      return;

    // redraw all body cells between min and max row positions inclusive
    int max = m_view.getData().getRowCount() - 1;
    if ( minRow < FIRSTCELL )
      minRow = FIRSTCELL;
    if ( maxRow > max )
      maxRow = max;

    cell.y = m_view.getRowStartY( minRow );
    for ( cell.rowIndex = minRow; cell.rowIndex <= maxRow; cell.rowIndex++ )
    {
      cell.h = m_view.getRowStartY( cell.rowIndex + 1 ) - cell.y;
      if ( cell.h > 0.0 )
      {
        cell.draw();
        cell.y += cell.h;
      }
    }

    // redraw column header
    cell.rowIndex = HEADER;
    cell.y = 0.0;
    cell.h = m_view.getHeaderHeight();
    cell.draw();
  }

  /**************************************** redrawRowNow *****************************************/
  public void redrawRowNow( int rowIndex )
  {
    // redraw visible bit of row including header
    if ( isVisible() && rowIndex >= HEADER )
    {
      CellDrawer cell = m_view.getCellDrawer();
      cell.view = m_view;
      cell.gc = m_view.getCanvas().getGraphicsContext2D();
      cell.rowIndex = rowIndex;

      // calculate which columns are visible
      int minColumn = m_view.getColumnIndex( m_view.getHeaderWidth() );
      int maxColumn = m_view.getColumnIndex( (int) m_view.getCanvas().getWidth() );
      cell.y = m_view.getRowStartY( rowIndex );
      cell.h = m_view.getRowsAxis().getIndexPixels( rowIndex );

      // redraw all body cells between min and max column positions inclusive
      int max = m_view.getData().getColumnCount() - 1;
      if ( minColumn < FIRSTCELL )
        minColumn = FIRSTCELL;
      if ( maxColumn > max )
        maxColumn = max;

      cell.x = m_view.getColumnStartX( minColumn );
      for ( cell.columnIndex = minColumn; cell.columnIndex <= maxColumn; cell.columnIndex++ )
      {
        cell.w = m_view.getColumnStartX( cell.columnIndex + 1 ) - cell.x;
        if ( cell.w > 0.0 )
        {
          cell.draw();
          cell.x += cell.w;
        }
      }

      // redraw row header
      cell.columnIndex = HEADER;
      cell.columnIndex = HEADER;
      cell.x = 0.0;
      cell.w = m_view.getHeaderWidth();
      cell.draw();
    }
  }

  /************************************* redrawColumnsNow ****************************************/
  public void redrawColumnsNow( int minColumn, int maxColumn )
  {
    // redraw all table body columns between min and max column positions inclusive
    int max = m_view.getData().getColumnCount() - 1;
    if ( minColumn <= max && maxColumn >= FIRSTCELL )
    {
      if ( minColumn < FIRSTCELL )
        minColumn = FIRSTCELL;
      if ( maxColumn > max )
        maxColumn = max;

      for ( int index = minColumn; index <= maxColumn; index++ )
        redrawColumnNow( index );
    }
  }

  /*************************************** redrawRowsNow *****************************************/
  public void redrawRowsNow( int minRow, int maxRow )
  {
    // redraw all table body rows between min and max row positions inclusive
    int max = m_view.getData().getRowCount() - 1;
    if ( minRow <= max && maxRow >= FIRSTCELL )
    {
      if ( minRow < FIRSTCELL )
        minRow = FIRSTCELL;
      if ( maxRow > max )
        maxRow = max;

      for ( int index = minRow; index <= maxRow; index++ )
        redrawRowNow( index );
    }
  }

  /************************************** redrawOverlayNow ***************************************/
  public void redrawOverlayNow()
  {
    // highlight focus cell with special border
    if ( m_view.getFocusCell().isVisible() )
    {
      GraphicsContext gc = m_overlay.getGraphicsContext2D();
      gc.clearRect( 0.0, 0.0, m_overlay.getWidth(), m_overlay.getHeight() );

      if ( isFocused() )
        gc.setStroke( Colours.OVERLAY_FOCUS );
      else
        gc.setStroke( Colours.OVERLAY_FOCUS.desaturate() );

      int focusColumnPos = m_view.getFocusCell().getColumn();
      int focusRowPos = m_view.getFocusCell().getRow();

      double x = m_view.getColumnStartX( focusColumnPos );
      double y = m_view.getRowStartY( focusRowPos );
      double w = m_view.getColumnStartX( focusColumnPos + 1 ) - x;
      double h = m_view.getRowStartY( focusRowPos + 1 ) - y;

      // clip drawing to table body
      gc.save();
      gc.beginPath();
      gc.rect( m_view.getHeaderWidth() - 1, m_view.getHeaderHeight() - 1, getWidth(), getHeight() );
      gc.clip();

      // draw special border
      gc.strokeRect( x - 0.5, y - 0.5, w, h );
      gc.strokeRect( x + 0.5, y + 0.5, w - 2, h - 2 );

      // remove clip
      gc.restore();
    }
  }

  /***************************************** getOverlay ******************************************/
  public Canvas getOverlay()
  {
    // return the table canvas overlay
    return m_overlay;
  }

}