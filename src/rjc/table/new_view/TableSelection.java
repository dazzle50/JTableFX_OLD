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

package rjc.table.new_view;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;

/*************************************************************************************************/
/************************************* Table area selection **************************************/
/*************************************************************************************************/

public class TableSelection extends TableNavigation
{
  public class Selected
  {
    public int c1; // smallest column position
    public int r1; // smallest row position
    public int c2; // largest column position
    public int r2; // largest row position

    public void set( int columnPos1, int rowPos1, int columnPos2, int rowPos2 )
    {
      // set private variables in correct order
      c1 = Math.min( columnPos1, columnPos2 );
      c2 = Math.max( columnPos1, columnPos2 );
      r1 = Math.min( rowPos1, rowPos2 );
      r2 = Math.max( rowPos1, rowPos2 );
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "@" + Integer.toHexString( hashCode() ) + "[c1=" + c1 + " r1=" + r1 + " c2="
          + c2 + " r2=" + r2 + "]";
    }
  }

  // observable list of selected areas for this table view
  final private ReadOnlyListWrapper<Selected> m_selected = new ReadOnlyListWrapper<>(
      FXCollections.observableArrayList() );

  private Selected                            m_currentSelection;                    // current selection area

  /************************************** getSelectionCount **************************************/
  public int getSelectionCount()
  {
    // return number of selected areas
    return m_selected.size();
  }

  /************************************** getSelectionCount **************************************/
  public int getSelectionCount( int columnPos, int rowPos )
  {
    // return count of selected areas covering specified cell
    int count = 0;
    for ( Selected area : m_selected )
      if ( columnPos >= area.c1 && columnPos <= area.c2 && rowPos >= area.r1 && rowPos <= area.r2 )
        count++;

    return count;
  }

  /*************************************** isCellSelected ****************************************/
  public boolean isCellSelected( int columnPos, int rowPos )
  {
    // return true if specified cell is in a selected area
    for ( Selected area : m_selected )
      if ( columnPos >= area.c1 && columnPos <= area.c2 && rowPos >= area.r1 && rowPos <= area.r2 )
        return true;

    return false;
  }

  /*************************************** hasRowSelection ***************************************/
  public boolean hasRowSelection( int rowPos )
  {
    // return true if specified row has any selection
    // TODO

    return false;
  }

  /************************************* hasColumnSelection **************************************/
  public boolean hasColumnSelection( int columnPos )
  {
    // return true if specified column has any selection
    // TODO

    return false;
  }

  /*********************************** setSelectFocusPosition ************************************/
  protected void setSelectFocusPosition( int columnPos, int rowPos, boolean setFocus, boolean clearSelection )
  {
    // TODO
    /*
    if ( setFocus && ( columnPos == RIGHT_OF_TABLE || rowPos == BELOW_TABLE ) )
      throw new IllegalArgumentException( "Setting focus not allowed " + columnPos + " " + rowPos );
    
    // clear previous selections
    if ( clearSelection )
      clearAllSelection();
    
    // set table select & focus cell position properties
    setSelectPosition( columnPos, rowPos );
    if ( setFocus )
      setFocusPosition( columnPos, rowPos );
    setCurrentSelection();
    
    // ensure column and row positions are visible
    if ( columnPos < Integer.MAX_VALUE )
      columnPos = ensureColumnShown( columnPos );
    if ( rowPos < Integer.MAX_VALUE )
      rowPos = ensureRowShown( rowPos );
    */
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string
    return getClass().getSimpleName() + "@" + Integer.toHexString( hashCode() ) + "[selected=" + m_selected.size()
        + "]";
  }

}
