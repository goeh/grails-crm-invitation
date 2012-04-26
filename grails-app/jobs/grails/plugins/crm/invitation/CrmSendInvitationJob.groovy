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
 * under the License.
 */

package grails.plugins.crm.invitation

import grails.util.Environment

/**
 * Find pending invitations and send them to invited people.
 */
class CrmSendInvitationJob {

    static triggers = { simple(name: 'crmSendInvitation', startDelay: 1000 * 60 * 3, repeatInterval: 1000 * 60 * 10) }

    def group = 'crm-account'

    def grailsApplication
    def crmInvitationService

    def execute() {
        if (grailsApplication.config.crm.job.sendInvitation.enabled) {
            for (i in CrmInvitation.pending.list()) {
                crmInvitationService.sendInvitationEmail(i)
                i.status = CrmInvitation.SENT
                i.save()
            }
        } else {
            log.info("Invitations disabled in environment ${Environment.current}")
        }
    }

}
