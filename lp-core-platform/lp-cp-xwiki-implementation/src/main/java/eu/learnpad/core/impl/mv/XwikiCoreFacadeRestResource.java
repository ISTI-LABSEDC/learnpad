/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package eu.learnpad.core.impl.mv;

import java.io.InputStream;

import eu.learnpad.core.rest.DefaultRestResource;
import eu.learnpad.exception.impl.LpRestExceptionImpl;
import eu.learnpad.me.rest.data.ModelSetType;
import eu.learnpad.mv.CoreFacade;

/*
 * The methods inherited form the CoreFacade in this
 * class should be implemented as a REST invocation
 * toward the CoreFacade binded at the provided URL
 */
public class XwikiCoreFacadeRestResource extends DefaultRestResource implements CoreFacade {

	public XwikiCoreFacadeRestResource() {
		this("localhost", 8080);
	}

	public XwikiCoreFacadeRestResource(String coreFacadeHostname, int coreFacadeHostPort) {
		// This constructor could change in the future
		this.updateConfiguration(coreFacadeHostname, coreFacadeHostPort);
	}

	public void updateConfiguration(String coreFacadeHostname, int coreFacadeHostPort) {
		// This constructor has to be fixed, since it requires changes on the
		// class eu.learnpad.core.rest.RestResource
	}

	@Override
	public InputStream getModel(String modelSetId, ModelSetType type) throws LpRestExceptionImpl {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyVerification(String verificationProcessId) throws LpRestExceptionImpl {
		// TODO Auto-generated method stub
	}
}