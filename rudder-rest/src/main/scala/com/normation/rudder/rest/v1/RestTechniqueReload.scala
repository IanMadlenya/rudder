/*
*************************************************************************************
* Copyright 2011 Normation SAS
*************************************************************************************
*
* This file is part of Rudder.
*
* Rudder is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* In accordance with the terms of section 7 (7. Additional Terms.) of
* the GNU General Public License version 3, the copyright holders add
* the following Additional permissions:
* Notwithstanding to the terms of section 5 (5. Conveying Modified Source
* Versions) and 6 (6. Conveying Non-Source Forms.) of the GNU General
* Public License version 3, when you create a Related Module, this
* Related Module is not considered as a part of the work and may be
* distributed under the license agreement of your choice.
* A "Related Module" means a set of sources files including their
* documentation that, without modification of the Source Code, enables
* supplementary functions or services in addition to those offered by
* the Software.
*
* Rudder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Rudder.  If not, see <http://www.gnu.org/licenses/>.

*
*************************************************************************************
*/

package com.normation.rudder.rest.v1

import com.normation.cfclerk.services.UpdateTechniqueLibrary
import com.normation.eventlog.ModificationId
import com.normation.rudder.UserService
import com.normation.rudder.rest.RestUtils
import com.normation.utils.StringUuidGenerator
import net.liftweb.common._
import net.liftweb.http.PlainTextResponse
import net.liftweb.http.rest.RestHelper

/**
 * A rest api that allows to deploy promises.
 */
class RestTechniqueReload(
    updatePTLibService : UpdateTechniqueLibrary
  , uuidGen            : StringUuidGenerator
) ( implicit userService : UserService )
 extends RestHelper with Loggable {

  serve {
    case Get("api" :: "techniqueLibrary" :: "reload" :: Nil, req) =>
      updatePTLibService.update(ModificationId(uuidGen.newUuid), RestUtils.getActor(req), Some("Technique library reloaded from REST API")) match {
        case Full(x) => PlainTextResponse("OK")
        case eb:EmptyBox =>
          val e = eb ?~! "An error occured when updating the Technique library from file system"
          logger.error(e.messageChain)
          e.rootExceptionCause.foreach { ex =>
            logger.error("Root exception cause was:", ex)
          }
          PlainTextResponse(s"Error: ${e.messageChain.split(" <- ").mkString("","\ncause:","\n")}", 500)
      }
  }

}
