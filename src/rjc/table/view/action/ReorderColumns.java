/**************************************************************************
 *  Copyright (C) 2023 by Richard Crook                                   *
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

package rjc.table.view.action;

import java.util.HashSet;

import javafx.geometry.Orientation;
import rjc.table.data.IDataReorderColumns;
import rjc.table.undo.CommandReorderView;
import rjc.table.view.TableView;
import rjc.table.view.axis.AxisBase;

/*************************************************************************************************/
/**************************** Supports table-view column re-ordering *****************************/
/*************************************************************************************************/

public class ReorderColumns
{
  private static TableView        m_view;     // table view for reordering
  private static HashSet<Integer> m_selected; // columns to be moved
  private static ReorderLine      m_line;     // red line to show new location
  private static int              m_x;        // latest x-coordinate used when table scrolled

  /******************************************** start ********************************************/
  public static void start( TableView view, int x )
  {
    // if selected is "null" means all indexes selected - cannot reorder
    m_view = view;
    m_selected = view.getSelection().getSelectedColumns();
    if ( m_selected == null )
      return;

    // start reordering
    m_view.getSelection().clear();
    m_view.getSelection().selectColumns( m_selected );
    m_line = new ReorderLine( view );
    drag( x );
  }

  /******************************************** drag *********************************************/
  public static void drag( int x )
  {
    // return without doing anything is reorder not started
    if ( m_line == null )
      return;

    // position line at nearest column edge
    m_x = x;
    int column = m_view.getColumnNearestStartIndex( x );
    m_line.setColumn( column );
  }

  /***************************************** inProgress ******************************************/
  public static boolean inProgress()
  {
    // if no reorder in progress return false
    if ( m_line == null )
      return false;

    // return true as reorder in progress
    drag( m_x );
    return true;
  }

  /********************************************* end *********************************************/
  public static void end()
  {
    // return without doing anything if reorder not started
    if ( m_line == null )
      return;

    int adjustedInsertIndex;
    if ( m_view.getData() instanceof IDataReorderColumns data )
      // if data-model supports column reordering
      adjustedInsertIndex = data.reorderColumns( m_selected, m_line.getIndex() );
    else
    {
      // reorder columns on view via undo-command
      var command = new CommandReorderView();
      adjustedInsertIndex = command.prepare( m_view, Orientation.HORIZONTAL, m_selected, m_line.getIndex() );

      // only push undo-command onto stack if valid adjusted insert index
      if ( adjustedInsertIndex != AxisBase.INVALID )
        m_view.getUndoStack().pushWithoutDo( command );
    }

    // update selected columns
    if ( adjustedInsertIndex >= AxisBase.FIRSTCELL )
    {
      m_view.getSelection().clear();
      int lastIndex = adjustedInsertIndex + m_selected.size() - 1;
      m_view.getSelection().select( adjustedInsertIndex, AxisBase.FIRSTCELL, lastIndex, AxisBase.AFTER );
    }

    // tidy up and redraw the view
    m_view.remove( m_line );
    m_view.redraw();
    m_view = null;
    m_line = null;
  }

}
