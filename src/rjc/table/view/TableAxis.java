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

package rjc.table.view;

import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.ReadOnlyIntegerProperty;

/*************************************************************************************************/
/************************** Extends axis to support section collapsing ***************************/
/*************************************************************************************************/

public class TableAxis extends AxisSize
{
  // set of collapsed positions
  private Set<Integer> m_collapsed = new HashSet<Integer>();

  /**************************************** constructor ******************************************/
  public TableAxis( ReadOnlyIntegerProperty countProperty )
  {
    // call super
    super( countProperty );
  }

  /******************************************** clear ********************************************/
  @Override
  public void reset()
  {
    // call super + clear set of collapsed positions
    super.reset();
    m_collapsed.clear();
  }

  /***************************************** isCollapsed *****************************************/
  public boolean isCollapsed( int position )
  {
    // return true if the summary at specified position is collapsed
    return m_collapsed.contains( position );
  }

  /****************************************** collapse *******************************************/
  public void collapse( int startPosition, int endPosition )
  {
    // TODO
  }

  /******************************************* expand ********************************************/
  public void expand( int startPosition, int endPosition )
  {
    // TODO
  }

  /**************************************** getCollapsed *****************************************/
  public Set<Integer> getCollapsed()
  {
    // TODO;
    return m_collapsed;
  }

  /**************************************** setCollapsed *****************************************/
  public void setCollapsed( Set<Integer> positions )
  {
    // TODO
  }

  /****************************************** getFirst *******************************************/
  public int getFirst()
  {
    // return first cell body position visible
    return getNext( HEADER );
  }

  /******************************************* getLast *******************************************/
  public int getLast()
  {
    // return last cell body position visible
    return getPrevious( getCount() );
  }

  /******************************************* getNext *******************************************/
  public int getNext( int position )
  {
    // return next cell body position visible, or last if there isn't one
    int max = getCount() - 1;
    boolean hidden = true;
    while ( position < max && hidden )
      hidden = isPositionHidden( ++position );

    if ( hidden )
      return getLast();
    return position;
  }

  /***************************************** getPrevious *****************************************/
  public int getPrevious( int position )
  {
    // return previous cell body position visible, or first if there isn't one
    boolean hidden = true;
    while ( position > FIRSTCELL && hidden )
      hidden = isPositionHidden( --position );

    if ( hidden )
      return getFirst();
    return position;
  }

}
