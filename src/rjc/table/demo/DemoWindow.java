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

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import rjc.table.support.Utils;

/*************************************************************************************************/
/******************************* Main window for demo application ********************************/
/*************************************************************************************************/

public class DemoWindow
{

  /**************************************** constructor ******************************************/
  public DemoWindow( Stage stage )
  {
    // construct demo table data source and view
    DemoTableData data = new DemoTableData();
    DemoTableView view = new DemoTableView( data );

    // construct demo scene
    BorderPane pane = new BorderPane();
    pane.setTop( region( 50, "ivory" ) );
    pane.setBottom( region( 50, "ivory" ) );
    pane.setLeft( region( 50, "floralwhite" ) );
    pane.setRight( region( 50, "floralwhite" ) );

    // setting alignment to top-left reduces positional stuttering during resizing
    BorderPane.setAlignment( view, Pos.TOP_LEFT );
    pane.setCenter( view );

    // construct demo application window
    Scene scene = new Scene( pane );
    stage.setScene( scene );
    stage.setTitle( "JTableFX " + Utils.VERSION + " TEST demo application" );
    stage.show();
  }

  /**************************************** region ******************************************/
  private Region region( int size, String colour )
  {
    // return new simple coloured region
    Region region = new Region();
    region.setStyle( "-fx-background-color: " + colour + ";" );
    region.setPrefSize( size, size );

    return region;
  }

}
