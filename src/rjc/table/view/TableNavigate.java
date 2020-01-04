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

import rjc.table.cell.CellProperty;

/*************************************************************************************************/
/************************** Table focus, select & mouse cell positions ***************************/
/*************************************************************************************************/

public class TableNavigate extends TableDisplay
{
  // observable cell position properties
  final private CellProperty m_focusCell  = new CellProperty();
  final private CellProperty m_selectCell = new CellProperty();
  final private CellProperty m_mouseCell  = new CellProperty();

  // directions of focus movement
  public static enum MoveDirection
  {
    LEFT, RIGHT, UP, DOWN, NONE
  }

  /************************************ getFocusCellProperty *************************************/
  public CellProperty getFocusCellProperty()
  {
    // return focus cell position property
    return m_focusCell;
  }

  /************************************ getSelectCellProperty ************************************/
  public CellProperty getSelectCellProperty()
  {
    // return select cell position property
    return m_selectCell;
  }

  /************************************ getMouseCellProperty *************************************/
  public CellProperty getMouseCellProperty()
  {
    // return mouse cell position property
    return m_mouseCell;
  }

  /****************************************** moveFocus ******************************************/
  public void moveFocus( MoveDirection direction )
  {
    // TODO if areas selected
    int columnPos = getFocusCellProperty().getColumnPos();
    int rowPos = getFocusCellProperty().getRowPos();
    //Utils.trace( "TODO - different behaviour for selected areas", m_view.getSelectionCount() );

    // otherwise move within full visible table
    switch ( direction )
    {
      case DOWN:
        rowPos = getRows().getNext( rowPos );
        if ( rowPos == getFocusCellProperty().getRowPos() )
        {
          rowPos = getRows().getFirst();
          columnPos = getColumns().getNext( columnPos );
          if ( columnPos == getFocusCellProperty().getColumnPos() )
            columnPos = getColumns().getFirst();
        }
        break;

      case UP:
        rowPos = getRows().getPrevious( rowPos );
        if ( rowPos == getFocusCellProperty().getRowPos() )
        {
          rowPos = getRows().getLast();
          columnPos = getColumns().getPrevious( columnPos );
          if ( columnPos == getFocusCellProperty().getColumnPos() )
            columnPos = getColumns().getLast();
        }
        break;

      case LEFT:
        columnPos = getColumns().getPrevious( columnPos );
        if ( columnPos == getFocusCellProperty().getColumnPos() )
        {
          columnPos = getColumns().getLast();
          rowPos = getRows().getPrevious( rowPos );
          if ( rowPos == getFocusCellProperty().getRowPos() )
            rowPos = getRows().getLast();
        }
        break;

      case RIGHT:
        columnPos = getColumns().getNext( columnPos );
        if ( columnPos == getFocusCellProperty().getColumnPos() )
        {
          columnPos = getColumns().getFirst();
          rowPos = getRows().getNext( rowPos );
          if ( rowPos == getFocusCellProperty().getRowPos() )
            rowPos = getRows().getFirst();
        }
        break;

      default:
        break;
    }

    // update select & focus cell positions
    getView().setSelectFocusPosition( columnPos, rowPos, true, true, true );
  }

}
