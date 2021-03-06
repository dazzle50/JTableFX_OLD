/**************************************************************************
 *  Copyright (C) 2021 by Richard Crook                                   *
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

package rjc.table.view.icons;

import javafx.scene.image.Image;

/*************************************************************************************************/
/******************************* Icons for menu items & other uses *******************************/
/*************************************************************************************************/

public final class Icons
{
  // icons from (PNG 1x) https://material.io/resources/icons/?style=twotone
  public static final Image CONTENT_CLEAR = makeIcon( "twotone_clear_black_18dp.png" );
  public static final Image CONTENT_COPY  = makeIcon( "twotone_content_copy_black_18dp.png" );
  public static final Image CONTENT_CUT   = makeIcon( "twotone_content_cut_black_18dp.png" );
  public static final Image CONTENT_PASTE = makeIcon( "twotone_content_paste_black_18dp.png" );
  public static final Image DELETE        = makeIcon( "twotone_delete_black_18dp.png" );
  public static final Image FILTER        = makeIcon( "twotone_filter_alt_black_18dp.png" );
  public static final Image INDENT        = makeIcon( "twotone_format_indent_decrease_black_18dp.png" );
  public static final Image OUTDENT       = makeIcon( "twotone_format_indent_increase_black_18dp.png" );
  public static final Image REDO          = makeIcon( "twotone_redo_black_18dp.png" );
  public static final Image UNDO          = makeIcon( "twotone_undo_black_18dp.png" );
  public static final Image SORT          = makeIcon( "twotone_sort_black_18dp.png" );
  public static final Image ZOOM_IN       = makeIcon( "twotone_zoom_in_black_18dp.png" );
  public static final Image ZOOM_OUT      = makeIcon( "twotone_zoom_out_black_18dp.png" );

  /****************************************** makeIcon *******************************************/
  private static Image makeIcon( String file )
  {
    // return an image based on image file with specified x & y hot spot
    return new Image( Icons.class.getResourceAsStream( file ) );
  }
}
