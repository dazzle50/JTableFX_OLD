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

package rjc.table.view.actions;

import java.util.Set;

import javafx.geometry.Orientation;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import rjc.table.Colors;
import rjc.table.Status.Level;
import rjc.table.Utils;
import rjc.table.undo.CommandReorder;
import rjc.table.view.TableSelection.SelectedSet;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/****************************** Supports column and row re-ordering ******************************/
/*************************************************************************************************/

public class Reorder
{
  private static final int   LINE_WIDTH = 5;
  private static Line        m_line;
  private static TableView   m_view;        // table view for reordering
  private static SelectedSet m_selected;    // columns or rows to be moved
  private static Orientation m_orientation; // orientation for the reordering
  private static int         m_pos;         // column or row axis position for reordering

  /******************************************** start ********************************************/
  public static void start( TableView view, Orientation orientation, int coordinate )
  {
    // determine selected columns or rows to be moved
    if ( orientation == Orientation.HORIZONTAL )
      m_selected = view.getSelection().getSelectedColumns();
    else
      m_selected = view.getSelection().getSelectedRows();

    if ( m_selected.all )
    {
      String msg = "All ";
      msg += orientation == Orientation.HORIZONTAL ? "columns" : "rows";
      msg += " selected - therefore cannot reorder";
      view.getStatus().update( Level.NORMAL, msg );
      return;
    }

    // initialise line
    m_line = new Line();
    m_line.setStrokeLineCap( StrokeLineCap.BUTT );
    m_line.setStrokeWidth( LINE_WIDTH );
    m_line.setStroke( Colors.REORDER_LINE );

    // prepare line length and reselect just the specified columns or rows 
    view.getSelection().clear();
    if ( orientation == Orientation.HORIZONTAL )
    {
      m_line.setStartY( 0.0 );
      m_line.setEndY( Math.min( view.getCanvas().getHeight(), view.getTableHeight() ) );

      Utils.trace( m_selected );
      for ( var columnPos : m_selected.set )
        view.getSelection().selectArea( columnPos, TableAxis.FIRSTCELL, columnPos, TableAxis.AFTER );
    }
    else
    {
      m_line.setStartX( 0.0 );
      m_line.setEndX( Math.min( view.getCanvas().getWidth(), view.getTableWidth() ) );

      for ( var rowPos : m_selected.set )
        view.getSelection().selectArea( TableAxis.FIRSTCELL, rowPos, TableAxis.AFTER, rowPos );
    }

    // start reordering
    m_view = view;
    m_orientation = orientation;
    drag( coordinate );
    view.redraw();
    view.add( m_line );
  }

  /******************************************** drag *********************************************/
  public static void drag( int coordinate )
  {
    // return without doing anything is reorder not started
    if ( m_view == null )
      return;

    // reorder columns or rows
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
      if ( x < m_view.getHeaderWidth() )
      {
        m_pos = m_view.getColumnPositionAtX( m_view.getHeaderWidth() );
        m_pos = m_view.getColumnsAxis().getNext( m_pos );
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
      if ( y < m_view.getHeaderHeight() )
      {
        m_pos = m_view.getRowPositionAtY( m_view.getHeaderHeight() );
        m_pos = m_view.getRowsAxis().getNext( m_pos );
        y = m_view.getYStartFromRowPos( m_pos );
      }

      // place line on row edge
      m_line.setStartY( y );
      m_line.setEndY( y );
    }

  }

  /********************************************* end *********************************************/
  public static void end()
  {
    // return without doing anything is reorder not started
    if ( m_view == null )
      return;

    // move selected columns or rows to new position via command
    m_view.remove( m_line );
    m_view.getSelection().clear();
    int start = m_pos - countBefore( m_selected.set, m_pos );
    int end = start + m_selected.set.size() - 1;
    int beforeHashcode = m_orientation == Orientation.HORIZONTAL ? m_view.getColumnsAxis().orderHashcode()
        : m_view.getRowsAxis().orderHashcode();

    CommandReorder command = new CommandReorder( m_view, m_orientation, m_selected.set, m_pos );
    command.redo();

    // check that move has resulted in changed order
    int afterHashcode = m_orientation == Orientation.HORIZONTAL ? m_view.getColumnsAxis().orderHashcode()
        : m_view.getRowsAxis().orderHashcode();
    if ( beforeHashcode != afterHashcode )
    {
      // do not execute command again when adding to undo-stack
      m_view.getUndoStack().pushNoExecute( command );
    }

    if ( m_orientation == Orientation.HORIZONTAL )
    {
      m_view.getSelection().selectArea( start, TableAxis.FIRSTCELL, end, TableAxis.AFTER );
      m_view.getFocusCell().setColumnPos( start );
      m_view.getSelectCell().setColumnPos( end );
    }
    else
    {
      m_view.getSelection().selectArea( TableAxis.FIRSTCELL, start, TableAxis.AFTER, end );
      m_view.getFocusCell().setRowPos( start );
      m_view.getSelectCell().setRowPos( end );
    }

    m_view.redraw();
    m_view = null;
  }

  /***************************************** countBefore *****************************************/
  public static int countBefore( Set<Integer> set, int position )
  {
    // count set entries with value lower than position
    int count = 0;
    for ( int value : set )
      if ( value < position )
        count++;

    return count;
  }

}
