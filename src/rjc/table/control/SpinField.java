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

package rjc.table.control;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import rjc.table.Utils;

/*************************************************************************************************/
/****************************** Base spin field for selecting value ******************************/
/*************************************************************************************************/

public class SpinField extends XTextField implements IWrapField
{
  private double     m_minValue;  // minimum value allowed
  private double     m_maxValue;  // maximum value allowed

  private double     m_step;      // value increment or decrement on arrow-up or arrow-down
  private double     m_page;      // value increment or decrement on page-up or page-down

  private String     m_prefix;    // prefix shown before value
  private String     m_suffix;    // suffix shown after value

  private IWrapField m_wrapField; // field for wrap support

  /**************************************** constructor ******************************************/
  public SpinField()
  {
    // set default spin field characteristics
    setPrefixSuffix( null, null );
    setRange( 0.0, 999.0 );
    setStepPage( 1.0, 10.0 );
    setButtonType( ButtonType.UP_DOWN );

    // react to key presses and button mouse clicks
    setOnKeyPressed( event -> keyPressed( event ) );
    getButton().setOnMousePressed( event -> buttonPressed( event ) );
  }

  /****************************************** getValue *******************************************/
  public String getValue()
  {
    // return field text after removing prefix + suffix
    return getText().substring( m_prefix.length(), getText().length() - m_suffix.length() );
  }

  /***************************************** getDouble *******************************************/
  public double getDouble()
  {
    // attempt to get number from field text, otherwise return min
    try
    {
      return Double.parseDouble( getValue() );
    }
    catch ( Exception exception )
    {
      return m_minValue;
    }
  }

  /****************************************** setValue *******************************************/
  public void setValue( Object value )
  {
    // set field text after adding prefix + suffix
    String text = value == null ? "null" : value.toString();
    setText( m_prefix + text + m_suffix );
    positionCaret( m_prefix.length() + text.length() );
  }

  /****************************************** getPrefix ******************************************/
  public String getPrefix()
  {
    // return prefix
    return m_prefix;
  }

  /****************************************** getSuffix ******************************************/
  public String getSuffix()
  {
    // return suffix
    return m_suffix;
  }

  /*************************************** setPrefixSuffix ***************************************/
  public void setPrefixSuffix( String prefix, String suffix )
  {
    // set prefix and suffix, translating null to ""
    m_prefix = ( prefix == null ? "" : prefix );
    m_suffix = ( suffix == null ? "" : suffix );
  }

  /******************************************* getMin ********************************************/
  public double getMin()
  {
    // return minimum allowed spin value
    return m_minValue;
  }

  /******************************************* getMax ********************************************/
  public double getMax()
  {
    // return maximum allowed spin value
    return m_maxValue;
  }

  /******************************************* setRange ******************************************/
  public void setRange( double minValue, double maxValue )
  {
    // check range is valid
    if ( minValue > maxValue )
      throw new IllegalArgumentException( "Min " + minValue + " > Max " + maxValue );

    // set range and reset value to ensure in range
    m_minValue = minValue;
    m_maxValue = maxValue;
  }

  /***************************************** setStepPage *****************************************/
  public void setStepPage( double step, double page )
  {
    // set step and page increment/decrement sizes
    m_step = step;
    m_page = page;
  }

  /**************************************** buttonPressed ****************************************/
  private void buttonPressed( MouseEvent event )
  {
    // if user clicked top half of buttons, step up, else step down
    requestFocus();
    event.consume();

    if ( event.getY() < getButton().getHeight() / 2 )
      changeValue( m_step );
    else
      changeValue( -m_step );
  }

  /***************************************** keyPressed ******************************************/
  protected void keyPressed( KeyEvent event )
  {
    // action key press to change value up or down
    switch ( event.getCode() )
    {
      case DOWN:
        changeValue( -m_step );
        event.consume();
        break;
      case PAGE_DOWN:
        changeValue( -m_page );
        event.consume();
        break;
      case UP:
        changeValue( m_step );
        event.consume();
        break;
      case PAGE_UP:
        changeValue( m_page );
        event.consume();
        break;
      case HOME:
        setValue( m_minValue );
        event.consume();
        break;
      case END:
        setValue( m_maxValue );
        event.consume();
        break;
      default:
        break;
    }
  }

  /***************************************** changeValue *****************************************/
  private void changeValue( double delta )
  {
    // change spin value by delta overflowing to wrap-field if available
    double num = getDouble() + delta;

    // if no wrap field simply limit to range
    if ( m_wrapField == null )
    {
      num = Utils.clamp( num, m_minValue, m_maxValue );
      setValue( num );
    }
    else
    {
      // if wrap field step its value as necessary
      double range = m_maxValue - m_minValue + m_step;
      while ( num < m_minValue )
      {
        m_wrapField.stepValue( -1 );
        num += range;
      }
      while ( num > m_maxValue )
      {
        m_wrapField.stepValue( 1 );
        num -= range;
      }
      setValue( num );
    }
  }

  /****************************************** stepValue ******************************************/
  @Override
  public void stepValue( double delta )
  {
    // increase or decrease spin value by step on mouse wheel scroll event
    changeValue( delta * m_step );
  }

  /**************************************** setWrapField *****************************************/
  public void setWrapField( IWrapField wrap )
  {
    // set overflow field to step when this spin goes beyond min or max
    m_wrapField = wrap;
  }

}