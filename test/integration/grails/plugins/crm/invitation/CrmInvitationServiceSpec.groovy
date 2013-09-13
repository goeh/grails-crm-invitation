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

class CrmInvitationServiceSpec extends grails.plugin.spock.IntegrationSpec {

    def crmInvitationService

    def "list invitation for an object"() {
        when:
        crmInvitationService.createInvitation("test", null, "grails", "joe@acme.com")

        then:
        crmInvitationService.getInvitationsFor("test").size() == 1

        when:
        crmInvitationService.createInvitation("test", null, "grails", "liza@acme.com")

        then:
        crmInvitationService.getInvitationsFor("test").size() == 2
        crmInvitationService.getInvitationsTo("joe@acme.com").size() == 1
        crmInvitationService.getInvitationsTo("liza@acme.com").size() == 1
    }

    def "accept invitation"() {
        given:
        def i = crmInvitationService.createInvitation("test", "guest", "grails", "joe@acme.com")

        expect:
        i.status == CrmInvitation.CREATED

        when:
        crmInvitationService.accept(i)

        then:
        i.status == CrmInvitation.ACCEPTED
    }

    def "deny invitation"() {
        given:
        def i = crmInvitationService.createInvitation("test", "guest", "grails", "joe@acme.com")

        expect:
        i.status == CrmInvitation.CREATED

        when:
        crmInvitationService.deny(i)

        then:
        i.status == CrmInvitation.DENIED
    }
}
