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
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import rjc.table.Status.Level;
import rjc.table.Utils;
import rjc.table.undo.CommandDelete;
import rjc.table.undo.CommandPasteCells;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/************************** Functionality for multi-cell content actions *************************/
/*************************************************************************************************/

public class Content
{
  final static private long      WAIT_MILLISECONDS = 2000L;                                                // 2 seconds

  final static public DataFormat JTABLEFX          = new DataFormat( "application/jtablefx-object-array" );

  /******************************************* insert ********************************************/
  public static void insert( TableView view )
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO" );
  }

  /******************************************* delete ********************************************/
  public static void delete( TableView view )
  {
    // attempt to set all table-view selected cells to null
    int selections = view.getSelection().getCount();
    var command = new CommandDelete( view );
    var data = view.getData();
    int maxC = data.getColumnCount() - 1;
    int maxR = data.getRowCount() - 1;

    for ( int index = 0; index < selections; index++ )
    {
      // get selected area
      var selected = view.getSelection().getSelected( index );
      int c1 = Utils.clamp( selected[0], TableAxis.FIRSTCELL, maxC );
      int r1 = Utils.clamp( selected[1], TableAxis.FIRSTCELL, maxR );
      int c2 = Utils.clamp( selected[2], TableAxis.FIRSTCELL, maxC );
      int r2 = Utils.clamp( selected[3], TableAxis.FIRSTCELL, maxR );

      // generate list of selected visible indexes
      var columnIndexes = view.getColumnsAxis().getVisibleIndexes( c1, c2 );
      var rowIndexes = view.getRowsAxis().getVisibleIndexes( r1, r2 );

      // for each visible selected cell if successfully deleted, add to command
      for ( int row : rowIndexes )
        for ( int col : columnIndexes )
          command.add( col, row );
    }

    // push command onto stack and update status
    command.push( view.getUndoStack() );
    view.getStatus().update( Level.NORMAL, command.text() );
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
    // attempt to paste system clipboard contents to table-view
    var array = getClipboardArray();
    if ( array != null )
      pasteArray( view, array );
    else
      pasteString( view, Clipboard.getSystemClipboard().getString() );
  }

  /***************************************** pasteString *****************************************/
  private static void pasteString( TableView view, String string )
  {
    // TODO Auto-generated method stub ......................................................
    Utils.trace( "Contains String !!!", string );

  }

  /***************************************** pasteArray ******************************************/
  private static void pasteArray( TableView view, Object[] array )
  {
    // TODO Auto-generated method stub ......................................................
    Utils.trace( "Contains JTABLEFX !!!", array );
    for ( int col = 0; col < array.length; col++ )
      Utils.trace( "Column " + col, array[col] );

    // paste cell values to view at focus position via undo command 
    int columnPos = view.getFocusCell().getColumnPos();
    int rowPos = view.getFocusCell().getRowPos();
    var command = new CommandPasteCells( view, columnPos, rowPos, array );

    // push command onto stack and update status
    command.push( view.getUndoStack() );
    view.getStatus().update( Level.NORMAL, command.text() );
  }

  /************************************** getClipboardArray **************************************/
  private static Object[] getClipboardArray()
  {
    // returns JTABLEFX array if present on system clipboard, otherwise null
    var contents = Clipboard.getSystemClipboard().getContent( JTABLEFX );
    if ( contents == null || !contents.getClass().isArray() )
      return null;

    Object[] array = (Object[]) contents;
    if ( array.length < 1 || !array[0].getClass().isArray() || ( (Object[]) array[0] ).length < 1 )
      return null;

    return array;
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
      alert.initOwner( view.getScene().getWindow() );
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
