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

class CrmInvitationService {

    static transactional = true

    def grailsApplication
    def crmCoreService
    def textTemplateService

    def createInvitation(Object reference, String senderUsername, String receiverEmail, String emailTemplate = null, Map binding = null) {
        if (!senderUsername) {
            throw new IllegalArgumentException("argument [senderUsername] is mandatory")
        }
        if (!receiverEmail) {
            throw new IllegalArgumentException("argument [receiverEmail] is mandatory")
        }
        def ref = crmCoreService.getReferenceIdentifier(reference)
        def i = new CrmInvitation(ref: ref, sender: senderUsername, receiver: receiverEmail).save(failOnError: true, flush: true)

        if (emailTemplate) {
            binding.invitation = i
            sendInvitationEmail(i, emailTemplate, binding)
        }
    }

    void sendInvitationEmail(CrmInvitation invitation, String template, Map binding = [:]) {
        def config = grailsApplication.config.crm.invitation.email
        def bodyText = textTemplateService.applyTemplate(template, "text/plain", binding)
        def bodyHtml = textTemplateService.applyTemplate(template, "text/html", binding)
        if (!(bodyText || bodyHtml)) {
            throw new RuntimeException("Template not found: [name=${template}]")
        }

        sendMail {
            from config.from ?: binding.user.email
            to invitation.receiver
            if (config.cc) {
                cc config.cc
            }
            if (config.bcc) {
                bcc config.bcc
            }
            subject config.subject
            if (bodyText) {
                text bodyText
            }
            if (bodyHtml) {
                html bodyHtml
            }
        }
    }

}
