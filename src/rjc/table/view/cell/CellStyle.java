/**************************************************************************
 *  Copyright (C) 2021 by Richard Crook                                   *
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

package rjc.table.view.cell;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import rjc.table.Colors;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/********************************** Defines cell drawing style ***********************************/
/*************************************************************************************************/

public class CellStyle extends CellContext
{
  protected final static Insets CELL_TEXT_INSERTS = new Insets( 0.0, 1.0, 1.0, 0.0 );

  /****************************************** getText ********************************************/
  protected String getText()
  {
    // return cell value as string
    Object value = view.getData().getValue( columnIndex, rowIndex );
    return value == null ? null : value.toString();
  }

  /************************************** getTextAlignment ***************************************/
  protected Pos getTextAlignment()
  {
    // return cell text alignment
    return Pos.CENTER;
  }

  /*************************************** getTextFamily *****************************************/
  protected String getTextFamily()
  {
    // return cell text family
    return Font.getDefault().getFamily();
  }

  /**************************************** getTextSize ******************************************/
  protected double getTextSize()
  {
    // return cell text family
    return Font.getDefault().getSize();
  }

  /*************************************** getTextWeight *****************************************/
  protected FontWeight getTextWeight()
  {
    // return cell text weight
    return FontWeight.NORMAL;
  }

  /*************************************** getTextPosture ****************************************/
  protected FontPosture getTextPosture()
  {
    // return cell text posture
    return FontPosture.REGULAR;
  }

  /**************************************** getTextInsets ****************************************/
  protected Insets getTextInsets()
  {
    // return cell text insets
    return CELL_TEXT_INSERTS;
  }

  /************************************** getZoomTextInsets **************************************/
  public Insets getZoomTextInsets()
  {
    // get text inserts adjusted for zoom
    Insets insets = getTextInsets();
    double zoom = view.getZoom().get();

    if ( zoom != 1.0 )
      insets = new Insets( insets.getTop() * zoom, insets.getRight() * zoom, insets.getBottom() * zoom,
          insets.getLeft() * zoom );

    return insets;
  }

  /**************************************** getZoomFont ******************************************/
  public Font getZoomFont()
  {
    // get font adjusted for zoom
    return Font.font( getTextFamily(), getTextWeight(), getTextPosture(), getTextSize() * view.getZoom().get() );
  }

  /*************************************** getBorderPaint ****************************************/
  protected Paint getBorderPaint()
  {
    // return cell border paint
    return Colors.CELL_BORDER;
  }

  /************************************* getBackgroundPaint **************************************/
  protected Paint getBackgroundPaint()
  {
    // return cell background paint, starting with header cells
    if ( rowIndex == TableAxis.HEADER || columnIndex == TableAxis.HEADER )
      return getBackgroundPaintHeader();

    // for selected cells
    if ( view.getSelection().isCellSelected( columnPos, rowPos ) )
      return getBackgroundPaintSelected();

    // otherwise default background
    return getBackgroundPaintDefault();
  }

  /********************************** getBackgroundPaintDefault **********************************/
  protected Paint getBackgroundPaintDefault()
  {
    // default table cell background
    return Colors.CELL_DEFAULT_FILL;
  }

  /********************************** getBackgroundPaintHeader ***********************************/
  protected Paint getBackgroundPaintHeader()
  {
    // return header cell background
    if ( rowIndex == TableAxis.HEADER )
    {
      if ( columnPos == view.getFocusCell().getColumnPos() )
        return Colors.HEADER_FOCUS;
      else
        return view.getSelection().hasColumnSelection( columnPos ) ? Colors.HEADER_SELECTED_FILL
            : Colors.HEADER_DEFAULT_FILL;
    }

    if ( columnIndex == TableAxis.HEADER )
    {
      if ( rowPos == view.getFocusCell().getRowPos() )
        return Colors.HEADER_FOCUS;
      else
        return view.getSelection().hasRowSelection( rowPos ) ? Colors.HEADER_SELECTED_FILL : Colors.HEADER_DEFAULT_FILL;
    }

    throw new IllegalArgumentException( "Not header " + columnIndex + " " + rowIndex );
  }

  /********************************* getBackgroundPaintSelected ***********************************/
  protected Paint getBackgroundPaintSelected()
  {
    // return selected cell background
    if ( rowPos == view.getFocusCell().getRowPos() && columnPos == view.getFocusCell().getColumnPos() )
      return getBackgroundPaintDefault();

    Color selected = Colors.CELL_SELECTED_FILL;
    for ( int count = view.getSelection().getSelectionCount( columnPos, rowPos ); count > 1; count-- )
      selected = selected.desaturate();

    if ( rowPos == view.getSelectCell().getRowPos() && columnPos == view.getSelectCell().getColumnPos() )
      selected = selected.desaturate();

    return view.isFocused() ? selected : selected.desaturate();
  }

  /**************************************** getTextPaint *****************************************/
  protected Paint getTextPaint()
  {
    // return cell text paint
    if ( view.getSelection().isCellSelected( columnPos, rowPos )
        && !( rowPos == view.getFocusCell().getRowPos() && columnPos == view.getFocusCell().getColumnPos() ) )
      return Colors.TEXT_SELECTED;
    else
      return Colors.TEXT_DEFAULT;
  }

}
