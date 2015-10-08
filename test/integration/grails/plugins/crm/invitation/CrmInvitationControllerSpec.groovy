/*
 * Copyright (c) 2014 Goran Ehrsson.
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

import javax.servlet.http.HttpServletResponse

/**
 * Test specification for CrmInvitationController.
 */
class CrmInvitationControllerSpec extends grails.test.spock.IntegrationSpec {

    def crmSecurityService
    def crmInvitationService

    def "index action should return some data"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we call the index action"
        def model = crmSecurityService.runAs("nobody") {
            controller.index()
        }

        then: "response should be OK"
        controller.response.status == HttpServletResponse.SC_OK
        model.tenant.id == 1L
        model.user.username == "nobody"
        model.invitations == []
    }

    def "it should be possible to share a tenant if you have admin role"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when:
        crmSecurityService.runAs("nobody") {
            controller.share(1L, "test@gr8crm.org", "Hi Joe, here is the invitation. Enjoy!", "guest")
        }

        then: "response should be success (FOUND)"
        controller.response.status == HttpServletResponse.SC_FOUND
        controller.response.redirectedUrl == "/crmInvitation/index/1#sent"
    }

    def "it should not be possible to share a tenant if you have guest role"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when:
        crmSecurityService.runAs("somebody") {
            controller.share(1L, "test@gr8crm.org", "Hi Joe, here is the invitation. Enjoy!", "guest")
        }

        then: "response should be FORBIDDEN"
        controller.response.status == HttpServletResponse.SC_FORBIDDEN
    }

    def "it should be possible to cancel a pending invitation"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation as nobody"
        def invitation = crmInvitationService.createInvitation("test1", null, "nobody", "test@gr8crm.org")

        then: "the invitation was created"
        crmInvitationService.getInvitationsFor("test1").size() == 1

        when: "user tries to cancel the invitation"
        crmSecurityService.runAs("nobody") {
            controller.cancel(invitation.ident())
        }

        then: "response should be success (FOUND)"
        controller.response.status == HttpServletResponse.SC_FOUND
        controller.response.redirectedUrl == "/crmInvitation/index"
    }

    def "invalid user should not be able to cancel an invitation"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation as someone"
        def invitation = crmInvitationService.createInvitation("party", null, "someone", "test@gr8crm.org")

        then: "the invitation was created"
        crmInvitationService.getInvitationsFor("party").size() == 1

        when: "a different user tries to cancel the invitation"
        crmSecurityService.runAs("nobody") {
            controller.cancel(invitation.ident())
        }

        then: "response should be FORBIDDEN"
        controller.response.status == HttpServletResponse.SC_FORBIDDEN
    }

    def "it should not be possible to cancel an expired invitation"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation and immediately set it to EXPIRED"
        def invitation = crmInvitationService.createInvitation("test2", null, "nobody", "test@gr8crm.org")
        invitation.status = CrmInvitation.EXPIRED
        invitation.save()

        then: "the invitation was created"
        CrmInvitation.findByRef("test2") // It's found but it's not active.
        crmInvitationService.getInvitationsFor("test2").size() == 0 // No active invitation exists.

        when: "user tries to cancel the expired invitation"
        crmSecurityService.runAs("nobody") {
            controller.cancel(invitation.ident())
        }

        then: "response should be GONE (410)"
        controller.response.status == HttpServletResponse.SC_GONE
    }

    def "it should be possible to accept a pending invitation"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation"
        def invitation = crmInvitationService.createInvitation("test3", null, "somebody", "nobody@unknown.net")

        then: "the invitation was created"
        crmInvitationService.getInvitationsFor("test3").size() == 1

        when: "user tries to accept the invitation"
        crmSecurityService.runAs("nobody") {
            controller.accept(invitation.ident())
        }

        then: "response should be success (FOUND)"
        controller.response.status == HttpServletResponse.SC_FOUND
        controller.response.redirectedUrl == "/crmInvitation/index" // mapping "home"
    }

    def "it should not be possible to accept an invitation directed to someone else"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation"
        def invitation = crmInvitationService.createInvitation("test4", null, "somebody", "foo@gr8crm.org")

        then: "the invitation was created"
        crmInvitationService.getInvitationsFor("test4").size() == 1

        when: "a different user user tries to accept the invitation"
        crmSecurityService.runAs("nobody") {
            controller.accept(invitation.ident())
        }

        then: "response should be FORBIDDEN"
        controller.response.status == HttpServletResponse.SC_FORBIDDEN
    }

    def "it should not be possible to accept an expired invitation"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation and immediately set it to EXPIRED"
        def invitation = crmInvitationService.createInvitation("test5", null, "someone", "nobody@unknown.net")
        invitation.status = CrmInvitation.EXPIRED
        invitation.save()

        then: "the invitation was created"
        CrmInvitation.findByRef("test5") // It's found but it's not active.
        crmInvitationService.getInvitationsFor("test5").size() == 0 // No active invitation exists.

        when: "user tries to accept the expired invitation"
        crmSecurityService.runAs("nobody") {
            controller.accept(invitation.ident())
        }

        then: "response should be GONE (410)"
        controller.response.status == HttpServletResponse.SC_GONE
    }


    def "it should be possible to deny a pending invitation"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation"
        def invitation = crmInvitationService.createInvitation("test3", null, "somebody", "nobody@unknown.net")

        then: "the invitation was created"
        crmInvitationService.getInvitationsFor("test3").size() == 1

        when: "user tries to deny the invitation"
        crmSecurityService.runAs("nobody") {
            controller.deny(invitation.ident())
        }

        then: "response should be success (FOUND)"
        controller.response.status == HttpServletResponse.SC_FOUND
        controller.response.redirectedUrl == "/crmInvitation/index" // mapping "home"
    }

    def "it should not be possible to deny an invitation directed to someone else"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation"
        def invitation = crmInvitationService.createInvitation("test4", null, "somebody", "foo@gr8crm.org")

        then: "the invitation was created"
        crmInvitationService.getInvitationsFor("test4").size() == 1

        when: "a different user user tries to deny the invitation"
        crmSecurityService.runAs("nobody") {
            controller.deny(invitation.ident())
        }

        then: "response should be FORBIDDEN"
        controller.response.status == HttpServletResponse.SC_FORBIDDEN
    }

    def "it should not be possible to deny an expired invitation"() {

        given: "configure the controller"
        def controller = new CrmInvitationController()
        controller.crmInvitationService = crmInvitationService
        controller.crmSecurityService = crmSecurityService

        when: "we create an invitation and immediately set it to EXPIRED"
        def invitation = crmInvitationService.createInvitation("test5", null, "someone", "nobody@unknown.net")
        invitation.status = CrmInvitation.EXPIRED
        invitation.save()

        then: "the invitation was created"
        CrmInvitation.findByRef("test5") // It's found but it's not active.
        crmInvitationService.getInvitationsFor("test5").size() == 0 // No active invitation exists.

        when: "user tries to deny the expired invitation"
        crmSecurityService.runAs("nobody") {
            controller.deny(invitation.ident())
        }

        then: "response should be GONE (410)"
        controller.response.status == HttpServletResponse.SC_GONE
    }
}
