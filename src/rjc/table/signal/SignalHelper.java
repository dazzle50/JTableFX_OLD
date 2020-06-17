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

package rjc.table.signal;

import java.util.ArrayList;
import java.util.HashMap;

/*************************************************************************************************/
/************************* Functionality to supporting ISignal interface *************************/
/*************************************************************************************************/

class SignalHelper
{
  final private static HashMap<ISignal, ArrayList<IListener>> m_listeners = new HashMap<>();

  /******************************************* signal ********************************************/
  static void signal( ISignal signaller, Object[] objects )
  {
    // send signal objects to each listener registered with specified signal sender
    var list = m_listeners.get( signaller );
    if ( list != null )
      list.forEach( ( listener ) -> listener.slot( objects ) );
  }

  /**************************************** addListener ******************************************/
  static void addListener( ISignal signaller, IListener lambda )
  {
    // register listener for specified signal sender
    var list = m_listeners.get( signaller );
    if ( list == null )
    {
      list = new ArrayList<>();
      m_listeners.put( signaller, list );
    }
    list.add( lambda );
  }

  /*************************************** removeListener ****************************************/
  static void removeListener( ISignal signaller, IListener lambda )
  {
    // unregister listener for specified signal sender, no-action if not present
    var list = m_listeners.get( signaller );
    if ( list != null )
      list.remove( lambda );
  }

  /************************************* removeAllListeners **************************************/
  static void removeAllListeners( ISignal signaller )
  {
    // unregister all listeners for specified signal sender
    var list = m_listeners.get( signaller );
    if ( list != null )
      list.clear();
  }

  /**************************************** getListeners ******************************************/
  static ArrayList<IListener> getListeners( ISignal signaller )
  {
    // return list of all listeners for specified signal sender, can be null
    return m_listeners.get( signaller );
  }

}