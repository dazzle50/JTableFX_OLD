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

import java.util.HashMap;
import java.util.HashSet;

import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/******************* UndoCommand for resizing columns widths or rows heights *********************/
/*************************************************************************************************/

public class CommandResize implements IUndoCommand
{
  private TableView                 m_view;                 // table view
  private TableAxis                 m_axis;                 // columns or rows being resized
  private HashSet<Integer>          m_indexes;              // indexes being resized (null = all)
  private String                    m_text;                 // text describing command

  private HashMap<Integer, Integer> m_oldExceptions;        // old size exceptions before resize
  private int                       m_oldDefault;           // old default before resize
  private int                       m_newSize;              // new size

  final static private int          NO_EXCEPTION = -999999; // no size exception

  /**************************************** constructor ******************************************/
  public CommandResize( TableView view, TableAxis axis, HashSet<Integer> selected )
  {
    // prepare resize command
    m_view = view;
    m_axis = axis;
    m_indexes = selected;

    // get old default size and exceptions before resizing starts
    m_oldDefault = axis.getDefaultSize();
    m_oldExceptions = new HashMap<>();
    if ( selected == null )
      // if selected is null means all indexes being resized with new default size
      axis.getSizeExceptions().forEach( ( index, size ) -> m_oldExceptions.put( index, size ) );
    else
      // otherwise selected contains the indexes to be resized
      selected.forEach( ( index ) ->
      {
        int size = axis.getSizeExceptions().getOrDefault( index, NO_EXCEPTION );
        m_oldExceptions.put( index, size );
      } );
  }

  /***************************************** setNewSize ******************************************/
  public void setNewSize( int size )
  {
    // set command new size
    m_newSize = size;
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // action command
    if ( m_indexes == null )
    {
      m_axis.setDefaultSize( m_newSize );
      m_axis.clearSizeExceptions();
    }
    else
      for ( var index : m_indexes )
        m_axis.setIndexSize( index, m_newSize );

    // update layout in case scroll-bar changed and redraw table view
    m_view.layoutDisplay();
    m_view.redraw();
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // revert command - if all revert default size
    if ( m_indexes == null )
      m_axis.setDefaultSize( m_oldDefault );

    // revert exceptions including hidden
    m_oldExceptions.forEach( ( index, size ) ->
    {
      if ( size == NO_EXCEPTION )
        m_axis.clearIndexSize( index );
      else
        m_axis.setIndexSize( index, size );
    } );

    // update layout in case scroll-bar need changed and redraw table view
    m_view.layoutDisplay();
    m_view.redraw();
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // command description
    if ( m_text == null )
    {
      m_text = "Resized " + ( m_indexes == null ? "all" : m_indexes.size() )
          + ( m_axis == m_view.getColumnsAxis() ? " column" : " row" )
          + ( m_indexes == null || m_indexes.size() > 1 ? "s" : "" );

      if ( m_view.getName() != null )
        m_text = m_view.getName() + " - " + m_text;
    }

    return m_text;
  }

}
