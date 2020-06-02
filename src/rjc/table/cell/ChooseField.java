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

package rjc.table.cell;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import javafx.scene.input.KeyEvent;

/*************************************************************************************************/
/**************************** Control for choosing a value from array ****************************/
/*************************************************************************************************/

public class ChooseField extends XTextField
{
  private DropDown          m_dropdown; // choice drop-down
  private Object[]          m_values;   // array of objects choice to be from
  private ArrayList<String> m_text;     // list of value string equivalents
  private int               m_index;    // index of current selected item

  /**************************************** constructor ******************************************/
  public ChooseField( Object[] values )
  {
    // initiate the field
    m_values = values;
    m_dropdown = new DropDown( this );

    // generate string equivalent of value objects
    m_text = new ArrayList<>( values.length );
    for ( int index = 0; index < m_values.length; index++ )
    {
      Object value = values[index];
      m_text.add( value == null ? "null" : value.toString() );
    }

    // configure the button  
    setEditable( false );
    setButtonType( ButtonType.DOWN );

    // react to key presses and button mouse clicks
    setOnKeyPressed( event -> keyPressed( event ) );
    setOnKeyTyped( event -> keyTyped( event ) );
  }

  /***************************************** changeValue *****************************************/
  @Override
  public void changeValue( double delta )
  {
    // change selected index
    setSelectedIndex( m_index - (int) delta );
  }

  /****************************************** getCount *******************************************/
  public int getCount()
  {
    // return number of items user can choose from
    return m_values.length;
  }

  /************************************** getSelectedIndex ***************************************/
  public int getSelectedIndex()
  {
    // return currently selected index
    return m_index;
  }

  /************************************** setSelectedIndex ***************************************/
  public void setSelectedIndex( int index )
  {
    // set selected index and update displayed text to match
    index = index % m_values.length;
    m_index = index < 0 ? index + m_values.length : index;

    setText( getText( m_index ) );
    m_dropdown.scrollToIndex( m_index );
  }

  /******************************************* getText *******************************************/
  public String getText( int index )
  {
    // return text equivalent of object at specified index
    return m_text.get( index );
  }

  /***************************************** getSelected *****************************************/
  public Object getSelected()
  {
    // return currently selected object
    return m_values[m_index];
  }

  /***************************************** setSelected *****************************************/
  public void setSelected( Object value )
  {
    // set currently selected object
    for ( int index = 0; index < m_values.length; index++ )
      if ( m_values[index] == value )
      {
        setSelectedIndex( index );
        return;
      }

    // value not found in list so throw exception
    throw new InvalidParameterException( value == null ? "null" : value.toString() );
  }

  /***************************************** keyPressed ******************************************/
  protected void keyPressed( KeyEvent event )
  {
    // action key press to change current selected item
    event.consume();
    switch ( event.getCode() )
    {
      case DOWN:
      case KP_DOWN:
      case RIGHT:
      case KP_RIGHT:
      case PAGE_DOWN:
        changeValue( -1 );
        break;
      case UP:
      case KP_UP:
      case LEFT:
      case KP_LEFT:
      case PAGE_UP:
        changeValue( 1 );
        break;
      case HOME:
        setSelectedIndex( 0 );
        break;
      case END:
        setSelectedIndex( getCount() - 1 );
        break;
      case F2:
        m_dropdown.toggle();
        break;
      default:
        break;
    }
  }

  /****************************************** keyTyped *******************************************/
  public void keyTyped( KeyEvent event )
  {
    // find next item that starts with typed key (case-insensitive)
    String key = event.getCharacter().toLowerCase();
    for ( int delta = 1; delta < m_values.length; delta++ )
    {
      int index = ( m_index + delta ) % m_values.length;
      if ( m_text.get( index ).toLowerCase().startsWith( key ) )
      {
        setSelectedIndex( index );
        return;
      }
    }
  }

}
