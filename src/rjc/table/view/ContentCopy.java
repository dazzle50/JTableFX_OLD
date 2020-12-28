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

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import rjc.table.Utils;

/*************************************************************************************************/
/************************** Copy table-cells to clipboard functionality **************************/
/*************************************************************************************************/

public class ContentCopy
{

  /**************************************** constructor ******************************************/
  public ContentCopy( TableView view, int colPos, int rowPos )
  {
    // copy cell values & format from selected cells
    Utils.trace( "Copy - TODO not yet implemented", colPos, rowPos, view );

    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString( view.toString() );
    clipboard.setContent( content );
  }

}
