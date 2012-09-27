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

/**
 * Invitation service for Grails CRM.
 */
class CrmInvitationService {

    static transactional = true

    def grailsApplication
    def crmCoreService
    def textTemplateService

    /**
     * Create new invitation.
     *
     * @param reference reference identifier for invited resource
     * @param senderUsername inviting user
     * @param receiverEmail invited user
     * @param emailTemplate text template name for invitation email
     * @param binding template binding
     * @return The created CrmInvitation instance
     */
    def createInvitation(Object reference, String param, String senderUsername, String receiverEmail, String emailTemplate = null, Map binding = null) {
        if (!senderUsername) {
            throw new IllegalArgumentException("argument [senderUsername] is mandatory")
        }
        if (!receiverEmail) {
            throw new IllegalArgumentException("argument [receiverEmail] is mandatory")
        }
        def tenant = (binding?.tenantId ?: binding?.tenant) ?: TenantUtils.tenant
        def ref = crmCoreService.getReferenceIdentifier(reference)
        def i = new CrmInvitation(tenantId: tenant, ref: ref, sender: senderUsername, receiver: receiverEmail, param: param).save(failOnError: true, flush: true)

        if (emailTemplate) {
            binding.invitation = i
            TenantUtils.withTenant(tenant) {
                sendInvitationEmail(i, emailTemplate, binding)
                i.status = CrmInvitation.SENT
                i.save()
            }
        }
        return i
    }

    /**
     * List invitations for a reference object.
     *
     * @param reference reference object
     * @param tenant optional tenant id
     * @return list of invitations for the object
     */
    List getInvitationsFor(reference, Long tenant = TenantUtils.tenant) {
        CrmInvitation.createCriteria().list([sort: 'dateCreated', order: 'asc']) {
            eq('tenantId', tenant)
            eq('ref', crmCoreService.getReferenceIdentifier(reference))
            inList('status', [CrmInvitation.CREATED, CrmInvitation.SENT])
        }
    }

    /**
     * List invitations for a user.
     *
     * @param email invited user
     * @param tenant optional tenant id
     * @return list of invitations sent to the user
     */
    List getInvitationsTo(String email, Long tenant = null) {
        CrmInvitation.createCriteria().list([sort: 'dateCreated', order: 'asc']) {
            if (tenant) {
                eq('tenantId', tenant)
            }
            eq('receiver', email)
            inList('status', [CrmInvitation.CREATED, CrmInvitation.SENT])
        }
    }

    /**
     * List invitations done by a user.
     *
     * @param username user doing the invitation
     * @param tenant optional tenant id
     * @return list of invitations sent by the user
     */
    List getInvitationsBy(String username, Long tenant = null) {
        CrmInvitation.createCriteria().list([sort: 'dateCreated', order: 'asc']) {
            if (tenant) {
                eq('tenantId', tenant)
            }
            eq('sender', username)
            inList('status', [CrmInvitation.CREATED, CrmInvitation.SENT])
        }
    }

    /**
     * Send invitation email to a user.
     *
     * @param invitation CrmInvitation instance
     * @param template email template
     * @param binding template binding
     */
    void sendInvitationEmail(CrmInvitation invitation, String template, Map binding = [:]) {
        def config = grailsApplication.config.crm.invitation.email
        def bodyText = textTemplateService.applyTemplate(template, "text/plain", binding)
        def bodyHtml = textTemplateService.applyTemplate(template, "text/html", binding)
        if (!(bodyText || bodyHtml)) {
            throw new RuntimeException("Template not found: [name=${template}]")
        }

        sendMail {
            if (bodyText && bodyHtml) {
                multipart true
            }
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

    /**
     * Get information about a specific invitation.
     *
     * @param id id of invitation
     * @return Invitation properties (DAO) or null if no invitation exists with the specified id
     */
    Map<String, Object> getInvitation(Long id) {
        CrmInvitation.get(id)?.dao
    }

    /**
     * Accept invitation.
     *
     * @param invitation CrmInvitation instance or Long invitation ID
     */
    void accept(invitation) {
        def crmInvitation = parseInvitationArgument(invitation)
        crmInvitation.status = CrmInvitation.ACCEPTED
        crmInvitation.save()
        event(for: "crm", topic: "invitationAccepted", data: crmInvitation.dao)
    }

    /**
     * Deny invitation.
     *
     * @param invitation CrmInvitation instance or Long invitation ID
     */
    void deny(invitation) {
        def crmInvitation = parseInvitationArgument(invitation)
        crmInvitation.status = CrmInvitation.DENIED
        crmInvitation.save()
        event(for: "crm", topic: "invitationDenied", data: crmInvitation.dao)
    }

    /**
     * Cancel invitation.
     *
     * @param invitation CrmInvitation instance or Long invitation ID
     */
    void cancel(invitation) {
        def crmInvitation = parseInvitationArgument(invitation)
        def info = crmInvitation.dao
        crmInvitation.delete(flush: true)
        event(for: "crm", topic: "invitationCancelled", data: info)
    }

    private CrmInvitation parseInvitationArgument(Object arg) {
        def invitation
        if (arg instanceof CrmInvitation) {
            invitation = arg
        } else {
            invitation = CrmInvitation.get(arg.toString())
            if (!invitation) {
                throw new IllegalArgumentException("CrmInvitation not found with id [$arg]")
            }
        }
        return invitation
    }
}
