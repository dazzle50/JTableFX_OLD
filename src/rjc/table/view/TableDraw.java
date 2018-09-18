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

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import rjc.table.support.Utils;

/*************************************************************************************************/
/****************************** Table body and header cells drawing ******************************/
/*************************************************************************************************/

public class TableDraw extends TableXML
{
  protected int             m_columnIndex; // column index for current table body or header cell being drawn
  protected int             m_rowIndex;    // row index for current table body or header cell being drawn
  protected GraphicsContext gc;            // graphics context for current table body or header cell being drawn
  protected double          x;             // x coordinate for current table body or header cell being drawn
  protected double          y;             // y coordinate for current table body or header cell being drawn
  protected double          w;             // width for current table body or header cell being drawn
  protected double          h;             // height for current table body or header cell being drawn

  private Text              text;          // used in drawCellText

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // redraw table body or header cell
    x = getColumnIndexXStart( columnIndex );
    y = getRowIndexYStart( rowIndex );
    w = getColumnIndexWidth( columnIndex );
    h = getRowIndexHeight( rowIndex );
    m_view.drawCell();
  }

  /**************************************** redrawColumn *****************************************/
  public void redrawColumn( int columnIndex )
  {
    // redraw visible bit of column including header
    Utils.trace( columnIndex );
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

      y = getRowIndexYStart( getRowIndexFromPosition( minRowPos ) );
      for ( int pos = minRowPos; pos <= maxRowPos; pos++ )
      {
        m_rowIndex = getRowIndexFromPosition( pos );
        h = getRowIndexHeight( m_rowIndex );
        if ( h > 0 )
        {
          m_view.drawCell();
          y += h;
        }
      }
    }

    // redraw column header
    m_rowIndex = HEADER;
    y = 0.0;
    h = getColumnHeaderHeight();
    m_view.drawCell();
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // redraw visible bit of row including header
    Utils.trace( rowIndex );
    m_rowIndex = rowIndex;

    // calculate which columns are visible
    int minColumnPos = m_view.getColumnPositionAtX( getRowHeaderWidth() );
    int maxColumnPos = m_view.getColumnPositionAtX( (int) m_canvas.getWidth() );
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

      x = getColumnIndexXStart( getColumnIndexFromPosition( minColumnPos ) );
      for ( int pos = minColumnPos; pos <= maxColumnPos; pos++ )
      {
        m_columnIndex = getColumnIndexFromPosition( pos );
        w = getColumnIndexWidth( m_columnIndex );
        if ( w > 0 )
        {
          m_view.drawCell();
          x += w;
        }
      }
    }

    // redraw row header
    m_columnIndex = HEADER;
    x = 0.0;
    w = getRowHeaderWidth();
    m_view.drawCell();
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
  public String getCellText()
  {
    // return cell value as string
    if ( m_rowIndex == HEADER && m_columnIndex == HEADER )
      return null;
    if ( m_rowIndex == HEADER )
      return m_data.getColumnTitle( m_columnIndex );
    if ( m_columnIndex == HEADER )
      return m_data.getRowTitle( m_rowIndex );

    return m_data.getValue( m_columnIndex, m_rowIndex ).toString();
  }

  /************************************* drawCellBackground **************************************/
  public void drawCellBackground()
  {
    // draw cell background
    gc.setFill( m_view.getCellBackgroundPaint() );
    gc.fillRect( x, y, w, h );
  }

  /*************************************** drawCellBorder ****************************************/
  public void drawCellBorder()
  {
    // draw cell border
    gc.setStroke( m_view.getCellBorderPaint() );
    gc.strokeLine( x + w - 0.5, y + 0.5, x + w - 0.5, y + h - 0.5 );
    gc.strokeLine( x + 0.5, y + h - 0.5, x + w - 1.5, y + h - 0.5 );
  }

  /************************************** drawCellContent ****************************************/
  public void drawCellContent()
  {
    // draw cell contents
    drawCellText( getCellText() );
  }

  /**************************************** drawCellText *****************************************/
  public void drawCellText( String cellText )
  {
    // convert string into text lines
    TextLines lines = new TextLines( cellText, m_view, w, h );

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
