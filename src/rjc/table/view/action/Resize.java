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

import rjc.table.undo.CommandResize;
import rjc.table.view.TableScrollBar;
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
  private static void start( int coordinate, HashSet<Integer> positions )
  {
  }

}