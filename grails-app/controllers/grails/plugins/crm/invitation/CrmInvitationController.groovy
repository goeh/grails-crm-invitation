/*
 * Copyright (c) 2012 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.crm.invitation

import grails.plugins.crm.core.TenantUtils
import javax.servlet.http.HttpServletResponse

class CrmInvitationController {

    def crmInvitationService
    def crmSecurityService

    def index() {
        def user = crmSecurityService.currentUser
        def invitations = crmInvitationService.getInvitationsTo(user.email, TenantUtils.tenant)
        [user: user, invitations: invitations]
    }

    def accept(Long id) {
        def crmInvitation = CrmInvitation.get(id)
        if (crmInvitation) {
            def user = crmSecurityService.currentUser
            if (crmInvitation.receiver != user.email) {
                log.warn("Invalid user [${user.email}] trying to accept invitation [${crmInvitation.id}] for [${crmInvitation.receiver}]")
                response.sendError(HttpServletResponse.SC_FORBIDDEN)
                return
            }
            crmInvitationService.accept(crmInvitation)
            flash.success = message(code: "crmInvitation.accepted.message", default: "Invitation accepted")
        } else {
            flash.error = message(code: 'default.not.found.message', args: [message(code: 'crmInvitation.label', default: 'Invitation'), id])
        }
        redirect(action: "index")
    }

    def deny(Long id) {
        def crmInvitation = CrmInvitation.get(id)
        if (crmInvitation) {
            def user = crmSecurityService.currentUser
            if (crmInvitation.receiver != user.email) {
                log.warn("Invalid user [${user.email}] trying to deny invitation [${crmInvitation.id}] for [${crmInvitation.receiver}]")
                response.sendError(HttpServletResponse.SC_FORBIDDEN)
                return
            }
            crmInvitationService.deny(crmInvitation)
            flash.warning = message(code: "crmInvitation.denied.message", default: "Invitation denied")
        } else {
            flash.error = message(code: 'default.not.found.message', args: [message(code: 'crmInvitation.label', default: 'Invitation'), id])
        }
        redirect(action: "index")
    }
}
