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

import com.icegreen.greenmail.util.*

class CrmInvitationServiceSpec extends grails.plugin.spock.IntegrationSpec {

    def grailsApplication
    def crmInvitationService
    def textTemplateService
    def greenMail

    def setup() {
        grailsApplication.config.crm.invitation.email.from = "test@test.it"
        grailsApplication.config.crm.invitation.email.subject = "Invitation"
    }

    def cleanup() {
        greenMail.deleteAllMessages()
    }

    def "invite"() {

        given:
        textTemplateService.createContent("invitation-email", "text/html", """
        <p>Hi, this is an invitation from <strong>\${invitation.sender.encodeAsHTML()}</strong>.</p>
        <p>\${message}</p>
        """)
        when:
        crmInvitationService.createInvitation("test", "grails", "joe@acme.com", "invitation-email", [message: "Hello World"])

        then:
        greenMail.getReceivedMessages().length == 1

        when:
        def message = greenMail.getReceivedMessages()[0]

        then:
        GreenMailUtil.getBody(message).contains('<strong>grails</strong>')
        GreenMailUtil.getBody(message).contains('<p>Hello World</p>')
        GreenMailUtil.getAddressList(message.from) == 'test@test.it'
        message.subject == 'Invitation'
    }

}
