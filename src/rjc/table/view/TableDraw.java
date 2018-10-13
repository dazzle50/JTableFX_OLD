/**************************************************************************
 *  Copyright (C) 2018 by Richard Crook                                   *
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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.GraphicsContext;
import rjc.table.support.Utils;

/*************************************************************************************************/
/****************************** Table body and header cells drawing ******************************/
/*************************************************************************************************/

public class TableDraw extends TableXML
{
  protected int                      m_columnIndex;                      // column index for current table body or header cell being drawn
  protected int                      m_rowIndex;                         // row index for current table body or header cell being drawn
  protected GraphicsContext          gc;                                 // graphics context for current table body or header cell being drawn
  protected double                   x;                                  // x coordinate for current table body or header cell being drawn
  protected double                   y;                                  // y coordinate for current table body or header cell being drawn
  protected double                   w;                                  // width for current table body or header cell being drawn
  protected double                   h;                                  // height for current table body or header cell being drawn

  public final SimpleBooleanProperty draw = new SimpleBooleanProperty(); // flag to say if drawing should occur, defaults to false

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    if ( draw.get() && columnIndex >= HEADER && rowIndex >= HEADER )
    {
      m_columnIndex = columnIndex;
      m_rowIndex = rowIndex;
      x = getColumnIndexXStart( columnIndex );
      y = getRowIndexYStart( rowIndex );
      w = getColumnIndexWidth( columnIndex );
      h = getRowIndexHeight( rowIndex );
      m_view.drawCell();

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
    if ( draw.get() && columnIndex >= HEADER )
    {
      m_columnIndex = columnIndex;

      // calculate which rows are visible
      int minRowPos = getRowPositionAtY( getColumnHeaderHeight() );
      int maxRowPos = getRowPositionAtY( (int) m_canvas.getHeight() );
      x = getColumnIndexXStart( m_columnIndex );
      w = getColumnIndexWidth( m_columnIndex );

      // redraw all body cells between min and max row positions inclusive
      int max = m_data.getRowCount() - 1;
      if ( minRowPos <= max && maxRowPos >= 0 )
      {
        if ( minRowPos < 0 )
          minRowPos = 0;
        if ( maxRowPos > max )
          maxRowPos = max;

        for ( int pos = minRowPos; pos <= maxRowPos; pos++ )
        {
          m_rowIndex = getRowIndexFromPosition( pos );
          y = getRowPositionYStart( pos );
          h = getRowPositionYStart( pos + 1 ) - y;
          if ( h > 0 )
            m_view.drawCell();
        }
      }

      // redraw column header
      m_rowIndex = HEADER;
      y = 0.0;
      h = getColumnHeaderHeight();
      m_view.drawCell();

      // redraw row header if overlaps column
      if ( columnIndex != HEADER && x < getRowHeaderWidth() )
        redrawColumn( HEADER );
    }
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // redraw visible bit of row including header
    if ( draw.get() && rowIndex >= HEADER )
    {
      m_rowIndex = rowIndex;

      // calculate which columns are visible
      int minColumnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int maxColumnPos = getColumnPositionAtX( (int) m_canvas.getWidth() );
      y = getRowIndexYStart( m_rowIndex );
      h = getRowIndexHeight( m_rowIndex );

      // redraw all body cells between min and max column positions inclusive
      int max = m_data.getColumnCount() - 1;
      if ( minColumnPos <= max && maxColumnPos >= 0 )
      {
        if ( minColumnPos < 0 )
          minColumnPos = 0;
        if ( maxColumnPos > max )
          maxColumnPos = max;

        for ( int pos = minColumnPos; pos <= maxColumnPos; pos++ )
        {
          m_columnIndex = getColumnIndexFromPosition( pos );
          x = getColumnPositionXStart( pos );
          w = getColumnPositionXStart( pos + 1 ) - x;
          if ( w > 0 )
            m_view.drawCell();
        }
      }

      // redraw row header
      m_columnIndex = HEADER;
      x = 0.0;
      w = getRowHeaderWidth();
      m_view.drawCell();

      // redraw column header if overlaps row
      if ( rowIndex != HEADER && y < getColumnHeaderHeight() )
        redrawRow( HEADER );
    }
  }

  /******************************************** reset ********************************************/
  public void reset()
  {
    // TODO #########################################################################
    Utils.trace( "RESET" );
  }

  /*************************************** redrawColumns *****************************************/
  public void redrawColumns( int minColumnPos, int maxColumnPos )
  {
    // redraw all columns between min and max column positions inclusive
    int max = m_data.getColumnCount() - 1;
    if ( minColumnPos <= max && maxColumnPos >= 0 )
    {
      if ( minColumnPos < 0 )
        minColumnPos = 0;
      if ( maxColumnPos > max )
        maxColumnPos = max;

      for ( int pos = minColumnPos; pos <= maxColumnPos; pos++ )
        redrawColumn( getColumnIndexFromPosition( pos ) );
    }
  }

  /***************************************** redrawRows ******************************************/
  public void redrawRows( int minRowPos, int maxRowPos )
  {
    // redraw all rows between min and max row positions inclusive
    int max = m_data.getRowCount() - 1;
    if ( minRowPos <= max && maxRowPos >= 0 )
    {
      if ( minRowPos < 0 )
        minRowPos = 0;
      if ( maxRowPos > max )
        maxRowPos = max;

      for ( int pos = minRowPos; pos <= maxRowPos; pos++ )
        redrawRow( getRowIndexFromPosition( pos ) );
    }
  }

  /**************************************** getCellText ******************************************/
  protected String getCellText()
  {
    // return cell value as string
    if ( m_rowIndex == HEADER && m_columnIndex == HEADER )
      return null;
    if ( m_rowIndex == HEADER )
      return m_data.getColumnTitle( m_columnIndex );
    if ( m_columnIndex == HEADER )
      return m_data.getRowTitle( m_rowIndex );

    Object value = m_data.getValue( m_columnIndex, m_rowIndex );
    return value == null ? null : value.toString();
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
    drawCellText( getCellText() );
  }

  /**************************************** drawCellText *****************************************/
  protected void drawCellText( String cellText )
  {
    // convert string into text lines
    CellText lines = new CellText( cellText, m_view, w, h );

    // draw the lines on cell
    int line = 0;
    while ( lines.getText( line ) != null )
    {
      gc.setFill( m_view.getCellTextPaint() );
      gc.fillText( lines.getText( line ), x + lines.getX( line ), y + lines.getY( line ) );
      line++;
    }
  }

}
