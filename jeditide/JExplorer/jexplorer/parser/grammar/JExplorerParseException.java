/*
 * JExplorerParseException.java
 * Copyright (c) 2003 Amedeo Farello
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jexplorer.parser.grammar;

import jexplorer.parser.Token;
/*------------------------------------------------------------------------------------------------------------------------------------
	JExplorerParseException
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * This exception is thrown when the parser encounters an unexpected exception.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.31
 */
public class JExplorerParseException extends Exception
{
private Throwable	m_Throwable;
private Token		m_Token;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public JExplorerParseException(Throwable inThrowable, Token inToken)
{
super(	"Unexpected '" +
		inThrowable.getClass().getName() +
		"': " +
		inThrowable.getMessage());

m_Throwable	= inThrowable;
m_Token		= inToken;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getThrowable
------------------------------------------------------------------------------------------------------------------------------------*/
public Throwable	getThrowable()
{
return m_Throwable;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getToken
------------------------------------------------------------------------------------------------------------------------------------*/
public Token		getToken()
{
return m_Token;
}
}
