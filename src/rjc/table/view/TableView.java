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

import javafx.scene.canvas.GraphicsContext;
import rjc.table.Colors;
import rjc.table.Utils;
import rjc.table.cell.CellContext;
import rjc.table.cell.CellDraw;
import rjc.table.cell.CellEditorBase;
import rjc.table.data.TableData;
import rjc.table.undo.CommandSetValue;

/*************************************************************************************************/
/********************************** Base class for table views ***********************************/
/*************************************************************************************************/

public class TableView extends TableXML
{
  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // setup and register table view
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

  /**************************************** setValue ******************************************/
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

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    CellDraw cell = getCellDrawer();
    if ( isVisible() && columnIndex >= HEADER && rowIndex >= HEADER )
    {
      cell.setIndex( getView(), columnIndex, rowIndex );
      cell.draw();

      // redraw column header if overlaps row
      if ( rowIndex != HEADER && cell.y < getColumnHeaderHeight() )
        redrawRow( HEADER );

      // redraw row header if overlaps column
      if ( columnIndex != HEADER && cell.x < getRowHeaderWidth() )
        redrawColumn( HEADER );
    }
  }

  /**************************************** redrawColumn *****************************************/
  public void redrawColumn( int columnIndex )
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

        for ( cell.rowPos = minRowPos; cell.rowPos <= maxRowPos; cell.rowPos++ )
        {
          cell.rowIndex = getRows().getIndexFromPosition( cell.rowPos );
          cell.y = getYStartFromRowPos( cell.rowPos );
          cell.h = getYStartFromRowPos( cell.rowPos + 1 ) - cell.y;
          if ( cell.h > 0 )
            cell.draw();
        }
      }

      // redraw column header
      cell.rowIndex = HEADER;
      cell.rowPos = HEADER;
      cell.y = 0.0;
      cell.h = getColumnHeaderHeight();
      cell.draw();

      // redraw row header if overlaps column
      if ( columnIndex != HEADER && cell.x < getRowHeaderWidth() )
        redrawColumn( HEADER );
    }
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
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

        for ( cell.columnPos = minColumnPos; cell.columnPos <= maxColumnPos; cell.columnPos++ )
        {
          cell.columnIndex = getColumns().getIndexFromPosition( cell.columnPos );
          cell.x = getXStartFromColumnPos( cell.columnPos );
          cell.w = getXStartFromColumnPos( cell.columnPos + 1 ) - cell.x;
          if ( cell.w > 0 )
            cell.draw();
        }
      }

      // redraw row header
      cell.columnIndex = HEADER;
      cell.columnPos = HEADER;
      cell.x = 0.0;
      cell.w = getRowHeaderWidth();
      cell.draw();

      // redraw column header if overlaps row
      if ( rowIndex != HEADER && cell.y < getColumnHeaderHeight() )
        redrawRow( HEADER );
    }
  }

  /*************************************** redrawColumns *****************************************/
  public void redrawColumns( int minColumnPos, int maxColumnPos )
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
        redrawColumn( getColumns().getIndexFromPosition( pos ) );
    }
  }

  /***************************************** redrawRows ******************************************/
  public void redrawRows( int minRowPos, int maxRowPos )
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
        redrawRow( getRows().getIndexFromPosition( pos ) );
    }
  }

  /**************************************** redrawOverlay ****************************************/
  public void redrawOverlay()
  {
    // highlight focus cell with special border
    int focusColumnPos = getFocusCellProperty().getColumnPos();
    int focusRowPos = getFocusCellProperty().getRowPos();

    if ( focusColumnPos >= FIRSTCELL && focusRowPos >= FIRSTCELL )
    {
      GraphicsContext gc = getCanvas().getGraphicsContext2D();
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

}
