/**************************************************************************
 *  Copyright (C) 2020 by Richard Crook                                   *
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

package rjc.table.undo;

import java.util.ArrayList;

import javafx.beans.property.SimpleObjectProperty;

/*************************************************************************************************/
/********************************** Stack of UndoCommand objects *********************************/
/*************************************************************************************************/

public class UndoStack extends SimpleObjectProperty<UndoStack>
{
  private ArrayList<IUndoCommand> m_stack;      // list of undo commands
  private int                     m_index;      // executed commands index
  private int                     m_cleanIndex; // index when declared clean

  /**************************************** constructor ******************************************/
  public UndoStack()
  {
    // initialise private variables
    m_stack = new ArrayList<>();
    clear();
  }

  /******************************************** clear ********************************************/
  public void clear()
  {
    // clean the stack
    m_stack.clear();
    m_index = 0;
    setClean();
  }

  /****************************************** setClean *******************************************/
  public void setClean()
  {
    // declare current index position as being clean
    m_cleanIndex = m_index;
    fireValueChangedEvent();
  }

  /****************************************** isClean ********************************************/
  public boolean isClean()
  {
    // stack is clean if current index equals clean index
    return m_index == m_cleanIndex;
  }

  /******************************************* getSize *******************************************/
  public int getSize()
  {
    // return stack command list size
    return m_stack.size();
  }

  /******************************************* getIndex ******************************************/
  public int getIndex()
  {
    // return current executed command index
    return m_index;
  }

  /******************************************** push *********************************************/
  public void push( IUndoCommand command )
  {
    // remove any commands from stack that haven't been actioned (i.e. above index)
    if ( m_stack.size() > m_index )
    {
      m_stack.subList( m_index, m_stack.size() - 1 ).clear();

      // if stack now shorter than clean-index it will be impossible to return to clean state
      if ( m_stack.size() < m_cleanIndex )
        m_cleanIndex = Integer.MIN_VALUE;
    }

    // add new command to stack, do it, and update stack index
    m_stack.add( command );
    command.redo();
    m_index = m_stack.size();
    update( command.update() );
    fireValueChangedEvent();
  }

  /******************************************* update ********************************************/
  public void update( long updates )
  {
    // overload to perform requested updates that can be merged (typically gui updates)
  }

  /******************************************** undo *********************************************/
  public void undo()
  {
    // revert command and decrement index (or no action if nothing to revert)
    if ( m_index > 0 )
    {
      m_index--;
      m_stack.get( m_index ).undo();
      update( m_stack.get( m_index ).update() );
      fireValueChangedEvent();
    }
  }

  /******************************************** redo *********************************************/
  public void redo()
  {
    // action command and increment index (or no action if nothing to redo)
    if ( m_index < m_stack.size() )
    {
      m_stack.get( m_index ).redo();
      update( m_stack.get( m_index ).update() );
      m_index++;
      fireValueChangedEvent();
    }
  }

  /***************************************** getUndoText *****************************************/
  public String getUndoText()
  {
    // return text associated with next potential undo (no need to check index as getText does)
    return getText( m_index - 1 );
  }

  /***************************************** getRedoText *****************************************/
  public String getRedoText()
  {
    // return text associated with next potential redo (no need to check index as getText does)
    return getText( m_index );
  }

  /******************************************* getText *******************************************/
  public String getText( int index )
  {
    // return text associated with command at index (or null if outside range)
    if ( index < 0 || index >= m_stack.size() )
      return null;

    // if valid index never return null
    String text = m_stack.get( index ).text();
    return text == null ? "" : text;
  }

  /****************************************** setIndex *******************************************/
  public void setIndex( int index )
  {
    // execute redo's or undo's as necessary to get to target index
    if ( index != m_index )
    {
      long updates = 0;
      while ( index < m_index && m_index > 0 )
      {
        m_index--;
        m_stack.get( m_index ).undo();
        updates |= m_stack.get( m_index ).update();
      }
      while ( index > m_index && m_index < m_stack.size() )
      {
        m_stack.get( m_index ).redo();
        updates |= m_stack.get( m_index ).update();
        m_index++;
      }

      // perform updates collected from the redo's and undo's
      update( updates );
      fireValueChangedEvent();
    }
  }

}