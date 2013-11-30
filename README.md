# GR8 CRM

CRM = [Customer Relationship Management](http://en.wikipedia.org/wiki/Customer_relationship_management)

GR8 CRM is a set of [Grails Web Application Framework](http://www.grails.org/)
plugins that makes it easy to develop web application with CRM functionality.
With CRM we mean features like:

- Contact Management
- Task/Todo Lists
- Project Management

# GR8 CRM - Invitation Plugin

GR8 CRM users can share information with other users. This plugin manages user invitations.
An invitation is created by calling `crmInvitationService.createInvitation(...)`.
This triggers an event `crmInvitation#created`.
The application must subscribe to this event and probably wants to send an invitation email to the invited user.
When the user responds to the invitation, one of two events are triggered:

- `crmInvitation#accepted` when the user accepts the invitation
- `crmInvitation#denied` when the user denies (says "no thanks") the invitation

Invitations not responded to within 30 days (config parameter `crm.invitation.expires`) will expire and cannot be accepted or denied anymore.
A new invitation must be created if needed.

An invitation can be made for a specific **target object**. For example an invitation to see a specific project, customer or document.
You pass the target instance or any other identifier to createInvitation. This identifier will be available in accepted/denied events above.

## CrmInvitationService
    CrmInvitationService#createInvitation(Object target, String param, String senderUsername, String receiverEmail, Map data)

## Example
    class MyService {

        def crmInvitationService

        def invitePeopleToViewCustomerAccount(Customer customer, List<String> emails) {
            for(email in emails) {
                crmInvitationService.createInvitation(customer, 'view', customer.accountManager.username, email, [:])
            }
        }

        @Listener(namespace = "crmInvitation", topic = "created")
        def sendInvitationEmail(data) {
            println "Send an email to ${data.email} inviting her to view customer record ${data.target}"
        }

        @Listener(namespace = "crmInvitation", topic = "accepted")
        def invitationAccepted(data) {
            println "${data.email} accepted my invitation!"
            // Assign view permissions for (data.target) to user (data.email)
        }

        @Listener(namespace = "crmInvitation", topic = "denied")
        def invitationDenied(data) {
            println "${data.email} did not accept my invitation :-("
        }
    }
