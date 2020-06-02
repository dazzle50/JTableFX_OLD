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

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import rjc.table.Status;

/*************************************************************************************************/
/***************************** Generic spin editor for number values *****************************/
/*************************************************************************************************/

public class NumberSpinField extends XTextField
{
  private double          m_minValue;          // minimum number allowed
  private double          m_maxValue;          // maximum number allowed
  private int             m_maxFractionDigits; // number of digits after decimal point

  private double          m_page;              // value increment or decrement on page-up or page-down
  private double          m_step;              // value increment or decrement on arrow-up or arrow-down
  private String          m_prefix;            // prefix shown before value
  private String          m_suffix;            // suffix shown after value

  private DecimalFormat   m_numberFormat;      // number decimal format
  private NumberSpinField m_wrapField;         // spin field for wrap support

  private Status          m_rangeError;        // text associated with number being out of range
  private Status          m_rangeNormal;       // text associated with number being in range

  /**************************************** constructor ******************************************/
  public NumberSpinField()
  {
    // set default spin editor characteristics
    setPrefixSuffix( null, null );
    setFormat( "0", 0 );
    setRange( 0.0, 999.0 );
    setStepPage( 1.0, 10.0 );
    setButtonType( ButtonType.UP_DOWN );

    // react to key presses and button mouse clicks
    setOnKeyPressed( event -> keyPressed( event ) );
    getButton().setOnMousePressed( event -> buttonPressed( event ) );

    // add listener to set control error state and remove any excess leading zeros
    textProperty().addListener( ( observable, oldText, newText ) ->
    {
      // if spinner value not in range, set control into error state
      double num = getDouble();
      if ( num < m_minValue || num > m_maxValue || getValue().length() < 1 )
      {
        setStatus( m_rangeError );
        setStyle( m_rangeError.getStyle() );
      }
      else
      {
        setStatus( m_rangeNormal );
        setStyle( m_rangeNormal.getStyle() );
      }
    } );

  }

  /****************************************** setValue *******************************************/
  public void setValue( String text )
  {
    // set editor text adding prefix and suffix
    setText( m_prefix + text + m_suffix );
    positionCaret( m_prefix.length() + text.length() );
  }

  /****************************************** getValue *******************************************/
  public String getValue()
  {
    // return editor text without prefix + suffix
    return getText().substring( m_prefix.length(), getText().length() - m_suffix.length() );
  }

  /****************************************** setDouble ******************************************/
  public void setDouble( double value )
  {
    // set editor text (adding prefix and suffix)
    setValue( m_numberFormat.format( value ) );
  }

  /****************************************** getDouble ******************************************/
  public double getDouble()
  {
    // return editor text (less prefix + suffix) converted to double number
    try
    {
      return Double.parseDouble( getValue() );
    }
    catch ( Exception exception )
    {
      return 0.0;
    }
  }

  /***************************************** setInteger ******************************************/
  public void setInteger( int value )
  {
    // set editor text (adding prefix and suffix)
    setValue( m_numberFormat.format( value ) );
  }

  /***************************************** getInteger ******************************************/
  public int getInteger()
  {
    // return editor text (less prefix + suffix) converted to integer number
    try
    {
      return Integer.parseInt( getValue() );
    }
    catch ( Exception exception )
    {
      return 0;
    }
  }

  /****************************************** setFormat ******************************************/
  public void setFormat( String format, int maxFractionDigits )
  {
    // check inputs
    if ( maxFractionDigits < 0 || maxFractionDigits > 8 )
      throw new IllegalArgumentException( "Digits after deciminal place out of 0-8 range! " + maxFractionDigits );

    // set number format
    m_maxFractionDigits = maxFractionDigits;
    m_numberFormat = new DecimalFormat( format );
    m_numberFormat.setMaximumFractionDigits( maxFractionDigits );
    setRange( m_minValue, m_maxValue );
  }

