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

package rjc.table.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rjc.table.Utils;
import rjc.table.signal.ISignal;
import rjc.table.signal.ObservableInteger;
import rjc.table.signal.ObservableInteger.ReadOnlyInteger;
import rjc.table.view.TableView;

/*************************************************************************************************/
/******************* Column & row counts + registering associated table views ********************/
/*************************************************************************************************/

public class TableBase implements ISignal
{
  //observable integers for table body column & row counts
  private ObservableInteger    m_columnCount = new ObservableInteger( 3 );
  private ObservableInteger    m_rowCount    = new ObservableInteger( 10 );

  // list of registered table views
  private ArrayList<TableView> m_views       = new ArrayList<>();

  public enum Signal
  {
    VIEW_REGISTERED, VIEW_UNREGISTERED
  }

  // column & row index starts at 0 for table body, index of -1 is for header
  final static public int HEADER = -1;

  /*************************************** getColumnCount ****************************************/
  final public int getColumnCount()
  {
    // return number of columns in table body
    return m_columnCount.get();
  }

  /*************************************** setColumnCount ****************************************/
  final public void setColumnCount( int columnCount )
  {
    // set number of columns in table body
    m_columnCount.set( columnCount );
  }

  /**************************************** getRowCount ******************************************/
  final public int getRowCount()
  {
    // return number of rows in table body
    return m_rowCount.get();
  }

  /**************************************** setRowCount ******************************************/
  final public void setRowCount( int rowCount )
  {
    // set number of rows in table body
    m_rowCount.set( rowCount );
  }

  /*********************************** getColumnCountProperty ************************************/
  final public ReadOnlyInteger getColumnCountProperty()
  {
    // return read-only property for column count
    return m_columnCount.getReadOnly();
  }

  /************************************ getRowCountProperty **************************************/
  final public ReadOnlyInteger getRowCountProperty()
  {
    // return read-only property for row count
    return m_rowCount.getReadOnly();
  }

  /****************************************** register *******************************************/
  public void register( TableView view )
  {
    // register associated table view
    if ( m_views.contains( view ) )
      Utils.trace( "WARNING: view already registered", view, this );
    else
    {
      m_views.add( view );
      signal( Signal.VIEW_REGISTERED, view );
    }
  }

  /***************************************** unregister ******************************************/
  public void unregister( TableView view )
  {
    // unregister associated table view
    boolean contained = m_views.remove( view );
    if ( contained )
      signal( Signal.VIEW_UNREGISTERED, view );
    else
      Utils.trace( "WARNING: view was not registered", view, this );
  }

  /****************************************** getViews *******************************************/
  public List<TableView> getViews()
  {
    // return read-only list of associated table views
    return Collections.unmodifiableList( m_views );
  }

  /***************************************** resetViews ******************************************/
  public void resetViews()
  {
    // reset associated views (for example after number of columns or rows changed) in PARALLEL
    m_views.parallelStream().forEach( view -> view.reset() );
  }

  /***************************************** redrawViews *****************************************/
  public void redrawViews()
  {
    // redraw associated whole views in PARALLEL
    m_views.parallelStream().forEach( view -> view.redraw() );
  }

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // redraw cell in associated views in PARALLEL
    if ( isColumnIndexValid( columnIndex ) && isRowIndexValid( rowIndex ) )
      m_views.parallelStream().forEach( view -> view.redrawCell( columnIndex, rowIndex ) );
  }

  /***************************************** redrawColumn ****************************************/
  public void redrawColumn( int columnIndex )
  {
    // redraw column in associated views in PARALLEL
    if ( isColumnIndexValid( columnIndex ) )
      m_views.parallelStream().forEach( view -> view.redrawColumn( columnIndex ) );
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // redraw row in associated views in PARALLEL
    if ( isRowIndexValid( rowIndex ) )
      m_views.parallelStream().forEach( view -> view.redrawRow( rowIndex ) );
  }

  /************************************* isColumnIndexValid **************************************/
  public boolean isColumnIndexValid( int columnIndex )
  {
    // return true if column index is in table body or header range
    int max = getColumnCount() - 1;
    if ( columnIndex < HEADER || columnIndex > max )
    {
      Utils.stack( "WARNING: column index out of range (-1," + max + ")", columnIndex, this );
      return false;
    }

    return true;
  }

  /*************************************** isRowIndexValid ***************************************/
  public boolean isRowIndexValid( int rowIndex )
  {
    // return true if row index is in table body or header range
    int max = getRowCount() - 1;
    if ( rowIndex < HEADER || rowIndex > max )
    {
      Utils.stack( "WARNING: row index out of range (-1," + max + ")", rowIndex, this );
      return false;
    }

    return true;
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // return as string
    return getClass().getSimpleName() + "@" + Integer.toHexString( System.identityHashCode( this ) ) + "[m_columnCount="
        + m_columnCount + " m_rowCount=" + m_rowCount + " m_views=" + m_views + "]";
  }

}
