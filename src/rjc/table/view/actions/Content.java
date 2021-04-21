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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import rjc.table.Utils;
import rjc.table.view.TableView;

/*************************************************************************************************/
/************************** Functionality for multi-cell content actions *************************/
/*************************************************************************************************/

public class Content
{
  final static private long WAIT_MILLISECONDS = 2000L; // 2 seconds

  /******************************************* insert ********************************************/
  public static void insert( TableView view )
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO" );
  }

  /******************************************* delete ********************************************/
  public static void delete( TableView view )
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO" );
  }

  /****************************************** fillDown *******************************************/
  public static void fillDown( TableView view )
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO" );
  }

  /******************************************* paste *********************************************/
  public static void paste( TableView view )
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO" );
  }

  /******************************************** copy *********************************************/
  public static void copy( TableView view )
  {
    // check there is only one selection
    int selections = view.getSelection().getCount();
    if ( selections == 0 )
      return;
    if ( selections > 1 )
    {
      Alert alert = new Alert( AlertType.WARNING );
      alert.setTitle( "Copy" );
      alert.setHeaderText( "This action won't work on multiple selections." );
      alert.showAndWait();
      return;
    }

    // prepare alert to give user option to cancel long running copy operations
    Alert alert = new Alert( AlertType.INFORMATION );
    alert.setTitle( "Copy" );
    alert.initOwner( view.getScene().getWindow() );
    alert.getButtonTypes().setAll( ButtonType.CANCEL );
    alert.setHeaderText( "Copy is taking a long time.\nPress 'Cancel' to terminate the copying." );
    alert.setContentText( "Preparing ..." );

    // create and start thread to copy the selected table view cells
    CopyThread copy = new CopyThread( view, alert );
    copy.start();

    // if copying the data is taking too long, give user option to cancel
    while ( copy.isAlive() && !copy.isInterrupted() )
    {
      try
      {
        // give the copy thread some time to complete before showing alert
        copy.join( WAIT_MILLISECONDS );
      }
      catch ( InterruptedException exception )
      {
        // don't expect this thread to get interrupted
        Utils.stack( exception );
      }

      // if copy thread is still alive after waiting, show alert allowing user to cancel
      if ( copy.isAlive() && !copy.isInterrupted() )
        if ( alert.showAndWait().isPresent() )
          copy.interrupt();
    }

  }

  /******************************************** cut **********************************************/
  public static void cut( TableView view )
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO" );
  }

}
