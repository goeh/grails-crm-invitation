package grails.plugins.crm.invitation

import grails.test.GroovyPagesTestCase

/**
 * Tests for CrmInvitationTagLib.
 */
class CrmInvitationTagLibTests extends GroovyPagesTestCase {

    def crmInvitationService

    def testInvitationsFor() {
        crmInvitationService.createInvitation("test", null, "grails", "joe@acme.com")
        crmInvitationService.createInvitation("test", null, "grails", "liza@acme.com")

        def template = '<crm:invitations for="test">\${it.receiver}</crm:invitations>'
        assert applyTemplate(template) == "joe@acme.comliza@acme.com".toString()
    }

    def testInvitationsTo() {
        crmInvitationService.createInvitation("test", null, "grails", "joe@acme.com")
        crmInvitationService.createInvitation("test", null, "grails", "liza@acme.com")
        crmInvitationService.createInvitation("foo", null, "grails", "joe@acme.com")

        def template = '<crm:invitations email="joe@acme.com">\${it.reference}</crm:invitations>'
        assert applyTemplate(template) == "testfoo".toString()
    }
}
