/* ---------------------------------------------------------------------------
 *  SimLog v 2.2
 *  Copyright (C) 2002-2003 Jean-Michel RICHER
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 *
 *  For any  comment please write to Jean-Michel RICHER at 
 *  Jean-Michel.Richer@univ-angers.fr
 * ------------------------------------------------------------------------ */

/* //////////////////////////////////////////////////////////////////////// */
/* // ------------------------------------------------------------------ // */
/* // | class   :  SimLogGate                                          | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  This class is the main class for Gates
 *  It defines all the gates types, states and colors.
 *  
 *  Basically a gate is defined by :
 *  - its type (AND, OR, NOT, ...) 
 *  - its coordinate on canvas where it is paint
 *  - a list of inputs
 *  - a series of outputs
 *
 *  @version 2.2, 14 October 2002
 *  @author Jean-Michel Richer
 */

import java.awt.*;
import java.util.Vector;


public abstract class SimLogGate extends Rectangle {

		//
		// gate type
		//

		public int type;

		public  final static int   NONE_GATE = 0;
		public  final static int    AND_GATE = 1;
		public  final static int   NAND_GATE = 2;
		public  final static int     OR_GATE = 3;
		public  final static int    NOR_GATE = 4;
		public  final static int    XOR_GATE = 5;
		public  final static int    NOT_GATE = 6;
		public  final static int SWITCH_GATE = 7;
		public  final static int    LED_GATE = 8;
		public  final static int     PA_GATE = 9;

		public final static String[] genericName = {
      "NONE", "AND", "NAND", "OR", "NOR", "XOR", "NOT", "SWITCH", "LED", ""
	  }; 

		//
		// Colors
		//

		public final static Color   GRID_COLOR = Color.gray;
		public final static Color   GATE_COLOR = Color.blue;
		public final static Color   MOVE_COLOR = Color.magenta;
		public final static Color  FOCUS_COLOR = Color.red;
		public final static Color ACTIVE_COLOR = Color.blue;

		//
		// gate dimensions
		//

		public  final static int HEIGHT = 70; // Gate Height in pixel
		public  final static int  WIDTH = 70; // Gate width in pixel
		private final static int   STEP = 15; // Space between to consecutive gates

		//
		// gate state
		//

		protected int state;

		public final static int STATE_NORMAL = 1;
		public final static int STATE_MOVING = 2;
		public final static int STATE_FOCUS  = 3;
		public final static int STATE_ACTIVE = 4;


		//
		// values
		//

		public final static int FALSE = 0;
		public final static int  TRUE = 1;
		public final static int UNSET = 2;
		protected int value;

		public final static String FALSE_STRING ="0";
  	public final static String  TRUE_STRING ="1";
			
		//
		// variables
		//

		protected String name;
		protected int    inputYPos;

		//
		//  used for display
		//

		int locc;
		int rocc;

		//
		// Links
		//
		// input links are limited to the number of ports defined
		// when creating the gate
		//
		// maximum number of inputs Ports
		//

		int        maxInputLinks;

		//
		// as the size is given by the previous variable we can make
		// it an array
		//

		SimLogLink inputLinks[];   // array of input ports
			 
		//
		// output links
		// the number of output links is not limited so make it a vector
		//
		
		private Vector outputLinks;  


		/**
		 *  Default constructor
		 *
		 *  @param _x position of gate
		 *  @param _y position of gate
		 *  @param typ gate type
		 *  @param nbrIn maximum number of inputs
		 *  @param s gate name
		 */

		public SimLogGate( int _x, int _y, int typ, int nbrIn, String s ) {
				super( _x, _y, WIDTH, HEIGHT );
				fitToGrid();
				type = typ;
				name = s;
				state = STATE_NORMAL;
				value = UNSET;
				maxInputLinks = nbrIn;
				inputLinks  = new SimLogLink[ nbrIn ];
				for (int i = 0; i < nbrIn; i++) {
						inputLinks[i] = null;
				}
				outputLinks = new Vector();
				inputYPos = (HEIGHT - (nbrIn-1)*STEP)/2;
		}

		/**
		 *  modify coordinate of gate to fit to a virtual grid
		 *
		 */

		private void fitToGrid( ) {
				x = (x / 20) * 20;
				y = (y / 20) * 20;
		}

		/**
		 *  Move gate to a new position
		 *
		 *  @param _x new coordinate
		 *  @param _y new coordinate
		 */

		public void moveTo( int _x, int _y ) {
 				this.x = _x; 
				this.y = _y;
				fitToGrid();
		}

		/**
		 *  return name of gate
		 *
		 *  @return name of gate
		 */

		public String getName() {
				return name;
		}

		/**
		 *  set new name of gate
		 *
		 *  @param s name of gate
		 */

