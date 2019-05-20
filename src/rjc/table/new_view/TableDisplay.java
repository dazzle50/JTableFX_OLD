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

package rjc.table.new_view;

import rjc.table.data.TableData;
import rjc.table.view.TableCanvas;

/*************************************************************************************************/
/************************ Table view display with canvas and scroll bars *************************/
/*************************************************************************************************/

public class TableDisplay extends TableParent
{
  protected TableView      m_view;       // shortcut to table view
  protected TableData      m_data;       // shortcut to table data

  protected TableAxis      m_columns;    // axis for vertical columns
  protected TableAxis      m_rows;       // axis for horizontal rows

  protected TableScrollBar m_vScrollBar; // vertical scroll bar
  protected TableScrollBar m_hScrollBar; // horizontal scroll bar
  protected TableCanvas    m_canvas;     // table canvas for column & row headers and body cells

  /******************************************* resize ********************************************/
  @Override
  public void resize( double width, double height )
  {
    // do nothing if no change in size
    if ( (int) width == getWidth() && (int) height == getHeight() )
      return;

    // do nothing if size is larger than max allowed
    //if ( width > MAX_PIXELS || height > MAX_PIXELS )
    //  return;

    // resize parent and re-layout canvas and scroll bars
    super.resize( width, height );
    //layoutDisplay();
  }

  /**************************************** requestFocus *****************************************/
  @Override
  public void requestFocus()
  {
    // setting focus on table should set focus on canvas
    m_canvas.requestFocus();
  }

  /*************************************** isTableFocused ****************************************/
  public boolean isTableFocused()
  {
    // return if table canvas has focus
    return m_canvas.isFocused();
  }

}
