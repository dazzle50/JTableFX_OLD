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

import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import rjc.table.Colors;
import rjc.table.Utils;
import rjc.table.view.TableSelect.SelectedSet;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/****************************** Supports column and row re-ordering ******************************/
/*************************************************************************************************/

public class Reorder
{
  private final int   LINE_WIDTH = 5;
  private Line        m_line;
  private Canvas      m_slider;

  private TableView   m_view;        // table view for reordering
  private SelectedSet m_selected;    // columns or rows to be moved
  private Orientation m_orientation; // orientation for the reordering
  private int         m_pos;         // column or row axis position for reordering

  /**************************************** constructor ******************************************/
  public Reorder()
  {
    // initialise line
    m_line = new Line();
    m_line.setStrokeLineCap( StrokeLineCap.BUTT );
    m_line.setStrokeWidth( LINE_WIDTH );
    m_line.setStroke( Colors.REORDER_LINE );

    // initialise slider
    m_slider = new Canvas();
  }

  /*************************************** getOrientation ****************************************/
  public Orientation getOrientation()
  {
    // return reordering orientation, or null is not started
    return m_orientation;
  }

  /******************************************** start ********************************************/
  public void start( TableView view, Orientation orientation, SelectedSet selected )
  {
    // is all is selected, do nothing
    if ( selected != null && selected.all )
      return;

    // checks before start reordering
    if ( getOrientation() != null )
      throw new IllegalStateException( "Reordering already started" );
    if ( view == null )
      throw new NullPointerException( "TableView must not be null" );
    if ( orientation == null )
      throw new NullPointerException( "Orientation must not be null" );
    if ( selected == null || selected.all || selected.set.isEmpty() )
      throw new IllegalArgumentException( "Invalid selection " + selected );

    // prepare line length and reselect just the specified columns or rows 
    view.clearAllSelection();
    if ( orientation == Orientation.HORIZONTAL )
    {
      m_line.setStartY( 0.0 );
      m_line.setEndY( Math.min( view.getCanvas().getHeight(), view.getTableHeight() ) );

      for ( var columnPos : selected.set )
        view.selectArea( columnPos, TableAxis.FIRSTCELL, columnPos, TableAxis.AFTER );
    }
    else
    {
      m_line.setStartX( 0.0 );
      m_line.setEndX( Math.min( view.getCanvas().getWidth(), view.getTableWidth() ) );

      for ( var rowPos : selected.set )
        view.selectArea( TableAxis.FIRSTCELL, rowPos, TableAxis.AFTER, rowPos );
    }

    // start reordering
    view.redraw();
    view.add( m_line );
    // TODO view.add( m_slider );
    m_orientation = orientation;
    m_selected = selected;
    m_view = view;
  }

  /**************************************** setPlacement *****************************************/
  public void setPlacement( int coordinate )
  {
    // update line and slider positions
    if ( getOrientation() == null )
      throw new IllegalStateException( "Reordering not started" );

    if ( m_orientation == Orientation.HORIZONTAL )
    {
      // horizontal reordering so position line at nearest column edge
      int columnPos = Utils.clamp( m_view.getColumnPositionAtX( coordinate ), TableAxis.FIRSTCELL,
          m_view.getData().getColumnCount() - 1 );
      double xs = m_view.getXStartFromColumnPos( columnPos );
      double xe = m_view.getXStartFromColumnPos( columnPos + 1 );
      m_pos = coordinate - xs < xe - coordinate ? columnPos : columnPos + 1;
      double x = coordinate - xs < xe - coordinate ? xs : xe;

      // check x is on visible edge
      if ( x > m_view.getCanvas().getWidth() )
      {
        m_pos = m_view.getColumnPositionAtX( (int) m_view.getCanvas().getWidth() );
        x = m_view.getXStartFromColumnPos( m_pos );
      }
      if ( x < m_view.getRowHeaderWidth() )
      {
        m_pos = m_view.getColumnPositionAtX( m_view.getRowHeaderWidth() );
        m_pos = m_view.getColumns().getNext( m_pos );
        x = m_view.getXStartFromColumnPos( m_pos );
      }

      // place line on column edge
      m_line.setStartX( x );
      m_line.setEndX( x );
    }
    else
    {
      // vertical reordering so position line at nearest row edge
      int rowPos = Utils.clamp( m_view.getRowPositionAtY( coordinate ), TableAxis.FIRSTCELL,
          m_view.getData().getRowCount() - 1 );
      double ys = m_view.getYStartFromRowPos( rowPos );
      double ye = m_view.getYStartFromRowPos( rowPos + 1 );
      m_pos = coordinate - ys < ye - coordinate ? rowPos : rowPos + 1;
      double y = coordinate - ys < ye - coordinate ? ys : ye;

      // check y is on visible edge
      if ( y > m_view.getCanvas().getHeight() )
      {
        m_pos = m_view.getRowPositionAtY( (int) m_view.getCanvas().getHeight() );
        y = m_view.getYStartFromRowPos( m_pos );
      }
      if ( y < m_view.getColumnHeaderHeight() )
      {
        m_pos = m_view.getRowPositionAtY( m_view.getColumnHeaderHeight() );
        m_pos = m_view.getRows().getNext( m_pos );
        y = m_view.getYStartFromRowPos( m_pos );
      }

      // place line on row edge
      m_line.setStartY( y );
      m_line.setEndY( y );
    }
  }

  /********************************************* end *********************************************/
  public void end()
  {
    // end any reordering
    if ( getOrientation() != null )
    {
      // remove line and slider from table, reorder, and end reordering
      m_view.remove( m_line );
      m_view.remove( m_slider );

      // move selected columns or rows to new position
      m_view.clearAllSelection();
      if ( m_orientation == Orientation.HORIZONTAL )
      {
        int left = m_pos + m_view.getColumns().movePositions( m_selected.set, m_pos );
        int right = left + m_selected.set.size() - 1;
        m_view.setCurrentSelection( left, TableAxis.FIRSTCELL, right, TableAxis.AFTER );
        m_view.getFocusCellProperty().setColumnPos( left );
        m_view.getSelectCellProperty().setColumnPos( right );
      }
      else
      {
        int top = m_pos + m_view.getRows().movePositions( m_selected.set, m_pos );
        int bottom = top + m_selected.set.size() - 1;
        m_view.setCurrentSelection( TableAxis.FIRSTCELL, top, TableAxis.AFTER, bottom );
        m_view.getFocusCellProperty().setRowPos( top );
        m_view.getSelectCellProperty().setRowPos( bottom );
      }

      m_view.redraw();
      m_view = null;
      m_orientation = null;
    }
  }

}
