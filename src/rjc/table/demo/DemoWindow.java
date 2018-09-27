/**************************************************************************
 *  Copyright (C) 2018 by Richard Crook                                   *
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

package rjc.table.demo;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import rjc.table.data.TableData;
import rjc.table.support.Utils;
import rjc.table.view.TableView;

/*************************************************************************************************/
/******************************* Main window for demo application ********************************/
/*************************************************************************************************/

public class DemoWindow
{

  /**************************************** constructor ******************************************/
  public DemoWindow( Stage stage )
  {
    // create default table in tab
    TableView defaultTable = new TableView( new TableData() );
    Tab defaultTab = new Tab();
    defaultTab.setText( "Default" );
    defaultTab.setContent( defaultTable );
    defaultTable.draw.bind( defaultTab.selectedProperty() );

    // create extra large table in tab
    TableView xlargeTable = new ExtraLargeView( new ExtraLargeData() );
    Tab xlargeTab = new Tab();
    xlargeTab.setText( "Extra large" );
    xlargeTab.setContent( xlargeTable );
    xlargeTable.draw.bind( xlargeTab.selectedProperty() );

    // create demo tab pane
    TabPane tabs = new TabPane();
    tabs.getTabs().add( defaultTab );
    tabs.getTabs().add( xlargeTab );

    // construct demo application window
    Scene scene = new Scene( tabs );
    stage.setScene( scene );
    stage.setTitle( "JTableFX " + Utils.VERSION + " demo application" );

    // TEMP placing and sizing for my convenience #############
    stage.setX( -1100 );
    stage.setY( 700 );
    stage.setWidth( 1000 );
    stage.show();
  }

}
