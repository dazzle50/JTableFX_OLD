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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rjc.table.cell.CellText;

/*************************************************************************************************/
/****************************** Table body and header cells drawing ******************************/
/*************************************************************************************************/

public class TableDraw extends TableXML
{
  protected int             m_columnIndex; // column index for current table body or header cell being drawn
  protected int             m_rowIndex;    // row index for current table body or header cell being drawn
  protected int             m_columnPos;   // column position for current table body or header cell being drawn
  protected int             m_rowPos;      // row position for current table body or header cell being drawn
  protected GraphicsContext gc;            // graphics context for current table body or header cell being drawn
  protected double          x;             // x coordinate for current table body or header cell being drawn
  protected double          y;             // y coordinate for current table body or header cell being drawn
  protected double          w;             // width for current table body or header cell being drawn
  protected double          h;             // height for current table body or header cell being drawn

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    if ( isVisible() && columnIndex >= HEADER && rowIndex >= HEADER )
    {
      m_columnIndex = columnIndex;
      m_rowIndex = rowIndex;
      m_columnPos = m_columns.getPositionFromIndex( columnIndex );
      m_rowPos = m_rows.getPositionFromIndex( rowIndex );
      x = getXStartFromColumnPos( m_columnPos );
      y = getYStartFromRowPos( m_rowPos );
      w = m_columns.getCellPixels( columnIndex );
      h = m_rows.getCellPixels( rowIndex );
      drawCell();

      // redraw column header if overlaps row
      if ( rowIndex != HEADER && y < getColumnHeaderHeight() )
        redrawRow( HEADER );

      // redraw row header if overlaps column
      if ( columnIndex != HEADER && x < getRowHeaderWidth() )
        redrawColumn( HEADER );
    }
  }

  /**************************************** redrawColumn *****************************************/
  public void redrawColumn( int columnIndex )
  {
    // redraw visible bit of column including header
    if ( isVisible() && columnIndex >= HEADER )
    {
      m_columnIndex = columnIndex;
      m_columnPos = m_columns.getPositionFromIndex( columnIndex );

      // calculate which rows are visible
      int minRowPos = getRowPositionAtY( getColumnHeaderHeight() );
      int maxRowPos = getRowPositionAtY( (int) m_canvas.getHeight() );
      x = getXStartFromColumnPos( m_columnPos );
      w = m_columns.getCellPixels( columnIndex );

      // redraw all body cells between min and max row positions inclusive
      int max = m_data.getRowCount() - 1;
      if ( minRowPos <= max && maxRowPos >= FIRSTCELL )
      {
        if ( minRowPos < FIRSTCELL )
          minRowPos = FIRSTCELL;
        if ( maxRowPos > max )
          maxRowPos = max;

        for ( m_rowPos = minRowPos; m_rowPos <= maxRowPos; m_rowPos++ )
        {
          m_rowIndex = m_rows.getIndexFromPosition( m_rowPos );
          y = getYStartFromRowPos( m_rowPos );
          h = getYStartFromRowPos( m_rowPos + 1 ) - y;
          if ( h > 0 )
            drawCell();
        }
      }

      // redraw column header
      m_rowIndex = HEADER;
      m_rowPos = HEADER;
      y = 0.0;
      h = getColumnHeaderHeight();
      drawCell();

      // redraw row header if overlaps column
      if ( columnIndex != HEADER && x < getRowHeaderWidth() )
        redrawColumn( HEADER );
    }
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // redraw visible bit of row including header
    if ( isVisible() && rowIndex >= HEADER )
    {
      m_rowIndex = rowIndex;
      m_rowPos = m_rows.getPositionFromIndex( rowIndex );

      // calculate which columns are visible
      int minColumnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int maxColumnPos = getColumnPositionAtX( (int) m_canvas.getWidth() );
      y = getYStartFromRowPos( m_rowPos );
      h = m_rows.getCellPixels( rowIndex );

      // redraw all body cells between min and max column positions inclusive
      int max = m_data.getColumnCount() - 1;
      if ( minColumnPos <= max && maxColumnPos >= FIRSTCELL )
      {
        if ( minColumnPos < FIRSTCELL )
          minColumnPos = FIRSTCELL;
        if ( maxColumnPos > max )
          maxColumnPos = max;

        for ( m_columnPos = minColumnPos; m_columnPos <= maxColumnPos; m_columnPos++ )
        {
          m_columnIndex = m_columns.getIndexFromPosition( m_columnPos );
          x = getXStartFromColumnPos( m_columnPos );
          w = getXStartFromColumnPos( m_columnPos + 1 ) - x;
          if ( w > 0 )
            drawCell();
        }
      }

      // redraw row header
      m_columnIndex = HEADER;
      m_columnPos = HEADER;
      x = 0.0;
      w = getRowHeaderWidth();
      drawCell();

      // redraw column header if overlaps row
      if ( rowIndex != HEADER && y < getColumnHeaderHeight() )
        redrawRow( HEADER );
    }
  }

  /*************************************** redrawColumns *****************************************/
  public void redrawColumns( int minColumnPos, int maxColumnPos )
  {
    // redraw all table body columns between min and max column positions inclusive
    int max = m_data.getColumnCount() - 1;
    if ( minColumnPos <= max && maxColumnPos >= FIRSTCELL )
    {
      if ( minColumnPos < FIRSTCELL )
        minColumnPos = FIRSTCELL;
      if ( maxColumnPos > max )
        maxColumnPos = max;

      for ( int pos = minColumnPos; pos <= maxColumnPos; pos++ )
        redrawColumn( m_columns.getIndexFromPosition( pos ) );
    }
  }

  /***************************************** redrawRows ******************************************/
  public void redrawRows( int minRowPos, int maxRowPos )
  {
    // redraw all table body rows between min and max row positions inclusive
    int max = m_data.getRowCount() - 1;
    if ( minRowPos <= max && maxRowPos >= FIRSTCELL )
    {
      if ( minRowPos < FIRSTCELL )
        minRowPos = FIRSTCELL;
      if ( maxRowPos > max )
        maxRowPos = max;

      for ( int pos = minRowPos; pos <= maxRowPos; pos++ )
        redrawRow( m_rows.getIndexFromPosition( pos ) );
    }
  }

  /****************************************** drawCell *******************************************/
  protected void drawCell()
  {
    // clip drawing to cell boundaries
    gc.save();
    gc.beginPath();
    gc.rect( x, y, w, h );
    gc.clip();

    // draw table body or header cell
    drawCellBackground();
    drawCellContent();
    drawCellBorder();

    // remove clip
    gc.restore();
  }

  /**************************************** redrawOverlay ****************************************/
  public void redrawOverlay()
  {
    // highlight focus cell with special border
    if ( getFocusColumnPosition() >= FIRSTCELL && getFocusRowPosition() >= FIRSTCELL )
    {
      if ( isTableFocused() )
        gc.setStroke( Color.CORNFLOWERBLUE );
      else
        gc.setStroke( Color.CORNFLOWERBLUE.desaturate() );

      x = getXStartFromColumnPos( getFocusColumnPosition() );
      y = getYStartFromRowPos( getFocusRowPosition() );
      w = getXStartFromColumnPos( getFocusColumnPosition() + 1 ) - x;
      h = getYStartFromRowPos( getFocusRowPosition() + 1 ) - y;

      // clip drawing to table body
      gc.save();
      gc.beginPath();
      gc.rect( getRowHeaderWidth() - 1, getColumnHeaderHeight() - 1, m_canvas.getWidth(), m_canvas.getHeight() );
      gc.clip();

      // draw special border
      gc.strokeRect( x - 0.5, y - 0.5, w, h );
      gc.strokeRect( x + 0.5, y + 0.5, w - 2, h - 2 );

      // remove clip
      gc.restore();
    }
  }

  /************************************* drawCellBackground **************************************/
  protected void drawCellBackground()
  {
    // draw cell background
    gc.setFill( m_view.getCellBackgroundPaint() );
    gc.fillRect( x, y, w, h );
  }

  /*************************************** drawCellBorder ****************************************/
  protected void drawCellBorder()
  {
    // draw cell border
    gc.setStroke( m_view.getCellBorderPaint() );
    gc.strokeLine( x + w - 0.5, y + 0.5, x + w - 0.5, y + h - 0.5 );
    gc.strokeLine( x + 0.5, y + h - 0.5, x + w - 1.5, y + h - 0.5 );
  }

  /************************************** drawCellContent ****************************************/
  protected void drawCellContent()
  {
    // draw cell contents
    drawCellText( m_view.getCellText() );
  }

  /**************************************** drawCellText *****************************************/
  protected void drawCellText( String cellText )
  {
    // get text inserts adjusted for zoom
    Insets insets = m_view.getCellTextInsets();
    if ( getZoom() != 1.0 )
      insets = new Insets( insets.getTop() * getZoom(), insets.getRight() * getZoom(), insets.getBottom() * getZoom(),
          insets.getLeft() * getZoom() );

    // get font adjusted for zoom
    Font font = Font.font( m_view.getCellTextFamily(), m_view.getCellTextWeight(), m_view.getCellTextPosture(),
        m_view.getCellTextSize() * getZoom() );

    // get alignment and convert string into text lines
    Pos alignment = m_view.getCellTextAlignment();
    CellText lines = new CellText( cellText, font, insets, alignment, w, h );

    // draw the lines on cell
    gc.setFont( font );
    gc.setFill( m_view.getCellTextPaint() );
    int line = 0;
    while ( lines.getText( line ) != null )
    {
      gc.fillText( lines.getText( line ), x + lines.getX( line ), y + lines.getY( line ) );
      line++;
    }
  }

}
