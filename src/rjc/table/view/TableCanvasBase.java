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
import javafx.scene.text.FontSmoothingType;
import rjc.table.Utils;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/************************ Base canvas for table-views with redraw methods ************************/
/*************************************************************************************************/

public class TableCanvasBase extends Canvas
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

  final static private int REDRAW_COUNT_MAX    = 1000;
  final static private int REDRAW_COUNT_CELL   = 1;
  final static private int REDRAW_COUNT_COLUMN = 20;
  final static private int REDRAW_COUNT_ROW    = 5;

  /**************************************** constructor ******************************************/
  public TableCanvasBase( TableView tableView )
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
      Utils.trace( "TODO" );
    }
  }

  /*************************************** redrawCellNow *****************************************/
  public void redrawCellNow( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    Utils.trace( "TODO" );
  }

  /*************************************** redrawColumnNow ***************************************/
  public void redrawColumnNow( int columnIndex )
  {
    // redraw visible bit of column including header
    Utils.trace( "TODO" );
  }

  /**************************************** redrawRowNow *****************************************/
  public void redrawRowNow( int rowIndex )
  {
    // redraw visible bit of row including header
    Utils.trace( "TODO" );
  }

  /************************************* redrawColumnsNow ****************************************/
  public void redrawColumnsNow( int minColumnPos, int maxColumnPos )
  {
    // redraw all table body columns between min and max column positions inclusive
    Utils.trace( "TODO" );
  }

  /*************************************** redrawRowsNow *****************************************/
  public void redrawRowsNow( int minRowPos, int maxRowPos )
  {
    // redraw all table body rows between min and max row positions inclusive
    Utils.trace( "TODO" );
  }

  /************************************** redrawOverlayNow ***************************************/
  public void redrawOverlayNow()
  {
    // highlight focus cell with special border
    Utils.trace( "TODO" );
  }

}