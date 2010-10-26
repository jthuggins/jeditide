/*
 * JExecutable.java
 * Copyright (C) 2003 Amedeo Farello
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

package jexplorer.parser.source;
/*------------------------------------------------------------------------------------------------------------------------------------
	JExecutable
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Base class for constructors and methods.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public abstract class JExecutable extends JMember
{
public final static JParameter[]	EMPTY_PARAMETERS_ARRAY = new JParameter[0];

protected JParameter[]	m_Parameters;
protected JType[]		m_Throwables;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JExecutable</code>.
 * @param inName		The local name of this executable.
 * @param inModifiers	The modifiers mask, decodable with <code>java.lang.reflect.Modifier</code>.
 * @param inComment		The related Javadoc comment or <code>null</code>.
 * @param inSourcePos	The reference to the source file.
 * @param inContainer	The reference to the containing element or <code>null</code>.
 * @param inParameters	This executable's parameters.
 * @param inThrowables	This executable's throwables.
 */
public	JExecutable(String			inName,
					int				inModifiers,
					Comment			inComment,
					SourcePos		inSourcePos,
					JElement		inContainer,
					JParameter[]	inParameters,
					JType[]			inThrowables)
{
super(inName, inModifiers, inComment, inSourcePos, inContainer);
m_Parameters = inParameters;
m_Throwables = inThrowables;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getParameters
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns this executable' parameters.
 */
public JParameter[]	getParameters()
{
return m_Parameters != null ? m_Parameters : EMPTY_PARAMETERS_ARRAY;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getThrowables
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns this executable' throwables.
 */
public JType[]		getThrowables()
{
return m_Throwables != null ? m_Throwables : JSource.EMPTY_TYPES_ARRAY;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	resolveTypes
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Resolve this executable's type references.
 */
public void		resolveTypes(Finder inFinder, JSource inSource)
{
JSource.LookupResult	r;

// resolve parameters
if(m_Parameters != null)
	for(int p = 0; p < m_Parameters.length; p++)
		m_Parameters[p].resolveTypes(inFinder, m_Container, inSource);

// resolve throwables
if(m_Throwables != null)
	for(int t = 0; t < m_Throwables.length; t++)
		m_Throwables[t].resolve(inFinder, inSource, m_Container);
}
}

