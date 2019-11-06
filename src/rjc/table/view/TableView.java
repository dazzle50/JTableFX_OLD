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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
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

  /**************************************** getCellText ******************************************/
  protected String getCellText( TableCell cell )
  {
    // return cell value as string
    Object value = getData().getValue( cell.columnIndex, cell.rowIndex );
    return value == null ? null : value.toString();
  }

  /************************************ getCellTextAlignment *************************************/
  protected Pos getCellTextAlignment( TableCell cell )
  {
    // return cell text alignment
    return Pos.CENTER;
  }

  /************************************* getCellTextFamily ***************************************/
  protected String getCellTextFamily( TableCell cell )
  {
    // return cell text family
    return Font.getDefault().getFamily();
  }

  /************************************** getCellTextSize ****************************************/
  protected double getCellTextSize( TableCell cell )
  {
    // return cell text family
    return Font.getDefault().getSize();
  }

  /************************************* getCellTextWeight ***************************************/
  protected FontWeight getCellTextWeight( TableCell cell )
  {
    // return cell text weight
    return FontWeight.NORMAL;
  }

  /************************************* getCellTextPosture **************************************/
  protected FontPosture getCellTextPosture( TableCell cell )
  {
    // return cell text posture
    return FontPosture.REGULAR;
  }

  /************************************** getCellTextInsets **************************************/
  protected Insets getCellTextInsets( TableCell cell )
  {
    // return cell text insets
    return CELL_TEXT_INSERTS;
  }

  /************************************** getZoomTextInsets **************************************/
  public Insets getZoomTextInsets( TableCell cell )
  {
    // get text inserts adjusted for zoom
    Insets insets = getCellTextInsets( cell );
    if ( getZoom() != 1.0 )
      insets = new Insets( insets.getTop() * getZoom(), insets.getRight() * getZoom(), insets.getBottom() * getZoom(),
          insets.getLeft() * getZoom() );

    return insets;
  }

  /**************************************** getZoomFont ******************************************/
  public Font getZoomFont( TableCell cell )
  {
    // get font adjusted for zoom
    return Font.font( getCellTextFamily( cell ), getCellTextWeight( cell ), getCellTextPosture( cell ),
        getCellTextSize( cell ) * getZoom() );
  }

  /************************************* getCellBorderPaint **************************************/
  protected Paint getCellBorderPaint( TableCell cell )
  {
    // return cell border paint
    return Colors.CELL_BORDER;
  }

  /*********************************** getCellBackgroundPaint ************************************/
  protected Paint getCellBackgroundPaint( TableCell cell )
  {
    // return cell background paint, starting with header cells
    if ( cell.rowIndex == HEADER || cell.columnIndex == HEADER )
      return getCellBackgroundPaintHeader( cell );

    // for selected cells
    if ( isCellSelected( cell.columnPos, cell.rowPos ) )
      return getCellBackgroundPaintSelected( cell );

    // otherwise default background
    return getCellBackgroundPaintDefault( cell );
  }

  /******************************** getCellBackgroundPaintDefault ********************************/
  protected Paint getCellBackgroundPaintDefault( TableCell cell )
  {
    // default table cell background
    return Colors.CELL_DEFAULT_FILL;
  }

  /******************************** getCellBackgroundPaintHeader *********************************/
  protected Paint getCellBackgroundPaintHeader( TableCell cell )
  {
    // return header cell background
    if ( cell.rowIndex == HEADER )
    {
      if ( cell.columnPos == getFocusCellProperty().getColumnPos() )
        return Colors.HEADER_FOCUS;
      else
        return hasColumnSelection( cell.columnPos ) ? Colors.HEADER_SELECTED_FILL : Colors.HEADER_DEFAULT_FILL;
    }

    if ( cell.columnIndex == HEADER )
    {
      if ( cell.rowPos == getFocusCellProperty().getRowPos() )
        return Colors.HEADER_FOCUS;
      else
        return hasRowSelection( cell.rowPos ) ? Colors.HEADER_SELECTED_FILL : Colors.HEADER_DEFAULT_FILL;
    }

    throw new IllegalArgumentException( "Not header " + cell.columnIndex + " " + cell.rowIndex );
  }

  /******************************* getCellBackgroundPaintSelected *********************************/
  protected Paint getCellBackgroundPaintSelected( TableCell cell )
  {
    // return selected cell background
    if ( cell.rowPos == getFocusCellProperty().getRowPos() && cell.columnPos == getFocusCellProperty().getColumnPos() )
      return getCellBackgroundPaintDefault( cell );

    Color selected = Colors.CELL_SELECTED_FILL;
    for ( int count = getSelectionCount( cell.columnPos, cell.rowPos ); count > 1; count-- )
      selected = selected.desaturate();

    if ( cell.rowPos == getSelectCellProperty().getRowPos()
        && cell.columnPos == getSelectCellProperty().getColumnPos() )
      selected = selected.desaturate();

    return isTableFocused() ? selected : selected.desaturate();
  }

  /************************************** getCellTextPaint ***************************************/
  protected Paint getCellTextPaint( TableCell cell )
  {
    // return cell text paint
    if ( isCellSelected( cell.columnPos, cell.rowPos ) && !( cell.rowPos == getFocusCellProperty().getRowPos()
        && cell.columnPos == getFocusCellProperty().getColumnPos() ) )
      return Colors.TEXT_SELECTED;
    else
      return Colors.TEXT_DEFAULT;
  }

  /**************************************** getCellEditor ****************************************/
  public CellEditorBase getCellEditor( TableCell cell )
  {
    // return cell editor, or null if cell is read-only
    return null;
  }

}
