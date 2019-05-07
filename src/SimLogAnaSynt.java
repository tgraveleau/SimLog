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
/* // | class   :  SimLogAnaSynt                                       | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2003                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This class contains a lexical and syntaxic analyser to parse
 *   logical expressions. 
 *
 *   Expressions are defined with the following symbols :
 *    '-' not
 *    '+' or
 *    '.' and
 *    '^' xor
 *    '(',')' parenthesis
 *
 *   @version 2.2, 14 October 2003
 *   @author Jean-Michel Richer
 */

import java.util.Vector;


public class SimLogAnaSynt {

	//
	// Variables
	//

	private String sentence;
  private String idf;
	private int    length;
	private int    pos;
	private int    errorPos=0;
	private Vector stack;
	private char   nextChar;
	private int    nbrOpenPar;
	private int    nbrClosePar;
	private int    nbrErrors;

	// constants for lexical analyzer

	private final static int LEX_END  = 0;
	private final static int LEX_IDF  = 1;
	private final static int LEX_PLUS = 2;
	private final static int LEX_MULT = 3;
	private final static int LEX_OPAR = 4;
	private final static int LEX_CPAR = 5;
	private final static int LEX_MINUS= 6;
	private final static int LEX_ERR  = 7;
	private final static int LEX_XOR  = 8;

	/**
   *  Constructor
	 * 
   *  @param s String to analyze
   */

	public SimLogAnaSynt(String s) {
		sentence=s.trim().toUpperCase();
		length=sentence.length();
		pos=0;
		stack=new Vector();
		nextChar=sentence.charAt(0);
		nbrOpenPar=nbrClosePar=nbrErrors=0;
	}

	/**
   *  Record syntax error
	 * 
   *  @param s Error string
   */

	private void SyntaxError(String s) {
		++nbrErrors;
	}

	/**
   *  indicates if expression is correct
	 * 
   */

	public boolean isCorrect() {	
		if (nbrErrors!=0) return false;
		if (nbrOpenPar!=nbrClosePar) return false;
		return true;
	}

	/**
   *  return next character, this is the main function of the syntaxic
   *  analyzer
	 * 
   *  @return character at current position
   */

	private char getNextChar() {
		if ((pos+1)>=length) nextChar=LEX_END;
		else {
		  ++pos;
		  nextChar=sentence.charAt(pos);
    }
		return nextChar;
	}

	/**
   *  return identifier at current syntaxic analyzer position
   *  
	 *  @return String corresponding to the identifier
   */

	public String getIDF() {
		int begPos, endPos=0;

		begPos=pos;
		while (pos<length) {
			nextChar=sentence.charAt(pos);
			if (Character.isLetter(nextChar) || Character.isDigit(nextChar)) {
				++pos; ++endPos;
			}
			else {--pos; break;}
		}
		endPos+=begPos;
		return sentence.substring(begPos,endPos);
	}

	/**
   *  This procedure is for debugging purpose
   *
   */

	public void printStack( ) {
		int i;

		for (i=0;i<stack.size();i++) {
			System.out.println((String)stack.elementAt(i));
		}
	}


	/**
   *  The following procedures are used by the syntaxic analyzer.
   *
   */

	public void expr() {
		//System.out.println("ENTER expr");
		terme();
		expr1();
	}

	public void expr1() {
		//System.out.println("ENTER expr1");
		if (nextChar==LEX_END) return ;
		if (nextChar=='+') {
			getNextChar();
			terme();
			expr1();
			stack.add(new String("+"));
		}
	}

	public void terme() {
		//System.out.println("ENTER terme"+(int)nextChar);
		if (nextChar==LEX_END) return ;
		facteur();
		terme1();
	}

	public void terme1() {
		//System.out.println("ENTER terme1"+(int)nextChar);
		if (nextChar==LEX_END) return ;
		if (nextChar=='.') {
			getNextChar();
			facteur();
			terme1();
			stack.add(new String("."));
		}
		if (nextChar=='^') {
			getNextChar();
			facteur();
			terme1();
			stack.add(new String("^"));
		}
	}

	public void facteur() {
		//System.out.println("ENTER facteur "+(int)nextChar);
		if (nextChar==LEX_END) return ;

		if (nextChar=='(') {
			++nbrOpenPar;
			getNextChar();
			expr();
			if (nextChar!=')')
				SyntaxError("HERE 1");
			else {
				++nbrClosePar;
				getNextChar();
			}
		} else if (nextChar=='-') {
			getNextChar();
			if (nextChar=='(') {
				++nbrOpenPar;
				getNextChar();
				expr();
				if (nextChar!=')') {
					SyntaxError("HERE 2");
				} else {
					++nbrClosePar;
					getNextChar();
					stack.add(new String("-"));
				}
			} else {
				primaire();
				stack.add(new String("-"));
			}
		} else {
			primaire();
		}
	}

	public void primaire() {
		//System.out.println("ENTER primaire");
		if (nextChar==LEX_END) return ;

		if (Character.isLetter(nextChar)) {
			stack.add(getIDF());
			getNextChar();
		} else if (nextChar=='-') {
			getNextChar();
			stack.add(new String(nextChar+""));
			getNextChar();
			stack.add(new String("-"));
		} else SyntaxError("HERE 3");
	}

	/**
	 *  return the stack containing the expression that has been analyzed.
   *  The expression es given in the postfix notation.
   *
	 *  @return stack of expression
	 */

	public Vector getStack() {
		return stack;
	}

}