		public void setName( String s ) {
			if (s.length() > 5) {
				name = s.substring( 0, 4 );
			} else {
				name = s;
			}
		}

		/**
		 *  return logic value of gate
		 *
		 *  @return <code>FALSE</code> means false, <code>TRUE</code> means true
     *          and <code>UNSET</code> means not yet defined
		 */

		public int getValue() {
				return value;
		}


		/**
		 * set value of gate
		 *
		 * @param n is either <code>FALSE</code>, <code>TRUE</code> or 
		 *				  <code>UNSET</code>
		 */

		public void setValue( int n ) {
				value = n;
		}

		/**
		 *  return state of gate
		 *
		 *  @return one of <code>STATE_NORMAL</code>, <code>STATE_MOVING</code>,
                <code>STATE_FOCUS</code>,<code>STATE_ACTIVE</code>
		 */

		public int getState() {
				return state;
		}

		/**
		 *  set gate in a NORMAL state
		 */

		public void setNormalState() {
				state = STATE_NORMAL;
		}

		/**
		 *  set gate in a MOVING state, when gate is being moved
		 */

		public void setMovingState() {
				state = STATE_MOVING;
		}

		/**
		 *  set gate in a Focus state, when all inputs or outputs are not
     *  connected
		 */

		public void setFocusState() {
				state = STATE_FOCUS;
		}

		/**
		 *  set gate in an ACTIVE state, when in simulation
		 */

		public void setActiveState() {
				state = STATE_ACTIVE;
		}

		/**
		 *  return gate type
		 * 
		 *  @return one of <code>NONE_GATE</code>, <code>AND_GATE</code>, 
                       <code>NAND_GATE</code>, <code>OR_GATE</code>,
                       <code>NOT_GATE</code>, <code>NOT_GATE</code>,
                       <code>XOR_GATE</code>, <code>SWITCH_GATE</code>,
                       <code>LED_GATE</code>, <code>PA_GATE</code>
		 */

		public int getType() {
				return type;
		}

		public void setType( int n ) {
				type=n;
		}


		/**
     *  return generic name of gate : AND, OR, NOT, ...
     *
		 *  @return a String
     */

		public String getGenericName() {
			return genericName[type];
		}

		/**
     *  return generic name of gate : AND, OR, NOT, ...
     *
		 *  @return a String
     */

		public String getGenericName( int n ) {
			return genericName[n];
		}


		/**
		 *  return maximum number of inputs ports
		 *  (also equal to number of allowed links)
		 *
		 *  @return maximum number of inputs
		 */

		public int getMaxInputLinks() {
				return maxInputLinks;
		}

		/**
		 *  return output a vector of links
		 *
		 *  @return vector containing all the output links
		 */

		public Vector getOutputLinks( ) {
				return outputLinks;
		}

		/**
		 *  return number of free input port
		 *  (used when trying to add a new link)
		 *
     *  @return -1 if no port is available, or the index of the port
		 */

		public int getAvailablePort() {
				for (int i = 0; i < maxInputLinks; i++) {
					if (inputLinks[i] == null) return i;
				}
				return -1;
		}

		/**
		 *  return Link related to given input port
		 *
     *  @return link 
     */

		public SimLogLink getInputLink( int n ) {
				return inputLinks[n];
		}

		/**
		 *  set given input port
		 *
		 *  @param lnk link to add
		 *  @param n input port on which the link is being added
		 */

		public void setInputLink( SimLogLink lnk, int n ) {
				inputLinks[n] = lnk;
		}

		/**
		 *  add output link
		 *
		 *  @param lnk link to add 
		 */

		public void addOutputLink( SimLogLink lnk ) {
				outputLinks.add( lnk );
		}
			 	 
		/**
		 *  Add Link from output gate to this input gate.
		 *  The output gate can not be of type <code>LED_GATE</code>
		 *  and this gate can not be of type <code>SWITCH_GATE</code>
		 *
		 *  @param lnk link to add
		 */

		public void addInputLink( SimLogLink lnk ) {
				inputLinks[ lnk.getInputPort() ] = lnk;
		}
	 
		/**
		 *  check if point is inside gate dimensions
		 *
		 *  @param x coordinate on canvas
		 *  @param y coordinate on canvas
		 *  @return <code>true</code> if point is inside gate,
     *          <code>false</code> otherwise
		 */

		public boolean isInside( int x, int y ) {
				// use method "inside" for older versions of JDK
				return contains( x, y );
		}

		/**
		 *  check if a gate at position x,y will intersect with current gate
		 *  
		 *  @param _x coordinate of gate on canvas
		 *  @param _y coordinate of gate on canvas
		 *  @return <code>true</code> if both gates intersect
     *          <code>false</code> otherwise
		 */

