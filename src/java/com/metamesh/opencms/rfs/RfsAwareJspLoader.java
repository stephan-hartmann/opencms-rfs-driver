/*  
    Copyright (c) Stephan Hartmann (www.metamesh.de)
    
    This file is part of Metamesh's RFS driver for OpenCms.

    Metamesh's RFS driver for OpenCms is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Metamesh's RFS driver for OpenCms is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Metamesh's RFS driver for OpenCms. 
    If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Metamesh's RFS Treiber für OpenCms.

    Metamesh's RFS Treiber für OpenCms ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Metamesh's RFS Treiber für OpenCms wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package com.metamesh.opencms.rfs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.flex.CmsFlexController;
import org.opencms.loader.CmsJspLoader;
import org.opencms.loader.CmsLoaderException;

import com.metamesh.opencms.rfs.file.RfsCmsFile;

public class RfsAwareJspLoader extends CmsJspLoader {

  @Override
  public void service(CmsObject cms, CmsResource resource, ServletRequest req,
      ServletResponse res) throws ServletException, IOException,
      CmsLoaderException {

    if (resource instanceof RfsCmsFile) {
      RfsCmsFile rcf = (RfsCmsFile)resource;
      CmsFlexController controller = CmsFlexController.getController(req);
      // get JSP target name on "real" file system
      String target = rcf.getWebappPath();
      if (target != null) {
        // important: Indicate that all output must be buffered
        controller.getCurrentResponse().setOnlyBuffering(true);
        // dispatch to external file
        controller.getCurrentRequest().getRequestDispatcherToExternal(cms.getSitePath(resource), target).include(
            req,
            res);
      }
      else {
        // update date last modified to make sure that the jsp is exported if it has been modified
        rcf.setDateLastModified(rcf.getRfsFile().lastModified());
        super.service(cms, resource, req, res);
      }
    }
    else {
      super.service(cms, resource, req, res);
    }
  }

}