  /******************************************* setRange ******************************************/
  public void setRange( double minValue, double maxValue )
  {
    // check inputs
    if ( minValue > maxValue )
      throw new IllegalArgumentException( "Min greater than max! " + minValue + " " + maxValue );

    // set range and number of digits after decimal point
    m_rangeNormal = new Status();
    m_rangeError = new Status( Status.Level.ERROR,
        "Value not between " + m_numberFormat.format( minValue ) + " and " + m_numberFormat.format( maxValue ) );
    m_minValue = minValue;
    m_maxValue = maxValue;
    determineAllowed();
  }

  /***************************************** setStepPage *****************************************/
  public void setStepPage( double step, double page )
  {
    // set step and page increment/decrement sizes
    m_step = step;
    m_page = page;
  }

  /*************************************** setPrefixSuffix ***************************************/
  public void setPrefixSuffix( String prefix, String suffix )
  {
    // set prefix and suffix, translating null to ""
    m_prefix = ( prefix == null ? "" : prefix );
    m_suffix = ( suffix == null ? "" : suffix );
    determineAllowed();
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

  /************************************** determineAllowed ***************************************/
  private void determineAllowed()
  {
    // determine regular expression defining text allowed to be entered
    StringBuilder allow = new StringBuilder( 32 );
    allow.append( Pattern.quote( m_prefix ) );

    if ( m_minValue < 0.0 )
      allow.append( "-?" );
    allow.append( "\\d*" );
    if ( m_maxFractionDigits > 0 )
      allow.append( "\\.?\\d{0," + m_maxFractionDigits + "}" );

    allow.append( Pattern.quote( m_suffix ) );
    setAllowed( allow.toString() );
  }

  /**************************************** buttonPressed ****************************************/
  private void buttonPressed( MouseEvent event )
  {
    // if user clicked top half of buttons, step up, else step down
    if ( event.getY() < getButton().getHeight() / 2 )
      changeNumber( m_step );
    else
      changeNumber( -m_step );

    event.consume();
    requestFocus();
  }

  /**************************************** changeNumber *****************************************/
  private void changeNumber( double delta )
  {
    // modify number, ensuring it is between min and max
    double num = getDouble() + delta;

    if ( m_wrapField == null )
    {
      // no wrap field so simply limit to min & max values
      if ( num < m_minValue )
        num = m_minValue;
      if ( num > m_maxValue )
        num = m_maxValue;
      setDouble( num );
    }
    else
    {
      // wrap field exists
      if ( num < m_minValue )
      {
        m_wrapField.changeNumber( -1 );
        num = m_maxValue - ( m_minValue - num - 1 );
      }
      if ( num > m_maxValue )
      {
        m_wrapField.changeNumber( 1 );
        num = m_minValue + ( num - m_maxValue - 1 );
      }
      setDouble( num );
    }
  }

  /***************************************** keyPressed ******************************************/
  protected void keyPressed( KeyEvent event )
  {
    // action key press to change value up or down
    switch ( event.getCode() )
    {
      case DOWN:
        changeNumber( -m_step );
        event.consume();
        break;
      case PAGE_DOWN:
        changeNumber( -m_page );
        event.consume();
        break;
      case UP:
        changeNumber( m_step );
        event.consume();
        break;
      case PAGE_UP:
        changeNumber( m_page );
        event.consume();
        break;
      case HOME:
        setDouble( m_minValue );
        event.consume();
        break;
      case END:
        setDouble( m_maxValue );
        event.consume();
        break;
      default:
        break;
    }
  }

  /***************************************** changeValue *****************************************/
  @Override
  public void changeValue( double delta )
  {
    // increase or decrease value by step on mouse wheel scroll event
    changeNumber( delta * m_step );
  }

  /**************************************** setWrapField *****************************************/
  public void setWrapField( NumberSpinField wrap )
  {
    // set spin field to increment or decrement on this spin trying to go beyond min or max
    m_wrapField = wrap;
  }

}