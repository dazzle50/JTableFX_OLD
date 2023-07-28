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

package rjc.table.demo.large;

import javafx.scene.control.Tab;
import rjc.table.data.TableData;

/*************************************************************************************************/
/*************************** Demonstrates a very large table and view ****************************/
/*************************************************************************************************/

public class DemoTableLarge extends Tab
{
  private TableData m_data; // data for the table view

  /**************************************** constructor ******************************************/
  public DemoTableLarge()
  {
    // create
    m_data = new TableData();
    m_data.setColumnCount( 1_000_000 );
    m_data.setRowCount( 1_000_000 );

    LargeView view = new LargeView( m_data );

    // make view only visible when tab is selected
    view.visibleProperty().bind( selectedProperty() );

    // configure the tab
    setText( "Large" );
    setClosable( false );
    setContent( view );
  }

}