		public boolean intersects( int _x, int _y ) {
				boolean   res;
				Rectangle rect = new Rectangle( _x, _y , WIDTH, HEIGHT );
				res = intersects( rect );
				rect = null;
				return res;
		}


		/**
		 *  chek if mouse position is near input
		 *  
		 *  @param _x coordinate of mouse on canvas
		 *  @param _y coordinate of mouse on canvas
		 *  @return index of nearest input or -1
		 */		 

		public int isNearInput( int _x, int _y ) {
				int i , xa, ya, xb, yb;

				xa = x-10;
				xb = x+15;
				ya = y + inputYPos - 5;
				yb = ya + 10;
				for (i = 0; i < maxInputLinks; i++) {
					if ((xa <= _x) && (_x <= xb) && (ya <= _y) && (_y <= yb)) {
						return i;
					}
					ya += STEP;
					yb += STEP;
				}
				return -1;
		}

		/**
		 *  chek if mouse position is near output
		 *  
		 *  @param _x coordinate of mouse on canvas
		 *  @param _y coordinate of mouse on canvas
		 *  @return 0 if position is near output or -1
		 */		 

		public int isNearOutput( int _x, int _y ) {
				int xa, ya, xb, yb;

				xa = x + WIDTH-10;
				xb = x + WIDTH+10;
				ya = y + HEIGHT/2 - 5;
				yb = ya + 5;
				if ((xa <= _x) && (_x <= xb) && (ya <= _y) && (_y <= yb)) return 0;
				return -1;
		}

		/**
		 *  chek if mouse position is inside remove zone
		 *  
		 *  @param _x coordinate of mouse on canvas
		 *  @param _y coordinate of mouse on canvas
		 *  @return <code>true</code> if position is inside remove zone,
		 *          <code>false</code> otherwise
		 */		 

		public boolean isInsideRemoveZone( int _x, int _y ) {
				int xa, ya, xb, yb;

				xa = x + WIDTH-20;
				xb = x + WIDTH;
				ya = y;
				yb = y + 20;
				if ((xa <= _x) && (_x <= xb) && (ya <= _y) && (_y <= yb)) return true;
				return false;
		}

		/**
		 *  chek if mouse position is inside naming zone
		 *  
		 *  @param _x coordinate of mouse on canvas
		 *  @param _y coordinate of mouse on canvas
		 *  @return <code>true</code> if position is inside naming zone,
		 *          <code>false</code> otherwise
		 */		 

		public boolean isInsideNameZone( int _x, int _y ) {
				int xa, ya, xb, yb;

				xa = x + 5;
				xb = x + WIDTH - 25;
				ya = y;
				yb = y + 20;
				if ((xa <= _x) && (_x <= xb) && (ya <= _y) && (_y <= yb)) return true;
				return false;
		}

		/**
		 *  remove input link given input port 
		 *
		 *  @param port input port number
		 */

		public void removeInputLink( int port ) {
				inputLinks[ port ] = null;
		}

		/**
		 *  remove output link
		 *
		 *  @param g gate from which the link comes from
		 *  @param port  input port of gate where the link is connected
		 */

		public void removeOutputLink( SimLogGate g, int port ) {
				SimLogLink link;

				for (int i = 0; i < outputLinks.size(); i++) {
					link = (SimLogLink) outputLinks.elementAt(i);
					if ((link.getInputGate() == g) && (link.getInputPort() == port)) {
						outputLinks.removeElementAt(i);
						break;
					}
				}
		}

		/**
		 *  remove all input and output links
		 */

		public void removeAllLinks() {
				SimLogLink link;

				// remove input links
				for (int i = 0; i < maxInputLinks; i++) {
					link = inputLinks[i];
					if (link != null) {
						link.remove();
						inputLinks[i] = null;
						link = null;
					}
				}

				// remove output links
				while (outputLinks.size() != 0) {
					link = (SimLogLink) outputLinks.elementAt(0);
					outputLinks.removeElementAt(0);
					link.remove();
					link = null;
				}
		}


		/**
		 *  check if all inputs are connected
		 *
		 *  @return <code>true</code> if all inputs are connected,
		 * 				  <code>false</code> otherwise
		 */

		public boolean checkInput() {
				if (type == SWITCH_GATE) return true;
				for (int i = 0; i < maxInputLinks; i++) {
					if (inputLinks[i] == null) return false;
				}
				return true;
		}

		/**
		 *  check if output is connected
		 *
		 *  @return <code>true</code> if output is connected,
		 * 				  <code>false</code> otherwise
		 */
			
		public boolean checkOutput() {
				if (type == LED_GATE) return true;
				if (outputLinks.size() == 0) return false;
				return true;
		}

 
		/**
		 *  return position of Y when connecting gate
		 *
		 *  @return y coordinate of connection
		 */

