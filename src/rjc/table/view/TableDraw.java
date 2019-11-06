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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import rjc.table.Colors;
import rjc.table.cell.CellText;

/*************************************************************************************************/
/****************************** Table body and header cells drawing ******************************/
/*************************************************************************************************/

public class TableDraw extends TableXML
{
  private TableCell cell = new TableCell(); // cell being drawn

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    if ( isVisible() && columnIndex >= HEADER && rowIndex >= HEADER )
    {
      cell.setContext( getView(), columnIndex, rowIndex );
      drawCell( cell );

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
            drawCell( cell );
        }
      }

      // redraw column header
      cell.rowIndex = HEADER;
      cell.rowPos = HEADER;
      cell.y = 0.0;
      cell.h = getColumnHeaderHeight();
      drawCell( cell );

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
            drawCell( cell );
        }
      }

      // redraw row header
      cell.columnIndex = HEADER;
      cell.columnPos = HEADER;
      cell.x = 0.0;
      cell.w = getRowHeaderWidth();
      drawCell( cell );

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

  /****************************************** drawCell *******************************************/
  protected void drawCell( TableCell cell )
  {
    // clip drawing to cell boundaries
    cell.gc.save();
    cell.gc.beginPath();
    cell.gc.rect( cell.x, cell.y, cell.w, cell.h );
    cell.gc.clip();

    // draw table body or header cell
    drawCellBackground( cell );
    drawCellContent( cell );
    drawCellBorder( cell );

    // remove clip
    cell.gc.restore();
  }

  /**************************************** redrawOverlay ****************************************/
  public void redrawOverlay()
  {
    // highlight focus cell with special border
    int focusColumnPos = getFocusCellProperty().getColumnPos();
    int focusRowPos = getFocusCellProperty().getRowPos();

    if ( focusColumnPos >= FIRSTCELL && focusRowPos >= FIRSTCELL )
    {
      if ( isTableFocused() )
        cell.gc.setStroke( Colors.OVERLAY_FOCUS );
      else
        cell.gc.setStroke( Colors.OVERLAY_FOCUS.desaturate() );

      cell.x = getXStartFromColumnPos( focusColumnPos );
      cell.y = getYStartFromRowPos( focusRowPos );
      cell.w = getXStartFromColumnPos( focusColumnPos + 1 ) - cell.x;
      cell.h = getYStartFromRowPos( focusRowPos + 1 ) - cell.y;

      // clip drawing to table body
      cell.gc.save();
      cell.gc.beginPath();
      cell.gc.rect( getRowHeaderWidth() - 1, getColumnHeaderHeight() - 1, getCanvas().getWidth(),
          getCanvas().getHeight() );
      cell.gc.clip();

      // draw special border
      cell.gc.strokeRect( cell.x - 0.5, cell.y - 0.5, cell.w, cell.h );
      cell.gc.strokeRect( cell.x + 0.5, cell.y + 0.5, cell.w - 2, cell.h - 2 );

      // remove clip
      cell.gc.restore();
    }
  }

  /************************************* drawCellBackground **************************************/
  protected void drawCellBackground( TableCell cell )
  {
    // draw cell background
    cell.gc.setFill( getView().getCellBackgroundPaint( cell ) );
    cell.gc.fillRect( cell.x, cell.y, cell.w, cell.h );
  }

  /*************************************** drawCellBorder ****************************************/
  protected void drawCellBorder( TableCell cell )
  {
    // draw cell border
    cell.gc.setStroke( getView().getCellBorderPaint( cell ) );
    cell.gc.strokeLine( cell.x + cell.w - 0.5, cell.y + 0.5, cell.x + cell.w - 0.5, cell.y + cell.h - 0.5 );
    cell.gc.strokeLine( cell.x + 0.5, cell.y + cell.h - 0.5, cell.x + cell.w - 1.5, cell.y + cell.h - 0.5 );
  }

  /************************************** drawCellContent ****************************************/
  protected void drawCellContent( TableCell cell )
  {
    // draw cell contents
    drawCellText( cell, getView().getCellText( cell ) );
  }

  /**************************************** drawCellText *****************************************/
  protected void drawCellText( TableCell cell, String cellText )
  {
    // get text inserts, font, alignment, and convert string into text lines
    Insets insets = getView().getZoomTextInsets( cell );
    Font font = getView().getZoomFont( cell );
    Pos alignment = getView().getCellTextAlignment( cell );
    CellText lines = new CellText( cellText, font, insets, alignment, cell.w, cell.h );

    // draw the lines on cell
    cell.gc.setFont( font );
    cell.gc.setFill( getView().getCellTextPaint( cell ) );
    int line = 0;
    while ( lines.getText( line ) != null )
    {
      cell.gc.fillText( lines.getText( line ), cell.x + lines.getX( line ), cell.y + lines.getY( line ) );
      line++;
    }
  }

}
