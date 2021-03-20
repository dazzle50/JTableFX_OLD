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

import rjc.table.undo.CommandResize;
import rjc.table.view.TableScrollBar;
import rjc.table.view.TableSelection.SelectedSet;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/******************************* Supports column and row re-sizing *******************************/
/*************************************************************************************************/

public class Resize
{
  private static TableView      m_view;       // table view for resizing
  private static TableAxis      m_axis;       // horizontal or vertical axis
  private static TableScrollBar m_scrollbar;  // horizontal or vertical scroll-bar

  private static int            m_coordinate; // latest coordinate used when table scrolled
  private static int            m_offset;     // resize coordinate offset
  private static int            m_before;     // number of positions being resized before current position
  private static CommandResize  m_command;    // command for undo-stack

  // static class to support column resizing
  public static class HorizontalResize extends Resize
  {
    /******************************************** start ********************************************/
    public static void start( TableView view, int coordinate )
    {
      // initialise variables
      m_view = view;
      m_axis = view.getColumnsAxis();
      m_scrollbar = view.getHorizontalScrollBar();
      start( coordinate, view.getSelection().getSelectedColumns() );
    }
  }

  // static class to support row resizing
  public static class VerticalResize extends Resize
  {
    /******************************************** start ********************************************/
    public static void start( TableView view, int coordinate )
    {
      // initialise variables
      m_view = view;
      m_axis = view.getRowsAxis();
      m_scrollbar = view.getVerticalScrollBar();
      start( coordinate, view.getSelection().getSelectedRows() );
    }
  }

  /******************************************** start ********************************************/
  private static void start( int coordinate, SelectedSet positions )
  {
    // determine cursor position for resizing
    int scroll = (int) m_scrollbar.getValue();
    int position = m_axis.getPositionFromCoordinate( coordinate, scroll );
    int posStart = m_axis.getStartFromPosition( position, scroll );
    int posEnd = m_axis.getStartFromPosition( position + 1, scroll );

    if ( coordinate - posStart < posEnd - coordinate )
    {
      m_offset = posStart + scroll;
      position--;
    }
    else
      m_offset = posEnd + scroll;

    // prepare resize command
    m_command = new CommandResize( m_view, m_axis, determineIndexes( position, positions ) );

    // start reordering
    drag( coordinate );
  }

  /******************************************** drag *********************************************/
  public static void drag( int coordinate )
  {
    // resize columns or rows
    m_coordinate = coordinate;
    double pixels = ( coordinate - m_offset + m_scrollbar.getValue() ) / m_before;
    int size = (int) ( pixels / m_view.getZoom().get() );

    // resize
    m_command.setNewSize( size );
    m_command.redo();
  }

  /***************************************** inProgress ******************************************/
  public static boolean inProgress()
  {
    // if no resize in progress return false
    if ( m_view == null )
      return false;

    // return true as resize in progress
    drag( m_coordinate );
    return true;
  }

  /********************************************* end *********************************************/
  public static void end()
  {
    // end resizing, push resize command onto undo-stack
    m_view.getUndoStack().push( m_command );
    m_view = null;
    m_command = null;
  }

  /************************************** determineIndexes ***************************************/
  private static SelectedSet determineIndexes( int position, SelectedSet positions )
  {
    // determine set of column or row indexes to be resized
    SelectedSet indexes = new SelectedSet();
    indexes.all = positions.all;
    m_before = 0;
    int index = m_axis.getIndexFromPosition( position );
    if ( positions.set != null && positions.set.contains( position ) )
    {
      // resize all selected positions
      for ( var pos : positions.set )
      {
        int i = m_axis.getIndexFromPosition( pos );
        indexes.set.add( i );
        if ( pos < position )
        {
          m_offset -= m_axis.getCellPixels( i );
          m_before++;
        }
      }
    }
    else
    {
      if ( positions.all )
        // resize all positions
        m_before = position;
      else
        // resize just current (not-selected) position
        indexes.set.add( index );
    }

    // calculate offset
    if ( positions.all )
    {
      m_axis.clearSizeExceptions();
      m_offset = m_axis.getCellPixels( TableAxis.HEADER );
    }
    else
      m_offset -= m_axis.getCellPixels( index );
    m_before++;

    return indexes;
  }

}
