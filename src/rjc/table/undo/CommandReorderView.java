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

package rjc.table.undo;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javafx.geometry.Orientation;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/******************** UndoCommand for reordering columns or rows in view only ********************/
/*************************************************************************************************/

public class CommandReorderView implements IUndoCommand
{
  private TableView    m_view;           // table view
  private TableAxis    m_axis;           // column or row table axis
  private Set<Integer> m_indexes;        // indexes to be moved
  private int          m_insertBefore;   // insert before this view-index
  private int          m_adjustedBefore; // insert position after reorder
  private String       m_text;           // text describing command

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // action command
    m_axis.reorder( m_indexes, m_insertBefore );
    m_view.redraw();
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // revert command
    int fromOffset = 0;
    int toOffset = m_indexes.size() + m_adjustedBefore - m_insertBefore;
    var set = new HashSet<Integer>( 1 );

    for ( int toIndex : new TreeSet<Integer>( m_indexes ) )
    {
      set.clear();
      if ( toIndex < m_insertBefore )
      {
        set.add( m_adjustedBefore + fromOffset++ );
        m_axis.reorder( set, toIndex );
      }
      else
      {
        set.add( m_insertBefore );
        m_axis.reorder( set, toIndex + toOffset-- );
      }
    }

    // redraw table in this view only
    m_view.getSelection().clear();
    m_view.redraw();
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // command description
    return m_text;
  }

  /******************************************* prepare *******************************************/
  public int prepare( TableView view, Orientation orientation, Set<Integer> indexes, int insertIndex )
  {
    // setup command
    m_view = view;
    m_indexes = indexes;
    m_insertBefore = insertIndex;
    m_axis = orientation == Orientation.HORIZONTAL ? m_view.getColumnsAxis() : m_view.getRowsAxis();
    m_text = "Moving " + m_indexes.size() + ( orientation == Orientation.HORIZONTAL ? " column" : " row" )
        + ( m_indexes.size() > 1 ? "s" : "" );

    // return start index of reordered (or INVALID(-2) if no changes)
    m_adjustedBefore = m_axis.reorder( indexes, insertIndex );
    return m_adjustedBefore;
  }
}
