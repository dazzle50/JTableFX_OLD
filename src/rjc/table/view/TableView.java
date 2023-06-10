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

public class TableView extends TableParent
{
  private TableData          m_data;

  protected TableCanvas      m_canvas;
  protected TableScrollBar   m_verticalScrollBar;
  protected TableScrollBar   m_horizontalScrollBar;

  protected ObservableDouble m_zoom;
  protected TableAxis        m_columnsAxis;        // columns (horizontal) axis
  protected TableAxis        m_rowsAxis;           // rows (vertical) axis

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // check parameters and setup table-view
    if ( data == null )
      throw new NullPointerException( "TableData must not be null" );
    m_data = data;

    // construct the table-view
    m_zoom = new ObservableDouble( 1.0 );
    m_columnsAxis = new TableAxis( m_data.getColumnCountProperty() );
    m_rowsAxis = new TableAxis( m_data.getRowCountProperty() );

    m_canvas = new TableCanvas( this );
    m_horizontalScrollBar = new TableScrollBar( m_columnsAxis, Orientation.HORIZONTAL );
    m_verticalScrollBar = new TableScrollBar( m_rowsAxis, Orientation.VERTICAL );
    getChildren().addAll( m_canvas, m_horizontalScrollBar, m_verticalScrollBar );

  }

}
