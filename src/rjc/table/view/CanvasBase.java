/**************************************************************************
 *  Copyright (C) 2022 by Richard Crook                                   *
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
import rjc.table.Colors;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cell.CellDraw;

/*************************************************************************************************/
/************************ Base canvas for table-views with redraw methods ************************/
/*************************************************************************************************/

public class CanvasBase extends Canvas
{
  protected TableView      m_view;
  protected Canvas         m_overlay;

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
  final static public int  BEFORE              = TableAxis.BEFORE;
  final static public int  AFTER               = TableAxis.AFTER;

  final static private int REDRAW_COUNT_MAX    = 1000;
  final static private int REDRAW_COUNT_CELL   = 1;
  final static private int REDRAW_COUNT_COLUMN = 20;
  final static private int REDRAW_COUNT_ROW    = 5;

  /**************************************** constructor ******************************************/
  public CanvasBase( TableView tableView )
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
      int minColumnPos = m_view.getColumnPositionAtX( m_view.getHeaderWidth() );
      int maxColumnPos = m_view.getColumnPositionAtX( (int) getWidth() );
      redrawColumnsNow( minColumnPos, maxColumnPos );
      redrawColumnNow( HEADER );
      redrawOverlayNow();
    }
  }

  /*************************************** redrawCellNow *****************************************/
  public void redrawCellNow( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    CellDraw cell = m_view.getCellDrawer();
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
    if ( isVisible() && columnIndex >= HEADER )
    {
      CellDraw cell = m_view.getCellDrawer();
      cell.view = m_view;
      cell.gc = getGraphicsContext2D();
      cell.columnIndex = columnIndex;
      cell.columnPos = m_view.getColumnsAxis().getPositionFromIndex( columnIndex );

      // calculate which rows are visible
      int minRowPos = m_view.getRowPositionAtY( m_view.getHeaderHeight() );
      int maxRowPos = m_view.getRowPositionAtY( (int) m_view.getCanvas().getHeight() );
      cell.x = m_view.getXStartFromColumnPos( cell.columnPos );
      cell.w = m_view.getColumnsAxis().getCellPixels( columnIndex );

      // redraw all body cells between min and max row positions inclusive
      int max = m_view.getData().getRowCount() - 1;
      if ( minRowPos <= max && maxRowPos >= FIRSTCELL )
      {
        if ( minRowPos < FIRSTCELL )
          minRowPos = FIRSTCELL;
        if ( maxRowPos > max )
          maxRowPos = max;

        cell.y = m_view.getYStartFromRowPos( minRowPos );
        for ( cell.rowPos = minRowPos; cell.rowPos <= maxRowPos; cell.rowPos++ )
        {
          cell.h = m_view.getYStartFromRowPos( cell.rowPos + 1 ) - cell.y;
          if ( cell.h > 0.0 )
          {
            cell.rowIndex = m_view.getRowsAxis().getIndexFromPosition( cell.rowPos );
            cell.draw();
            cell.y += cell.h;
          }
        }
      }

      // redraw column header
      cell.rowIndex = HEADER;
      cell.rowPos = HEADER;
      cell.y = 0.0;
      cell.h = m_view.getHeaderHeight();
      cell.draw();
    }
  }

  /**************************************** redrawRowNow *****************************************/
  public void redrawRowNow( int rowIndex )
  {
    // redraw visible bit of row including header
    if ( isVisible() && rowIndex >= HEADER )
    {
      CellDraw cell = m_view.getCellDrawer();
      cell.view = m_view;
      cell.gc = m_view.getCanvas().getGraphicsContext2D();
      cell.rowIndex = rowIndex;
      cell.rowPos = m_view.getRowsAxis().getPositionFromIndex( rowIndex );

      // calculate which columns are visible
      int minColumnPos = m_view.getColumnPositionAtX( m_view.getHeaderWidth() );
      int maxColumnPos = m_view.getColumnPositionAtX( (int) m_view.getCanvas().getWidth() );
      cell.y = m_view.getYStartFromRowPos( cell.rowPos );
      cell.h = m_view.getRowsAxis().getCellPixels( rowIndex );

      // redraw all body cells between min and max column positions inclusive
      int max = m_view.getData().getColumnCount() - 1;
      if ( minColumnPos <= max && maxColumnPos >= FIRSTCELL )
      {
        if ( minColumnPos < FIRSTCELL )
          minColumnPos = FIRSTCELL;
        if ( maxColumnPos > max )
          maxColumnPos = max;

        cell.x = m_view.getXStartFromColumnPos( minColumnPos );
        for ( cell.columnPos = minColumnPos; cell.columnPos <= maxColumnPos; cell.columnPos++ )
        {
          cell.w = m_view.getXStartFromColumnPos( cell.columnPos + 1 ) - cell.x;
          if ( cell.w > 0.0 )
          {
            cell.columnIndex = m_view.getColumnsAxis().getIndexFromPosition( cell.columnPos );
            cell.draw();
            cell.x += cell.w;
          }
        }
      }

      // redraw row header
      cell.columnIndex = HEADER;
      cell.columnPos = HEADER;
      cell.x = 0.0;
      cell.w = m_view.getHeaderWidth();
      cell.draw();
    }
  }

  /************************************* redrawColumnsNow ****************************************/
  public void redrawColumnsNow( int minColumnPos, int maxColumnPos )
  {
    // redraw all table body columns between min and max column positions inclusive
    int max = m_view.getData().getColumnCount() - 1;
    if ( minColumnPos <= max && maxColumnPos >= FIRSTCELL )
    {
      if ( minColumnPos < FIRSTCELL )
        minColumnPos = FIRSTCELL;
      if ( maxColumnPos > max )
        maxColumnPos = max;

      for ( int pos = minColumnPos; pos <= maxColumnPos; pos++ )
        redrawColumnNow( m_view.getColumnsAxis().getIndexFromPosition( pos ) );
    }
  }

  /*************************************** redrawRowsNow *****************************************/
  public void redrawRowsNow( int minRowPos, int maxRowPos )
  {
    // redraw all table body rows between min and max row positions inclusive
    int max = m_view.getData().getRowCount() - 1;
    if ( minRowPos <= max && maxRowPos >= FIRSTCELL )
    {
      if ( minRowPos < FIRSTCELL )
        minRowPos = FIRSTCELL;
      if ( maxRowPos > max )
        maxRowPos = max;

      for ( int pos = minRowPos; pos <= maxRowPos; pos++ )
        redrawRowNow( m_view.getRowsAxis().getIndexFromPosition( pos ) );
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
        gc.setStroke( Colors.OVERLAY_FOCUS );
      else
        gc.setStroke( Colors.OVERLAY_FOCUS.desaturate() );

      int focusColumnPos = m_view.getFocusCell().getColumnPos();
      int focusRowPos = m_view.getFocusCell().getRowPos();

      double x = m_view.getXStartFromColumnPos( focusColumnPos );
      double y = m_view.getYStartFromRowPos( focusRowPos );
      double w = m_view.getXStartFromColumnPos( focusColumnPos + 1 ) - x;
      double h = m_view.getYStartFromRowPos( focusRowPos + 1 ) - y;

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

}
