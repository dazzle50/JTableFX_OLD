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

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import rjc.table.Status.Level;
import rjc.table.Utils;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/************************** Thread for multi-cell copying to clipboard ***************************/
/*************************************************************************************************/

public class CopyThread extends Thread
{
  private TableView         m_view;
  private Alert             m_alert;
  private int               m_cellsCopied;

  final static private long UPDATE_NANOS = 1000L * 1000L * 100L; // 0.1 seconds
  final static private long MAX_COPY     = 1000L * 1000L * 5L;   // five million

  /**************************************** constructor ******************************************/
  public CopyThread( TableView view, Alert alert )
  {
    // create a copy thread for specified view
    m_view = view;
    m_alert = alert;
    setName( "CopyThread " + view.toString() );
  }

  /********************************************* run *********************************************/
  public void run()
  {
    // copy selected area contents
    m_view.getStatus().update( Level.NORMAL, "Copying ..." );
    var selected = m_view.getSelection().getSelected( 0 );
    int maxC = m_view.getData().getColumnCount() - 1;
    int maxR = m_view.getData().getRowCount() - 1;
    int c1 = Utils.clamp( selected[0], TableAxis.FIRSTCELL, maxC );
    int r1 = Utils.clamp( selected[1], TableAxis.FIRSTCELL, maxR );
    int c2 = Utils.clamp( selected[2], TableAxis.FIRSTCELL, maxC );
    int r2 = Utils.clamp( selected[3], TableAxis.FIRSTCELL, maxR );

    // generate list of selected visible indexes
    var columnIndexes = m_view.getColumnsAxis().getVisibleIndexes( c1, c2 );
    var rowIndexes = m_view.getRowsAxis().getVisibleIndexes( r1, r2 );

    // calculate number of cells to be copied
    long cellsCount = (long) columnIndexes.size() * (long) rowIndexes.size();
    if ( cellsCount > MAX_COPY )
    {
      m_view.getStatus().update( Level.ERROR, "Copy area too large " + cellsCount + " cells" );
      return;
    }

    // storage for the copied data
    var copyText = new StringBuilder();
    var copyValues = new Object[columnIndexes.size()][rowIndexes.size()];

    // copy selected visible cells
    long updateNanos = System.nanoTime() + UPDATE_NANOS;
    var drawer = m_view.getCellDrawer();
    drawer.setIndex( m_view, 0, 0 );

    for ( int rowNum = 0; rowNum < rowIndexes.size(); rowNum++ )
    {
      int rowIndex = rowIndexes.get( rowNum );
      for ( int colNum = 0; colNum < columnIndexes.size(); colNum++ )
      {
        int columnIndex = columnIndexes.get( colNum );

        // collect cell value and text
        copyValues[colNum][rowNum] = m_view.getData().getValue( columnIndex, rowIndex );
        copyText.append( drawer.getText( columnIndex, rowIndex ) );
        copyText.append( '\t' );
        m_cellsCopied++;

        // check if thread interrupted
        if ( isInterrupted() )
        {
          m_view.getStatus().update( Level.NORMAL, "Copy cancelled" );
          return;
        }

        // periodically update alert text
        if ( System.nanoTime() > updateNanos )
        {
          Platform.runLater( () -> m_alert.setContentText(
              "Copied " + ( 100L * m_cellsCopied / cellsCount ) + "% (" + m_cellsCopied + " of " + cellsCount + ")" ) );
          updateNanos = System.nanoTime() + UPDATE_NANOS;
        }
      }
      copyText.deleteCharAt( copyText.length() - 1 );
      copyText.append( '\n' );
    }
    copyText.deleteCharAt( copyText.length() - 1 );
    Platform.runLater( () -> m_alert.setContentText( "Finishing ..." ) );

    // put copied contents on system clipboard
    var content = new ClipboardContent();
    content.putString( copyText.toString() );
    content.put( Content.JTABLEFX, copyValues );

    Platform.runLater( () ->
    {
      content.putImage( m_view.snapshot( c1, r1, c2, r2, false ) );
      Clipboard.getSystemClipboard().setContent( content );
      m_alert.hide();
    } );

    m_view.getStatus().update( Level.NORMAL, "Copied " + cellsCount + " cell" + ( cellsCount == 1 ? "" : "s" ) );
  }

}
