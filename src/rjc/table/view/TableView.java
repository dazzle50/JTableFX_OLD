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

package rjc.table.view;

import javafx.geometry.Orientation;
import rjc.table.data.TableData;
import rjc.table.signal.ObservableDouble;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/********************************** Base class for table views ***********************************/
/*************************************************************************************************/

public class TableView extends TableViewParent
{
  private TableData        m_data;

  private TableCanvas      m_canvas;
  private TableScrollBar   m_verticalScrollBar;
  private TableScrollBar   m_horizontalScrollBar;

  private ObservableDouble m_zoom;
  private TableAxis        m_columnsAxis;        // columns (horizontal) axis
  private TableAxis        m_rowsAxis;           // rows (vertical) axis

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // check parameters and setup table-view
    if ( data == null )
      throw new NullPointerException( "TableData must not be null" );
    m_data = data;

    // construct the table-view
    m_zoom = new ObservableDouble( 1.0 );
    m_columnsAxis = new TableAxis( m_data.columnCountProperty() );
    m_rowsAxis = new TableAxis( m_data.rowCountProperty() );

    m_canvas = new TableCanvas( this );
    m_horizontalScrollBar = new TableScrollBar( m_columnsAxis, Orientation.HORIZONTAL );
    m_verticalScrollBar = new TableScrollBar( m_rowsAxis, Orientation.VERTICAL );
    getChildren().addAll( m_canvas, m_horizontalScrollBar, m_verticalScrollBar );
  }

  /******************************************* resize ********************************************/
  @Override
  public void resize( double width, double height )
  {
    // do nothing if no change in size
    if ( (int) width == getWidth() && (int) height == getHeight() )
      return;

    // only resize if width && height less than max integer (which happens on first pass)
    if ( width < Integer.MAX_VALUE && height < Integer.MAX_VALUE )
    {
      // resize parent and re-layout canvas and scroll bars
      super.resize( width, height );
      layoutDisplay();
    }
  }

  /**************************************** layoutDisplay ****************************************/
  public void layoutDisplay()
  {
    // determine which scroll-bars should be visible
    int tableH = m_rowsAxis.getAxisPixels();
    int tableW = m_columnsAxis.getAxisPixels();
    int scrollbarSize = (int) m_verticalScrollBar.getWidth();

    boolean isVSBvisible = getHeight() < tableH;
    int visibleWidth = isVSBvisible ? getWidth() - scrollbarSize : getWidth();
    boolean isHSBvisible = visibleWidth < tableW;
    int visibleHeight = isHSBvisible ? getHeight() - scrollbarSize : getHeight();
    isVSBvisible = visibleHeight < tableH;
    visibleWidth = isVSBvisible ? getWidth() - scrollbarSize : getWidth();

    // update vertical scroll bar
    var sb = m_verticalScrollBar;
    sb.setVisible( isVSBvisible );
    if ( isVSBvisible )
    {
      sb.setPrefHeight( visibleHeight );
      sb.relocate( getWidth() - scrollbarSize, 0.0 );

      double max = tableH - visibleHeight;
      sb.setMax( max );
      sb.setVisibleAmount( max * visibleHeight / tableH );
      sb.setBlockIncrement( visibleHeight - m_rowsAxis.getHeaderPixels() );

      if ( sb.getValue() > max )
        sb.setValue( max );
    }
    else
    {
      sb.setValue( 0.0 );
      sb.setMax( 0.0 );
    }

    // update horizontal scroll bar
    sb = m_horizontalScrollBar;
    sb.setVisible( isHSBvisible );
    if ( isHSBvisible )
    {
      sb.setPrefWidth( visibleWidth );
      sb.relocate( 0.0, getHeight() - scrollbarSize );

      double max = tableW - visibleWidth;
      sb.setMax( max );
      sb.setVisibleAmount( max * visibleWidth / tableW );
      sb.setBlockIncrement( visibleWidth - m_columnsAxis.getHeaderPixels() );

      if ( sb.getValue() > max )
        sb.setValue( max );
    }
    else
    {
      sb.setValue( 0.0 );
      sb.setMax( 0.0 );
    }

    // update canvas & overlay size (table + blank excess space)
    m_canvas.resize( visibleWidth, visibleHeight );
  }
}