		private int getInputYPos( SimLogGate g ) {
				int i;
				SimLogLink link;
							 
				for (i = 0; i < maxInputLinks; i++) {
					link = (SimLogLink) inputLinks[i];
					if (link.getOutputGate() == g) return y + inputYPos +i*STEP;
				}
				return 0;
		}

		/**
		 *  return position on canvas of given input
		 *
		 *  @param n input number
		 *  @return position on canvas
		 */

		private int getInputYPos( int n ) {
				return y + inputYPos + n*STEP;
		}

		/**
		 *  paint inputs
		 */

		void paintInputs( Graphics g ) {
				int i, dist;
				dist = inputYPos;
						
				for (i = 0; i < maxInputLinks; i++) {
					g.drawLine( x, y+dist, x+35, y+dist );
					dist += STEP;					
				}
		}

		/**
		 *  paint output
		 */

		public void paintOutput( Graphics g ) {
				g.drawLine( x+50, y+35, x+WIDTH, y+35 );
		}

		/**
		 *  paint output links
		 */

		public void paintLinks( Graphics g ) {
				int i, j;
				int xSrc, ySrc, xDst, yDst, xMid, yMid;
				SimLogGate dstGate;
				SimLogLink link;

				g.setColor( GATE_COLOR );
				for (i = 0; i < outputLinks.size(); i++) {
					link = (SimLogLink) outputLinks.elementAt(i);
					dstGate = link.getInputGate();
					xSrc = x + WIDTH;
					ySrc = y + HEIGHT / 2;
					xDst = dstGate.x;
					yDst = dstGate.getInputYPos( link.getInputPort() );
					if (xSrc < xDst) {
						xMid = (xSrc + xDst) / 2;
						g.drawLine( xSrc, ySrc, xMid, ySrc );
						g.drawLine( xMid, ySrc, xMid, yDst );
						g.drawLine( xMid, yDst, xDst, yDst );
					} else {
					  xMid = xSrc + 10;
						yMid = (ySrc + yDst) / 2;
						g.drawLine( xSrc, ySrc, xMid, ySrc );
						g.drawLine( xMid, ySrc, xMid, yMid );
						g.drawLine( xMid, yMid, xDst-10, yMid );
						g.drawLine( xDst-10, yMid, xDst-10, yDst );
						g.drawLine( xDst-10, yDst, xDst, yDst );
					}
				}
		}

		/**
		 *  paint grid name
		 */

		public void paintName( Graphics g ) {
				if (name != null) {
					g.setColor( GATE_COLOR );
					g.drawString( name, x+5, y+15 );
				}
		}

		/**
		 *  paint grid around gate
		 */

		public void paintGrid( Graphics g ) {
				g.setColor( GRID_COLOR );
				g.draw3DRect( x, y, WIDTH, HEIGHT, true );
				g.setColor( GATE_COLOR );
				g.drawRect( x+50, y, 20, 20 );
				g.drawLine( x+50, y, x+70, y+20 );
				g.drawLine( x+50, y+20, x+70, y );
		}

		/**
		 *  default method to paint gate
		 */

		public void paint( Graphics g ) {
			 
		}

		/**
		 *  compute gate value (please override)
		 */

		public void compute() {
				value = UNSET;
		}		 

		/**
		 *
		 */

		public void exchangeLinks() {
			SimLogLink l1, l2, lnk;

			l1=inputLinks[0];
			l2=inputLinks[1];
			if ((l1==null) || (l2==null)) return ;
			if (l1.getInputPort()==0) {
				l1.setInputPort(1);
				l2.setInputPort(0);
			} else {
				l1.setInputPort(0);
				l2.setInputPort(1);
			}
			inputLinks[0]=l2;
			inputLinks[1]=l1;
		}

		/**
		 *
		 */

		public SimLogGate replace( int t ) {
			int i;
			SimLogGate g=null;
			SimLogLink lnk;

			switch(t) {
				case SimLogGate.AND_GATE:
					g=new SimLogAndGate(x,y,name);
					break;
				case SimLogGate.OR_GATE:
					g=new SimLogOrGate(x,y,name);
					break;
				case SimLogGate.NAND_GATE:
					g=new SimLogNandGate(x,y,name);
					break;
				case SimLogGate.NOR_GATE:
					g=new SimLogNorGate(x,y,name);
					break;
				case SimLogGate.XOR_GATE:
					g=new SimLogXorGate(x,y,name);
					break;
			};
			for (i=0;i<maxInputLinks;i++) {
				lnk=inputLinks[i];
				if (lnk!=null) lnk.setInputGate(g);
				g.inputLinks[i]=lnk;
			}
			for (i=0;i<outputLinks.size();i++) {
				lnk=(SimLogLink)outputLinks.elementAt(i);
				lnk.setOutputGate(g);
				g.outputLinks.add(lnk);
			}
			return g;
		}
}