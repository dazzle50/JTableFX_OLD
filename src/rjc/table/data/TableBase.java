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

package rjc.table.data;

import java.util.ArrayList;

import rjc.table.Utils;
import rjc.table.view.TableView;

/*************************************************************************************************/
/************************** Supports registered associated table views ***************************/
/*************************************************************************************************/

public class TableBase
{
  private ArrayList<TableView> m_views = new ArrayList<TableView>();

  /****************************************** register *******************************************/
  public void register( TableView view )
  {
    // register associated table view
    if ( !m_views.contains( view ) )
      m_views.add( view );
    else
      Utils.trace( "WARNING: view already registered", view, this );
  }

  /***************************************** unregister ******************************************/
  public void unregister( TableView view )
  {
    // unregister associated table view
    boolean contained = m_views.remove( view );
    if ( !contained )
      Utils.trace( "WARNING: view was not registered", view, this );
  }

  /****************************************** getViews *******************************************/
  public ArrayList<TableView> getViews()
  {
    // return list of associated table views
    return m_views;
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
    m_views.parallelStream().forEach( view -> view.redrawCell( columnIndex, rowIndex ) );
  }

  /***************************************** redrawColumn ****************************************/
  public void redrawColumn( int columnIndex )
  {
    // redraw column in associated views in PARALLEL
    m_views.parallelStream().forEach( view -> view.redrawColumn( columnIndex ) );
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // redraw row in associated views in PARALLEL
    m_views.parallelStream().forEach( view -> view.redrawRow( rowIndex ) );
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // return as string
    return getClass().getSimpleName() + "@" + Integer.toHexString( hashCode() ) + "[m_views=" + m_views + "]";
  }

}
