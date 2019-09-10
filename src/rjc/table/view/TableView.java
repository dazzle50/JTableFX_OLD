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
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import rjc.table.Colors;
import rjc.table.cell.CellEditorBase;
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

    // create table canvas, axis and scroll bars
    m_canvas = new Canvas();
    m_columns = new TableAxis( data.getColumnCountProperty() );
    m_rows = new TableAxis( data.getRowCountProperty() );
    m_hScrollBar = new TableScrollBar( m_columns, Orientation.HORIZONTAL );
    m_vScrollBar = new TableScrollBar( m_rows, Orientation.VERTICAL );

    // when canvas size changes draw new areas
    m_canvas.widthProperty()
        .addListener( ( observable, oldW, newW ) -> widthChange( oldW.intValue(), newW.intValue() ) );
    m_canvas.heightProperty()
        .addListener( ( observable, oldH, newH ) -> heightChange( oldH.intValue(), newH.intValue() ) );

    // redraw table when focus changes or becomes visible
    m_canvas.focusedProperty().addListener( ( observable, oldF, newF ) -> redraw() );
    visibleProperty().addListener( ( observable, oldV, newV ) -> redraw() );

    // react to mouse events
    m_canvas.setOnMouseExited( event -> mouseExited( event ) );
    m_canvas.setOnMouseEntered( event -> mouseEntered( event ) );
    m_canvas.setOnMouseMoved( event -> mouseMoved( event ) );
    m_canvas.setOnMouseDragged( event -> mouseDragged( event ) );
    m_canvas.setOnMouseReleased( event -> mouseReleased( event ) );
    m_canvas.setOnMousePressed( event -> mousePressed( event ) );
    m_canvas.setOnMouseClicked( event -> mouseClicked( event ) );
    m_canvas.setOnScroll( event -> mouseScroll( event ) );

    // react to keyboard events
    m_canvas.setOnKeyPressed( event -> keyPressed( event ) );
    m_canvas.setOnKeyTyped( event -> keyTyped( event ) );

    // react to scroll bar position value changes
    m_hScrollBar.valueProperty().addListener( ( observable, oldV, newV ) -> tableScrolled() );
    m_vScrollBar.valueProperty().addListener( ( observable, oldV, newV ) -> tableScrolled() );

    // add canvas and scroll bars to parent displayed children
    add( m_canvas );
    add( m_vScrollBar );
    add( m_hScrollBar );

    // setup graphics context & reset view
    gc = m_canvas.getGraphicsContext2D();
    gc.setFontSmoothingType( FontSmoothingType.LCD );
    reset();
  }

  /******************************************** reset ********************************************/
  public void reset()
  {
    // reset table view to default settings
    m_columns.reset();
    m_rows.reset();

    m_rows.setDefaultSize( 20 );
    m_rows.setHeaderSize( 20 );
  }

  /**************************************** getCellText ******************************************/
  protected String getCellText()
  {
    // return cell value as string
    Object value = m_data.getValue( m_columnIndex, m_rowIndex );
    return value == null ? null : value.toString();
  }

  /************************************ getCellTextAlignment *************************************/
  protected Pos getCellTextAlignment()
  {
    // return cell text alignment
    return Pos.CENTER;
  }

  /************************************* getCellTextFamily ***************************************/
  protected String getCellTextFamily()
  {
    // return cell text family
    return Font.getDefault().getFamily();
  }

  /************************************** getCellTextSize ****************************************/
  protected double getCellTextSize()
  {
    // return cell text family
    return Font.getDefault().getSize();
  }

  /************************************* getCellTextWeight ***************************************/
  protected FontWeight getCellTextWeight()
  {
    // return cell text weight
    return FontWeight.NORMAL;
  }

  /************************************* getCellTextPosture **************************************/
  protected FontPosture getCellTextPosture()
  {
    // return cell text posture
    return FontPosture.REGULAR;
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
    return Colors.CELL_BORDER;
  }

  /*********************************** getCellBackgroundPaint ************************************/
  protected Paint getCellBackgroundPaint()
  {
    // return cell background paint, starting with header cells
    if ( m_rowIndex == HEADER || m_columnIndex == HEADER )
      return getCellBackgroundPaintHeader();

    // for selected cells
    if ( isCellSelected( m_columnPos, m_rowPos ) )
      return getCellBackgroundPaintSelected();

    // otherwise default background
    return getCellBackgroundPaintDefault();
  }

  /******************************** getCellBackgroundPaintDefault ********************************/
  protected Paint getCellBackgroundPaintDefault()
  {
    // default table cell background
    return Colors.CELL_DEFAULT_FILL;
  }

  /******************************** getCellBackgroundPaintHeader *********************************/
  protected Paint getCellBackgroundPaintHeader()
  {
    // return header cell background
    if ( m_rowIndex == HEADER )
    {
      if ( m_columnPos == getFocusColumnPosition() )
        return Colors.HEADER_FOCUS;
      else
        return hasColumnSelection( m_columnPos ) ? Colors.HEADER_SELECTED_FILL : Colors.HEADER_DEFAULT_FILL;
    }

    if ( m_columnIndex == HEADER )
    {
      if ( m_rowPos == getFocusRowPosition() )
        return Colors.HEADER_FOCUS;
      else
        return hasRowSelection( m_rowPos ) ? Colors.HEADER_SELECTED_FILL : Colors.HEADER_DEFAULT_FILL;
    }

    throw new IllegalArgumentException( "Not header " + m_columnIndex + " " + m_rowIndex );
  }

  /******************************* getCellBackgroundPaintSelected *********************************/
  protected Paint getCellBackgroundPaintSelected()
  {
    // return selected cell background
    if ( m_rowPos == getFocusRowPosition() && m_columnPos == getFocusColumnPosition() )
      return getCellBackgroundPaintDefault();

    Color selected = Colors.CELL_SELECTED_FILL;
    for ( int count = getSelectionCount( m_columnPos, m_rowPos ); count > 1; count-- )
      selected = selected.desaturate();

    if ( m_rowPos == getSelectRowPosition() && m_columnPos == getSelectColumnPosition() )
      selected = selected.desaturate();

    return isTableFocused() ? selected : selected.desaturate();
  }

  /************************************** getCellTextPaint ***************************************/
  protected Paint getCellTextPaint()
  {
    // return cell text paint
    if ( isCellSelected( m_columnPos, m_rowPos )
        && !( m_rowPos == getFocusRowPosition() && m_columnPos == getFocusColumnPosition() ) )
      return Colors.TEXT_SELECTED;
    else
      return Colors.TEXT_DEFAULT;
  }

  /**************************************** getCellEditor ****************************************/
  public CellEditorBase getCellEditor()
  {
    // return cell editor, or null if cell is read-only
    return null;
  }

}
