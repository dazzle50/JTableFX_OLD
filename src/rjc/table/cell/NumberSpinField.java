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

import rjc.table.Status.Level;

/*************************************************************************************************/
/**************************** Generic spin control for number values *****************************/
/*************************************************************************************************/

public class NumberSpinField extends SpinField
{
  private int           m_maxFractionDigits; // number of digits after decimal point
  private DecimalFormat m_numberFormat;      // number decimal format

  private String        m_prefix;            // prefix shown before value
  private String        m_suffix;            // suffix shown after value

  private String        m_errorMsg;          // status error text

  /**************************************** constructor ******************************************/
  public NumberSpinField()
  {
    // set default spin editor characteristics
    setPrefixSuffix( null, null );
    setFormat( "0", 0 );

    // add listener to set control error state and remove any excess leading zeros
    textProperty().addListener( ( observable, oldText, newText ) ->
    {
      // if spinner value not in range, set control into error state
      double num = getDouble();
      if ( num < getMin() || num > getMax() )
        getStatus().update( Level.ERROR, m_errorMsg );
      else
        getStatus().update( Level.NORMAL, null );
      setStyle( getStatus().getStyle() );
    } );
  }

  /****************************************** setField *******************************************/
  @Override
  public void setField( Object value )
  {
    // if no number formating available or string, set field text as specified 
    if ( m_numberFormat == null || value instanceof String )
      super.setField( value );
    else
    {
      // set field text adding prefix and suffix
      String text = m_numberFormat.format( getValue() );
      setText( m_prefix + text + m_suffix );
      positionCaret( m_prefix.length() + text.length() );
    }
  }

  /****************************************** getDouble ******************************************/
  public double getDouble()
  {
    // return field text (less prefix + suffix) converted to double number
    try
    {
      String value = getText().substring( m_prefix.length(), getText().length() - m_suffix.length() );
      return Double.parseDouble( value );
    }
    catch ( Exception exception )
    {
      return 0.0;
    }
  }

  /***************************************** getInteger ******************************************/
  public int getInteger()
  {
    // return field text (less prefix + suffix) converted to integer number
    try
    {
      String value = getText().substring( m_prefix.length(), getText().length() - m_suffix.length() );
      return Integer.parseInt( value );
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
    setRange( getMin(), getMax() );
  }

  /******************************************* setRange ******************************************/
  @Override
  public void setRange( double minValue, double maxValue )
  {
    // set range and number of digits after decimal point
    super.setRange( minValue, maxValue );
    if ( m_numberFormat != null )
    {
      m_errorMsg = "Value not between " + m_numberFormat.format( minValue ) + " and "
          + m_numberFormat.format( maxValue );
      determineAllowed();
    }
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

    if ( getMin() < 0.0 )
      allow.append( "-?" );
    allow.append( "\\d*" );
    if ( m_maxFractionDigits > 0 )
      allow.append( "\\.?\\d{0," + m_maxFractionDigits + "}" );

    allow.append( Pattern.quote( m_suffix ) );
    setAllowed( allow.toString() );
  }

}