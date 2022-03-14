/**************************************************************************
 *  Copyright (C) 2022 by Richard Crook                                   *
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

package rjc.table.signal;

/*************************************************************************************************/
/***************************** Observable double & read-only double ******************************/
/*************************************************************************************************/

public class ObservableDouble implements ISignal
{
  private double m_value; // stored double value

  public class ReadOnlyDouble implements ISignal // provides read-only access
  {
    private ObservableDouble m_observable;

    public ReadOnlyDouble( ObservableDouble observable )
    {
      m_observable = observable;
      m_observable.addListener( x -> signal() );
    }

    public double get()
    {
      return m_observable.get();
    }
  }

  /**************************************** constructor ******************************************/
  public ObservableDouble()
  {
    // construct
  }

  /**************************************** constructor ******************************************/
  public ObservableDouble( double value )
  {
    // construct
    m_value = value;
  }

  /********************************************* get *********************************************/
  public double get()
  {
    // return value of integer
    return m_value;
  }

  /********************************************* set *********************************************/
  public void set( double value )
  {
    // set value of integer, and signal if change
    if ( value != m_value )
    {
      m_value = value;
      signal();
    }
  }

  /***************************************** getReadOnly *****************************************/
  public ReadOnlyDouble getReadOnly()
  {
    // return read-only version of integer
    return new ReadOnlyDouble( this );
  }

}
