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

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import rjc.table.data.TableData;

/*************************************************************************************************/
/********************************** Base class for table views ***********************************/
/*************************************************************************************************/

public class TableView extends TableDraw
{
  protected final static Insets CELL_TEXT_INSERTS = new Insets( 0.0, 1.0, 1.0, 0.0 );

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // setup and register table view
    m_view = this;
    m_data = data;
    data.register( m_view );
    draw.addListener( ( observable, oldValue, newValue ) ->
    {
      if ( newValue )
        redraw();
    } );

    // create table canvas and scroll bars
    m_canvas = new TableCanvas( m_view );
    m_vScrollBar = new TableScrollBar( m_view, Orientation.VERTICAL );
    m_hScrollBar = new TableScrollBar( m_view, Orientation.HORIZONTAL );

    // add canvas and scroll bars to parent displayed children
    add( m_canvas );
    add( m_vScrollBar );
    add( m_hScrollBar );

    // setup graphics context
    gc = m_canvas.getGraphicsContext2D();
    gc.setFontSmoothingType( FontSmoothingType.LCD );
  }

  /************************************ getCellTextAlignment *************************************/
  protected Pos getCellTextAlignment()
  {
    // return cell text alignment
    return Pos.CENTER;
  }

  /************************************** getCellTextFont ****************************************/
  protected Font getCellTextFont()
  {
    // return cell text font (includes family, weight, posture, size)
    return Font.getDefault();
  }

  /************************************** getCellTextInsets **************************************/
  protected Insets getCellTextInsets()
  {
    // return cell text insets
    return CELL_TEXT_INSERTS;
  }

  /************************************* getCellBorderPaint **************************************/
  protected Paint getCellBorderPaint()
  {
    // return cell border paint
    return Color.gray( 0.8 );
  }

  /*********************************** getCellBackgroundPaint ************************************/
  protected Paint getCellBackgroundPaint()
  {
    // return cell background paint
    if ( m_columnIndex == HEADER )
      return isColumnSelected( m_columnPos ) ? Color.gray( 0.9 ) : Color.gray( 0.95 );
    else if ( m_rowIndex == HEADER )
      return isRowSelected( m_rowPos ) ? Color.gray( 0.9 ) : Color.gray( 0.95 );
    else if ( isCellSelected( m_columnPos, m_rowPos ) )
      return isTableFocused() ? Color.ALICEBLUE.saturate() : Color.ALICEBLUE;
    else
      return Color.WHITE;
  }

  /************************************** getCellTextPaint ***************************************/
  protected Paint getCellTextPaint()
  {
    // return cell text paint
    return Color.BLACK;
  }

  /************************************** getFocusCellPaint **************************************/
  protected Paint getFocusCellPaint()
  {
    // return focus cell paint
    if ( isTableFocused() )
      return Color.CORNFLOWERBLUE;
    else
      return Color.CORNFLOWERBLUE.desaturate();
  }

}
